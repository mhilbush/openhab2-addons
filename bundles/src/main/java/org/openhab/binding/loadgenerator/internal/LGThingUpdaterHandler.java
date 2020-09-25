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

import static org.openhab.binding.loadgenerator.internal.LGBindingConstants.CHANNEL_RUN;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.builder.ChannelBuilder;
import org.eclipse.smarthome.core.thing.binding.builder.ThingBuilder;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link LGThingUpdaterHandler} is responsible for
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class LGThingUpdaterHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(LGThingUpdaterHandler.class);

    private @NonNullByDefault({}) Integer delayBetweenUpdates;

    private @Nullable ScheduledFuture<?> updateGeneratorJob;

    private long counter;
    private long startTime;
    private long runTime;

    public LGThingUpdaterHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        logger.debug("Initializing handler for thing {}", getThing().getUID());

        delayBetweenUpdates = getConfigAs(LGThingUpdaterConfig.class).delayBetweenUpdates;
        logger.debug("Delay between updates: {}", delayBetweenUpdates);

        updateStatus(ThingStatus.ONLINE);
        updateState(CHANNEL_RUN, OnOffType.OFF);
        logger.debug("Thing Updater '{}' generates {} thing config updates per second", getID(),
                1000 / delayBetweenUpdates);

    }

    @Override
    public void dispose() {
        logger.debug("Disposing handler for thing {}", getThing().getUID());
        turnThingUpdaterOff();
        updateState(CHANNEL_RUN, OnOffType.OFF);
        updateStatus(ThingStatus.OFFLINE);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            return;
        }

        switch (channelUID.getId()) {
            case CHANNEL_RUN:
                if (command instanceof OnOffType) {
                    if (command.equals(OnOffType.ON)) {
                        turnThingUpdaterOn();
                    } else {
                        turnThingUpdaterOff();
                    }
                }
                break;
        }
    }

    private void turnThingUpdaterOn() {
        if (updateGeneratorJob == null) {
            counter = 1;
            startTime = System.currentTimeMillis();

            Channel channel = ChannelBuilder.create(new ChannelUID(this.getThing().getUID(), "test-channel"), "String")
                    .withLabel("Test Channel").build();

            logger.info("Thing Updater running");
            updateGeneratorJob = scheduler.scheduleAtFixedRate(() -> {
                ThingBuilder thingBuilder;

                if ((counter % 2) == 1) {
                    thingBuilder = editThing();
                    thingBuilder.withChannel(channel);
                    updateThing(thingBuilder.build());
                } else {
                    thingBuilder = editThing();
                    thingBuilder.withoutChannel(channel.getUID());
                    updateThing(thingBuilder.build());
                }

                // Configuration c = editConfiguration();
                // c.put("testParameter", String.format("%06d", counter));
                // updateConfiguration(c);
                runTime = System.currentTimeMillis() - startTime;
                counter++;
            }, 0, delayBetweenUpdates, TimeUnit.MILLISECONDS);
        }
    }

    private void turnThingUpdaterOff() {
        if (updateGeneratorJob != null) {
            updateGeneratorJob.cancel(true);
            logger.info("Thing Updater stopping after runtime of {} seconds", runTime / 1000);
            updateGeneratorJob = null;
        }
    }

    private String getID() {
        String parts[] = getThing().getUID().toString().split(":");
        return parts[parts.length - 1];
    }
}
