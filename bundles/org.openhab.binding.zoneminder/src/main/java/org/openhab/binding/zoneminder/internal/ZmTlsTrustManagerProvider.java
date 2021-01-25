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
package org.openhab.binding.zoneminder.internal;

import javax.net.ssl.X509ExtendedTrustManager;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.zoneminder.internal.handler.ZmBridgeHandler;
import org.openhab.core.io.net.http.TlsTrustManagerProvider;
import org.openhab.core.io.net.http.TrustAllTrustManager;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a TrustManager to allow secure connections to a Zoneminder server
 * that uses a self-signed certificate.
 *
 * @author Mark Hilbush - Initial Contribution
 */
@Component
@NonNullByDefault
public class ZmTlsTrustManagerProvider implements TlsTrustManagerProvider, ThingHandlerService {

    private final Logger logger = LoggerFactory.getLogger(ZmTlsTrustManagerProvider.class);

    private @NonNullByDefault({}) ZmBridgeHandler bridgeHandler;

    // public ZmTlsTrustManagerProvider() {
    // }

    // @Override
    // public void activate() {
    // // logger.debug("ZmTlsTrustManagerProvider: In activate");
    // // super.activate(null);
    // }
    //
    // @Override
    // public void deactivate() {
    // // logger.debug("ZmTlsTrustManagerProvider: In deactivate");
    // // super.deactivate();
    // }

    @Override
    public void setThingHandler(@Nullable ThingHandler handler) {
        if (handler instanceof ZmBridgeHandler) {
            bridgeHandler = (ZmBridgeHandler) handler;
        }
        logger.debug("ZmTlsTrustManagerProvider: setThingHandler: {}", handler);
    }

    @Override
    public @Nullable ThingHandler getThingHandler() {
        return bridgeHandler;
    }

    @Override
    public String getHostName() {
        if (bridgeHandler != null) {
            return bridgeHandler.getCertificateCommonName();
        }
        logger.debug("ZmTlsTrustManagerProvider: getHostName, bridgeHandler is null!");
        return "unknown.common.name";
    }

    @Override
    public X509ExtendedTrustManager getTrustManager() {
        return TrustAllTrustManager.getInstance();
    }
}
