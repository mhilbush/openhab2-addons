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
package org.openhab.binding.bhyve.internal.handler;

import static org.openhab.binding.bhyve.internal.BhyveBindingConstants.URL_EVENTS;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.openhab.binding.bhyve.internal.dto.request.EventSubscribeRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * The {@link EventListener} is responsible for establishing
 * a websocket connection with events API.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class EventListener {
    private static final int PING_DELAY_SECONDS = 20;
    private static final int PING_INTERVAL_SECONDS = 25;
    private static final int RECONNECT_DELAY_SECONDS = 5;
    private static final int RECONNECT_INTERVAL_SECONDS = 10800;

    private final Logger logger = LoggerFactory.getLogger(EventListener.class);

    private final Gson gson = new Gson();
    private final JsonParser parser = new JsonParser();

    // Identifies if connected to the events API
    private boolean connected;

    private BhyveAbstractThingHandler handler;

    private ScheduledExecutorService scheduler;
    private WebSocketClient webSocketClient;
    private BhyveWebSocketListener socket = new BhyveWebSocketListener();
    private ClientUpgradeRequest request = new ClientUpgradeRequest();

    private @Nullable ScheduledFuture<?> pingJob;
    private @Nullable ScheduledFuture<?> reconnectJob;

    private @Nullable String sessionToken;
    private @Nullable String deviceId;
    private @Nullable URI eventsUri;
    private @Nullable Session session;

    public EventListener(BhyveAbstractThingHandler handler, WebSocketClient webSocketClient,
            ScheduledExecutorService scheduler) throws URISyntaxException {
        this.handler = handler;
        this.webSocketClient = webSocketClient;
        this.scheduler = scheduler;
        this.eventsUri = new URI(URL_EVENTS);
        logger.debug("Listener: Event listener created");
    }

    public void start() {
        logger.debug("Listener: Event listener starting");
        scheduleReconnectJob();
    }

    /*
     * Stop the event listener
     */
    public void stop() {
        logger.debug("Listener: Event listener stopping");
        cancelReconnectJob();
        cancelPingJob();
        disconnectFromService();
    }

    /*
     * Initiate the connection
     */
    private synchronized void connectToService() {
        deviceId = handler.getDeviceId();
        sessionToken = handler.getSessionToken();
        logger.debug("Listener: Websocket connecting to service for device {}", deviceId);
        try {
            webSocketClient.connect(socket, eventsUri, request);
        } catch (IOException e) {
            logger.debug("Listener: IOException getting websocket connection: {}", e.getMessage());
        }
    }

    /*
     * Initiate a disconnect
     */
    private void disconnectFromService() {
        Session localSession = session;
        if (localSession != null) {
            logger.debug("Listener: Closing websocket session to disconnect from event service");
            localSession.close();
            handler.markOffline("No event listener connection");
        }
    }

    @NonNullByDefault
    @WebSocket
    public class BhyveWebSocketListener {
        @OnWebSocketConnect
        public void onConnect(Session wssession) {
            logger.debug("Listener: Websocket session successfully connected");
            session = wssession;
            connected = true;
            handler.markOnline();
            requestSubscribe();
            startPingJob();
        }

        @OnWebSocketMessage
        public void onMessage(String message) {
            logger.trace("Listener: Message received from server: {}", message);
            final JsonObject json = parser.parse(message).getAsJsonObject();
            if (json.has("event")) {
                String eventId = json.get("event").getAsString();
                logger.debug("Listener: Message is an event of type '{}'", eventId);
                handler.handleEvent(eventId, message);
            }
        }

        @OnWebSocketClose
        public void onClose(int statusCode, String reason) {
            logger.debug("Listener: Websocket closing with statusCode {} due to {}", statusCode, reason);
            session = null;
            connected = false;
            cancelPingJob();
        }

        @OnWebSocketError
        public void onError(Throwable cause) {
            logger.warn("Listener: Websocket received error: {}", cause.getMessage());
            cancelReconnectJob();
            scheduleReconnectJob();
        }
    }

    /*
     * Subscribe to events for a deviceId
     */
    private void requestSubscribe() {
        try {
            logger.debug("Listener: Sending SUBSCRIBE event");
            EventSubscribeRequestDTO subscribe = new EventSubscribeRequestDTO();
            subscribe.event = "app_connection";
            subscribe.orbitSessionToken = sessionToken;
            subscribe.subscribeDeviceId = deviceId;
            String subscribeData = gson.toJson(subscribe);
            sendMessage(subscribeData);
        } catch (IOException e) {
            logger.debug("Listener: IOException sending message: {}", e.getMessage());
        }
    }

    /*
     * Send a message
     */
    public void sendMessage(String message) throws IOException {
        logger.debug("Listener: Send message: {}", message);
        sendMessageToService(message);
    }

    /*
     * Send a message to the service
     */
    public void sendMessageToService(String message) throws IOException {
        if (session != null && connected) {
            session.getRemote().sendString(message);
        } else {
            throw new IOException("socket not initialized");
        }
    }

    /*
     * Reconnect to the event listener service periodically
     */
    private Runnable reconnect = new Runnable() {
        @Override
        public void run() {
            disconnectFromService();
            connectToService();
        }
    };

    private void scheduleReconnectJob() {
        if (reconnectJob == null) {
            logger.debug("Listener: Starting reconnect job in {} seconds", RECONNECT_DELAY_SECONDS);
            reconnectJob = scheduler.scheduleWithFixedDelay(reconnect, RECONNECT_DELAY_SECONDS,
                    RECONNECT_INTERVAL_SECONDS, TimeUnit.SECONDS);
        }
    }

    private void cancelReconnectJob() {
        if (reconnectJob != null) {
            reconnectJob.cancel(true);
            reconnectJob = null;
            logger.debug("Listener: Canceling reconnect job");
        }
    }

    /*
     * Ping the service periodically to keep the connection open
     */
    private Runnable pingJobRunnable = new Runnable() {
        @Override
        public void run() {
            final String pingMessage = "{\"event\":\"ping\"}";
            try {
                sendMessageToService(pingMessage);
            } catch (IOException e) {
                logger.debug("Listener: IOException sending message: {}", e.getMessage());
            }
        }
    };

    private void startPingJob() {
        if (pingJob == null) {
            logger.debug("Listener: Starting ping job in {} seconds", PING_DELAY_SECONDS);
            pingJob = scheduler.scheduleWithFixedDelay(pingJobRunnable, PING_DELAY_SECONDS, PING_INTERVAL_SECONDS,
                    TimeUnit.SECONDS);
        }
    }

    private void cancelPingJob() {
        if (pingJob != null) {
            pingJob.cancel(true);
            pingJob = null;
            logger.debug("Listener: Canceling ping job");
        }
    }
}
