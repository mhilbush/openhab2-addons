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
package org.openhab.binding.bhyve.internal.dto;

import org.openhab.binding.bhyve.internal.dto.common.ProgramDTO;

import com.google.gson.annotations.SerializedName;

/**
 * The {@link EventProgramChangedDTO} is responsible for
 *
 * @author Mark Hilbush - Initial contribution
 */
public class EventProgramChangedDTO {

    @SerializedName("event")
    public String event;

    @SerializedName("lifecycle_phase")
    public String lifecyclePhase;

    @SerializedName("program")
    public ProgramDTO program;

    @SerializedName("timestamp")
    public String timestamp;
}
