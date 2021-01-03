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
package org.openhab.binding.bhyve.internal.handler;

import java.net.URISyntaxException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.openhab.binding.bhyve.internal.dto.ResponseDeviceDTO;
import org.openhab.core.i18n.TimeZoneProvider;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.ThingStatusInfo;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * The {@link BhyveAbstractThingHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public abstract class BhyveAbstractThingHandler extends BaseThingHandler {
    protected static final Gson GSON = new Gson();

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    // Listener for receiving events from online service
    protected final EventListener listener;

    // Session token retrieved from bridge
    protected @Nullable String sessionToken;

    // Used for converting to local time
    protected @NonNullByDefault({}) TimeZoneProvider timeZoneProvider;

    public BhyveAbstractThingHandler(Thing thing, WebSocketClient webSocketClient, TimeZoneProvider timeZoneProvider)
            throws URISyntaxException {
        super(thing);
        this.timeZoneProvider = timeZoneProvider;
        listener = new EventListener(this, webSocketClient, scheduler);
    }

    /**
     * Get the device id, which uniquely identifies a Bhyve device
     *
     * @return
     */
    protected abstract @Nullable String getDeviceId();

    /**
     * Handle an event received from the event listener
     *
     * @param eventId
     * @param message
     */
    protected abstract void handleEvent(String eventId, String message);

    /**
     * Handle an update to the device information
     *
     * @param device
     */
    protected abstract void handleDeviceUpdate(ResponseDeviceDTO device);

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.UNKNOWN);
        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_PENDING,
                "Awaiting bridge connection to service");
    }

    @Override
    public void dispose() {
        listener.stop();
        updateStatus(ThingStatus.OFFLINE);
    }

    @Override
    public void bridgeStatusChanged(ThingStatusInfo bridgeStatusInfo) {
        if (bridgeStatusInfo.getStatus() == ThingStatus.ONLINE) {
            logger.debug("AbstractHandler: Bridge status changed to ONLINE. Start event listener");
            listener.start();
        } else {
            logger.debug("AbstractHandler: Bridge status no longer ONLINE. Stop event listener");
            listener.stop();
        }
    }

    public boolean isLoggedIn() {
        return getSessionToken() != null ? true : false;
    }

    public @Nullable String getSessionToken() {
        sessionToken = null;
        Thing bridge = getBridge();
        if (bridge != null) {
            BhyveBridgeHandler bridgeHandler = (BhyveBridgeHandler) bridge.getHandler();
            if (bridgeHandler != null) {
                sessionToken = bridgeHandler.getSessionToken();
            }
        }
        return sessionToken;
    }

    /*
     * Helper function called by the listener to update the channel state
     */
    public void updateChannel(String channelId, State state) {
        if (isLinked(channelId)) {
            updateState(channelId, state);
        }
    }

    /**
     * Helper function to get the time zone from the framework
     */
    public ZoneId getZoneId() {
        return timeZoneProvider != null ? timeZoneProvider.getTimeZone() : ZoneId.systemDefault();
    }

    /**
     * Helper function to convert UTC time milliseconds to local time and return
     * a DateTimeType object
     */
    public DateTimeType getLocalDateTimeType(long dateTimeMillis, ZoneId zoneId) {
        Instant instant = Instant.ofEpochMilli(dateTimeMillis);
        ZonedDateTime localDateTime = instant.atZone(zoneId);
        DateTimeType dateTimeType = new DateTimeType(localDateTime);
        return dateTimeType;
    }

    /**
     * Helper function to convert UTC time string to local time
     * Input string is of form 2018-12-02T10:47:00.000Z
     */
    public DateTimeType getLocalDateTimeType(String dateTimeString, ZoneId zoneId) {
        ZonedDateTime localDateTime = getZonedDateTime(dateTimeString, zoneId);
        return localDateTime != null ? new DateTimeType(localDateTime) : new DateTimeType();
    }

    /**
     * Helper function to convert a date/time string to a ZonedDateTime object using
     * the provided time zone.
     *
     * @param dateTimeString Date/time string
     * @param zoneId Time zone to which date/time should be converted
     * @return ZonedDateTime object, or null
     */
    protected @Nullable ZonedDateTime getZonedDateTime(String dateTimeString, ZoneId zoneId) {
        ZonedDateTime localDateTime = null;
        try {
            Instant instant = Instant.parse(dateTimeString);
            localDateTime = instant.atZone(zoneId);
        } catch (DateTimeParseException e) {
            logger.debug("AbstractHandler: Error parsing date/time string: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.debug("AbstractHandler: Error converting to DateTimeType: {}", e.getMessage());
        }
        return localDateTime;
    }

    /**
     * Convert date/time string to an OffsetDateTime object
     *
     * @param dateTimeString Date/time string
     * @return OffsetDateTime object
     */
    public DateTimeType getOffsetDateTimeType(String dateTimeString) {
        DateTimeType dateTimeType;
        try {
            OffsetDateTime odt = OffsetDateTime.parse(dateTimeString);
            ZonedDateTime localDateTime = odt.toZonedDateTime();
            dateTimeType = new DateTimeType(localDateTime);
        } catch (DateTimeParseException e) {
            logger.debug("AbstractHandler: Error parsing date/time string: {}", e.getMessage());
            dateTimeType = new DateTimeType();
        } catch (IllegalArgumentException e) {
            logger.debug("AbstractHandler: Error converting to DateTimeType: {}", e.getMessage());
            dateTimeType = new DateTimeType();
        }
        return dateTimeType;
    }

    // Callback used by event listener to update thing status
    public void markOffline(String reason) {
        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, reason);
    }

    // Callback used by event listener to update thing status
    public void markOnline() {
        updateStatus(ThingStatus.ONLINE, ThingStatusDetail.NONE);
    }
}
