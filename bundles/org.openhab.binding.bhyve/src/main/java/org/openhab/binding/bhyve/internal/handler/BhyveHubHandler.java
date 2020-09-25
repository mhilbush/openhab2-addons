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
package org.openhab.binding.bhyve.internal.handler;

import java.net.URISyntaxException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.openhab.binding.bhyve.internal.config.BhyveHubConfig;
import org.openhab.binding.bhyve.internal.dto.ResponseDeviceDTO;
import org.openhab.core.i18n.TimeZoneProvider;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;

/**
 * The {@link BhyveHubHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class BhyveHubHandler extends BhyveAbstractThingHandler {
    // Thing config parameters
    private @NonNullByDefault({}) BhyveHubConfig config;

    public BhyveHubHandler(Thing thing, WebSocketClient webSocketClient, TimeZoneProvider timeZoneProvider)
            throws URISyntaxException {
        super(thing, webSocketClient, timeZoneProvider);
    }

    @Override
    public void initialize() {
        logger.debug("Hub: Initializing");
        super.initialize();
        config = getConfigAs(BhyveHubConfig.class);
    }

    @Override
    public void dispose() {
        logger.debug("Hub: Disposing");
        super.dispose();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            return;
        }
        switch (channelUID.getId()) {
            default:
                logger.debug("Hub: Can't handle command '{}' on channel {}", command, channelUID.getId());
                break;
        }
    }

    /*
     * Requests sent to the online service
     */
    // None

    @Override
    public @Nullable String getDeviceId() {
        return config.deviceId;
    }

    @Override
    public void handleDeviceUpdate(ResponseDeviceDTO device) {
        logger.debug("Hub: Handle device update for {}", device.id);
    }

    /*
     * Top level handler for received events
     */
    @Override
    public void handleEvent(String eventId, String message) {
        switch (eventId) {
            default:
                logger.debug("Hub: Received unknown event {}: {}", eventId, message);
                break;
        }
    }

    /*
     * Events received from the server
     */
    // None
}
