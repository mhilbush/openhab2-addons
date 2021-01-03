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
package org.openhab.binding.sunsetwx.internal.dto;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * The {@link FeatureContainerDTO} class is used to parse the SunsetWx JSON message returned by the SunsetWx API.
 *
 * @author Mark Hilbush - Initial contribution
 */
public class FeatureContainerDTO {

    @SerializedName("type")
    private String type;

    @SerializedName("features")
    public List<QualityFeatureDTO> features;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
