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

import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.BINDING_ID;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_CURRENT_HUMIDITY;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_CURRENT_PRECIPITATION;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_CURRENT_PRESSURE;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_CURRENT_SUNSHINE;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_CURRENT_TEMPERATURE;

import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_FORECAST24H_HUMIDITY_AVG;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_FORECAST24H_HUMIDITY_MAX;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_FORECAST24H_HUMIDITY_MIN;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_FORECAST24H_PRECIPITATION_ACC;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_FORECAST24H_PRESSURE_AVG;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_FORECAST24H_PRESSURE_MAX;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_FORECAST24H_PRESSURE_MIN;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_FORECAST24H_SUNSHINE_ACC;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_FORECAST24H_TEMPERATURE_AVG;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_FORECAST24H_TEMPERATURE_MAX;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_FORECAST24H_TEMPERATURE_MIN;

import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_LAST24H_HUMIDITY_AVG;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_LAST24H_HUMIDITY_MAX;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_LAST24H_HUMIDITY_MIN;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_LAST24H_PRECIPITATION_ACC;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_LAST24H_PRESSURE_AVG;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_LAST24H_PRESSURE_MAX;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_LAST24H_PRESSURE_MIN;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_LAST24H_SUNSHINE_ACC;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_LAST24H_TEMPERATURE_AVG;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_LAST24H_TEMPERATURE_MAX;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_LAST24H_TEMPERATURE_MIN;

import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_FORECAST12H_HUMIDITY_AVG;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_FORECAST12H_HUMIDITY_MAX;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_FORECAST12H_HUMIDITY_MIN;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_FORECAST12H_PRECIPITATION_ACC;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_FORECAST12H_PRESSURE_AVG;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_FORECAST12H_PRESSURE_MAX;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_FORECAST12H_PRESSURE_MIN;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_FORECAST12H_SUNSHINE_ACC;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_FORECAST12H_TEMPERATURE_AVG;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_FORECAST12H_TEMPERATURE_MAX;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_FORECAST12H_TEMPERATURE_MIN;

import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_LAST12H_HUMIDITY_AVG;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_LAST12H_HUMIDITY_MAX;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_LAST12H_HUMIDITY_MIN;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_LAST12H_PRECIPITATION_ACC;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_LAST12H_PRESSURE_AVG;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_LAST12H_PRESSURE_MAX;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_LAST12H_PRESSURE_MIN;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_LAST12H_SUNSHINE_ACC;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_LAST12H_TEMPERATURE_AVG;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_LAST12H_TEMPERATURE_MAX;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.CHANNEL_LAST12H_TEMPERATURE_MIN;

import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.THING_TYPE_WEATHERLOCATION_FORECAST;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.THING_TYPE_WEATHERSTATION_CURRENT;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.THING_TYPE_WEATHERSTATION_HISTORIC;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.openhab.core.types.State;
import org.openhab.core.types.UnDefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link geosphereATHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Armin Berger - Initial contribution
 */
