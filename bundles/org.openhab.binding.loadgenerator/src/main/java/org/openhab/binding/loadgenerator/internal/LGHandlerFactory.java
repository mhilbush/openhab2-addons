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
package org.openhab.binding.loadgenerator.internal;

import static org.openhab.binding.loadgenerator.internal.LGBindingConstants.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Component;

/**
 * The {@link LGHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.loadgenerator", service = ThingHandlerFactory.class)
public class LGHandlerFactory extends BaseThingHandlerFactory {

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (THING_TYPE_ADMIN.equals(thingTypeUID)) {
            return new LGAdminHandler(thing);
        }
        if (THING_TYPE_STATE_UPDATER.equals(thingTypeUID)) {
            return new LGStateUpdaterHandler(thing);
        }
        if (THING_TYPE_COMMAND_GENERATOR.equals(thingTypeUID)) {
            return new LGCommandGeneratorHandler(thing);
        }
        if (THING_TYPE_THING_UPDATER.equals(thingTypeUID)) {
            return new LGThingUpdaterHandler(thing);
        }
        return null;
    }
}
