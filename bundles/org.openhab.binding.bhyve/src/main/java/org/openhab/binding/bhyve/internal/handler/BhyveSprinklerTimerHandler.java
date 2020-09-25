/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
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

import static org.openhab.binding.bhyve.internal.BhyveBindingConstants.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.openhab.binding.bhyve.internal.config.BhyveSprinklerTimerConfig;
import org.openhab.binding.bhyve.internal.dto.EventChangeModeDTO;
import org.openhab.binding.bhyve.internal.dto.EventLowBatteryDTO;
import org.openhab.binding.bhyve.internal.dto.EventProgramChangedDTO;
import org.openhab.binding.bhyve.internal.dto.EventRainDelayDTO;
import org.openhab.binding.bhyve.internal.dto.EventWateringInProgressNotificationDTO;
import org.openhab.binding.bhyve.internal.dto.ResponseDeviceDTO;
import org.openhab.binding.bhyve.internal.dto.ResponseSprinklerTimerProgramsDTO;
import org.openhab.binding.bhyve.internal.dto.common.WateringPlanDTO;
import org.openhab.binding.bhyve.internal.dto.request.EventChangeModeRequestDTO;
import org.openhab.binding.bhyve.internal.dto.request.EventRainDelayRequestDTO;
import org.openhab.core.i18n.TimeZoneProvider;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.openhab.core.types.UnDefType;

