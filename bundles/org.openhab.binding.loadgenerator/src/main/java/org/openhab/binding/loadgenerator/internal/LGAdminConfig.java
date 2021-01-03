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
package org.openhab.binding.loadgenerator.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * The {@link LGAdminConfig} class contains fields mapping thing configuration parameters.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class LGAdminConfig {
    // State updater parameters
    public @Nullable Integer numStateUpdaterThings;
    public @Nullable Integer stateUpdaterDelayBetweenUpdates;
    public @Nullable Integer stateUpdaterNumberOfChannels;

    // Command generator parameters
    public @Nullable Integer numCommandGeneratorThings;

    // Thing updater parameters
    public @Nullable Integer numThingUpdaterThings;
    public @Nullable Integer thingUpdaterDelayBetweenUpdates;
}