@NonNullByDefault
public class geosphereATHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(geosphereATHandler.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private @Nullable ScheduledFuture<?> refreshJobCurrent;

    private @Nullable geosphereATConfiguration config;

    public geosphereATHandler(Thing thing) {
        super(thing);
    }

    private void startAutomaticRefresh() {
        refreshJobCurrent = scheduler.scheduleWithFixedDelay(this::updateWeatherData, 0, config.refreshInterval,
                TimeUnit.SECONDS);
    }

    private void updateWeatherData() {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (THING_TYPE_WEATHERSTATION_CURRENT.equals(thingTypeUID)) {
            updateCurrentWeatherData();
        } else if (THING_TYPE_WEATHERSTATION_HISTORIC.equals(thingTypeUID)) {
            updateHistoricWeatherData();
        } else if (THING_TYPE_WEATHERLOCATION_FORECAST.equals(thingTypeUID)) {
            updateForecastWeatherData();
        }
    }

    private void updateCurrentWeatherData() {
        Map<String, Number> data = geosphereATAPI.getCurrentWeather(config.stationName);
        State state = UnDefType.UNDEF;
        @Nullable
        Number value;

        if (data != null) {
            value = data.get("TL");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_CURRENT_TEMPERATURE, state);
        if (data != null) {
            value = data.get("RF");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_CURRENT_HUMIDITY, state);
        if (data != null) {
            value = data.get("P");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_CURRENT_PRESSURE, state);
        if (data != null) {
            value = data.get("RR");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_CURRENT_PRECIPITATION, state);
        if (data != null) {
            value = data.get("SO");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_CURRENT_SUNSHINE, state);
    }

    private void updateHistoricWeatherData() {
        Map<String, Number> data = geosphereATAPI.getHistoricWeather(config.stationName);
        State state = UnDefType.UNDEF;
        @Nullable
        Number value;

        if (data != null) {
            value = data.get("TL_MIN");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_LAST24H_TEMPERATURE_MIN, state);
        if (data != null) {
            value = data.get("TL_MAX");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_LAST24H_TEMPERATURE_MAX, state);
        if (data != null) {
            value = data.get("TL_AVG");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_LAST24H_TEMPERATURE_AVG, state);
        if (data != null) {
            value = data.get("RF_MIN");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_LAST24H_HUMIDITY_MIN, state);
        if (data != null) {
            value = data.get("RF_MAX");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_LAST24H_HUMIDITY_MAX, state);
        if (data != null) {
            value = data.get("RF_AVG");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_LAST24H_HUMIDITY_AVG, state);
        if (data != null) {
            value = data.get("P_MIN");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_LAST24H_PRESSURE_MIN, state);
        if (data != null) {
            value = data.get("P_MAX");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_LAST24H_PRESSURE_MAX, state);
        if (data != null) {
            value = data.get("P_AVG");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_LAST24H_PRESSURE_AVG, state);
        if (data != null) {
            value = data.get("RR_ACC");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_LAST24H_PRECIPITATION_ACC, state);
        if (data != null) {
            value = data.get("SO_ACC");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_LAST24H_SUNSHINE_ACC, state);

        if (data != null) {
            value = data.get("TL_MIN_12H");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_LAST12H_TEMPERATURE_MIN, state);
        if (data != null) {
            value = data.get("TL_MAX_12H");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_LAST12H_TEMPERATURE_MAX, state);
        if (data != null) {
            value = data.get("TL_AVG_12H");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_LAST12H_TEMPERATURE_AVG, state);
        if (data != null) {
            value = data.get("RF_MIN_12H");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_LAST12H_HUMIDITY_MIN, state);
        if (data != null) {
            value = data.get("RF_MAX_12H");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_LAST12H_HUMIDITY_MAX, state);
        if (data != null) {
            value = data.get("RF_AVG_12H");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_LAST12H_HUMIDITY_AVG, state);
        if (data != null) {
            value = data.get("P_MIN_12H");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_LAST12H_PRESSURE_MIN, state);
        if (data != null) {
            value = data.get("P_MAX_12H");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_LAST12H_PRESSURE_MAX, state);
        if (data != null) {
            value = data.get("P_AVG_12H");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_LAST12H_PRESSURE_AVG, state);
        if (data != null) {
            value = data.get("RR_ACC_12H");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_LAST12H_PRECIPITATION_ACC, state);
        if (data != null) {
            value = data.get("SO_ACC_12H");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_LAST12H_SUNSHINE_ACC, state);
    }

    private void updateForecastWeatherData() {
        Map<String, Number> data = geosphereATAPI.getForecastWeather(config.locationLatLon);
        State state = UnDefType.UNDEF;
        @Nullable
        Number value;

        if (data != null) {
            value = data.get("t2m_MIN");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_FORECAST24H_TEMPERATURE_MIN, state);
        if (data != null) {
            value = data.get("t2m_MAX");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_FORECAST24H_TEMPERATURE_MAX, state);
        if (data != null) {
            value = data.get("t2m_AVG");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_FORECAST24H_TEMPERATURE_AVG, state);
        if (data != null) {
            value = data.get("rh2m_MIN");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_FORECAST24H_HUMIDITY_MIN, state);
        if (data != null) {
            value = data.get("rh2m_MAX");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_FORECAST24H_HUMIDITY_MAX, state);
        if (data != null) {
            value = data.get("rh2m_AVG");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_FORECAST24H_HUMIDITY_AVG, state);
        if (data != null) {
            value = data.get("sp_MIN");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_FORECAST24H_PRESSURE_MIN, state);
        if (data != null) {
            value = data.get("sp_MAX");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_FORECAST24H_PRESSURE_MAX, state);
        if (data != null) {
            value = data.get("sp_AVG");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_FORECAST24H_PRESSURE_AVG, state);
        if (data != null) {
            value = data.get("rain_acc_ACC");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_FORECAST24H_PRECIPITATION_ACC, state);
        state = UnDefType.UNDEF;
        updateState(CHANNEL_FORECAST24H_SUNSHINE_ACC, state);

        if (data != null) {
            value = data.get("t2m_MIN_12H");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_FORECAST12H_TEMPERATURE_MIN, state);
        if (data != null) {
            value = data.get("t2m_MAX_12H");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_FORECAST12H_TEMPERATURE_MAX, state);
        if (data != null) {
            value = data.get("t2m_AVG_12H");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_FORECAST12H_TEMPERATURE_AVG, state);
        if (data != null) {
            value = data.get("rh2m_MIN_12H");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_FORECAST12H_HUMIDITY_MIN, state);
        if (data != null) {
            value = data.get("rh2m_MAX_12H");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_FORECAST12H_HUMIDITY_MAX, state);
        if (data != null) {
            value = data.get("rh2m_AVG_12H");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_FORECAST12H_HUMIDITY_AVG, state);
        if (data != null) {
            value = data.get("sp_MIN_12H");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_FORECAST12H_PRESSURE_MIN, state);
        if (data != null) {
            value = data.get("sp_MAX_12H");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_FORECAST12H_PRESSURE_MAX, state);
        if (data != null) {
            value = data.get("sp_AVG_12H");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_FORECAST12H_PRESSURE_AVG, state);
        if (data != null) {
            value = data.get("rain_acc_ACC_12H");
            state = new DecimalType(value.doubleValue());
        }
        updateState(CHANNEL_FORECAST12H_PRECIPITATION_ACC, state);
        state = UnDefType.UNDEF;
        updateState(CHANNEL_FORECAST12H_SUNSHINE_ACC, state);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            updateWeatherData();
        } else {
            logger.debug("Binding {} only supports refresh command", BINDING_ID);
        }
    }

    @Override
    public void initialize() {
        config = getConfigAs(geosphereATConfiguration.class);

        updateStatus(ThingStatus.UNKNOWN);

        // Example for background initialization:
        scheduler.execute(() -> {
            boolean apiReachable = geosphereATAPI.getWeatherStations("GRAZ");

            // <background task with long running initialization here>
            // when done do:
            if (apiReachable) {
                logger.debug("successfully retrieved weather station ids");
                updateStatus(ThingStatus.ONLINE);
                startAutomaticRefresh();
            } else {
                logger.debug("failed to retrieve weather station ids");
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "unable to retrieve weather station ids");
            }
        });
    }

    @Override
    public void dispose() {
        if (refreshJobCurrent != null && !refreshJobCurrent.isCancelled()) {
            refreshJobCurrent.cancel(true);
        }
    }
}
