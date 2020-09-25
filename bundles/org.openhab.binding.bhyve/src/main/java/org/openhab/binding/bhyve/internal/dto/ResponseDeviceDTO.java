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
package org.openhab.binding.bhyve.internal.dto;

import org.openhab.binding.bhyve.internal.dto.common.BatteryStatusDTO;
import org.openhab.binding.bhyve.internal.dto.common.LocationDTO;
import org.openhab.binding.bhyve.internal.dto.common.StatusDTO;

import com.google.gson.annotations.SerializedName;

/**
 * The {@link ResponseDeviceDTO} is responsible for
 *
 * @author Mark Hilbush - Initial contribution
 */
public class ResponseDeviceDTO {
    /*
     * Types:
     * - bridge
     * - sprinkler_timer
     */
    @SerializedName("type")
    public String type;

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("location")
    public LocationDTO location;

    @SerializedName("num_stations")
    public String numStations;

    // Only if device is battery-powered; otherwise null
    @SerializedName("battery")
    public BatteryStatusDTO battery;

    @SerializedName("status")
    public StatusDTO status;

    @SerializedName("is_connected")
    public Boolean isConnected;

    @SerializedName("last_connected_at")
    public String lastConnectedAt;

    @SerializedName("notified_disconnected_at")
    public String notifiedDisconnectedAt;

    @SerializedName("user_id")
    public String userId;

    @SerializedName("device_gateway_topic")
    public String deviceGatewayTopic;

    @SerializedName("mac_address")
    public String macAddress;

    @SerializedName("hardware_version")
    public String hardwareVersion;

    @SerializedName("firmware_version")
    public String firmwareVersion;

    @SerializedName("wifi_version")
    public String wifiVersion;

    @SerializedName("reference")
    public String reference;

    @SerializedName("mesh_id")
    public String meshId;

    @SerializedName("created_at")
    public String createdAt;

    @SerializedName("updated_at")
    public String updatedAt;

    // Format "HH:MM"
    @SerializedName("suggested_start_time")
    public String suggestedStartTime;
}
