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

import static org.openhab.binding.bhyve.internal.BhyveBindingConstants.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.bhyve.internal.config.BhyveBridgeConfig;
import org.openhab.binding.bhyve.internal.discovery.BhyveDevice;
import org.openhab.binding.bhyve.internal.discovery.BhyveDiscoveryService;
import org.openhab.binding.bhyve.internal.dto.ResponseDeviceDTO;
import org.openhab.binding.bhyve.internal.dto.ResponseLoginDTO;
import org.openhab.binding.bhyve.internal.dto.ResponseSprinklerTimerProgramsDTO;
import org.openhab.binding.bhyve.internal.dto.request.RequestLoginDTO;
import org.openhab.core.io.net.http.HttpUtil;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * The {@link BhyveBridgeHandler} ...
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class BhyveBridgeHandler extends BaseBridgeHandler {
    private static final long LOGIN_DELAY_SECONDS = 6L;
    private static final long RELOGIN_DELAY_SECONDS = 60L;
    private static final int REQUEST_TIMEOUT_MSEC = 15000;
    private static final int REFRESH_INITIAL_DELAY_SECONDS = 4;

    private final Logger logger = LoggerFactory.getLogger(BhyveBridgeHandler.class);

    private final Gson gson = new Gson();

    // Thing config parameters
    private @NonNullByDefault({}) BhyveBridgeConfig config;

    private @Nullable Future<?> refreshJob;
    private int refreshInterval;

    private @Nullable BhyveDiscoveryService discoveryService;

    // Token returned after a successful login and used in subsequent API calls
    private @Nullable String sessionToken;

    // User id returned after a successful login
    private @Nullable String userId;

    private @Nullable ScheduledFuture<?> loginJob;

    // Cache the response from the devices query
    private final Map<String, ResponseDeviceDTO> devicesCache = new ConcurrentHashMap<>();

    // Maintain mapping of handler and device id
    private final Map<String, BhyveAbstractThingHandler> handlers = new ConcurrentHashMap<>();

    public BhyveBridgeHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void initialize() {
        logger.debug("Bridge: Initializing bhyve bridge handler");
        config = getConfigAs(BhyveBridgeConfig.class);
        refreshInterval = getRefreshDevicesIntervalSeconds();
        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE, "Awaiting login to service");
        scheduleLoginJob(LOGIN_DELAY_SECONDS);
    }

    @Override
    public void dispose() {
        logger.debug("Bridge: Disposing bhyve bridge handler");
        cancelLoginJob();
        cancelRefreshDevicesJob();
        updateStatus(ThingStatus.OFFLINE);
    }

    /*
     * Keep track of the handlers so that we can send them updates
     */
    @Override
    public void childHandlerInitialized(ThingHandler childHandler, Thing childThing) {
        logger.debug("Bridge: Child handler initialized for thing {}", childThing.getUID());
        BhyveAbstractThingHandler handler = (BhyveAbstractThingHandler) childHandler;
        String deviceId = (String) childThing.getConfiguration().get(CONFIG_DEVICE_ID);
        logger.debug("Bridge: Saving handler with device id {}", deviceId);
        handlers.put(deviceId, handler);
    }

    @Override
    public void childHandlerDisposed(ThingHandler childHandler, Thing childThing) {
        logger.debug("Bridge: Child handler disposed for thing {}", childThing.getUID());
        String deviceId = (String) childThing.getConfiguration().get(CONFIG_DEVICE_ID);
        handlers.remove(deviceId);
    }

    /**
     * Called by the discovery service to request device discovery
     */
    public void discoverDevices() {
        // TODO: Launch a thread to do this
        refreshDevices();
        List<ResponseDeviceDTO> devices = new ArrayList<>(devicesCache.values());
        for (ResponseDeviceDTO device : devices) {
            if (discoveryService != null) {
                discoveryService.addDevice(new BhyveDevice(device.type, device.id, device.name, device.macAddress));
            }
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // Bridge doesn't support any commands
    }

    @Nullable
    public ResponseDeviceDTO getDevice(String id) {
        return devicesCache.get(id);
    }

    public void setDiscoveryService(BhyveDiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    public void unsetDiscoveryService() {
        this.discoveryService = null;
    }

    // Callback used by EventListener to get session token
    @Nullable
    public String getSessionToken() {
        return sessionToken;
    }

    public boolean isLoggedIn() {
        return sessionToken != null ? true : false;
    }

    public void updateThingStatus(@Nullable String errorDetail, String statusDescription) {
        logger.debug("Bridge: Login FAILED. Setting bridge OFFLINE: {}", errorDetail);
        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.COMMUNICATION_ERROR, statusDescription);
    }

    private synchronized boolean refreshDevices() {
        String response = null;
        try {
            logger.debug("Bridge: Sending DEVICES request: {}", URL_DEVICES + userId);
            Properties headers = new Properties();
            headers.put("orbit-api-key", getSessionToken());
            headers.put("orbit-app-id", "Orbit Support Dashboard");
            response = HttpUtil.executeUrl("GET", URL_DEVICES + userId, headers, null, null, REQUEST_TIMEOUT_MSEC);
            logger.trace("Bridge: Response = {}", response);
        } catch (IOException e) {
            // executeUrl throws IOException when it gets a Not Authorized (401) response
            logger.debug("Bridge: Got IOException: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            logger.debug("Bridge: Got IllegalArgumentException: {}", e.getMessage());
            return false;
        }
        ResponseDeviceDTO[] devicesArray;
        try {
            // Fill the array with the devices from the response
            devicesArray = gson.fromJson(response, ResponseDeviceDTO[].class);
            logger.debug("Bridge: Number of devices: {}", devicesArray.length);
            for (ResponseDeviceDTO device : devicesArray) {
                logger.debug("Bridge: Adding device to cache with device id {}", device.id);
                devicesCache.put(device.id, device);
            }
        } catch (JsonSyntaxException e) {
            logger.debug("Bridge: Got JsonSyntaxException: {}", e.getMessage());
            return false;
        }
        // Inform the handlers so they can process the device information
        devicesCache.forEach((key, device) -> {
            BhyveAbstractThingHandler handler = handlers.get(device.id);
            if (handler != null) {
                logger.debug("Bridge: Inform handler {} for device id {}", handler.getThing().getUID(), device.id);
                handler.handleDeviceUpdate(device);
            }
        });
        return true;
    }

    private void refreshPrograms() {
        devicesCache.forEach((key, device) -> {
            BhyveAbstractThingHandler handler = handlers.get(device.id);
            if (handler instanceof BhyveSprinklerTimerHandler) {
                String response = null;
                try {
                    logger.debug("Bridge: Sending SPRINKLER_TIMER_PROGRAMS request: {}", URL_PROGRAMS + device.id);
                    Properties headers = new Properties();
                    headers.put("orbit-api-key", getSessionToken());
                    headers.put("orbit-app-id", "Orbit Support Dashboard");
                    response = HttpUtil.executeUrl("GET", URL_PROGRAMS + device.id, headers, null, null,
                            REQUEST_TIMEOUT_MSEC);
                    logger.trace("Bridge: Response = {}", response);
                } catch (IOException e) {
                    // executeUrl throws IOException when it gets a Not Authorized (401) response
                    logger.debug("Bridge: Got IOException: {}", e.getMessage());
                    return;
                } catch (IllegalArgumentException e) {
                    logger.debug("Bridge: Got IllegalArgumentException: {}", e.getMessage());
                    return;
                }

                ResponseSprinklerTimerProgramsDTO[] programsArray;
                try {
                    // Fill the array with the devices from the response
                    programsArray = gson.fromJson(response, ResponseSprinklerTimerProgramsDTO[].class);
                    logger.debug("Bridge: Number of programs: {}", programsArray.length);
                    if (programsArray.length == 0) {
                        return;
                    }
                } catch (JsonSyntaxException e) {
                    logger.debug("Bridge: Got JsonSyntaxException: {}", e.getMessage());
                    return;
                }
                logger.debug("Bridge: Inform handler {} for device id {}", handler.getThing().getUID(), device.id);
                ((BhyveSprinklerTimerHandler) handler).handleProgramUpdate(programsArray[0]);
                return;
            }
        });
    }

    private void scheduleLoginJob(long delay) {
        if (loginJob == null) {
            logger.debug("Bridge: Scheduling LOGIN job in {} seconds", delay);
            loginJob = scheduler.schedule(() -> {
                logger.debug("Bridge: Logging into bhyve online service");
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE, "Logging in to service");
                String loginResponse = null;
                try {
                    Properties headers = new Properties();
                    headers.setProperty("Content-Type", "application/json; charset=UTF-8");
                    headers.put("Accept", "application/json");
                    headers.put("Accept-Encoding", "gzip, deflate, br");
                    headers.put("Accept-Language", "en-US,en;q=0.9,de;q=0.8");
                    headers.put("DNT", "1");
                    headers.put("Host", "api.orbitbhyve.com");
                    headers.put("orbit-api-key", "null");
                    headers.put("orbit-app-id", "Orbit Support Dashboard");

                    RequestLoginDTO loginRequest = new RequestLoginDTO();
                    loginRequest.session.email = config.userId;
                    loginRequest.session.password = config.password;
                    String loginRequestData = gson.toJson(loginRequest);
                    InputStream content = new ByteArrayInputStream(loginRequestData.getBytes(Charset.forName("UTF-8")));

                    logger.debug("Bridge: Sending LOGIN request");
                    loginResponse = HttpUtil.executeUrl("POST", URL_LOGIN, headers, content, null,
                            REQUEST_TIMEOUT_MSEC);
                    logger.debug("Bridge: Response = {}", loginResponse);
                } catch (IOException e) {
                    // executeUrl throws IOException when it gets a Not Authorized (401) response
                    logger.debug("Bridge: Got IOException: {}", e.getMessage());
                    updateThingStatus(e.getMessage(), "Unable to login");
                    scheduleLoginJob(RELOGIN_DELAY_SECONDS);
                    return;
                } catch (IllegalArgumentException e) {
                    logger.debug("Bridge: Got IllegalArgumentException: {}", e.getMessage());
                    updateThingStatus(e.getMessage(), "Unable to login");
                    scheduleLoginJob(RELOGIN_DELAY_SECONDS);
                    return;
                }
                try {
                    // Got a response
                    ResponseLoginDTO login = gson.fromJson(loginResponse, ResponseLoginDTO.class);
                    sessionToken = login.orbitApiKey;
                    userId = login.userId;
                } catch (JsonSyntaxException e) {
                    logger.debug("Bridge: Got JsonSyntaxException: {}", e.getMessage());
                    updateThingStatus(e.getMessage(), "Error parsing json response");
                    scheduleLoginJob(RELOGIN_DELAY_SECONDS);
                    return;
                }
                scheduleRefreshDevicesJob();
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE,
                        "Awaiting information about devices");
                loginJob = null;
            }, delay, TimeUnit.SECONDS);
        }
    }

    private void cancelLoginJob() {
        ScheduledFuture<?> localLoginJob = loginJob;
        if (localLoginJob != null) {
            logger.debug("Bridge: Canceling LOGIN job");
            localLoginJob.cancel(true);
            loginJob = null;
        }
    }

    /*
     * The refresh job updates the device information
     */
    private void scheduleRefreshDevicesJob() {
        logger.debug("Handler: Scheduling DEVICES refresh job in {} seconds", REFRESH_INITIAL_DELAY_SECONDS);
        cancelRefreshDevicesJob();
        refreshJob = scheduler.scheduleWithFixedDelay(() -> {
            logger.debug("Bridge: Refreshing devices and programs information");
            if (refreshDevices()) {
                refreshPrograms();
                if (getThing().getStatus() != ThingStatus.ONLINE) {
                    logger.debug("Bridge: Device refresh successful. Marking bridge ONLINE!");
                    updateStatus(ThingStatus.ONLINE);
                }
            }
        }, REFRESH_INITIAL_DELAY_SECONDS, refreshInterval, TimeUnit.SECONDS);
    }

    private void cancelRefreshDevicesJob() {
        Future<?> localRefreshJob = refreshJob;
        if (localRefreshJob != null) {
            logger.debug("Handler: Canceling DEVICES refresh job");
            localRefreshJob.cancel(true);
            refreshJob = null;
        }
    }

    private int getRefreshDevicesIntervalSeconds() {
        return config.refreshInterval * 60;
    }
}
