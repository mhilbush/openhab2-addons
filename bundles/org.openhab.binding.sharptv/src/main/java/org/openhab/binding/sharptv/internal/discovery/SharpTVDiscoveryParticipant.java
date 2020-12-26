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
package org.openhab.binding.sharptv.internal.discovery;

import static org.openhab.binding.sharptv.internal.SharpTVBindingConstants.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.jupnp.model.meta.DeviceDetails;
import org.jupnp.model.meta.RemoteDevice;
import org.openhab.binding.sharptv.handler.SharpTVHandler;
import org.openhab.core.config.discovery.DiscoveryResult;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.config.discovery.upnp.UpnpDiscoveryParticipant;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SharpTVDiscoveryParticipant} is responsible for discovering new Sharp TVs on the network.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
// @Component(service = UpnpDiscoveryParticipant.class)
public class SharpTVDiscoveryParticipant implements UpnpDiscoveryParticipant, ThingHandlerService {

    private final Logger logger = LoggerFactory.getLogger(SharpTVDiscoveryParticipant.class);

    private @NonNullByDefault({}) SharpTVHandler thingHandler;

    @Override
    public Set<ThingTypeUID> getSupportedThingTypeUIDs() {
        return SUPPORTED_THING_TYPES_UIDS;
    }

    @Override
    public void setThingHandler(ThingHandler handler) {
        if (handler instanceof SharpTVHandler) {
            thingHandler = (SharpTVHandler) handler;
        }
    }

    @Override
    public @Nullable ThingHandler getThingHandler() {
        return thingHandler;
    }

    @Override
    public @Nullable DiscoveryResult createResult(RemoteDevice device) {
        ThingUID uid = getThingUID(device);
        if (uid != null) {
            DeviceDetails details = device.getDetails();
            final String ipAddress = device.getIdentity().getDescriptorURL().getHost();
            final String friendlyName = details.getFriendlyName();

            if (!thingHandler.isDiscoveryEnabled()) {
                logger.debug("SharpTvDiscovery: Skipping '{}' at '{}' because discovery is disabled", friendlyName,
                        ipAddress);
                return null;
            }
            Map<String, Object> properties = new HashMap<>(3);
            properties.put("ipAddress", ipAddress);
            properties.put("friendlyName", friendlyName);
            logger.debug("SharpTvDiscovery: Adding '{}' to inbox at IP {}", friendlyName, ipAddress);
            return DiscoveryResultBuilder.create(uid).withProperties(properties).withLabel(friendlyName).build();
        }
        return null;
    }

    @Override
    public @Nullable ThingUID getThingUID(RemoteDevice device) {
        DeviceDetails details = device.getDetails();
        String friendlyName = details.getFriendlyName().toUpperCase();

        logger.debug("SharpTvDiscovery: Discovered a uPnP device {} at {}", friendlyName,
                device.getIdentity().getDescriptorURL());

        if (friendlyName != null && isSharpTV(friendlyName)) {
            if (logger.isTraceEnabled()) {
                logger.trace("SharpTvDiscovery: Device is a Sharp TV named {} with the following details",
                        friendlyName);
                logger.trace("SharpTvDiscovery:   device.identity.toString={}", device.getIdentity().toString());
                logger.trace("  ---------------------------------------------");

                logger.trace("SharpTvDiscovery:   device.displayDetails={}", device.getDisplayString());
                logger.trace("SharpTvDiscovery:   device.embeddedDevices={}", device.getEmbeddedDevices().toString());
                logger.trace("SharpTvDiscovery:   ---------------------------------------------");

                logger.trace("SharpTvDiscovery:   device.identity.localAddress={}",
                        device.getIdentity().getDiscoveredOnLocalAddress());
                logger.trace("SharpTvDiscovery:   device.identity.interfaceMacAddress={}",
                        device.getIdentity().getInterfaceMacAddress());
                logger.trace("SharpTvDiscovery:   device.identity.descriptorUrl={}",
                        device.getIdentity().getDescriptorURL());
                logger.trace("SharpTvDiscovery:   device.identity.descriptorUrl={}",
                        device.getIdentity().getWakeOnLANBytes());
                logger.trace("SharpTvDiscovery:   device.identity.udn={}",
                        device.getIdentity().getUdn().getIdentifierString());
                logger.trace("SharpTvDiscovery:   ---------------------------------------------");
                logger.trace("SharpTvDiscovery:   device.root.services={}", device.getRoot().getServices().toString());
                logger.trace("SharpTvDiscovery:   ---------------------------------------------");

                logger.trace("SharpTvDiscovery:   device.details.friendlyName={}", details.getFriendlyName());
                logger.trace("SharpTvDiscovery:   device.details.serialNumber={}", details.getSerialNumber());
                logger.trace("SharpTvDiscovery:   device.details.secProductCaps={}", details.getSecProductCaps());
                logger.trace("SharpTvDiscovery:   device.details.baseURL={}", details.getBaseURL());
                logger.trace("SharpTvDiscovery:   device.details.dlnaCaps={}", details.getDlnaCaps());
                logger.trace("SharpTvDiscovery:   device.details.dlnaDocs={}", details.getDlnaDocs().toString());
                logger.trace("SharpTvDiscovery:   ---------------------------------------------");

                logger.trace("SharpTvDiscovery:   device.details.model.name={}",
                        details.getModelDetails().getModelName());
                logger.trace("SharpTvDiscovery:   device.details.model.description={}",
                        details.getModelDetails().getModelDescription());
                logger.trace("SharpTvDiscovery:   device.details.model.number={}",
                        details.getModelDetails().getModelNumber());
                logger.trace("SharpTvDiscovery:   device.details.model.uri={}",
                        details.getModelDetails().getModelURI());
                logger.trace("SharpTvDiscovery:   ---------------------------------------------");

                logger.trace("SharpTvDiscovery:   device.details.manufacturer.name={}",
                        details.getManufacturerDetails().getManufacturer());
                logger.trace("SharpTvDiscovery:   device.details.manufacturer.uri={}",
                        details.getManufacturerDetails().getManufacturerURI());
                logger.trace("SharpTvDiscovery:   ---------------------------------------------");
            }
            return new ThingUID(THING_TYPE_SHARP_TV, device.getIdentity().getUdn().getIdentifierString());
        }
        return null;
    }

    private boolean isSharpTV(String friendlyName) {
        if (friendlyName.contains("SHARP")) {
            return true;
        } else if (friendlyName.contains("AQUOS")) {
            return true;
        }
        return false;
    }
}
