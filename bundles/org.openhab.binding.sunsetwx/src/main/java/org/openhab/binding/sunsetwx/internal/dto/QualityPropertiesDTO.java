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

import java.util.Date;

import com.google.gson.annotations.SerializedName;

/**
 * The {@link QualityFeatureDTO} class is used to parse the SunsetWx JSON message returned by the SunsetWx API.
 *
 * @author Mark Hilbush - Initial contribution
 */
public class QualityPropertiesDTO {

    @SerializedName("type")
    private String type;

    @SerializedName("quality")
    private String quality;

    @SerializedName("quality_percent")
    private double qualityPercent;

    @SerializedName("quality_value")
    private double qualityValue;

    @SerializedName("real_humidity")
    private double realHumidity;

    @SerializedName("high_clouds")
    private double highClouds;

    @SerializedName("vertical_vel")
    private double verticalVelocities;

    @SerializedName("pressure_tend")
    private double pressureTendency;

    @SerializedName("last_updated")
    private Date lastUpdated;

    @SerializedName("imported_at")
    private Date importedAt;

    @SerializedName("valid_at")
    private Date validAt;

    @SerializedName("source")
    private String source;

    @SerializedName("distance")
    private double distance;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public double getQualityPercent() {
        return qualityPercent;
    }

    public void setQualityPercent(double quality_percent) {
        this.qualityPercent = quality_percent;
    }

    public double getQualityValue() {
        return qualityValue;
    }

    public void setQualityValue(double quality_value) {
        this.qualityValue = quality_value;
    }

    public double getRealHumidity() {
        return realHumidity;
    }

    public void setRealHumidity(double real_humidity) {
        this.realHumidity = real_humidity;
    }

    public double getHighClouds() {
        return highClouds;
    }

    public void setHighClouds(double high_clouds) {
        this.highClouds = high_clouds;
    }

    public double getVerticalVelocities() {
        return verticalVelocities;
    }

    public void setVerticalVelocities(double vertical_vel) {
        this.verticalVelocities = vertical_vel;
    }

    public double getPressureTendency() {
        return pressureTendency;
    }

    public void setPressureTendency(double pressure_tend) {
        this.pressureTendency = pressure_tend;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date last_updated) {
        this.lastUpdated = last_updated;
    }

    public Date getImportedAt() {
        return importedAt;
    }

    public void setImportedAt(Date imported_at) {
        this.importedAt = imported_at;
    }

    public Date getValidAt() {
        return validAt;
    }

    public void setValidAt(Date valid_at) {
        this.validAt = valid_at;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
