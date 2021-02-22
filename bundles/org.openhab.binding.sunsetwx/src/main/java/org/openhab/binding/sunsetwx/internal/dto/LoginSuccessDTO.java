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

import com.google.gson.annotations.SerializedName;

/**
 * The {@link LoginSuccessDTO} class is used to parse the SunsetWx JSON
 * message returned by the SunsetWx Login API.
 *
 * @author Mark Hilbush - Initial contribution
 */
public class LoginSuccessDTO {

    @SerializedName("message")
    private String message;

    @SerializedName("token")
    private String token;

    @SerializedName("token_exp_sec")
    private int tokenExpiresSeconds;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getTokenExpSec() {
        return tokenExpiresSeconds;
    }

    public void setTokenExpSec(int token_exp_sec) {
        this.tokenExpiresSeconds = token_exp_sec;
    }
}