/**
 * The {@link BhyveSprinklerTimerHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class BhyveSprinklerTimerHandler extends BhyveAbstractThingHandler {
    private @NonNullByDefault({}) BhyveSprinklerTimerConfig config;

    private int runTimeStation1;

    public BhyveSprinklerTimerHandler(Thing thing, WebSocketClient webSocketClient, TimeZoneProvider timeZoneProvider)
            throws URISyntaxException {
        super(thing, webSocketClient, timeZoneProvider);
    }

    @Override
    public void initialize() {
        super.initialize();
        config = getConfigAs(BhyveSprinklerTimerConfig.class);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            return;
        }
        switch (channelUID.getId()) {
            case CHANNEL_RAIN_DELAY_DURATION:
                if (command instanceof DecimalType) {
                    requestRainDelay(((DecimalType) command).intValue());
                }
                break;
            case CHANNEL_MODE:
                if (command instanceof StringType) {
                    requestChangeMode(command);
                }
                break;
            case CHANNEL_RUN_TIME_STATION1:
                if (command instanceof DecimalType) {
                    runTimeStation1 = ((DecimalType) command).intValue();
                    updateChannel(CHANNEL_RUN_TIME_STATION1, new DecimalType(runTimeStation1));
                    logger.debug("SprinklerTimerHandler: Set station 1 runtime to {} minutes", runTimeStation1);
                }
                break;
        }
    }

    @Override
    public @Nullable String getDeviceId() {
        return config.deviceId;
    }

    /*
     * Requests sent to the online service
     */
    private void requestRainDelay(int numHours) {
        logger.debug("SprinklerTimerHandler: Set rain delay of {}", numHours);
        EventRainDelayRequestDTO rainDelay = new EventRainDelayRequestDTO();
        rainDelay.event = "rain_delay";
        rainDelay.delay = numHours;
        rainDelay.deviceId = getDeviceId();
        String message = GSON.toJson(rainDelay);
        try {
            listener.sendMessage(message);
        } catch (IOException e) {
            logger.debug("SprinklerTimerHandler: IOException sending message: {}", e.getMessage());
        }
    }

    private void requestChangeMode(Command command) {
        logger.debug("SprinklerTimerHandler: Set mode to {}", command.toString());
        String mode = command.toString().toLowerCase();
        if (isValidMode(mode)) {
            EventChangeModeRequestDTO changeMode = new EventChangeModeRequestDTO();
            changeMode.event = "change_mode";
            changeMode.mode = mode;
            changeMode.deviceId = getDeviceId();
            changeMode.stations = null;
            if (MODE_MANUAL.equals(mode)) {
                changeMode.addStationAndRunTime(1, runTimeStation1);
            }
            String message = GSON.toJson(changeMode);
            try {
                listener.sendMessage(message);
            } catch (IOException e) {
                logger.debug("SprinklerTimerHandler: IOException sending message: {}", e.getMessage());
            }
        }
    }

    private boolean isValidMode(String mode) {
        return MODE_AUTO.equals(mode) || MODE_MANUAL.equals(mode) || MODE_OFF.equals(mode);
    }

    @Override
    public void handleDeviceUpdate(ResponseDeviceDTO device) {
        logger.debug("SprinklerTimerHandler: Handle DEVICE update for {} with id {}", device.name, device.id);
        if (device.status != null) {
            updateChannel(CHANNEL_MODE, new StringType(device.status.runMode));

            // Watering status
            updateChannel(CHANNEL_SUGGESTED_START_TIME, new StringType(device.suggestedStartTime));
            if (device.status.wateringStatus != null) {
                logger.debug("SprinklerTimerHandler: Setting watering status ON");
                updateChannel(CHANNEL_STARTED_WATERING_AT,
                        getOffsetDateTimeType(device.status.wateringStatus.startedWateringStationAt));
                updateChannel(CHANNEL_WATERING_STATUS, OnOffType.ON);
            } else {
                logger.debug("SprinklerTimerHandler: Setting watering status OFF");
                updateChannel(CHANNEL_STARTED_WATERING_AT, UnDefType.UNDEF);
                updateChannel(CHANNEL_WATERING_STATUS, OnOffType.OFF);
            }

            // Rain delay info
            updateChannel(CHANNEL_RAIN_DELAY_DURATION, new DecimalType(device.status.rainDelay));
            if (device.status.rainDelay == 0) {
                logger.debug("SprinklerTimerHandler: Setting rain delay OFF");
                updateChannel(CHANNEL_RAIN_DELAY_STATUS, OnOffType.OFF);
                updateChannel(CHANNEL_RAIN_DELAY_WEATHER_TYPE, UnDefType.UNDEF);
                updateChannel(CHANNEL_RAIN_DELAY_STARTED_AT, UnDefType.UNDEF);
                updateChannel(CHANNEL_RAIN_DELAY_END_AT, UnDefType.UNDEF);
            } else {
                updateChannel(CHANNEL_RAIN_DELAY_STATUS, OnOffType.ON);
                updateChannel(CHANNEL_RAIN_DELAY_WEATHER_TYPE, new StringType(device.status.rainDelayWeatherType));
                updateChannel(CHANNEL_RAIN_DELAY_STARTED_AT,
                        getLocalDateTimeType(device.status.rainDelayStartedAt, getZoneId()));
                long endTime = System.currentTimeMillis() + (device.status.rainDelay * 60 * 60 * 1000);
                updateChannel(CHANNEL_RAIN_DELAY_END_AT, getLocalDateTimeType(endTime, getZoneId()));
            }
        }
        if (device.battery != null) {
            updateChannel(CHANNEL_BATTERY_LEVEL, new DecimalType(device.battery.percent));
        }
        updateChannel(CHANNEL_NUMBER_OF_STATIONS, new DecimalType(device.numStations));
        updateChannel(CHANNEL_LAST_CONNECTED_AT, getLocalDateTimeType(device.lastConnectedAt, getZoneId()));
        updateChannel(CHANNEL_IS_CONNECTED, device.isConnected ? OnOffType.ON : OnOffType.OFF);
    }

    public void handleProgramUpdate(ResponseSprinklerTimerProgramsDTO program) {
        logger.debug("SprinklerTimerHandler: Handle PROGRAM update for: {}", program.name);
        updateNextWateringDate(program.wateringPlan);
    }

    private void updateNextWateringDate(WateringPlanDTO[] plans) {
        ZonedDateTime nextWateringAt = null;
        for (WateringPlanDTO plan : plans) {
            logger.debug("SprinklerTimerHandler: Processing watering plan for {}", plan.date);
            if (plan.runTimes.length != 0) {
                ZonedDateTime zdt = getZonedDateTime(plan.date, getZoneId());
                if (zdt == null) {
                    logger.debug("SprinklerTimerHandler: Watering plan contains bad date: {}", plan.date);
                    continue;
                }
                for (int i = 0; i < plan.runTimes.length; i++) {
                    logger.debug("SprinklerTimerHandler: Station={}, StartTime={}, RunTime={}",
                            plan.runTimes[i].station, plan.startTimes[i], plan.runTimes[i].runTime);
                    Integer startTimeMinutes = parseTimeToMinutes(plan.startTimes[i]);
                    ZonedDateTime nextTime = zdt.plusMinutes(startTimeMinutes != null ? startTimeMinutes : 0);
                    logger.debug("SprinklerTimerHandler: Next watering time is {}", nextTime.toString());
                    if (nextTime.isAfter(ZonedDateTime.now())) {
                        // If start time after current time, continue
                        if (nextWateringAt == null) {
                            nextWateringAt = nextTime;
                        }
                    }
                }
            }
        }
        if (nextWateringAt != null) {
            logger.debug("SprinklerTimerHandler: Update NextWateringAt channel to {}", nextWateringAt);
            updateChannel(CHANNEL_NEXT_WATERING_AT, new DateTimeType(nextWateringAt));
        } else {
            logger.debug("SprinklerTimerHandler: Update NextWateringAt channel to UNDEF");
            updateChannel(CHANNEL_NEXT_WATERING_AT, UnDefType.UNDEF);
        }
    }

    private @Nullable Integer parseTimeToMinutes(String hourFormat) {
        int minutes = 0;
        String[] split = hourFormat.split(":");
        try {
            minutes += Integer.parseInt(split[0]) * 60;
            minutes += Integer.parseInt(split[1]);
            return minutes;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Top level handler for events received from websocket listener
     */
    @Override
    public void handleEvent(String eventId, String message) {
        switch (eventId) {
            case "change_mode":
                handleChangeModeEvent(message);
                break;
            case "watering_in_progress_notification":
                handleWateringInProgressNotificationEvent(message);
                break;
            case "watering_complete":
                handleWateringCompleteEvent(message);
                break;
            case "low_battery":
                handleLowBatteryEvent(message);
                break;
            case "clear_low_battery":
                handleClearLowBatteryEvent(message);
                break;
            case "device_idle":
                handleDeviceIdleEvent(message);
                break;
            case "rain_delay":
                handleRainDelayEvent(message);
                break;
            case "flow_sensor_state_changed":
                handleFlowSensorStateChangedEvent(message);
                break;
            case "program_changed":
                handleProgramChangedEvent(message);
                break;
            case "connected":
                handleConnectedEvent(message);
                break;
            case "disconnected":
                handleDisconnectedEvent(message);
                break;
            default:
                logger.warn("SprinklerTimerHandler: Received unknown event '{}': {}", eventId, message);
                break;
        }
    }

    /*
     * Handlers for events received from the websocket listener
     */
    @SuppressWarnings("null")
    private void handleChangeModeEvent(String message) {
        logger.debug("SprinklerTimerHandler: Handling EVENT 'change_mode': {}", message);
        EventChangeModeDTO changeMode = GSON.fromJson(message, EventChangeModeDTO.class);
        if (changeMode != null) {
            logger.debug("SprinklerTimerHandler: Mode is: {}", changeMode.mode);
            updateChannel(CHANNEL_MODE, new StringType(changeMode.mode));
        }
    }

    @SuppressWarnings("null")
    private void handleWateringInProgressNotificationEvent(String message) {
        logger.debug("SprinklerTimerHandler: Handling EVENT 'watering_in_progress_notification': {}", message);
        EventWateringInProgressNotificationDTO watering = GSON.fromJson(message,
                EventWateringInProgressNotificationDTO.class);
        if (watering != null) {
            logger.debug("SprinklerTimerHandler: Watering status changed to ON, water for {} minutes",
                    watering.runTime);
            updateChannel(CHANNEL_WATERING_STATUS, OnOffType.ON);
            updateChannel(CHANNEL_STARTED_WATERING_AT,
                    getLocalDateTimeType(watering.startedWateringStationAt, getZoneId()));
            updateChannel(CHANNEL_RUN_TIME_STATION1, new DecimalType(Math.round(watering.runTime)));
        }
    }

    private void handleWateringCompleteEvent(String message) {
        logger.debug("SprinklerTimerHandler: Handling EVENT 'watering_complete': {}", message);
        logger.debug("SprinklerTimerHandler: Watering status changed to OFF");
        updateChannel(CHANNEL_WATERING_STATUS, OnOffType.OFF);
        updateChannel(CHANNEL_STARTED_WATERING_AT, UnDefType.UNDEF);
    }

    @SuppressWarnings("null")
    private void handleLowBatteryEvent(String message) {
        logger.debug("SprinklerTimerHandler: Handling EVENT 'low_battery': {}", message);
        EventLowBatteryDTO lowBattery = GSON.fromJson(message, EventLowBatteryDTO.class);
        logger.debug("SprinklerTimerHandler: Set low battery alarm, level={}", lowBattery.percentRemaining);
        updateChannel(CHANNEL_LOW_BATTERY_ALARM, OnOffType.ON);
        if (lowBattery != null) {
            updateChannel(CHANNEL_BATTERY_LEVEL, new DecimalType(lowBattery.percentRemaining));
        }
    }

    private void handleClearLowBatteryEvent(String message) {
        logger.debug("SprinklerTimerHandler: Handling EVENT 'clear_low_battery': {}", message);
        logger.debug("SprinklerTimerHandler: Clear low battery alarm");
        updateChannel(CHANNEL_LOW_BATTERY_ALARM, OnOffType.OFF);
    }

    private void handleDeviceIdleEvent(String message) {
        logger.debug("SprinklerTimerHandler: Handling EVENT 'device_idle': {}", message);
        logger.debug("SprinklerTimerHandler: Watering status changed to OFF");
        updateChannel(CHANNEL_WATERING_STATUS, OnOffType.OFF);
    }

    @SuppressWarnings("null")
    private void handleRainDelayEvent(String message) {
        logger.debug("SprinklerTimerHandler: Handling EVENT 'rain_delay': {}", message);
        EventRainDelayDTO rainDelay = GSON.fromJson(message, EventRainDelayDTO.class);
        if (rainDelay != null) {
            updateChannel(CHANNEL_RAIN_DELAY_DURATION, new DecimalType(rainDelay.delay));
            if (rainDelay.delay == 0) {
                logger.debug("SprinklerTimerHandler: Clear rain delay");
                updateChannel(CHANNEL_RAIN_DELAY_STATUS, OnOffType.OFF);
                updateChannel(CHANNEL_RAIN_DELAY_END_AT, UnDefType.UNDEF);
            } else {
                logger.debug("SprinklerTimerHandler: Set rain delay for duration {} hours", rainDelay.delay);
                updateChannel(CHANNEL_RAIN_DELAY_STATUS, OnOffType.ON);
                long endTime = System.currentTimeMillis() + (rainDelay.delay * 60 * 60 * 1000);
                updateChannel(CHANNEL_RAIN_DELAY_END_AT, getLocalDateTimeType(endTime, getZoneId()));
            }
        }
    }

    private void handleFlowSensorStateChangedEvent(String message) {
        logger.debug("SprinklerTimerHandler: Handling EVENT 'flow_sensor_state_changed': {}", message);
        // {"timestamp":"2019-02-18T18:30:35.000Z","event":"flow_sensor_state_changed","cycle_run_time_sec":46,"flow_rate_gpm":0.38402,"stream-id":"fa6a7bd1-e396-478a-b73d-39bdf0413364","client-topics":["device-clients-1"],"gateway-topic":"devices-2","device_id":"5ad72e5a4f0c72d7d6257c5b"}
    }

    @SuppressWarnings("null")
    private void handleProgramChangedEvent(String message) {
        logger.debug("SprinklerTimerHandler: Handling EVENT 'program_changed': {}", message);
        EventProgramChangedDTO programChanged = GSON.fromJson(message, EventProgramChangedDTO.class);
        if (programChanged != null && programChanged.program != null && programChanged.program.enabled) {
            updateNextWateringDate(programChanged.program.wateringPlan);
        }
    }

    private void handleConnectedEvent(String message) {
        logger.debug("SprinklerTimerHandler: Handling EVENT 'connect': {}", message);
    }

    private void handleDisconnectedEvent(String message) {
        logger.debug("SprinklerTimerHandler: Handling EVENT 'disconnect': {}", message);
    }
}
