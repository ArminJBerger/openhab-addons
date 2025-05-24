/**
 * Copyright (c) 2010-2025 Contributors to the openHAB project
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
package org.openhab.binding.geosphereat.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link geosphereATConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author Armin Berger - Initial contribution
 */
@NonNullByDefault
public class geosphereATConfiguration {

    /**
     * Sample configuration parameters. Replace with your own.
     */
    public int refreshInterval = 3600;
    public String stationName = "GRAZ/STRASSGANG";
    public String locationLatLon = "47.047657,15.464803";
}
