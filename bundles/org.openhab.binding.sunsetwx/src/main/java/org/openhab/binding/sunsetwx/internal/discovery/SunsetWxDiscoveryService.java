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
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.sunsetwx.internal.dto.DiscoveryLocationResponse;
import org.openhab.binding.sunsetwx.internal.handler.SunsetWxAccountHandler;
import org.openhab.core.config.discovery.AbstractDiscoveryService;
import org.openhab.core.config.discovery.DiscoveryResult;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.config.discovery.DiscoveryService;
import org.openhab.core.io.net.http.HttpUtil;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SunsetWxDiscoveryService} tries to automatically discover the
 * geolocation based on the Internet IP address.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class SunsetWxDiscoveryService extends AbstractDiscoveryService
        implements DiscoveryService, ThingHandlerService {

    private static final int DISCOVERY_INTERVAL_SECONDS = 300;
    private static final int DISCOVERY_INITIAL_DELAY_SECONDS = 10;

    private static final String THING_PROPERTY_GEOLOCATION = "geoLocation";

    public static final String THING_SUNRISE_LABEL = "SunsetWx Local Sunrise";
    public static final String THING_SUNSET_LABEL = "SunsetWx Local Sunset";

    private final Logger logger = LoggerFactory.getLogger(SunsetWxDiscoveryService.class);

    private @NonNullByDefault({}) SunsetWxAccountHandler bridgeHandler;

    private @Nullable Future<?> discoveryJob;

    public SunsetWxDiscoveryService() {
        super(SUPPORTED_SUNSETWX_THING_TYPES_UIDS, 10, true);
    }

    @Override
    public void activate() {
        super.activate(null);
    }

    @Override
    public void deactivate() {
        super.deactivate();
    }

    @Override
    public void setThingHandler(ThingHandler handler) {
        if (handler instanceof SunsetWxAccountHandler) {
            bridgeHandler = (SunsetWxAccountHandler) handler;
        }
    }

    @Override
    public @Nullable ThingHandler getThingHandler() {
        return bridgeHandler;
    }

    @Override
    protected void startBackgroundDiscovery() {
        Future<?> localDiscoveryJob = discoveryJob;
        if (localDiscoveryJob == null || localDiscoveryJob.isCancelled()) {
            logger.debug("SunsetWxDiscovery: Starting background discovery job");
            discoveryJob = scheduler.scheduleWithFixedDelay(this::discoverThings, DISCOVERY_INITIAL_DELAY_SECONDS,
                    DISCOVERY_INTERVAL_SECONDS, TimeUnit.SECONDS);
        }
    }

    @Override
    protected void stopBackgroundDiscovery() {
        Future<?> localDiscoveryJob = discoveryJob;
        if (localDiscoveryJob != null) {
            logger.debug("SunsetWxDiscovery: Stopping background discovery job");
            localDiscoveryJob.cancel(true);
            discoveryJob = null;
        }
    }

    @Override
    protected void startScan() {
        discoverThings();
    }

    private void discoverThings() {
        logger.debug("SunsetWxDiscovery: Starting SunsetWx discovery scan");
        String geoLocation = bridgeHandler.getGeoLocation();
        if (geoLocation == null) {
            geoLocation = getGeoLocationFromIpAddress();
        }
        if (geoLocation == null) {
            logger.debug("SunsetWxDiscovery: Can't find geolocation to use for discovery");
            return;
        }
        logger.debug("SunsetWxDiscovery: SunsetWx propGeolocation: {}", geoLocation);
        Map<String, Object> properties = new HashMap<>();
        properties.put(THING_PROPERTY_GEOLOCATION, geoLocation);

        ThingUID bridgeUID = bridgeHandler.getThing().getUID();
        ThingUID sunriseThingUID = new ThingUID(THING_TYPE_UID_SUNRISE, bridgeUID, "local");
        ThingUID sunsetThingUID = new ThingUID(THING_TYPE_UID_SUNSET, bridgeUID, "local");
        logger.debug("SunsetWxDiscovery: Creating things ({}) and ({})", sunriseThingUID, sunsetThingUID);
        thingDiscovered(createDiscoveryResult(sunriseThingUID, bridgeUID, THING_SUNRISE_LABEL, properties));
        thingDiscovered(createDiscoveryResult(sunsetThingUID, bridgeUID, THING_SUNSET_LABEL, properties));
    }

    private DiscoveryResult createDiscoveryResult(ThingUID thingUID, ThingUID bridgeUID, String label,
            Map<String, Object> properties) {
        return DiscoveryResultBuilder.create(thingUID).withProperties(properties).withBridge(bridgeUID).withLabel(label)
                .build();
    }

    private @Nullable String getGeoLocationFromIpAddress() {
        try {
            String response = HttpUtil.executeUrl("GET", "http://ip-api.com/json/?fields=lat,lon", 5000);
            logger.debug("SunsetWxDiscovery: Response from ip-api.com: {}", response);
            DiscoveryLocationResponse location = bridgeHandler.getGson().fromJson(response,
                    DiscoveryLocationResponse.class);
            if (location != null) {
                Double lat = location.lat;
                Double lon = location.lon;
                logger.debug("SunsetWxDiscovery: Got location from IP address: lat: {}, lon: {}", lat, lon);
                return String.format("%s,%s", lon.toString(), lat.toString());
            }
        } catch (IOException e) {
            logger.debug("SunsetWxDiscovery: IOException getting location from IP address: {}", e.getMessage());
        }
        return null;
    }
}
