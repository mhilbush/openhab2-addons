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
package org.openhab.binding.bhyve.internal.dto.common;

import com.google.gson.annotations.SerializedName;

/**
 * The {@link WateringPlanDTO} is responsible for
 *
 * @author Mark Hilbush - Initial contribution
 */
public class WateringPlanDTO {

    @SerializedName("date")
    public String date;

    @SerializedName("run_times")
    public RunTimeDTO[] runTimes;

    @SerializedName("start_times")
    public String[] startTimes;

    @SerializedName("zone_forecasts")
    public Object[] zoneForecasts;
}
