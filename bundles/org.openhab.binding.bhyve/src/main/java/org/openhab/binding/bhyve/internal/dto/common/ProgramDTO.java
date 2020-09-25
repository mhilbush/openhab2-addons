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
 * The {@link ProgramDTO} is responsible for
 *
 * @author Mark Hilbush - Initial contribution
 */
public class ProgramDTO {

    @SerializedName("budget")
    public Integer budget;

    @SerializedName("created_at")
    public String createdAt;

    @SerializedName("device_id")
    public String deviceId;

    @SerializedName("enabled")
    public Boolean enabled;

    @SerializedName("frequency")
    public FrequencyDTO frequency;

    @SerializedName("is_smart_program")
    public Boolean isSmartProgram;

    @SerializedName("lock_at")
    public String lockAt;

    @SerializedName("long_term_program")
    public Object longTermProgram;

    @SerializedName("name")
    public String name;

    @SerializedName("pending_ack_timer")
    public Boolean pendingAckTimer;

    @SerializedName("process_at")
    public String processAt;

    @SerializedName("program")
    public String program;

    @SerializedName("run_times")
    public RunTimeDTO[] runTimes;

    @SerializedName("start_times")
    public String[] startTimes;

    @SerializedName("updated_at")
    public String updatedAt;

    @SerializedName("watering_plan")
    public WateringPlanDTO[] wateringPlan;

    @SerializedName("timestamp")
    public String timestamp;
}
