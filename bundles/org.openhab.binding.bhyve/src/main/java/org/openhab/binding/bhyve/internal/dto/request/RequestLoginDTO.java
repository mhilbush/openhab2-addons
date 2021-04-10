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

import com.google.gson.annotations.SerializedName;

/**
 * The {@link RequestLoginDTO} is responsible for
 *
 * @author Mark Hilbush - Initial contribution
 */
public class RequestLoginDTO {

    @SerializedName("session")
    public Session session;

    public class Session {

        @SerializedName("email")
        public String email;

        @SerializedName("password")
        public String password;
    }

    public RequestLoginDTO() {
        session = new Session();
    }
}
