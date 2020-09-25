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
import org.openhab.core.config.discovery.DiscoveryResult;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.config.discovery.upnp.UpnpDiscoveryParticipant;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SharpTVDiscoveryParticipant} is responsible for discovering new Sharp TVs on the network.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
@Component(service = UpnpDiscoveryParticipant.class, immediate = true)
public class SharpTVDiscoveryParticipant implements UpnpDiscoveryParticipant {
    private final Logger logger = LoggerFactory.getLogger(SharpTVDiscoveryParticipant.class);

    private Set<ThingTypeUID> supportedThingTypes = SUPPORTED_THING_TYPES_UIDS;

    @Override
    public Set<ThingTypeUID> getSupportedThingTypeUIDs() {
        return supportedThingTypes;
    }

    @Override
    public @Nullable DiscoveryResult createResult(RemoteDevice device) {
        ThingUID uid = getThingUID(device);
        if (uid != null) {
            DeviceDetails details = device.getDetails();
            Map<String, Object> properties = new HashMap<>(3);
            properties.put("ipAddress", device.getIdentity().getDescriptorURL().getHost());
            properties.put("friendlyName", details.getFriendlyName());

            logger.debug("Adding inbox entry for Sharp TV at IP {}", device.getIdentity().getDescriptorURL().getHost());

            return DiscoveryResultBuilder.create(uid).withProperties(properties).withLabel(details.getFriendlyName())
                    .build();
        }
        return null;
    }

    @Override
    public @Nullable ThingUID getThingUID(RemoteDevice device) {
        DeviceDetails details = device.getDetails();
        String friendlyName = details.getFriendlyName().toUpperCase();

        logger.debug("Discovered a uPnP device {} at {}", friendlyName, device.getIdentity().getDescriptorURL());

        if (friendlyName != null && isSharpTV(friendlyName)) {
            logger.trace("Device is a Sharp TV named {} with the following details", friendlyName);
            logger.trace("  device.identity.toString={}", device.getIdentity().toString());
            logger.trace("  ---------------------------------------------");

            logger.trace("  device.displayDetails={}", device.getDisplayString());
            logger.trace("  device.embeddedDevices={}", device.getEmbeddedDevices().toString());
            logger.trace("  ---------------------------------------------");

            logger.trace("  device.identity.localAddress={}", device.getIdentity().getDiscoveredOnLocalAddress());
            logger.trace("  device.identity.interfaceMacAddress={}", device.getIdentity().getInterfaceMacAddress());
            logger.trace("  device.identity.descriptorUrl={}", device.getIdentity().getDescriptorURL());
            logger.trace("  device.identity.descriptorUrl={}", device.getIdentity().getWakeOnLANBytes());
            logger.trace("  device.identity.udn={}", device.getIdentity().getUdn().getIdentifierString());
            logger.trace("  ---------------------------------------------");
            logger.trace("  device.root.services={}", device.getRoot().getServices().toString());
            logger.trace("  ---------------------------------------------");

            logger.trace("  device.details.friendlyName={}", details.getFriendlyName());
            logger.trace("  device.details.serialNumber={}", details.getSerialNumber());
            logger.trace("  device.details.secProductCaps={}", details.getSecProductCaps());
            logger.trace("  device.details.baseURL={}", details.getBaseURL());
            logger.trace("  device.details.dlnaCaps={}", details.getDlnaCaps());
            logger.trace("  device.details.dlnaDocs={}", details.getDlnaDocs().toString());
            logger.trace("  ---------------------------------------------");

            logger.trace("  device.details.model.name={}", details.getModelDetails().getModelName());
            logger.trace("  device.details.model.description={}", details.getModelDetails().getModelDescription());
            logger.trace("  device.details.model.number={}", details.getModelDetails().getModelNumber());
            logger.trace("  device.details.model.uri={}", details.getModelDetails().getModelURI());
            logger.trace("  ---------------------------------------------");

            logger.trace("  device.details.manufacturer.name={}", details.getManufacturerDetails().getManufacturer());
            logger.trace("  device.details.manufacturer.uri={}", details.getManufacturerDetails().getManufacturerURI());
            logger.trace("  ---------------------------------------------");

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
