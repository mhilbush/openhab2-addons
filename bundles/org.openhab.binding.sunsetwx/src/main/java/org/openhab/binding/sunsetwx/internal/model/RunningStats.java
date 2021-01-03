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
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class RunningStats {

    private int count = 0;
    private double average = 0.0;
    private double pwrSumAvg = 0.0;
    private double stdDev = 0.0;

    /**
     * Add another value and calculate average and standard deviation
     */
    public void put(double value) {
        count++;
        average += (value - average) / count;
        pwrSumAvg += (value * value - pwrSumAvg) / count;
        stdDev = Math.sqrt((pwrSumAvg * count - count * average * average) / (count - 1));
    }

    public double getAverage() {
        return average;
    }

    public double getStandardDeviation() {
        return Double.isNaN(stdDev) ? 0.0 : stdDev;
    }
}
