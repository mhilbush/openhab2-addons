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
package org.openhab.binding.bhyve.internal.discovery;

import static org.openhab.binding.bhyve.internal.BhyveBindingConstants.*;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.bhyve.internal.handler.BhyveBridgeHandler;
import org.openhab.core.config.discovery.AbstractDiscoveryService;
import org.openhab.core.config.discovery.DiscoveryResult;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.thing.ThingUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * When a {@link BhyveDiscoveryService} finds a new device we will
 * add it to the system.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class BhyveDiscoveryService extends AbstractDiscoveryService {
    private final Logger logger = LoggerFactory.getLogger(BhyveDiscoveryService.class);

    private static final int TIMEOUT = 60;

    private BhyveBridgeHandler bridgeHandler;

    /**
     * Discover Bhyve devices
     *
     * @param bridgeHandler
     */
    public BhyveDiscoveryService(BhyveBridgeHandler bridgeHandler) {
        super(SUPPORTED_THING_TYPES_UIDS, TIMEOUT, false);
        this.bridgeHandler = bridgeHandler;
    }

    @Override
    protected void startScan() {
        logger.debug("Discovery: startScan invoked");
        bridgeHandler.discoverDevices();
    }

    public void addDevice(BhyveDevice device) {
        ThingUID thingUID;
        ThingUID bridgeUID = bridgeHandler.getThing().getUID();

        switch (device.getType()) {
            case BHYVE_TYPE_BRIDGE:
                thingUID = new ThingUID(UID_HUB, bridgeUID, device.getMacAddress());
                break;

            case BHYVE_TYPE_SPRINKLER_TIMER:
                thingUID = new ThingUID(UID_SPRINKLER_TIMER, bridgeUID, device.getMacAddress());
                break;

            default:
                return;
        }

        // if (!deviceThingExists(thingUID)) {
        logger.debug("Discovery: Adding device {}: macAddress={} name={} deviceId={}", device.getType(),
                device.getMacAddress(), device.getName(), device.getDeviceId());
        Map<String, Object> properties = new HashMap<>();
        properties.put("deviceId", device.getDeviceId());
        properties.put("name", "B-hyve " + device.getName());
        properties.put("macAddress", device.getMacAddress());
        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID).withProperties(properties)
                .withBridge(bridgeUID).withRepresentationProperty("deviceId").withLabel("Bhyve " + device.getName())
                .build();
        thingDiscovered(discoveryResult);
        // }
    }

    // private boolean deviceThingExists(ThingUID newThingUID) {
    // return bridgeHandler.getThingByUID(newThingUID) != null ? true : false;
    // }
}
