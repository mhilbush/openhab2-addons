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
package org.openhab.binding.bhyve.internal;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link BhyveBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class BhyveBindingConstants {

    private static final String BINDING_ID = "bhyve";

    // Bridge
    public static final String THING_TYPE_BRIDGE = "bridge";
    public static final ThingTypeUID UID_BRIDGE = new ThingTypeUID(BINDING_ID, THING_TYPE_BRIDGE);
    public static final Set<ThingTypeUID> SUPPORTED_BRIDGE_THING_TYPES_UIDS = Collections
            .unmodifiableSet(Stream.of(UID_BRIDGE).collect(Collectors.toSet()));

    // Hub
    public static final String THING_TYPE_HUB = "hub";
    public static final ThingTypeUID UID_HUB = new ThingTypeUID(BINDING_ID, THING_TYPE_HUB);

    // Sprinkler Timer
    public static final String THING_TYPE_SPRINKLER_TIMER = "sprinkler-timer";
    public static final ThingTypeUID UID_SPRINKLER_TIMER = new ThingTypeUID(BINDING_ID, THING_TYPE_SPRINKLER_TIMER);

    // Collection of supported non-bridge thing types
    public static final Set<ThingTypeUID> SUPPORTED_STATION_THING_TYPES_UIDS = Collections
            .unmodifiableSet(Stream.of(UID_SPRINKLER_TIMER, UID_HUB).collect(Collectors.toSet()));

    // Collection of all supported thing types
    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.unmodifiableSet(
            Stream.concat(SUPPORTED_BRIDGE_THING_TYPES_UIDS.stream(), SUPPORTED_STATION_THING_TYPES_UIDS.stream())
                    .collect(Collectors.toSet()));

    // Configuration parameters
    public static final String CONFIG_DEVICE_ID = "deviceId";

    // List of all Channel Ids
    public static final String CHANNEL_MODE = "mode";
    public static final String CHANNEL_WATERING_STATUS = "wateringStatus";
    public static final String CHANNEL_STARTED_WATERING_AT = "startedWateringAt";
    public static final String CHANNEL_NEXT_WATERING_AT = "nextWateringAt";
    public static final String CHANNEL_SUGGESTED_START_TIME = "suggestedStartTime";
    public static final String CHANNEL_RAIN_DELAY_STATUS = "rainDelayStatus";
    public static final String CHANNEL_RAIN_DELAY_WEATHER_TYPE = "rainDelayWeatherType";
    public static final String CHANNEL_RAIN_DELAY_DURATION = "rainDelayDuration";
    public static final String CHANNEL_RAIN_DELAY_STARTED_AT = "rainDelayStartedAt";
    public static final String CHANNEL_RAIN_DELAY_END_AT = "rainDelayEndAt";
    public static final String CHANNEL_BATTERY_LEVEL = "batteryLevel";
    public static final String CHANNEL_LOW_BATTERY_ALARM = "lowBatteryAlarm";
    public static final String CHANNEL_LAST_CONNECTED_AT = "lastConnectedAt";
    public static final String CHANNEL_IS_CONNECTED = "isConnected";
    public static final String CHANNEL_NUMBER_OF_STATIONS = "numberOfStations";
    public static final String CHANNEL_RUN_TIME_STATION1 = "runTimeStation1";

    // Types of Bhyve devices
    public static final String BHYVE_TYPE_BRIDGE = "bridge";
    public static final String BHYVE_TYPE_SPRINKLER_TIMER = "sprinkler_timer";

    // Mode types
    public static final String MODE_AUTO = "auto";
    public static final String MODE_MANUAL = "manual";
    public static final String MODE_OFF = "off";

    // URL to log in to service
    public static final String URL_LOGIN = "https://api.orbitbhyve.com/v1/session";

    // URL to get information for all installed devices
    public static final String URL_DEVICES = "https://api.orbitbhyve.com/v1/devices?user_id=";

    // URL to get the program information for a Sprinkler Timer
    public static final String URL_PROGRAMS = "https://api.orbitbhyve.com/v1/sprinkler_timer_programs?device_id=";

    // URL to get websocket event stream
    public static final String URL_EVENTS = "wss://api.orbitbhyve.com/v1/events";
}
