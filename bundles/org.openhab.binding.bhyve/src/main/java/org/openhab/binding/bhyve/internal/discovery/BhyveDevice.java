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
package org.openhab.binding.bhyve.internal.discovery;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link BhyveDevice}
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class BhyveDevice {

    private String type;
    private String macAddress;
    private String name;
    private String deviceId;

    public BhyveDevice(String type, String deviceId, String name, String macAddress) {
        this.type = type;
        this.deviceId = deviceId;
        this.name = name;
        this.macAddress = macAddress;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
