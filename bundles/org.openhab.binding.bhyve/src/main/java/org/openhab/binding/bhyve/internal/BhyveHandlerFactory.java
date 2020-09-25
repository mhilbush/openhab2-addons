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
package org.openhab.binding.bhyve.internal;

import static org.openhab.binding.bhyve.internal.BhyveBindingConstants.*;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.openhab.binding.bhyve.internal.discovery.BhyveDiscoveryService;
import org.openhab.binding.bhyve.internal.handler.BhyveBridgeHandler;
import org.openhab.binding.bhyve.internal.handler.BhyveHubHandler;
import org.openhab.binding.bhyve.internal.handler.BhyveSprinklerTimerHandler;
import org.openhab.core.config.discovery.DiscoveryService;
import org.openhab.core.i18n.TimeZoneProvider;
import org.openhab.core.io.net.http.WebSocketFactory;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BhyveHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.bhyve", service = ThingHandlerFactory.class)
public class BhyveHandlerFactory extends BaseThingHandlerFactory {
    private final Logger logger = LoggerFactory.getLogger(BhyveHandlerFactory.class);

    private Map<ThingUID, ServiceRegistration<?>> discoveryServiceRegs = new HashMap<>();

    private TimeZoneProvider timeZoneProvider;
    private WebSocketClient webSocketClient;

    @Activate
    public BhyveHandlerFactory(@Reference TimeZoneProvider timeZoneProvider,
            @Reference WebSocketFactory webSocketFactory) {
        this.timeZoneProvider = timeZoneProvider;
        this.webSocketClient = webSocketFactory.getCommonWebSocketClient();
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (UID_SPRINKLER_TIMER.equals(thingTypeUID)) {
            logger.debug("HandlerFactory: Creating sprinkler timer handler");
            try {
                return new BhyveSprinklerTimerHandler(thing, webSocketClient, timeZoneProvider);
            } catch (URISyntaxException e) {
                logger.warn("HandlerFactory: Handler threw URIException: {}", e.getMessage(), e);
            }
        }

        if (UID_HUB.equals(thingTypeUID)) {
            logger.debug("HandlerFactory: Creating hub handler");
            try {
                return new BhyveHubHandler(thing, webSocketClient, timeZoneProvider);
            } catch (URISyntaxException e) {
                logger.warn("HandlerFactory: Handler threw URIException: {}", e.getMessage(), e);
            }
        }

        if (SUPPORTED_BRIDGE_THING_TYPES_UIDS.contains(thingTypeUID)) {
            logger.debug("Creating bridge handler for thing {}", thing.getUID());
            BhyveBridgeHandler bridgeHandler = new BhyveBridgeHandler((Bridge) thing);

            logger.debug("Creating and registering discovery service");
            BhyveDiscoveryService discoveryService = new BhyveDiscoveryService(bridgeHandler);
            // Register the discovery service with the bridge handler
            bridgeHandler.setDiscoveryService(discoveryService);
            // Register the discovery service
            ServiceRegistration<?> reg = bundleContext.registerService(DiscoveryService.class.getName(),
                    discoveryService, new Hashtable<String, Object>());
            // Add the service to the ServiceRegistration map
            discoveryServiceRegs.put(bridgeHandler.getThing().getUID(), reg);
            return bridgeHandler;
        }
        return null;
    }

    @SuppressWarnings("null")
    @Override
    protected synchronized void removeHandler(ThingHandler thingHandler) {
        if (thingHandler instanceof BhyveBridgeHandler) {
            ServiceRegistration<?> serviceReg = this.discoveryServiceRegs.get(thingHandler.getThing().getUID());
            if (serviceReg != null) {
                logger.debug("Unregistering and removing discovery service");
                // Unregister the discovery service from the bridge handler
                ((BhyveBridgeHandler) thingHandler).unsetDiscoveryService();
                // Unregister the discovery service
                serviceReg.unregister();
                // Remove the discovery service from the ServiceRegistration map
                discoveryServiceRegs.remove(thingHandler.getThing().getUID());
            }
            logger.debug("Removing bridge handler for thing {}", thingHandler.getThing().getUID());
            super.removeHandler(thingHandler);
        }
        if (thingHandler instanceof BhyveSprinklerTimerHandler) {
            logger.debug("Removing sprinkler timer handler for thing {}", thingHandler.getThing().getUID());
            super.removeHandler(thingHandler);
        }
    }
}
