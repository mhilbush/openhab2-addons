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

import org.openhab.binding.bhyve.internal.dto.common.StationDTO;

import com.google.gson.annotations.SerializedName;

/**
 * The {@link EventChangeModeDTO} is responsible for
 *
 * @author Mark Hilbush - Initial contribution
 */
public class EventChangeModeDTO {

    @SerializedName("event")
    public String event;

    @SerializedName("mode")
    public String mode;

    @SerializedName("program")
    public Object program;

    @SerializedName("stations")
    public StationDTO[] stations;

    @SerializedName("device_id")
    public String deviceId;

    @SerializedName("timestamp")
    public String timestamp;
}

// {"event":"change_mode","mode":"manual","program":null,"stations":[{"station":1,"run_time":5.0}],"device_id":"5ad72e5a4f0c72d7d6257c5b","timestamp":"2019-02-18T16:21:52.000Z"}
// {"event":"change_mode","mode":"auto","device_id":"5ad72e5a4f0c72d7d6257c5b","timestamp":"2019-02-18T17:07:01.000Z"}
// {"event":"change_mode","mode":"off","device_id":"5ad72e5a4f0c72d7d6257c5b","timestamp":"2019-02-19T15:16:25.000Z"}
