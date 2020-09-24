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
package org.openhab.binding.sunsetwx.internal.model;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.sunsetwx.internal.SunsetWxBindingConstants.ModelType;

/**
 * Convert the quality value to a quality string based on the conversion
 * rules defined by SunsetWx.
 *
 * Sunrise
 * (0-25%) Poor: Less than or equal to -223
 * (25-50%) Fair: Greater than -223 to -40
 * (50-75%) Good: Greater than -40 to 142
 * (75-100%) Great: Greater than 142
 *
 * Sunset
 * (0-17.63%) Poor: Less than or equal to -262.75
 * (17.63-50%) Fair: Greater than -262.75 to -75
 * (50-75%) Good: Greater than -75 to 70
 * (75-100%) Great: Greater than 70
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class QualityConverter {
    ModelType type;

    public QualityConverter(ModelType type) {
        this.type = type;
    }

    public String convertQuality(double qualityValue) {
        if (type.equals(ModelType.SUNRISE)) {
            if (qualityValue <= -223.0d) {
                return "Poor";
            } else if (qualityValue > -223.0d && qualityValue <= -40.0d) {
                return "Fair";
            } else if (qualityValue > -40.0d && qualityValue <= 142.0d) {
                return "Good";
            } else {
                return "Great";
            }
        } else {
            if (qualityValue <= -262.75d) {
                return "Poor";
            } else if (qualityValue > -262.75d && qualityValue <= -75.0d) {
                return "Fair";
            } else if (qualityValue > -75.0d && qualityValue <= 70.0d) {
                return "Good";
            } else {
                return "Great";
            }
        }
    }
}
