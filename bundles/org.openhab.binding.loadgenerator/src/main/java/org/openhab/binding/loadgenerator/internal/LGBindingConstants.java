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
package org.openhab.binding.loadgenerator.internal;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link LGBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class LGBindingConstants {

    private static final String BINDING_ID = "loadgenerator";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_ADMIN = new ThingTypeUID(BINDING_ID, "admin");
    public static final ThingTypeUID THING_TYPE_STATE_UPDATER = new ThingTypeUID(BINDING_ID, "stateUpdater");
    public static final ThingTypeUID THING_TYPE_COMMAND_GENERATOR = new ThingTypeUID(BINDING_ID, "commandGenerator");
    public static final ThingTypeUID THING_TYPE_THING_UPDATER = new ThingTypeUID(BINDING_ID, "thingUpdater");
    public static final ThingTypeUID THING_TYPE_THING_CREATER = new ThingTypeUID(BINDING_ID, "thingCreater");

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections
            .unmodifiableSet(Stream.of(THING_TYPE_ADMIN, THING_TYPE_STATE_UPDATER, THING_TYPE_COMMAND_GENERATOR,
                    THING_TYPE_THING_UPDATER, THING_TYPE_THING_CREATER).collect(Collectors.toSet()));

    // List of all Channel ids
    public static final String CHANNEL_RUN = "run";

    public static final String CHANNEL_STATE = "state";
    public static final String CHANNEL_NUM_STATE_UPDATES = "numStateUpdates";

    public static final String CHANNEL_COMMAND = "command";
    public static final String CHANNEL_NUM_COMMANDS_SENT = "numCommandsSent";
    public static final String CHANNEL_NUM_COMMANDS_PROCESSED = "numCommandsProcessed";

    public static final String CHANNEL_RUNTIME = "runtime";

    public static final String CHANNEL_CREATE_THINGS = "createThings";
    public static final String CHANNEL_REMOVE_THINGS = "removeThings";
}
