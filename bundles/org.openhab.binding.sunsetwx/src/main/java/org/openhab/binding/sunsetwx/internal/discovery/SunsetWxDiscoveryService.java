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
package org.openhab.binding.sunsetwx.internal.discovery;

import static org.openhab.binding.sunsetwx.internal.SunsetWxBindingConstants.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.sunsetwx.internal.dto.DiscoveryLocationResponse;
import org.openhab.core.config.discovery.AbstractDiscoveryService;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.config.discovery.DiscoveryService;
import org.openhab.core.io.net.http.HttpUtil;
import org.openhab.core.thing.ThingUID;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * The {@link SunsetWxDiscoveryService} tries to automatically discover the
 * geolocation based on the Internet IP address.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
@Component(service = DiscoveryService.class, immediate = true)
public class SunsetWxDiscoveryService extends AbstractDiscoveryService {

    private final Logger logger = LoggerFactory.getLogger(SunsetWxDiscoveryService.class);

    private final Gson gson = new Gson();

    /**
     * Creates a SunsetWxDiscoveryService with disabled autostart.
     */
    public SunsetWxDiscoveryService() {
        super(SUPPORTED_THING_TYPES_UIDS, 0, false);
    }

    @Override
    protected void startScan() {
        logger.info("Starting SunsetWx discovery scan");

        String geoLocation;
        String result;
        try {
            result = HttpUtil.executeUrl("GET", "http://ip-api.com/json/?fields=lat,lon", 5000);
        } catch (IOException e) {
            result = null;
        }

        geoLocation = DEFAULT_COORDINATES;
        if (result != null) {
            DiscoveryLocationResponse location = gson.fromJson(result, DiscoveryLocationResponse.class);
            if (location != null) {
                Double latitude = location.lat;
                Double longitude = location.lon;
                logger.info("Evaluated geolocation: longitude: {}, latitude: {}", longitude, latitude);
                geoLocation = String.format("%s,%s", longitude.toString(), latitude.toString());
            } else {
                logger.info("Can't discover geolocation, using defaults of {}", geoLocation);
            }
        } else {
            logger.info("Can't discover geolocation, using defaults of {}", geoLocation);
        }
        logger.debug("SunsetWx propGeolocation: {}", geoLocation);

        ThingUID sunriseThing = new ThingUID(THING_TYPE_SUNRISE, "local");
        ThingUID sunsetThing = new ThingUID(THING_TYPE_SUNSET, "local");

        Map<String, Object> properties = new HashMap<>();
        properties.put(THING_PROPERTY_GEOLOCATION, geoLocation);

        // Create sunrise thing
        thingDiscovered(DiscoveryResultBuilder.create(sunriseThing).withLabel(THING_SUNRISE_LABEL)
                .withProperties(properties).build());

        // Create sunset thing
        thingDiscovered(DiscoveryResultBuilder.create(sunsetThing).withLabel(THING_SUNSET_LABEL)
                .withProperties(properties).build());
    }
}
