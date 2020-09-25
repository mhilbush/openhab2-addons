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
package org.openhab.binding.sharptv.internal;

import java.util.Collections;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link SharpTVBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class SharpTVBindingConstants {
    public static final String BINDING_ID = "sharptv";

    public static final ThingTypeUID THING_TYPE_SHARP_TV = new ThingTypeUID(BINDING_ID, "sharptv");

    // List of all Thing Type UIDs
    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(THING_TYPE_SHARP_TV);

    // List of all Channel ids
    public static final String CHANNEL_POWER = "power";
    public static final String CHANNEL_VOLUME = "volume";
    public static final String CHANNEL_MUTE = "mute";
    public static final String CHANNEL_CHANNEL = "channel";
    public static final String CHANNEL_CHANNEL_DIGITAL_AIR = "channel-digital-air";
    public static final String CHANNEL_CHANNEL_DIGITAL_CABLE = "channel-digital-cable";
    public static final String CHANNEL_CHANNEL_3_DIGIT_DIRECT = "channel-3-digit-direct";
    public static final String CHANNEL_CHANNEL_4_DIGIT_DIRECT = "channel-4-digit-direct";
    public static final String CHANNEL_INPUT = "input";
    public static final String CHANNEL_AVMODE = "av-mode";
    public static final String CHANNEL_SLEEP_TIMER = "sleep-timer";
    public static final String CHANNEL_DISABLE_ECO = "disable-eco";
}
