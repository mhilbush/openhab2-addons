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
package org.openhab.binding.bhyve.internal.dto.request;

import org.openhab.binding.bhyve.internal.dto.common.StationDTO;

import com.google.gson.annotations.SerializedName;

/**
 * The {@link EventChangeModeRequestDTO} is responsible for
 *
 * @author Mark Hilbush - Initial contribution
 */
public class EventChangeModeRequestDTO {

    @SerializedName("event")
    public String event;

    @SerializedName("device_id")
    public String deviceId;

    @SerializedName("mode")
    public String mode;

    @SerializedName("stations")
    public StationDTO[] stations;

    @SerializedName("program")
    public String program;

    public void addStationAndRunTime(int station, int runTime) {
        stations = new StationDTO[1];
        stations[0] = new StationDTO();
        stations[0].station = station;
        stations[0].runTime = runTime;
    }
}
