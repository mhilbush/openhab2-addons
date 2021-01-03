/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.sunsetwx.internal.handler;

import static org.openhab.binding.sunsetwx.internal.SunsetWxBindingConstants.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Properties;

import org.openhab.binding.sunsetwx.internal.dto.ErrorResponseDTO;
import org.openhab.binding.sunsetwx.internal.dto.FeatureContainerDTO;
import org.openhab.binding.sunsetwx.internal.dto.LocationFeatureDTO;
import org.openhab.binding.sunsetwx.internal.dto.LocationPropertiesDTO;
import org.openhab.binding.sunsetwx.internal.dto.LoginErrorDTO;
import org.openhab.binding.sunsetwx.internal.dto.LoginSuccessDTO;
import org.openhab.binding.sunsetwx.internal.dto.QualityFeatureDTO;
import org.openhab.binding.sunsetwx.internal.dto.QualityPropertiesDTO;
import org.openhab.binding.sunsetwx.internal.model.QualityConverter;
import org.openhab.binding.sunsetwx.internal.model.RunningStats;
import org.openhab.core.io.net.http.HttpUtil;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * The {@link SunsetWxSession} is responsible for handling interactions with the SunsetWx service.
 *
 * @author Mark Hilbush - Initial contribution
 */
public class SunsetWxSession {
    private Logger logger = LoggerFactory.getLogger(SunsetWxSession.class);

    private SunsetWxHandler handler;
    private Thing thing;

    // SunsetWX URL fragments
    private static final String URL_DOMAIN = "https://sunburst.sunsetwx.com/";
    private static final String URL_VERSION = "v1/";
    private static final String URL_LOGIN = "login";
    private static final String URL_QUALITY = "quality";
    private static final String URL_LOCATION = "location";

    private static final int TIMEOUT_LOGIN = 10000;
    private static final int TIMEOUT_OTHER = 20000;

    private static final String USER_AGENT = "openHAB SunsetWx Binding";

    private String emailAddress;
    private String password;

    private String sessionToken;
    private long sessionTokenExpirationTime;

    private LocationPropertiesDTO locationProperties;

    public SunsetWxSession(final SunsetWxHandler handler) {
        this.handler = handler;
        this.thing = handler.getThing();

        emailAddress = (String) thing.getConfiguration().get(THING_PROPERTY_EMAILADDRESS);
        password = (String) thing.getConfiguration().get(THING_PROPERTY_PASSWORD);
        sessionTokenExpirationTime = 0L;
        locationProperties = null;
        logger.debug("Creating session object for {}", thing.getUID());
    }

    private void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    private boolean isExpired() {
        if (System.currentTimeMillis() > sessionTokenExpirationTime) {
            logger.debug("Session token is expired for {}; need to login to service", thing.getUID());
            return true;
        }
        return false;
    }

    /*
     * Get the location and sunrise/sunset quality information from
     * the SunsetWx service, then publish the data to the thing's channels
     */
    public void sunsetWxPublishQuality() {
        try {
            if (sunsetWxLogin()) {
                // sunsetWxLocation();
                sunsetWxQuality();
            }
        } catch (RuntimeException e) {
            logger.info("sunsetWxPublishQuality caught unhandled exception: {}", e.getMessage());
        }
    }

    /*
     * Log in to the SunsetWx service
     */
    private synchronized boolean sunsetWxLogin() {
        // Check if session token is still valid
        if (!isExpired()) {
            return true;
        }

        // Session token is not valid, need to log in
        String loginURL = URL_DOMAIN + URL_VERSION + URL_LOGIN;

        // Validate that we have an email address and password
        if (emailAddress == null || password == null) {
            logger.warn("Email address and/or password are not set in configuration for thing {}", thing.getUID());
            handler.updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Email address or password not set");
            return false;
        }
        String content = "email=" + emailAddress + "&" + "password=" + password;
        String contentType = "application/x-www-form-urlencoded";

        Properties headers = new Properties();
        headers.setProperty("UserAgent: ", USER_AGENT);

        String response;
        try {
            logger.debug("Execute login request for {} with url={} and email={}", thing.getUID(), loginURL,
                    emailAddress);
            ByteArrayInputStream contentInputStream = new ByteArrayInputStream(content.getBytes());
            long startTime = System.currentTimeMillis();
            response = HttpUtil.executeUrl("POST", loginURL, contentInputStream, contentType, TIMEOUT_LOGIN);
            long duration = System.currentTimeMillis() - startTime;
            logger.debug("Login http request returned in {} milliseconds", duration);
            contentInputStream.close();
        } catch (IOException e) {
            logger.error("IOException on call to 'login', message={}", e.getMessage());
            handler.updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Login failed; check log file");
            return false;
        }

        return parseLoginResponse(response);
    }

