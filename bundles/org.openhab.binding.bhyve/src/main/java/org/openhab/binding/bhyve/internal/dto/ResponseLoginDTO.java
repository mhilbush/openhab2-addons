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

import com.google.gson.annotations.SerializedName;

/**
 * The {@link ResponseLoginDTO} is responsible for
 *
 * @author Mark Hilbush - Initial contribution
 */
public class ResponseLoginDTO {

    @SerializedName("orbit_api_key")
    public String orbitApiKey;

    @SerializedName("user_id")
    public String userId;

    @SerializedName("user_name")
    public String userName;

    @SerializedName("bhyve_account_id")
    public String bhyveAccountId;

    @SerializedName("bhyve_account_roles")
    public String bhyveAccountRoles;

    @SerializedName("bhyve_account_groups")
    public String bhyveAccountGroups;

    @SerializedName("roles")
    public String roles;
    // {
    // "orbit_api_key":
    // "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJ1c2VyLWlkIjoiNWFkNDk4Mjg0ZjBjNzJkN2Q2MjU1YTRjIiwiYXBwLWlkIjoiT3JiaXQgU3VwcG9ydCBEYXNoYm9hcmQifQ.DkyC0qg3pYym-LN2G3IqKTBhxJlLxB4ynfJ3WWgETs4",
    // "user_id": "5ad498284f0c72d7d6255a4c",
    // "user_name": "Mark Hilbush",
    // "bhyve_account_id": null,
    // "bhyve_account_roles": null,
    // "bhyve_account_groups": null,
    // "roles": null
    // }
}
