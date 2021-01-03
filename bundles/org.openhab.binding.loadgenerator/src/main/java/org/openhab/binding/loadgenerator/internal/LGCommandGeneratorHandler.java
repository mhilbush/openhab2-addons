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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.io.net.http.HttpUtil;
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
 * The {@link LGCommandGeneratorHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class LGCommandGeneratorHandler extends BaseThingHandler {
    private final Logger logger = LoggerFactory.getLogger(LGCommandGeneratorHandler.class);

    private final String openhabAddress = "test.md.hilbush.com";
    private final String openhabPort = "8080";

    private @NonNullByDefault({}) Integer delayBetweenCommands;
    private @NonNullByDefault({}) Integer commandProcessingTime;
    private @NonNullByDefault({}) String itemName;

    private @NonNullByDefault({}) String commandUrl;

    private final byte[] ON_BYTES = "ON".getBytes();
    private final byte[] OFF_BYTES = "OFF".getBytes();

    private @Nullable ScheduledFuture<?> commandGeneratorJob;

    private long sentCount;
    private long processedCount;
    private long startTime;
    private long runTime;

    public LGCommandGeneratorHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        logger.debug("Initializing handler for thing {}", getThing().getUID());

        delayBetweenCommands = getConfigAs(LGCommandGeneratorConfig.class).delayBetweenCommands;
        commandProcessingTime = getConfigAs(LGCommandGeneratorConfig.class).commandProcessingTime;
        itemName = getConfigAs(LGCommandGeneratorConfig.class).itemName;

        logger.debug("Delay between commands: {} msec", delayBetweenCommands);
        logger.debug("Command processing delay: {} msec", commandProcessingTime);
        logger.debug("Item name: {}", itemName);

        logger.debug("Command Generator '{}' sends command to item '{}' every {} ms with {} ms processing time",
                getID(), itemName, delayBetweenCommands, commandProcessingTime);

        commandUrl = "http://" + openhabAddress + ":" + openhabPort + "/rest/items/" + itemName;

        updateStatus(ThingStatus.ONLINE);
        updateState(CHANNEL_RUN, OnOffType.OFF);
    }

    @Override
    public void dispose() {
        logger.debug("Disposing handler for '{}': sent={}, received={}", getThing().getUID(), sentCount,
                processedCount);
        turnCommandGeneratorOff();
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
                        turnCommandGeneratorOn();
                    } else {
                        turnCommandGeneratorOff();
                    }
                }
                break;
            case CHANNEL_COMMAND:
                try {
                    Thread.sleep(commandProcessingTime);
                } catch (InterruptedException e) {
                }
                processedCount++;
                updateState(CHANNEL_NUM_COMMANDS_PROCESSED, new DecimalType(processedCount));
                break;
        }
    }

    private void turnCommandGeneratorOn() {
        if (commandGeneratorJob == null) {
            sentCount = 0;
            processedCount = 0;
            startTime = System.currentTimeMillis();
            logger.info("Command Generator running");
            commandGeneratorJob = scheduler.scheduleAtFixedRate(() -> {
                sendCommand(sentCount);
                sentCount++;
                runTime = System.currentTimeMillis() - startTime;
                updateState(CHANNEL_NUM_COMMANDS_SENT, new DecimalType(sentCount));
                updateState(CHANNEL_RUNTIME, new DecimalType(runTime / 1000));
            }, 0, delayBetweenCommands, TimeUnit.MILLISECONDS);
        }
    }

    private void turnCommandGeneratorOff() {
        if (commandGeneratorJob != null) {
            commandGeneratorJob.cancel(true);
            logger.info("Command Generator stopping after runtime of {} seconds", runTime / 1000);
            commandGeneratorJob = null;
            logger.info("Stats for '{}': sent={}, processed={}", getThing().getUID(), sentCount, processedCount);
        }
    }

    private void sendCommand(long counter) {
        try {
            ByteArrayInputStream content = new ByteArrayInputStream((counter % 2) == 0 ? OFF_BYTES : ON_BYTES);
            HttpUtil.executeUrl("POST", commandUrl, content, "text/plain", 1000);
            // HttpUtil.executeUrl("POST", commandUrl, new ByteArrayInputStream("ON".getBytes()), "text/plain", 1000);
        } catch (IOException e) {
            logger.debug("IOException sending command: {}", e.getMessage());
        }
    }

    private String getID() {
        String parts[] = getThing().getUID().toString().split(":");
        return parts[parts.length - 1];
    }
}
