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
package org.openhab.binding.sunsetwx.internal.handler;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.sunsetwx.internal.config.SunsetWxAccountConfig;
import org.openhab.binding.sunsetwx.internal.discovery.SunsetWxDiscoveryService;
import org.openhab.binding.sunsetwx.internal.dto.QualityRequestDTO;
import org.openhab.core.i18n.LocaleProvider;
import org.openhab.core.i18n.LocationProvider;
import org.openhab.core.library.types.PointType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * The {@link SunsetWxAccountHandler} is responsible for managing communication with the
 * SunsetWx online service.
 *
 * @author Mark Hilbush - Initial contribution
 */
@NonNullByDefault
public class SunsetWxAccountHandler extends BaseBridgeHandler {
    private final Logger logger = LoggerFactory.getLogger(SunsetWxAccountHandler.class);

    private final Gson gson = new Gson();

    private final HttpClient httpClient;
    private final LocationProvider locationProvider;
    private final LocaleProvider localeProvider;

    private @Nullable SunsetWxAPI api;

    private boolean backgroundDiscoveryEnabled;

    public SunsetWxAccountHandler(Bridge thing, HttpClient httpClient, LocationProvider locationProvider,
            LocaleProvider localeProvider) {
        super(thing);
        this.httpClient = httpClient;
        this.locationProvider = locationProvider;
        this.localeProvider = localeProvider;
    }

    @Override
    public void initialize() {
        logger.debug("Initializing sunsetwx account handler for {}", getThing().getUID());
        SunsetWxAccountConfig config = getConfigAs(SunsetWxAccountConfig.class);
        backgroundDiscoveryEnabled = config.backgroundDiscoveryEnabled;
        logger.debug("Bridge: Background discovery is {}", backgroundDiscoveryEnabled == true ? "ENABLED" : "DISABLED");

        String emailAddress = config.emailAddress;
        String password = config.password;
        if (emailAddress != null && password != null) {
            api = new SunsetWxAPI(this, emailAddress, password);
            updateStatus(ThingStatus.ONLINE);
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "Username/password configuration missing");
        }
    }

    @Override
    public void dispose() {
        logger.debug("Disposing sunsetwx account handler for {}", getThing().getUID());
        api = null;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    @Override
    public Collection<Class<? extends ThingHandlerService>> getServices() {
        return Collections.singleton(SunsetWxDiscoveryService.class);
    }

    @Override
    public void updateState(String channelID, State state) {
        super.updateState(channelID, state);
    }

    @Override
    public void updateStatus(ThingStatus status) {
        super.updateStatus(status);
    }

    @Override
    public void updateStatus(ThingStatus status, ThingStatusDetail statusDetail, @Nullable String description) {
        super.updateStatus(status, statusDetail, description);
    }

    public Gson getGson() {
        return gson;
    }

    public @Nullable String getGeoLocation() {
        PointType location = locationProvider.getLocation();
        if (location == null) {
            return null;
        }
        return String.format("%d,%d", location.getLongitude(), location.getLatitude());
    }

    public LocaleProvider getLocaleProvider() {
        return localeProvider;
    }

    public @Nullable String getQuality(QualityRequestDTO request) {
        if (api != null) {
            return api.getSunsetWxQuality(request);
        }
        return null;
    }

    public boolean isBackgroundDiscoveryEnabled() {
        return backgroundDiscoveryEnabled;
    }
}
