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
package org.openhab.binding.loadgenerator.internal;

import static org.openhab.binding.loadgenerator.internal.LGBindingConstants.*;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link LGStateUpdaterHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class LGStateUpdaterHandler extends BaseThingHandler {
    private final Logger logger = LoggerFactory.getLogger(LGStateUpdaterHandler.class);

    private @NonNullByDefault({}) Integer delayBetweenUpdates;
    private @NonNullByDefault({}) Integer numberOfChannels;

    private @Nullable ScheduledFuture<?> eventGeneratorJob;

    private long counter;
    private long eventCounter;
    private long runTime;
    private long startTime;

    public LGStateUpdaterHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        logger.debug("Initializing handler for thing {}", getThing().getUID());

        delayBetweenUpdates = getConfigAs(LGStateUpdaterConfig.class).delayBetweenUpdates;
        numberOfChannels = getConfigAs(LGStateUpdaterConfig.class).numberOfChannels;

        logger.debug("Number of channels: {}", numberOfChannels);
        logger.debug("Delay between updates: {} msec", delayBetweenUpdates);

        int eventRate = (int) (numberOfChannels * (1.0 / (delayBetweenUpdates / 1000.0)));
        logger.debug("State Updater '{}' generates {} state updates per second", getID(), eventRate);

        updateStatus(ThingStatus.ONLINE);
        updateState(CHANNEL_RUN, OnOffType.OFF);
    }

    @Override
    public void dispose() {
        logger.debug("Disposing handler for thing {}", getThing().getUID());
        turnLoadGeneratorOff();
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
                logger.debug("Handle command {} on RUN channel for {}", command, getThing().getUID());
                if (command instanceof OnOffType) {
                    if (command.equals(OnOffType.ON)) {
                        turnEventGeneratorOn();
                    } else {
                        turnLoadGeneratorOff();
                    }
                }
                break;
        }
    }

    private void turnEventGeneratorOn() {
        if (eventGeneratorJob == null) {
            counter = 1;
            eventCounter = 1;
            startTime = System.currentTimeMillis();
            logger.info("State Updater running");
            eventGeneratorJob = scheduler.scheduleAtFixedRate(() -> {
                for (int i = 0; i <= numberOfChannels; i++) {
                    updateState(CHANNEL_STATE + String.format("%02d", i), new DecimalType(counter));
                    eventCounter++;
                }
                runTime = System.currentTimeMillis() - startTime;
                updateState(CHANNEL_NUM_STATE_UPDATES, new DecimalType(eventCounter));
                updateState(CHANNEL_RUNTIME, new DecimalType(runTime / 1000));
                counter++;
            }, 0, delayBetweenUpdates, TimeUnit.MILLISECONDS);
        }
    }

    private void turnLoadGeneratorOff() {
        if (eventGeneratorJob != null) {
            eventGeneratorJob.cancel(true);
            logger.info("State Updater stopping after runtime of {} seconds", runTime / 1000);
            eventGeneratorJob = null;
        }
    }

    private String getID() {
        String parts[] = getThing().getUID().toString().split(":");
        return parts[parts.length - 1];
    }
}
