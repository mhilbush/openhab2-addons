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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.sunsetwx.internal.config.SunsetWxConfig;
import org.openhab.binding.sunsetwx.internal.dto.FeatureContainerDTO;
import org.openhab.binding.sunsetwx.internal.dto.QualityFeatureDTO;
import org.openhab.binding.sunsetwx.internal.dto.QualityPropertiesDTO;
import org.openhab.binding.sunsetwx.internal.dto.QualityRequestDTO;
import org.openhab.binding.sunsetwx.internal.model.ModelType;
import org.openhab.binding.sunsetwx.internal.model.QualityConverter;
import org.openhab.binding.sunsetwx.internal.model.RunningStats;
import org.openhab.core.common.AbstractUID;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * The {@link SunsetWxHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class SunsetWxHandler extends BaseThingHandler {
    private Logger logger = LoggerFactory.getLogger(SunsetWxHandler.class);

    private Gson gson = new Gson();

    private QualityRequestDTO qualityRequestDTO = new QualityRequestDTO();

    private int configRefreshIntervalSeconds;

    private @Nullable ScheduledFuture<?> updateQualityJob;

    public SunsetWxHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        logger.debug("Initializing sunsetwx handler for {}", getThing().getUID());

        String[] segments = thing.getUID().toString().split(AbstractUID.SEPARATOR);
        if (segments.length >= 2) {
            qualityRequestDTO.setModelType(ModelType.valueOf(segments[1].toUpperCase()));
            logger.debug("Initializing {} with modelType={}", getThing().getUID(), qualityRequestDTO.getModelType());
        } else {
            qualityRequestDTO.setModelType(ModelType.SUNSET);
            logger.debug("Initializing {} with default modelType={}", getThing().getUID(),
                    qualityRequestDTO.getModelType());
        }

        SunsetWxConfig config = getConfigAs(SunsetWxConfig.class);
        qualityRequestDTO.setCoordinates(config.geolocation);
        qualityRequestDTO.setLimit(config.limit);
        qualityRequestDTO.setRadius(config.radius);
        qualityRequestDTO.setLocation(config.location);

        configRefreshIntervalSeconds = config.refreshIntervalMinutes * 60;

        scheduleUpdateQualityJob();
        updateStatus(ThingStatus.ONLINE);
    }

    @Override
    public void dispose() {
        logger.debug("Disposing sunsetwx handler for {}", getThing().getUID());
        cancelUpdateQualityJob();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command == RefreshType.REFRESH && channelUID.getId().equals(CHANNEL_QUALITY)) {
            logger.debug("Handle command to refresh quality for {}", getThing().getUID());
            scheduler.schedule(this::refreshQuality, 0, TimeUnit.SECONDS);
        }
    }

    private void refreshQuality() {
        Bridge bridge = getBridge();
        if (bridge != null && bridge.getStatus() == ThingStatus.ONLINE) {
            SunsetWxAccountHandler bridgeHandler = (SunsetWxAccountHandler) bridge.getHandler();
            if (bridgeHandler != null) {
                parseResponseAndUpdateChannels(bridgeHandler.getQuality(qualityRequestDTO));
            }
        }
    }

    private void scheduleUpdateQualityJob() {
        logger.debug("Scheduling update {} quality job every {} minutes for {}", qualityRequestDTO.getModelType(),
                configRefreshIntervalSeconds, getThing().getUID());
        cancelUpdateQualityJob();
        updateQualityJob = scheduler.scheduleWithFixedDelay(this::refreshQuality, 4L, configRefreshIntervalSeconds,
                TimeUnit.SECONDS);
    }

    private void cancelUpdateQualityJob() {
        final @Nullable ScheduledFuture<?> localUpdateQualityJob = updateQualityJob;
        if (localUpdateQualityJob != null) {
            logger.debug("Canceling update {} quality job for {}", qualityRequestDTO.getModelType(),
                    getThing().getUID());
            localUpdateQualityJob.cancel(true);
            updateQualityJob = null;
        }
    }

    private void parseResponseAndUpdateChannels(@Nullable String response) {
        if (response == null) {
            logger.debug("Quality response returned from service was null");
            return;
        }
        logger.trace("Raw JSON response: {}", response.trim());

        FeatureContainerDTO fc;
        try {
            fc = gson.fromJson(response, FeatureContainerDTO.class);
        } catch (JsonSyntaxException e) {
            logger.info("Exception parsing quality response: {}", e.getMessage());
            logger.info("Exception occurred on response: {}", response.trim());
            return;
        } catch (IllegalStateException ise) {
            logger.info("IllegalState exception parsing quality response: {}", ise.getMessage());
            logger.info("Exception occurred on response: {}", response.trim());
            return;
        }
        if (fc.features == null) {
            logger.debug("Quality features was null.");
            logger.debug("Null response: {}", response.trim());
            return;
        }
        if (fc.features.size() < 1) {
            logger.debug("Result set contains no features.");
            logger.debug("Featureless response: {}", response.trim());
            return;
        }
        logger.debug("Quality response contains {} features", fc.features.size());
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
     * Update the response channel with the raw JSON response from SunsetWx,
     * but only of the channel is linked
     */
    private void updateResponseChannel(String response) {
        if (isLinked(CHANNEL_RAW_RESPONSE)) {
            updateState(CHANNEL_RAW_RESPONSE, new StringType(response.trim()));
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
        QualityConverter converter = new QualityConverter(qualityRequestDTO.getModelType());
        String quality = converter.convertQuality(qualityValueAverage);

        // Update the quality channels
        updateState(CHANNEL_QUALITY, new StringType(quality));
        updateState(CHANNEL_QUALITY_PERCENT, new DecimalType(qualityPercentAverage));
        updateState(CHANNEL_QUALITY_VALUE, new DecimalType(qualityValueAverage));
        logger.debug("Quality statistics: q={}; qPctAvg={}; qPctStdDev={}; qValAvg={}; qValStdDev={}", quality,
                qualityPercentAverage, qualityPercentStdDev, qualityValueAverage, qualityValueStdDev);
    }

    /*
     * Update the date channels
     */
    private void updateDateChannels(QualityPropertiesDTO props) {
        ZonedDateTime date;
        date = ZonedDateTime.ofInstant(props.getLastUpdated().toInstant(), ZoneId.systemDefault());
        updateState(CHANNEL_LAST_UPDATED, new DateTimeType(date));
        date = ZonedDateTime.ofInstant(props.getImportedAt().toInstant(), ZoneId.systemDefault());
        updateState(CHANNEL_IMPORTED_AT, new DateTimeType(date));
        date = ZonedDateTime.ofInstant(props.getValidAt().toInstant(), ZoneId.systemDefault());
        updateState(CHANNEL_VALID_AT, new DateTimeType(date));
        date = ZonedDateTime.now(ZoneId.systemDefault());
        updateState(CHANNEL_LAST_REPORT_TIME, new DateTimeType(date));
        updateState(CHANNEL_SOURCE, new StringType(props.getSource()));
    }
}
