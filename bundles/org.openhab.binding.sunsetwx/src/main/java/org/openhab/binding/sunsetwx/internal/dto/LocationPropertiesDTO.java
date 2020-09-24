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
package org.openhab.binding.sunsetwx.internal.dto;

import com.google.gson.annotations.SerializedName;

/**
 * The {@link LocationPropertiesDTO} class is used to parse the SunsetWx JSON message returned by the SunsetWx API.
 *
 * @author Mark Hilbush - Initial contribution
 */
public class LocationPropertiesDTO {

    @SerializedName("locale")
    private String locale;

    @SerializedName("region")
    private String region;

    @SerializedName("country")
    private String country;

    @SerializedName("source")
    private String source;

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
