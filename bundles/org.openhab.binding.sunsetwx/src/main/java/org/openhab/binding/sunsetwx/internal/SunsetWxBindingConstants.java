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
package org.openhab.binding.sunsetwx.internal;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link SunsetWxBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class SunsetWxBindingConstants {

    public static final String BINDING_ID = "sunsetwx";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_SUNRISE = new ThingTypeUID(BINDING_ID, "sunrise");
    public static final ThingTypeUID THING_TYPE_SUNSET = new ThingTypeUID(BINDING_ID, "sunset");

    public static final String THING_SUNRISE_LABEL = "Local SunsetWx Sunrise";
    public static final String THING_SUNSET_LABEL = "Local SunsetWx Sunset";

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections
            .unmodifiableSet(Stream.of(THING_TYPE_SUNRISE, THING_TYPE_SUNSET).collect(Collectors.toSet()));

    // Thing configuration items
    public static final String THING_PROPERTY_GEOLOCATION = "geoLocation";
    public static final String THING_PROPERTY_RADIUS = "radius";
    public static final String THING_PROPERTY_LIMIT = "limit";
    public static final String THING_PROPERTY_LOCATION = "location";
    public static final String THING_PROPERTY_UPDATE_FREQUENCY = "updateFrequency";
    public static final String THING_PROPERTY_EMAILADDRESS = "emailAddress";
    public static final String THING_PROPERTY_PASSWORD = "password";

    // Configuration defaults
    public static final String DEFAULT_TYPE = "sunset";
    public static final String DEFAULT_COORDINATES = "-77.8600012,40.7933949";
    public static final int DEFAULT_LIMIT = 8;
    public static final double DEFAULT_RADIUS = 20.0;
    public static final String DEFAULT_LOCATION = "northamerica";
    public static final int DEFAULT_UPDATE_FREQUENCY = 15;

    // List of channel IDs
    public static final String CHANNEL_GET_QUALITY = "getQuality";
    public static final String CHANNEL_QUALITY = "quality";
    public static final String CHANNEL_QUALITY_PERCENT = "qualityPercent";
    public static final String CHANNEL_QUALITY_VALUE = "qualityValue";
    public static final String CHANNEL_LAST_UPDATED = "lastUpdated";
    public static final String CHANNEL_IMPORTED_AT = "importedAt";
    public static final String CHANNEL_LOCALE = "locale";
    public static final String CHANNEL_REGION = "region";
    public static final String CHANNEL_COUNTRY = "country";
    public static final String CHANNEL_SOURCE = "source";
    public static final String CHANNEL_VALID_AT = "validAt";
    public static final String CHANNEL_RAW_RESPONSE = "rawResponse";
    public static final String CHANNEL_LAST_REPORT_TIME = "lastReportTime";

    // Models supported by the SunsetWx service
    public enum ModelType {
        SUNRISE,
        SUNSET
    }

    // List of sunrise/sunset quality terms
    public static final String QUALITY_POOR = "Poor";
    public static final String QUALITY_FAIR = "Fair";
    public static final String QUALITY_GOOD = "Good";
    public static final String QUALITY_GREAT = "Great";
}