    private boolean parseLoginResponse(String response) {
        if (response == null) {
            logger.warn("Login response returned from service was null for {}", thing.getUID());
            return false;
        }
        logger.trace("Raw JSON login response: {}", response.trim());

        Gson g = new Gson();
        LoginSuccessDTO loginResponse;

        // Check for login success
        try {
            logger.debug("Parsing login response using Login class for {}", thing.getUID());
            loginResponse = g.fromJson(response, LoginSuccessDTO.class);
        } catch (JsonSyntaxException e) {
            logger.error("Syntax exception parsing response using Login class: {}", e.getMessage());
            logger.error("Exception occurred on response: {}", response.trim());
            return false;
        } catch (IllegalStateException ise) {
            logger.error("IllegalState exception parsing login response: {}", ise.getMessage());
            logger.error("Exception occurred on response: {}", response.trim());
            return false;
        }

        // If we can't find the message field, then maybe this is an error response
        if (loginResponse.getMessage() == null) {
            LoginErrorDTO loginErrorResponse;
            String loginErrorMessage;
            try {
                logger.debug("Parsing login response using LoginError class for {}", thing.getUID());
                loginErrorResponse = g.fromJson(response, LoginErrorDTO.class);
            } catch (JsonSyntaxException e) {
                logger.error("Syntax exception parsing response using LoginError class: {}", e.getMessage());
                logger.error("Exception occurred on response: {}", response.trim());
                return false;
            } catch (IllegalStateException ise) {
                logger.error("IllegalState exception parsing login response: {}", ise.getMessage());
                logger.error("Exception occurred on response: {}", response.trim());
                return false;
            }

            if (loginErrorResponse.getError() == null) {
                loginErrorMessage = loginErrorResponse.getError();
                logger.warn("Unable to get login error message from login error response for {}", thing.getUID());
            } else {
                loginErrorMessage = loginErrorResponse.getError();
                logger.warn("Login error for {}: {}", thing.getUID(), loginErrorMessage);
            }

            handler.updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, loginErrorMessage);
            return false;
        }

        if (!loginResponse.getMessage().contains("successful")) {
            logger.warn("Login failed to SunsetWx for {}, message={}", thing.getUID(), loginResponse.getMessage());
            handler.updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Login failed, check log file");
            return false;
        }

        // Save the session token returned from the service
        setSessionToken(loginResponse.getToken());

        // Set the token expiration time minus a few seconds of wiggle room
        sessionTokenExpirationTime = System.currentTimeMillis() + (loginResponse.getTokenExpSec() * 1000) - 60000;

        Calendar expires = Calendar.getInstance();
        expires.setTimeInMillis(sessionTokenExpirationTime);
        logger.info("Logged in to SunsetWx for {}, token expires at {}", thing.getUID(), expires.getTime().toString());

        if (!thing.getStatus().equals(ThingStatus.ONLINE)) {
            logger.debug("Marking thing {} ONLINE", thing.getUID());
            handler.updateStatus(ThingStatus.ONLINE);
        }

