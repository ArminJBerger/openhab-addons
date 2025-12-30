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

import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.THING_TYPE_WEATHERLOCATION_FORECAST;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.THING_TYPE_WEATHERSTATION_CURRENT;
import static org.openhab.binding.geosphereat.internal.geosphereATBindingConstants.THING_TYPE_WEATHERSTATION_HISTORIC;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Component;

/**
 * The {@link geosphereATHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Armin Berger - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.geosphereat", service = ThingHandlerFactory.class)
public class geosphereATHandlerFactory extends BaseThingHandlerFactory {

    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Set.of(THING_TYPE_WEATHERLOCATION_FORECAST,
            THING_TYPE_WEATHERSTATION_CURRENT, THING_TYPE_WEATHERSTATION_HISTORIC);

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (THING_TYPE_WEATHERSTATION_CURRENT.equals(thingTypeUID)) {
            return new geosphereATHandler(thing);
        } else if (THING_TYPE_WEATHERSTATION_HISTORIC.equals(thingTypeUID)) {
            return new geosphereATHandler(thing);
        } else if (THING_TYPE_WEATHERLOCATION_FORECAST.equals(thingTypeUID)) {
            return new geosphereATHandler(thing);
        }

        return null;
    }
}
