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

import com.google.gson.annotations.SerializedName;

/**
 * The {@link EventWateringInProgressNotificationDTO} is responsible for
 *
 * @author Mark Hilbush - Initial contribution
 */
public class EventWateringInProgressNotificationDTO {

    @SerializedName("event")
    public String event;

    @SerializedName("program")
    public String program;

    @SerializedName("current_station")
    public Integer currentStation;

    @SerializedName("run_time")
    public Double runTime;

    @SerializedName("started_watering_station_at")
    public String startedWateringStationAt;

    @SerializedName("rain_sensor_hold")
    public Boolean rainSensorHold;

    @SerializedName("device_id")
    public String deviceId;

    @SerializedName("timestamp")
    public String timestamp;
}

// {"event":"watering_in_progress_notification","program":"manual","current_station":1,"run_time":2,
// "started_watering_station_at":"2019-02-26T14:52:42.000Z","rain_sensor_hold":false,"device_id":"5ad72e5a4f0c72d7d6257c5b",
// "timestamp":"2019-02-26T14:52:42.000Z"}