        return true;
    }

    /*
     * Get the sunrise/sunset quality from the SunsetWx service
     */
    private synchronized void sunsetWxQuality() {
        parseQualityResponse(executeHttpGetRequest(buildQualityURL()));
    }

    private String buildQualityURL() {
        StringBuilder sb = new StringBuilder();
        sb.append(URL_DOMAIN);
        sb.append(URL_VERSION);
        sb.append(URL_QUALITY);
        sb.append("?");
        sb.append("type=");
        sb.append(handler.getModelType().toString().toLowerCase());
        sb.append("&");
        sb.append("coords=");
        sb.append(handler.getCoordinates());
        sb.append("&");
        sb.append("limit=");
        sb.append(handler.getLimit());
        sb.append("&");
        sb.append("radius=");
        sb.append(handler.getRadius());
        // sb.append("&");
        // sb.append("location=");
        // sb.append(handler.getLocation());
        return sb.toString();
    }

    private void parseQualityResponse(String response) {
        if (response == null) {
            logger.info("Quality response returned from service was null for {}", thing.getUID());
            return;
        }

        logger.trace("Raw JSON response: {}", response.trim());

        Gson g = new Gson();
        FeatureContainerDTO fc;
        logger.debug("Parsing quality response for {}", thing.getUID());

        try {
            fc = g.fromJson(response, FeatureContainerDTO.class);
        } catch (JsonSyntaxException e) {
            logger.error("Exception parsing quality response: {}", e.getMessage());
            logger.error("Exception occurred on response: {}", response.trim());
            return;
        } catch (IllegalStateException ise) {
            logger.error("IllegalState exception parsing quality response: {}", ise.getMessage());
            logger.error("Exception occurred on response: {}", response.trim());
            return;
        }

        logger.debug("Successfully parsed quality response for {}", thing.getUID());

        if (fc.features == null) {
            logger.warn("Quality features for {} was null.", thing.getUID());
            logger.warn("Null response: {}", response.trim());
            return;
        }

        if (fc.features.size() < 1) {
            logger.warn("Result set for {} contains no features.", thing.getUID());
            logger.warn("Featureless response: {}", response.trim());
            return;
        }

        logger.debug("Quality response for {} contains {} features", thing.getUID(), fc.features.size());

        updateChannels(response, fc);
    }

    /*
     * Update the response channel, the quality channels, and the date channels
     */
    private void updateChannels(String response, FeatureContainerDTO fc) {
        updateResponseChannel(response);
        updateQualityChannels(fc);
        updateDateChannels(fc.features.get(0).properties);
    }

    /*
     * Update the response channel with the raw JSON response from SunsetWx
     */
    private void updateResponseChannel(String response) {
        if (handler.isLinked(CHANNEL_RAW_RESPONSE)) {
            // Only update this channel if it's linked
            updateChannel(CHANNEL_RAW_RESPONSE, new StringType(response.trim()));
        }
    }

    /*
     * Update the quality channels (quality, qualityPercent, qualityValue)
     */
    private void updateQualityChannels(FeatureContainerDTO fc) {
        RunningStats qualityPercentStats = new RunningStats();
        RunningStats qualityValueStats = new RunningStats();

        // Calculate the average quality percent and quality value
        for (QualityFeatureDTO f : fc.features) {
            QualityPropertiesDTO p = f.properties;
            qualityPercentStats.put(p.getQualityPercent());
            qualityValueStats.put(p.getQualityValue());

            if (logger.isTraceEnabled()) {
                logger.trace("Coord={},{}; type={}; qual={}; qPct={}; qVal={}", f.geometry.coordinates[1],
                        f.geometry.coordinates[0], p.getType(), p.getQuality(), p.getQualityPercent(),
                        p.getQualityValue());
            }
        }
        double qualityPercentAverage = Math.round(qualityPercentStats.getAverage() * 100.0) / 100.0;
        double qualityPercentStdDev = Math.round(qualityPercentStats.getStandardDeviation() * 1000.0) / 1000.0;
        double qualityValueAverage = Math.round(qualityValueStats.getAverage() * 100.0) / 100.0;
        double qualityValueStdDev = Math.round(qualityValueStats.getStandardDeviation() * 1000.0) / 1000.0;

        // Use the average quality value to determine the quality (Poor, Fair, Good, or Great)
        QualityConverter converter = new QualityConverter(handler.getModelType());
        String quality = converter.convertQuality(qualityValueAverage);

        // Update the quality channels
        updateChannel(CHANNEL_QUALITY, new StringType(quality));
        updateChannel(CHANNEL_QUALITY_PERCENT, new DecimalType(qualityPercentAverage));
        updateChannel(CHANNEL_QUALITY_VALUE, new DecimalType(qualityValueAverage));

        logger.debug("Quality statistics: q={}; qPctAvg={}; qPctStdDev={}; qValAvg={}; qValStdDev={}", quality,
                qualityPercentAverage, qualityPercentStdDev, qualityValueAverage, qualityValueStdDev);
    }

    /*
     * Update the date channels
     */
    private void updateDateChannels(QualityPropertiesDTO props) {
        ZonedDateTime date;

        date = ZonedDateTime.ofInstant(props.getLastUpdated().toInstant(), ZoneId.systemDefault());
        updateChannel(CHANNEL_LAST_UPDATED, new DateTimeType(date));

        date = ZonedDateTime.ofInstant(props.getImportedAt().toInstant(), ZoneId.systemDefault());
        updateChannel(CHANNEL_IMPORTED_AT, new DateTimeType(date));

        date = ZonedDateTime.ofInstant(props.getValidAt().toInstant(), ZoneId.systemDefault());
        updateChannel(CHANNEL_VALID_AT, new DateTimeType(date));

        date = ZonedDateTime.now(ZoneId.systemDefault());
        updateChannel(CHANNEL_LAST_REPORT_TIME, new DateTimeType(date));

        updateChannel(CHANNEL_SOURCE, new StringType(props.getSource()));

        // Calendar c = Calendar.getInstance();
        // c.setTime(props.getLastUpdated());
        // updateChannel(CHANNEL_LAST_UPDATED, new DateTimeType(c));
        // c.setTime(props.getImportedAt());
        // updateChannel(CHANNEL_IMPORTED_AT, new DateTimeType(c));
        // c.setTime(props.getValidAt());
        // updateChannel(CHANNEL_VALID_AT, new DateTimeType(c));
        // c.setTimeInMillis(System.currentTimeMillis());
        // updateChannel(CHANNEL_LAST_REPORT_TIME, new DateTimeType(c));
    }

    /*
     * Get the location information from the SunsetWx service
     * using the geolocation information in the thing config
     */
    private synchronized void sunsetWxLocation() {
        if (locationProperties == null) {
            parseLocationResponse(executeHttpGetRequest(buildLocationURL()));
        }
        updateLocationChannels();
    }

    private String buildLocationURL() {
        StringBuilder sb = new StringBuilder();
        sb.append(URL_DOMAIN);
        sb.append(URL_VERSION);
        sb.append(URL_LOCATION);
        sb.append("?");
        sb.append("coords=");
        sb.append(handler.getCoordinates());
        return sb.toString();
    }

    private void parseLocationResponse(String response) {
        if (response == null) {
            logger.info("Location response returned from service was null for {}", thing.getUID());
            return;
        }

        String errorMessage = getError(response);
        if (errorMessage != null) {
            logger.error("Error getting location information from service for {}: {}", thing.getUID(), errorMessage);
            return;
        }

        String trimmedResponse = response.trim();
        logger.trace("Raw JSON location response: {}", response);

        Gson g = new Gson();
        LocationFeatureDTO lf;
        logger.debug("Parsing location response for {}", thing.getUID());
        try {
            lf = g.fromJson(trimmedResponse, LocationFeatureDTO.class);
        } catch (JsonSyntaxException e) {
            logger.error("Exception parsing location response: {}", e.getMessage());
            return;
        } catch (IllegalStateException e) {
            logger.error("IllegalState exception parsing location response: {}", e.getMessage());
            logger.error("Exception occurred on response: {}", trimmedResponse);
            return;
        }

        if (lf.properties == null) {
            logger.error("Location response for {} contains no properties", thing.getUID());
            return;
        }
        logger.debug("Successfully parsed location response for {}", thing.getUID());

        locationProperties = new LocationPropertiesDTO();
        locationProperties.setLocale(lf.properties.getLocale());
        locationProperties.setRegion(lf.properties.getRegion());
        locationProperties.setCountry(lf.properties.getCountry());
        locationProperties.setSource(lf.properties.getSource());

        logger.debug("Location: locale={}; region={}; country={}; source={}", locationProperties.getLocale(),
                locationProperties.getRegion(), locationProperties.getCountry(), locationProperties.getSource());
    }

    private void updateLocationChannels() {
        if (locationProperties != null) {
            updateChannel(CHANNEL_LOCALE, new StringType(locationProperties.getLocale()));
            updateChannel(CHANNEL_REGION, new StringType(locationProperties.getRegion()));
            updateChannel(CHANNEL_COUNTRY, new StringType(locationProperties.getCountry()));
            updateChannel(CHANNEL_SOURCE, new StringType(locationProperties.getSource()));
        }
    }

    private void updateChannel(String channelName, State state) {
        Channel channel = thing.getChannel(channelName);
        if (channel != null) {
            handler.updateState(channel.getUID().getId(), state);
        }
    }

    private String getError(String response) {
        ErrorResponseDTO errorResponse;
        Gson g = new Gson();

        try {
            logger.debug("Attempting to parsing response using ErrorResponse class for {}", thing.getUID());
            errorResponse = g.fromJson(response, ErrorResponseDTO.class);
        } catch (JsonSyntaxException e) {
            return "JsonSyntaxException parsing respone";
        } catch (IllegalStateException e) {
            return "IllegalStateException parsing response";
        }

        if (errorResponse.getError() != null) {
            return errorResponse.getError();
        }
        return null;
    }

    private Properties setHeaders() {
        Properties headers = new Properties();
        headers.setProperty("UserAgent", USER_AGENT);
        headers.setProperty("Authorization", "Bearer " + sessionToken);
        return headers;
    }

    private String executeHttpGetRequest(String url) {
        String response;
        try {
            logger.debug("Http request sent using url={}", url);
            long startTime = System.currentTimeMillis();
            response = HttpUtil.executeUrl("GET", url, setHeaders(), null, null, TIMEOUT_OTHER);
            long duration = System.currentTimeMillis() - startTime;
            logger.debug("Http request returned in {} milliseconds", duration);
        } catch (IOException e) {
            logger.debug("IOException on http request, message={}", e.getMessage());
            response = null;
        }
        return response;
    }
}
