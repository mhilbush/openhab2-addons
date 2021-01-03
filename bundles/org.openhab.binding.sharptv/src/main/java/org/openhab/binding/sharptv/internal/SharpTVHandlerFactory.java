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
package org.openhab.binding.sharptv.internal;

import static org.openhab.binding.sharptv.internal.SharpTVBindingConstants.SUPPORTED_THING_TYPES_UIDS;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.sharptv.handler.SharpTVHandler;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SharpTVHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
@Component(service = ThingHandlerFactory.class, configurationPid = "binding.sharptv", configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class SharpTVHandlerFactory extends BaseThingHandlerFactory {
    private Logger logger = LoggerFactory.getLogger(SharpTVHandlerFactory.class);

    @Override
    protected void activate(ComponentContext componentContext) {
        super.activate(componentContext);
        logger.info("SharpTV binding v{}", bundleContext.getBundle().getVersion());
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID)) {
            return new SharpTVHandler(thing);
        }
        return null;
    }
}
