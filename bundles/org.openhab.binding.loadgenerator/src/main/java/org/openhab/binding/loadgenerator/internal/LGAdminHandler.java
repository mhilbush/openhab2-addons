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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.io.net.http.HttpUtil;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link LGAdminHandler} is responsible for
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class LGAdminHandler extends BaseThingHandler {

    private static final String THING_JSON = "{ \"label\": \"%LABEL%\", \"UID\": \"%THINGUID%\", \"thingTypeUID\": \"%THINGTYPEUID%\", \"configuration\": { %CONFIGURATION% } }";

    private final Logger logger = LoggerFactory.getLogger(LGAdminHandler.class);

    private @NonNullByDefault({}) Integer numStateUpdaterThings;
    private @NonNullByDefault({}) Integer stateUpdaterDelayBetweenUpdates;
    private @NonNullByDefault({}) Integer stateUpdaterNumberOfChannels;

    private @NonNullByDefault({}) Integer numCommandGeneratorThings;

    private @NonNullByDefault({}) Integer numThingUpdaterThings;
    private @NonNullByDefault({}) Integer thingUpdaterDelayBetweenUpdates;

    public LGAdminHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        logger.debug("Initializing handler for thing {}", getID());

        numStateUpdaterThings = getConfigAs(LGAdminConfig.class).numStateUpdaterThings;
        stateUpdaterDelayBetweenUpdates = getConfigAs(LGAdminConfig.class).stateUpdaterDelayBetweenUpdates;
        stateUpdaterNumberOfChannels = getConfigAs(LGAdminConfig.class).stateUpdaterNumberOfChannels;
        numCommandGeneratorThings = getConfigAs(LGAdminConfig.class).numCommandGeneratorThings;
        numThingUpdaterThings = getConfigAs(LGAdminConfig.class).numThingUpdaterThings;
        thingUpdaterDelayBetweenUpdates = getConfigAs(LGAdminConfig.class).thingUpdaterDelayBetweenUpdates;

        updateStatus(ThingStatus.ONLINE);
        updateState(CHANNEL_CREATE_THINGS, OnOffType.OFF);
        updateState(CHANNEL_REMOVE_THINGS, OnOffType.OFF);

        logger.info("Load Generator Admin is running!");
        logger.info("Admin: State Updater    : Number of things: {}", numStateUpdaterThings);
        logger.info("Admin:                  : delayBetweenUpdates: {}", stateUpdaterDelayBetweenUpdates);
        logger.info("Admin:                  : numberOfChannels: {}", stateUpdaterNumberOfChannels);
        logger.info("Admin: Command Generator: Number of things: {}", numCommandGeneratorThings);
        logger.info("Admin: Thing Updater    : Number of things: {}", numThingUpdaterThings);
        logger.info("Admin:                  : delayBetweenUpdates: {}", thingUpdaterDelayBetweenUpdates);
    }

    @Override
    public void dispose() {
        logger.debug("Disposing handler for thing {}", getID());
        updateStatus(ThingStatus.OFFLINE);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            return;
        }

        switch (channelUID.getId()) {
            case CHANNEL_CREATE_THINGS:
                if (command instanceof OnOffType) {
                    if (command.equals(OnOffType.ON)) {
                        logger.debug("Handle command {} on CREATE_THINGS channel for {}", command, getID());
                        createThings();
                        updateState(CHANNEL_CREATE_THINGS, OnOffType.OFF);
                    }
                }
                break;
            case CHANNEL_REMOVE_THINGS:
                if (command instanceof OnOffType) {
                    if (command.equals(OnOffType.ON)) {
                        logger.debug("Handle command {} on REMOVE_THINGS channel for {}", command, getID());
                        removeThings();
                        updateState(CHANNEL_REMOVE_THINGS, OnOffType.OFF);
                    }
                }
                break;
        }
    }

    private void createThings() {
        // Create state updaters
        logger.info("Create {} STATE_UPDATER things with config: {}", numStateUpdaterThings,
                formatStateUpdaterConfiguration());
        for (int i = 1; i <= numStateUpdaterThings; i++) {
            String s = new String(THING_JSON);
            s = s.replace("%LABEL%", formatThingLabel("State Updater", i));
            s = s.replace("%THINGUID%", formatThingUID(THING_TYPE_STATE_UPDATER, "update", i));
            s = s.replace("%THINGTYPEUID%", formatThingTypeUID(THING_TYPE_STATE_UPDATER));
            s = s.replace("%CONFIGURATION%", formatStateUpdaterConfiguration());
            logger.debug("Create STATE_UPDATER thing with {}", s);
            createThing(s);
        }

        // Create command generators
        logger.info("Create {} COMMAND_GENERATOR things", numCommandGeneratorThings);
        for (int i = 1; i <= numCommandGeneratorThings; i++) {
            String s = new String(THING_JSON);
            s = s.replace("%LABEL%", formatThingLabel("Command Generator", i));
            s = s.replace("%THINGUID%", formatThingUID(THING_TYPE_COMMAND_GENERATOR, "generator", i));
            s = s.replace("%THINGTYPEUID%", formatThingTypeUID(THING_TYPE_COMMAND_GENERATOR));
            s = s.replace("%CONFIGURATION%", formatItemName(i));
            logger.debug("Create COMMAND_GENERATOR thing with {}", s);
            createThing(s);
        }

        // Create thing updaters
        logger.info("Create {} THING_UPDATER things with config: {}", numThingUpdaterThings,
                formatThingUpdaterConfiguration());
        for (int i = 1; i <= numThingUpdaterThings; i++) {
            String s = new String(THING_JSON);
            s = s.replace("%LABEL%", formatThingLabel("Thing Updater", i));
            s = s.replace("%THINGUID%", formatThingUID(THING_TYPE_THING_UPDATER, "update", i));
            s = s.replace("%THINGTYPEUID%", formatThingTypeUID(THING_TYPE_THING_UPDATER));
            s = s.replace("%CONFIGURATION%", formatThingUpdaterConfiguration());
            logger.debug("Create THING_UPDATER thing with {}", s);
            createThing(s);
        }
    }

    private void createThing(String s) {
        String url = "http://test.md.hilbush.com:8080/rest/things";
        try {
            ByteArrayInputStream content = new ByteArrayInputStream(s.getBytes());
            String response = HttpUtil.executeUrl("POST", url, content, "application/json", 2000);
            // logger.debug("Response: {}", response);
        } catch (IOException e) {
            logger.debug("IOException: {}", e.getMessage());
        }
    }

    private void removeThings() {
        logger.info("Remove {} STATE_UPDATER things", numStateUpdaterThings);
        for (int i = 1; i <= numStateUpdaterThings; i++) {
            removeThing(formatThingUID(THING_TYPE_STATE_UPDATER, "update", i));
        }
        logger.info("Remove {} COMMAND_GENERATOR things", numCommandGeneratorThings);
        for (int i = 1; i <= numCommandGeneratorThings; i++) {
            removeThing(formatThingUID(THING_TYPE_COMMAND_GENERATOR, "generator", i));
        }
        logger.info("Remove {} THING_UPDATER things", numThingUpdaterThings);
        for (int i = 1; i <= numThingUpdaterThings; i++) {
            removeThing(formatThingUID(THING_TYPE_THING_UPDATER, "update", i));
        }
    }

    private void removeThing(String s) {
        String url = "http://test.md.hilbush.com:8080/rest/things/" + s;
        try {
            logger.debug("Remove thing with {}", s);
            ByteArrayInputStream content = new ByteArrayInputStream("".getBytes());
            String response = HttpUtil.executeUrl("DELETE", url, content, "application/json", 2000);
            // logger.debug("Response: {}", response);
        } catch (IOException e) {
            logger.debug("IOException: {}", e.getMessage());
        }
    }

    private String formatThingUID(ThingTypeUID thingTypeUID, String thing, int number) {
        return String.format("%s:%s%02d", thingTypeUID.toString(), thing, number);
    }

    private String formatThingTypeUID(ThingTypeUID thingTypeUID) {
        return thingTypeUID.toString();
    }

    private String formatThingLabel(String labelFragment, int number) {
        return labelFragment + String.format(" %02d", number);
    }

    private String formatItemName(int number) {
        return String.format("\"itemName\": \"CG%02d_Command\"", number);
    }

    private String formatThingUpdaterConfiguration() {
        return String.format("\"delayBetweenUpdates\": %5d", thingUpdaterDelayBetweenUpdates);
    }

    private String formatStateUpdaterConfiguration() {
        return String.format("\"delayBetweenUpdates\": %5d, \"numberOfChannels\": %2d", stateUpdaterDelayBetweenUpdates,
                stateUpdaterNumberOfChannels);
    }

    private String getID() {
        String parts[] = getThing().getUID().toString().split(":");
        return parts[parts.length - 1];
    }
}
