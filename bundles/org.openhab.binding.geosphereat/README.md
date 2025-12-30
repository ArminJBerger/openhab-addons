# geosphereAT Binding

Fetches weather data for Austria from geosphere.at using a REST API (see https://dataset.api.hub.geosphere.at/v1/docs/).
Provides the following data:
- current weather data from a defined weather station
- historic weather data for the past 24h from a defined weather statino
- forecast weather data for the next 24h for a defined location (lat, lon)

## Supported Things

- `weatherstation_current`: provides multiple channels with current weather data for a given weather station in Austria
- `weatherstation_historic`: provides multiple channels with historic weather data for the past 24h for a given weather station in Austria
- `weatherlocation_forecast`: provides multiple channels with forecast weather data for the next 24h for a given location (lat,lon) in Austria

## Binding Configuration

_If your binding requires or supports general configuration settings, please create a folder ```cfg``` and place the configuration file ```<bindingId>.cfg``` inside it._
_In this section, you should link to this file and provide some information about the options._
_The file could e.g. look like:_

```
# Configuration for the geosphereAT Binding
#
# Default secret key for the pairing of the geosphereAT Thing.
# It has to be between 10-40 (alphanumeric) characters.
# This may be changed by the user for security reasons.
secret=openHABSecret
```

_Note that it is planned to generate some part of this based on the information that is available within ```src/main/resources/OH-INF/binding``` of your binding._

_If your binding does not offer any generic configurations, you can remove this section completely._

## Thing Configuration

_Describe what is needed to manually configure a thing, either through the UI or via a thing-file._
_This should be mainly about its mandatory and optional configuration parameters._

_Note that it is planned to generate some part of this based on the XML files within ```src/main/resources/OH-INF/thing``` of your binding._

### `sample` Thing Configuration

| Name            | Type    | Description                                   | Default | Required | Advanced |
|-----------------|---------|-----------------------------------------------|---------|----------|----------|
| stationName     | text    | Name of the weather station                   | N/A     | yes      | no       |
| locationLatLon  | text    | Location in as "Lat,Lon" for weather forecast | N/A     | yes      | no       |
| refreshInterval | integer | Interval the REST API is polled in sec.       | 3600    | no       | no       |

## Channels

_Here you should provide information about available channel types, what their meaning is and how they can be used._

_Note that it is planned to generate some part of this based on the XML files within ```src/main/resources/OH-INF/thing``` of your binding._

| Channel | Type   | Read/Write | Description                 |
|---------|--------|------------|-----------------------------|
| control | Switch | RW         | This is the control channel |

## Full Example

_Provide a full usage example based on textual configuration files._
_*.things, *.items examples are mandatory as textual configuration is well used by many users._
_*.sitemap examples are optional._

### Thing Configuration

```java
Example thing configuration goes here.
```
### Item Configuration

```java
Example item configuration goes here.
```

### Sitemap Configuration

```perl
Optional Sitemap configuration goes here.
Remove this section, if not needed.
```

## Any custom content here!

_Feel free to add additional sections for whatever you think should also be mentioned about your binding!_
