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
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link geosphereATBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Armin Berger - Initial contribution
 */
@NonNullByDefault
public class geosphereATBindingConstants {

    public static final String BINDING_ID = "geosphereat";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_WEATHERSTATION_CURRENT = new ThingTypeUID(BINDING_ID,
            "weatherstation_current");
    public static final ThingTypeUID THING_TYPE_WEATHERSTATION_HISTORIC = new ThingTypeUID(BINDING_ID,
            "weatherstation_historic");
    public static final ThingTypeUID THING_TYPE_WEATHERLOCATION_FORECAST = new ThingTypeUID(BINDING_ID,
            "weatherlocation_forecast");

    // List of all Channel ids
    public static final String CHANNEL_CURRENT_TEMPERATURE = "current_temperature";
    public static final String CHANNEL_CURRENT_PRECIPITATION = "current_precipitation";
    public static final String CHANNEL_CURRENT_PRESSURE = "current_pressure";
    public static final String CHANNEL_CURRENT_HUMIDITY = "current_humidity";
    public static final String CHANNEL_CURRENT_SUNSHINE = "current_sunshine";

    public static final String CHANNEL_LAST24H_TEMPERATURE_MIN = "last24h_temperature_min";
    public static final String CHANNEL_LAST24H_TEMPERATURE_MAX = "last24h_temperature_max";
    public static final String CHANNEL_LAST24H_TEMPERATURE_AVG = "last24h_temperature_avg";
    public static final String CHANNEL_LAST24H_PRECIPITATION_ACC = "last24h_precipitation_acc";
    public static final String CHANNEL_LAST24H_PRESSURE_MIN = "last24h_pressure_min";
    public static final String CHANNEL_LAST24H_PRESSURE_MAX = "last24h_pressure_max";
    public static final String CHANNEL_LAST24H_PRESSURE_AVG = "last24h_pressure_avg";
    public static final String CHANNEL_LAST24H_HUMIDITY_MIN = "last24h_humidity_min";
    public static final String CHANNEL_LAST24H_HUMIDITY_MAX = "last24h_humidity_max";
    public static final String CHANNEL_LAST24H_HUMIDITY_AVG = "last24h_humidity_avg";
    public static final String CHANNEL_LAST24H_SUNSHINE_ACC = "last24h_sunshine_acc";

    public static final String CHANNEL_FORECAST24H_TEMPERATURE_MIN = "forecast24h_temperature_min";
    public static final String CHANNEL_FORECAST24H_TEMPERATURE_MAX = "forecast24h_temperature_max";
    public static final String CHANNEL_FORECAST24H_TEMPERATURE_AVG = "forecast24h_temperature_avg";
    public static final String CHANNEL_FORECAST24H_PRECIPITATION_ACC = "forecast24h_precipitation_acc";
    public static final String CHANNEL_FORECAST24H_PRESSURE_MIN = "forecast24h_pressure_min";
    public static final String CHANNEL_FORECAST24H_PRESSURE_MAX = "forecast24h_pressure_max";
    public static final String CHANNEL_FORECAST24H_PRESSURE_AVG = "forecast24h_pressure_avg";
    public static final String CHANNEL_FORECAST24H_HUMIDITY_MIN = "forecast24h_humidity_min";
    public static final String CHANNEL_FORECAST24H_HUMIDITY_MAX = "forecast24h_humidity_max";
    public static final String CHANNEL_FORECAST24H_HUMIDITY_AVG = "forecast24h_humidity_avg";
    public static final String CHANNEL_FORECAST24H_SUNSHINE_ACC = "forecast24h_sunshine_acc";

    public static final String CHANNEL_LAST12H_TEMPERATURE_MIN = "last12h_temperature_min";
    public static final String CHANNEL_LAST12H_TEMPERATURE_MAX = "last12h_temperature_max";
    public static final String CHANNEL_LAST12H_TEMPERATURE_AVG = "last12h_temperature_avg";
    public static final String CHANNEL_LAST12H_PRECIPITATION_ACC = "last12h_precipitation_acc";
    public static final String CHANNEL_LAST12H_PRESSURE_MIN = "last12h_pressure_min";
    public static final String CHANNEL_LAST12H_PRESSURE_MAX = "last12h_pressure_max";
    public static final String CHANNEL_LAST12H_PRESSURE_AVG = "last12h_pressure_avg";
    public static final String CHANNEL_LAST12H_HUMIDITY_MIN = "last12h_humidity_min";
    public static final String CHANNEL_LAST12H_HUMIDITY_MAX = "last12h_humidity_max";
    public static final String CHANNEL_LAST12H_HUMIDITY_AVG = "last12h_humidity_avg";
    public static final String CHANNEL_LAST12H_SUNSHINE_ACC = "last12h_sunshine_acc";

    public static final String CHANNEL_FORECAST12H_TEMPERATURE_MIN = "forecast12h_temperature_min";
    public static final String CHANNEL_FORECAST12H_TEMPERATURE_MAX = "forecast12h_temperature_max";
    public static final String CHANNEL_FORECAST12H_TEMPERATURE_AVG = "forecast12h_temperature_avg";
    public static final String CHANNEL_FORECAST12H_PRECIPITATION_ACC = "forecast12h_precipitation_acc";
    public static final String CHANNEL_FORECAST12H_PRESSURE_MIN = "forecast12h_pressure_min";
    public static final String CHANNEL_FORECAST12H_PRESSURE_MAX = "forecast12h_pressure_max";
    public static final String CHANNEL_FORECAST12H_PRESSURE_AVG = "forecast12h_pressure_avg";
    public static final String CHANNEL_FORECAST12H_HUMIDITY_MIN = "forecast12h_humidity_min";
    public static final String CHANNEL_FORECAST12H_HUMIDITY_MAX = "forecast12h_humidity_max";
    public static final String CHANNEL_FORECAST12H_HUMIDITY_AVG = "forecast12h_humidity_avg";
    public static final String CHANNEL_FORECAST12H_SUNSHINE_ACC = "forecast12h_sunshine_acc";
}
