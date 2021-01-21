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
package org.openhab.binding.sunsetwx.internal.config;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link SunsetWxConfig} contains configuration for sunrise and sunset things.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class SunsetWxConfig {

    public String geolocation = "-77.8600012,40.7933949";

    /**
     * 1 to 100 kilometers
     */
    public Integer radius = 50;

    /**
     * 1 to 250
     */
    public Integer limit = 40;

    /**
     * <option value="northamerica">North America</option>
     * <option value="global">Global</option>
     */
    public String location = "northamerica";

    /**
     * 2 to 360
     */
    public Integer refreshIntervalMinutes = 20;
}
