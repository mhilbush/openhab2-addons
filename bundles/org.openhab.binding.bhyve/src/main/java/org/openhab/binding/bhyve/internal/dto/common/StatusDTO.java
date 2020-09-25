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
package org.openhab.binding.bhyve.internal.dto.common;

import com.google.gson.annotations.SerializedName;

/**
 * The {@link StatusDTO} is responsible for
 *
 * @author Mark Hilbush - Initial contribution
 */
public class StatusDTO {

    @SerializedName("run_mode")
    public String runMode;

    @SerializedName("next_start_programs")
    public Object[] nextStartPrograms;

    @SerializedName("next_start_time")
    public String nextStartTime;

    @SerializedName("watering_status")
    public WateringStatusDTO wateringStatus;

    @SerializedName("rain_delay_suggested_at")
    public String rainDelaySuggestedAt;

    @SerializedName("rain_delay")
    public Integer rainDelay;

    @SerializedName("rain_delay_cause")
    public String rainDelayCause;

    @SerializedName("rain_delay_weather_type")
    public String rainDelayWeatherType;

    @SerializedName("rain_delay_started_at")
    public String rainDelayStartedAt;

    @SerializedName("rain_delay_overridden_at")
    public String rainDelayOverriddenAt;

    @SerializedName("flow_sensor")
    public Object flowSensor;

    @SerializedName("rain_sensor_hold")
    public Boolean rainSensorHold;
}
