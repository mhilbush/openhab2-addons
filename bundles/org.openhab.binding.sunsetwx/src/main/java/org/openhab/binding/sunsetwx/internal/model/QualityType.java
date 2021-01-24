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
package org.openhab.binding.sunsetwx.internal.model;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link QualityType} defines the quality levels supported by the service
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public enum QualityType {
    POOR("Poor"),
    FAIR("Fair"),
    GOOD("Good"),
    GREAT("Great");

    public final String label;

    private QualityType(String label) {
        this.label = label;
    }
}
