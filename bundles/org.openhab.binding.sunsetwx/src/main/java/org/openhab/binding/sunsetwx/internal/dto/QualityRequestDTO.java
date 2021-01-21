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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.sunsetwx.internal.model.ModelType;

/**
 * The {@link QualityRequestDTO} class is used to hold the information needed for a SunsetWx
 * quality request.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class QualityRequestDTO {

    private ModelType modelType = ModelType.SUNSET;

    private String coordinates = "";

    private Integer limit = 0;

    private Integer radius = 0;

    private String location = "northamerica";

    public ModelType getModelType() {
        return modelType;
    }

    public void setModelType(ModelType type) {
        this.modelType = type;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
