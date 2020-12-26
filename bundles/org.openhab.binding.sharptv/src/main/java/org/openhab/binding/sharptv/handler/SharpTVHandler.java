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
package org.openhab.binding.sharptv.handler;

import static org.openhab.binding.sharptv.internal.SharpTVBindingConstants.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.sharptv.internal.SharpTVConfig;
import org.openhab.binding.sharptv.internal.discovery.SharpTVDiscoveryParticipant;
import org.openhab.binding.sharptv.internal.hardware.SharpTVProxy;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.IncreaseDecreaseType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SharpTVHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class SharpTVHandler extends BaseThingHandler {
    private Logger logger = LoggerFactory.getLogger(SharpTVHandler.class);

    private @NonNullByDefault({}) SharpTVConfig config;
    private @NonNullByDefault({}) String ipAddress;
    private @NonNullByDefault({}) Integer port;
    private @NonNullByDefault({}) String user;
    private @NonNullByDefault({}) String password;

    private @NonNullByDefault({}) SharpTVProxy proxy;

    private boolean discoveryEnabled = false;

    private static final int MAX_VOLUME = 100;

    public SharpTVHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        logger.debug("SharpTvHandler: Initializing for thing {}", thingID());

        config = getConfig().as(SharpTVConfig.class);
        logger.debug("SharpTvHandler: Config is {}", config);

        if (!config.isValid()) {
            logger.debug("SharpTVHandler: Config of thing '{}' is invalid", thingID());
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Invalid SharpTV configuration.");
            return;
        }
        ipAddress = config.getIpAddress();
        port = config.getPort();
        user = config.getUser();
        password = config.getPassword();
        proxy = new SharpTVProxy(ipAddress, port, user, password);
        discoveryEnabled = config.getDiscoveryEnabled();
        logger.debug("SharpTvHandler: Discovery is {}", discoveryEnabled == true ? "ENABLED" : "DISABLED");
        updateStatus(ThingStatus.ONLINE);
    }

    @Override
    public void dispose() {
        logger.debug("SharpTvHandler: Disposing for thing {}", thingID());
    }

    @Override
    public Collection<Class<? extends ThingHandlerService>> getServices() {
        return Collections.singleton(SharpTVDiscoveryParticipant.class);
    }

    public boolean isDiscoveryEnabled() {
        return discoveryEnabled;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            return;
        }

        logger.debug("SharpTvHandler: Handle command {} on {} channel", command, channelUID);
        try {
            switch (channelUID.getId()) {
                case CHANNEL_POWER:
                    handlePower(command);
                    break;
                case CHANNEL_VOLUME:
                    handleVolume(command);
                    break;
                case CHANNEL_MUTE:
                    handleVolumeMute(command);
                    break;
                case CHANNEL_CHANNEL:
                    handleChannel(command);
                    break;
                case CHANNEL_CHANNEL_DIGITAL_AIR:
                    handleChannelDigitalAir(command);
                    break;
                case CHANNEL_CHANNEL_DIGITAL_CABLE:
                    handleChannelDigitalCable(command);
                    break;
                case CHANNEL_CHANNEL_3_DIGIT_DIRECT:
                    handleChannel3DigitDirect(command);
                    break;
                case CHANNEL_CHANNEL_4_DIGIT_DIRECT:
                    handleChannel4DigitDirect(command);
                    break;
                case CHANNEL_INPUT:
                    handleInput(command);
                    break;
                case CHANNEL_AVMODE:
                    handleAvMode(command);
                    break;
                case CHANNEL_SLEEP_TIMER:
                    handleSleepTimer(command);
                    break;
                case CHANNEL_DISABLE_ECO:
                    handleDisableEco(command);
                    break;
            }
        } catch (IOException e) {
            logger.debug("IOException handling command: {}", command);
        }
    }

    private void handlePower(Command command) throws IOException {
        logger.debug("SharpTvHandler: Handling power command: {}", command);
        if (command instanceof OnOffType) {
            if (((OnOffType) command).equals(OnOffType.ON)) {
                proxy.setPower(true);
            } else if (((OnOffType) command).equals(OnOffType.OFF)) {
                proxy.setPower(false);
            }
        }
    }

    private void handleVolume(Command command) throws IOException {
        if (command instanceof PercentType) {
            logger.debug("SharpTvHandler: Handling volume command, set volume to {}", command);
            // Set value from 0 to 100 percent
            double percent = .01f * Integer.parseInt(command.toString());
            proxy.setVolume((int) (MAX_VOLUME * percent));
        } else if (command instanceof IncreaseDecreaseType) {
            logger.debug("SharpTvHandler: Handling volume increase/decrease");
            if (((IncreaseDecreaseType) command).equals(IncreaseDecreaseType.INCREASE)) {
                proxy.volumeUp();
            } else if (((IncreaseDecreaseType) command).equals(IncreaseDecreaseType.DECREASE)) {
                proxy.volumeDown();
            }
        }
    }

    private void handleVolumeMute(Command command) throws IOException {
        if (command instanceof OnOffType) {
            logger.debug("SharpTvHandler: Handling mute command, set mute to {}", command);
            if (((OnOffType) command).equals(OnOffType.ON)) {
                proxy.setMute(true);
            } else if (((OnOffType) command).equals(OnOffType.OFF)) {
                proxy.setMute(false);
            }
        }
    }

    private void handleChannel(Command command) throws IOException {
        if (command instanceof DecimalType) {
            logger.debug("SharpTvHandler: Handling channel command, set channel to {}", command);
            proxy.setChannelAnalog(((DecimalType) command).intValue());
        } else if (command instanceof IncreaseDecreaseType) {
            logger.debug("SharpTvHandler: Handling channel increase/decrease");
            if (((IncreaseDecreaseType) command).equals(IncreaseDecreaseType.INCREASE)) {
                proxy.channelUp();
            } else if (((IncreaseDecreaseType) command).equals(IncreaseDecreaseType.DECREASE)) {
                proxy.channelDown();
            }
        }
    }

    private void handleChannelDigitalAir(Command command) throws IOException {
        if (command instanceof DecimalType) {
            logger.debug("SharpTvHandler: Handling channel digital air command, set channel to {}", command);
            proxy.setChannelDigitalAir(((DecimalType) command).intValue());
        }
    }

    private void handleChannelDigitalCable(Command command) throws IOException {
        if (command instanceof DecimalType) {
            logger.debug("SharpTvHandler: Handling channel digital cable command, set channel to {}", command);
            proxy.setChannelDigitalCableOnePart(((DecimalType) command).intValue());
        }
    }

    private void handleChannel3DigitDirect(Command command) throws IOException {
        if (command instanceof DecimalType) {
            logger.debug("SharpTvHandler: Handling channel 3 digit direct command, set channel to {}", command);
            proxy.setChannel3DigitDirect(((DecimalType) command).intValue());
        }
    }

    private void handleChannel4DigitDirect(Command command) throws IOException {
        if (command instanceof DecimalType) {
            logger.debug("SharpTvHandler: Handling channel 4 digit direct command, set channel to {}", command);
            proxy.setChannel4DigitDirect(((DecimalType) command).intValue());
        }
    }

    private void handleInput(Command command) throws IOException {
        if (command instanceof DecimalType) {
            logger.debug("SharpTvHandler: Handling input selection for input {}", command.toString());
            int input = Integer.parseInt(command.toString());
            if (input >= 0 && input <= 9) {
                proxy.setInput(input);
            }
        }
    }

    private void handleAvMode(Command command) throws IOException {
        if (command instanceof DecimalType) {
            logger.debug("SharpTvHandler: Handling av mode selection for input {}", command.toString());
            int input = Integer.parseInt(command.toString());
            if (input >= 0 && input <= 999) {
                proxy.setAvMode(input);
            }
        }
    }

    private void handleSleepTimer(Command command) throws IOException {
        if (command instanceof DecimalType) {
            logger.debug("SharpTvHandler: Handling sleep timer command {}", command.toString());
            int timer = Integer.parseInt(command.toString());
            if (timer >= 0 && timer <= 4) {
                proxy.setSleepTimer(timer);
            }
        }
    }

    private void handleDisableEco(Command command) throws IOException {
        logger.debug("SharpTvHandler: Disabling eco mode");
        proxy.disableEco();
    }

    private String thingID() {
        return thing.getUID().getId();
    }
}
