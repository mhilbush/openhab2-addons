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
package org.openhab.binding.zm.internal.config;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * The {@link ZmBridgeConfig} class contains fields mapping thing configuration parameters.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class ZmBridgeConfig {

    /**
     * Host name or IP address of Zoneminder server
     */
    public String host = "";

    /**
     * Use http or https
     */
    public Boolean useSSL = Boolean.FALSE;

    /**
     * Port number
     */
    public @Nullable Integer portNumber;

    /**
     * Use default URL path
     */
    public Boolean useDefaultUrlPath = Boolean.TRUE;

    /**
     * URL fragment (e.g. /zm)
     */
    public @Nullable String urlPath;

    /**
     * Frequency at which monitor status will be updated
     */
    public @Nullable Integer refreshInterval;

    /**
     * Enable/disable monitor discovery
     */
    public @Nullable Boolean discoveryEnabled;

    /**
     * Frequency at which the binding will try to discover monitors
     */
    public @Nullable Integer discoveryInterval;

    /**
     * Alarm duration set on monitor things when they're discovered
     */
    public @Nullable Integer defaultAlarmDuration;

    /**
     * Default image refresh interval set on monitor things when they're discovered
     */
    public @Nullable Integer defaultImageRefreshInterval;

    /**
     * Zoneminder user name
     */
    public @Nullable String user;

    /**
     * Zoneminder password
     */
    public @Nullable String pass;
}
