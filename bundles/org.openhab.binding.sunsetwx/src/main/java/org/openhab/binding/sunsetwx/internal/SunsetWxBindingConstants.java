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

    // Bridge
    public static final ThingTypeUID THING_TYPE_UID_ACCOUNT = new ThingTypeUID(BINDING_ID, "account");
    public static final Set<ThingTypeUID> SUPPORTED_BRIDGE_THING_TYPES_UIDS = Collections
            .unmodifiableSet(Stream.of(THING_TYPE_UID_ACCOUNT).collect(Collectors.toSet()));

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_UID_SUNRISE = new ThingTypeUID(BINDING_ID, "sunrise");
    public static final ThingTypeUID THING_TYPE_UID_SUNSET = new ThingTypeUID(BINDING_ID, "sunset");

    // Collection of SunsetWx thing types
    public static final Set<ThingTypeUID> SUPPORTED_SUNSETWX_THING_TYPES_UIDS = Collections
            .unmodifiableSet(Stream.of(THING_TYPE_UID_SUNRISE, THING_TYPE_UID_SUNSET).collect(Collectors.toSet()));

    // Collection of all supported thing types
    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.unmodifiableSet(
            Stream.concat(SUPPORTED_BRIDGE_THING_TYPES_UIDS.stream(), SUPPORTED_SUNSETWX_THING_TYPES_UIDS.stream())
                    .collect(Collectors.toSet()));

    // List of channel IDs
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

    // List of sunrise/sunset quality terms
    public static final String QUALITY_POOR = "Poor";
    public static final String QUALITY_FAIR = "Fair";
    public static final String QUALITY_GOOD = "Good";
    public static final String QUALITY_GREAT = "Great";
}
