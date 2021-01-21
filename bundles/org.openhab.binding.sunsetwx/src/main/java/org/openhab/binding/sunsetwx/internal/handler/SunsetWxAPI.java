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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.sunsetwx.internal.dto.ErrorResponseDTO;
import org.openhab.binding.sunsetwx.internal.dto.LocationFeatureDTO;
import org.openhab.binding.sunsetwx.internal.dto.LocationPropertiesDTO;
import org.openhab.binding.sunsetwx.internal.dto.LoginErrorDTO;
import org.openhab.binding.sunsetwx.internal.dto.LoginSuccessDTO;
import org.openhab.binding.sunsetwx.internal.dto.QualityRequestDTO;
import org.openhab.core.io.net.http.HttpUtil;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * The {@link SunsetWxAPI} interacts with the SunsetWx API.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class SunsetWxAPI {

    private Logger logger = LoggerFactory.getLogger(SunsetWxAPI.class);

    private SunsetWxAccountHandler handler;

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

    private String sessionToken = "";
    private long sessionTokenExpirationTime;

    private @Nullable LocationPropertiesDTO locationProperties;

    public SunsetWxAPI(final SunsetWxAccountHandler handler, String emailAddress, String password) {
        this.handler = handler;
        this.emailAddress = emailAddress;
        this.password = password;
        sessionTokenExpirationTime = 0L;
        locationProperties = null;
        logger.debug("Created SunsetWx API object");
    }

    private void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    private boolean isExpired() {
        if (System.currentTimeMillis() > sessionTokenExpirationTime) {
            logger.debug("Session token is expired; need to login to service");
            return true;
        }
        return false;
    }

    /*
     * Get the location and sunrise/sunset quality information from
     * the SunsetWx service, then publish the data to the thing's channels
     */
    public @Nullable String getSunsetWxQuality(QualityRequestDTO request) {
        try {
            if (sunsetWxLogin()) {
                // sunsetWxLocation();
                return sunsetWxQuality(request);
            }
        } catch (RuntimeException e) {
            logger.info("sunsetWxPublishQuality caught unhandled exception: {}", e.getMessage());
        }
        return null;
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

        String content = "email=" + emailAddress + "&" + "password=" + password;
        String contentType = "application/x-www-form-urlencoded";

        Properties headers = new Properties();
        headers.setProperty("UserAgent: ", USER_AGENT);

        String response;
        try {
            logger.debug("Execute login request with url={} and email={}", loginURL, emailAddress);
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
            logger.warn("Login response returned from service was null");
            return false;
        }
        logger.trace("Raw JSON login response: {}", response.trim());

        Gson g = new Gson();
        LoginSuccessDTO loginResponse;

        // Check for login success
        try {
            logger.debug("Parsing login response using Login class");
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
                logger.debug("Parsing login response using LoginError class");
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
                logger.warn("Unable to get login error message from login error response");
            } else {
                loginErrorMessage = loginErrorResponse.getError();
                logger.warn("Login error: {}", loginErrorMessage);
            }
            handler.updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, loginErrorMessage);
            return false;
        }

        if (!loginResponse.getMessage().contains("successful")) {
            logger.warn("Login failed to SunsetWx, message={}", loginResponse.getMessage());
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
        logger.info("Logged in to SunsetWx, token expires at {}", expires.getTime().toString());
        handler.updateStatus(ThingStatus.ONLINE);
        return true;
    }

    /*
     * Get the sunrise/sunset quality from the SunsetWx service
     */
    private @Nullable synchronized String sunsetWxQuality(QualityRequestDTO request) {
        return executeHttpGetRequest(buildQualityURL(request));
    }

    private String buildQualityURL(QualityRequestDTO request) {
        StringBuilder sb = new StringBuilder();
        sb.append(URL_DOMAIN);
        sb.append(URL_VERSION);
        sb.append(URL_QUALITY);
        sb.append("?");
        sb.append("type=");
        sb.append(request.getModelType().toString().toLowerCase());
        sb.append("&");
        sb.append("coords=");
        sb.append(request.getCoordinates());
        sb.append("&");
        sb.append("limit=");
        sb.append(request.getLimit());
        sb.append("&");
        sb.append("radius=");
        sb.append(request.getRadius());
        // sb.append("&");
        // sb.append("location=");
        // sb.append(request.getLocation());
        return sb.toString();
    }

    /*
     * Get the location information from the SunsetWx service
     * using the geolocation information in the thing config
     */
    private synchronized void sunsetWxLocation(QualityRequestDTO request) {
        if (locationProperties == null) {
            parseLocationResponse(executeHttpGetRequest(buildLocationURL(request)));
        }
        // FIXME
        // updateLocationChannels();
    }

    private String buildLocationURL(QualityRequestDTO request) {
        StringBuilder sb = new StringBuilder();
        sb.append(URL_DOMAIN);
        sb.append(URL_VERSION);
        sb.append(URL_LOCATION);
        sb.append("?");
        sb.append("coords=");
        sb.append(request.getCoordinates());
        return sb.toString();
    }

    private void parseLocationResponse(@Nullable String response) {
        if (response == null) {
            logger.info("Location response returned from service was null");
            return;
        }
        String errorMessage = getError(response);
        if (errorMessage != null) {
            logger.error("Error getting location information from service: {}", errorMessage);
            return;
        }
        String trimmedResponse = response.trim();
        logger.trace("Raw JSON location response: {}", response);

        Gson g = new Gson();
        LocationFeatureDTO lf;
        logger.debug("Parsing location response");
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
            logger.error("Location response contains no properties");
            return;
        }
        logger.debug("Successfully parsed location response");

        locationProperties = new LocationPropertiesDTO();
        locationProperties.setLocale(lf.properties.getLocale());
        locationProperties.setRegion(lf.properties.getRegion());
        locationProperties.setCountry(lf.properties.getCountry());
        locationProperties.setSource(lf.properties.getSource());
        logger.debug("Location: locale={}; region={}; country={}; source={}", locationProperties.getLocale(),
                locationProperties.getRegion(), locationProperties.getCountry(), locationProperties.getSource());
    }

    private @Nullable String getError(String response) {
        ErrorResponseDTO errorResponse;
        Gson g = new Gson();

        try {
            logger.debug("Attempting to parsing response using ErrorResponse class");
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

    private @Nullable String executeHttpGetRequest(String url) {
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
