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

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link SharpTVConfig} is responsible for storing the SharpTV thing configuration.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class SharpTVConfig {
    private String ipAddress = "";
    private int port;
    private String user = "";
    private String password = "";
    private boolean discoveryEnabled = true;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getDiscoveryEnabled() {
        return discoveryEnabled;
    }

    public void setDiscoveryEnabled(boolean discoveryEnabled) {
        this.discoveryEnabled = discoveryEnabled;
    }

    public boolean isValid() {
        if (ipAddress.isBlank()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SharpTVConfig{ ipAddress=" + ipAddress + ", port=" + port + ", user=" + user + ", password=XXXX"
                + ", discoveryEnabled=" + discoveryEnabled + " }";
    }
}
