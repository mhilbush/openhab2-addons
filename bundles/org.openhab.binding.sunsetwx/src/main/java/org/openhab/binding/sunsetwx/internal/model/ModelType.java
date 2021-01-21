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
import org.openhab.binding.sunsetwx.internal.handler.SunsetWxHandler;

/**
 * The {@link SunsetWxHandler} defines the models supported by the SunsetWx service
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public enum ModelType {
    SUNRISE("sunrise"),
    SUNSET("sunset");

    public final String label;

    private ModelType(String label) {
        this.label = label;
    }
}
