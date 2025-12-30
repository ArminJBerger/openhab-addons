// GeosphereWeatherAPI.java
package org.openhab.binding.geosphereat.internal;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class WeatherData {
    Map<String, Number> weatherData = new HashMap<>();
    SortedMap<Instant, Number> cloudinessData = new TreeMap<>();
    SortedMap<Instant, Number> radiationData = new TreeMap<>();

    public WeatherData(Map<String, Number> w, SortedMap<Instant, Number> c, SortedMap<Instant, Number> r) {
        weatherData = w;
        cloudinessData = c;
        radiationData = r;
    }
}

public class geosphereATAPI {
    private static final String BASE_URL = "https://dataset.api.hub.geosphere.at/v1";
    private static final String DATASET = "tawes-v1-10min";
    private static final String FCDATASET = "nwp-v1-1h-2500m";

    private static final Boolean DEVELOPMENT_MODE = false;

    private static final Map<String, Integer> weather_station_ids = new HashMap<>();

    private static final ReentrantLock weather_station_query = new ReentrantLock();;

    public static boolean getWeatherStations(String locationName) {
        Logger logger = LoggerFactory.getLogger(geosphereATHandler.class);

        try {
            weather_station_query.lock();

            if (!weather_station_ids.isEmpty())
                return true;

            String url = BASE_URL + "/station/current/" + DATASET + "/filter?name=" + locationName
                    + "&output_format=geojson";
            logger.debug("send weather stations request: " + url);

            String resp_string;
            if (DEVELOPMENT_MODE) {
                resp_string = "{\"num_stations_total\":268,\"num_stations_matching\":4,\"matching_stations\":[{\"type\":\"INDIVIDUAL\",\"id\":\"11290\",\"name\":\"GRAZ UNIVERSITAET\",\"state\":\"Steiermark\",\"lat\":47.07777777777778,\"lon\":15.44888888888889,\"altitude\":367.0,\"valid_from\":\"1992-08-29T00:00:00\",\"valid_to\":\"2026-09-28T06:15:40\",\"has_sunshine\":false,\"has_global_radiation\":false,\"is_active\":true,\"sub_stations\":[],\"matches\":true},{\"type\":\"INDIVIDUAL\",\"id\":\"11240\",\"name\":\"GRAZ-THALERHOF-FLUGHAFEN\",\"state\":\"Steiermark\",\"lat\":46.980555555555554,\"lon\":15.44,\"altitude\":340.0,\"valid_from\":\"1972-01-01T00:00:00\",\"valid_to\":\"2026-09-28T06:15:40\",\"has_sunshine\":false,\"has_global_radiation\":false,\"is_active\":true,\"sub_stations\":[],\"matches\":true},{\"type\":\"INDIVIDUAL\",\"id\":\"11238\",\"name\":\"GRAZ/STRASSGANG\",\"state\":\"Steiermark\",\"lat\":47.04611111111111,\"lon\":15.410277777777779,\"altitude\":357.0,\"valid_from\":\"2007-08-24T00:00:00\",\"valid_to\":\"2026-09-28T06:15:40\",\"has_sunshine\":false,\"has_global_radiation\":false,\"is_active\":true,\"sub_stations\":[],\"matches\":true},{\"type\":\"INDIVIDUAL\",\"id\":\"11291\",\"name\":\"GRAZ UNIVERSITAET/HEINRICHSTRASSE\",\"state\":\"Steiermark\",\"lat\":47.080000000000005,\"lon\":15.448055555555555,\"altitude\":366.0,\"valid_from\":\"2022-08-16T00:00:00\",\"valid_to\":\"2026-09-28T06:15:40\",\"has_sunshine\":false,\"has_global_radiation\":false,\"is_active\":true,\"sub_stations\":[],\"matches\":true}]}";
                logger.debug("DEVELOPMENT MODE active - data replay");
            } else {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).build();
                HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
                resp_string = resp.body();
            }
            logger.debug("got response to weather station id request: " + resp_string);

            JsonElement root_node = JsonParser.parseString(resp_string);
            JsonArray stations = root_node.getAsJsonObject().getAsJsonArray("matching_stations");

            if (stations.size() == 0) {
                logger.error("no matching weather stations found for location: " + locationName);
                return false;
            }

            weather_station_ids.clear();

            for (JsonElement station : stations) {
                Integer stationId = station.getAsJsonObject().get("id").getAsInt();
                if (station.getAsJsonObject().get("is_active").getAsBoolean()) {
                    weather_station_ids.put(station.getAsJsonObject().get("name").getAsString(), stationId);
                }
            }

            logger.debug("weather_station_ids: " + weather_station_ids.toString());
            return true;
        } catch (Exception e) {
            // e.printStackTrace();
            logger.error(e.getMessage());
            return false;
        } finally {
            weather_station_query.unlock();
        }
    }

    public static Map<String, Number> getCurrentWeather(String stationName) {
        Logger logger = LoggerFactory.getLogger(geosphereATHandler.class);
        try {
            String url = BASE_URL + "/station/current/" + DATASET + "?station_ids="
                    + weather_station_ids.get(stationName)
                    + "&parameters=TL&parameters=RR&parameters=P&parameters=RF&parameters=SO&parameters=GLOW"
                    + "&output_format=geojson";
            logger.debug("send current request: " + url);

            String resp_string;
            if (DEVELOPMENT_MODE) {
                resp_string = "{\"media_type\":\"application/json\",\"type\":\"FeatureCollection\",\"version\":\"v1\",\"timestamps\":[\"2025-09-27T07:00+00:00\"],\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[15.410277777777779,47.04611111111111]},\"properties\":{\"parameters\":{\"TL\":{\"name\":\"Lufttemperatur\",\"unit\":\"°C\",\"data\":[21.0]},\"RR\":{\"name\":\"Niederschlag der letzten 10 Minuten\",\"unit\":\"mm\",\"data\":[0.0]},\"P\":{\"name\":\"Luftdruck\",\"unit\":\"hPa\",\"data\":[981.9]},\"RF\":{\"name\":\"Relative Feuchte\",\"unit\":\"%\",\"data\":[90.0]},\"SO\":{\"name\":\"Sonnenscheindauer\",\"unit\":\"sec\",\"data\":[0.0]}},\"station\":\"11238\"}}]}";
                logger.debug("DEVELOPMENT MODE active - data replay");
            } else {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).build();
                HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
                resp_string = resp.body();
            }
            logger.debug("got response to current weather request: " + resp_string);

            JsonElement root_node = JsonParser.parseString(resp_string);
            JsonArray features = root_node.getAsJsonObject().getAsJsonArray("features");
            JsonElement properties = features.get(0).getAsJsonObject().get("properties");
            JsonObject params = properties.getAsJsonObject().get("parameters").getAsJsonObject();

            Map<String, Number> weatherData = new HashMap<>();

            for (Map.Entry<String, JsonElement> para : params.entrySet()) {
                JsonObject para_object = para.getValue().getAsJsonObject();
                JsonArray data = para_object.getAsJsonArray("data");
                String unit = para_object.get("unit").getAsString();
                if (data.size() > 0) {
                    Double value;
                    JsonElement val = data.get(0);
                    if (val != null && !val.isJsonNull())
                        value = val.getAsDouble();
                    else
                        value = 0.0;
                    weatherData.put(para.getKey(), value);
                }
            }
            logger.debug("adding data to map: " + weatherData.toString());
            return weatherData;
        } catch (Exception e) {
            // e.printStackTrace();
            logger.error(e.getMessage());
            return null;
        }
    }

    public static Map<String, Number> getHistoricWeather(String stationName) {
        Logger logger = LoggerFactory.getLogger(geosphereATHandler.class);
        try {
            Instant now = Instant.now();
            Instant start_time = now.minus(1, ChronoUnit.DAYS);
            Instant start_time_12h = now.minus(12, ChronoUnit.HOURS);
            Instant end_time = now;

            String url = BASE_URL + "/station/historical/" + DATASET + "?station_ids="
                    + weather_station_ids.get(stationName)
                    + "&parameters=TL&parameters=RR&parameters=P&parameters=RF&parameters=SO&parameters=GLOW"
                    + "&start=" + start_time.toString() + "&end=" + end_time.toString() + "&output_format=geojson";
            logger.debug("send historic request: " + url);

            String resp_string;
            if (DEVELOPMENT_MODE) {
                resp_string = "{\"media_type\":\"application/json\",\"type\":\"FeatureCollection\",\"version\":\"v1\",\"timestamps\":[\"2025-09-26T07:10+00:00\",\"2025-09-26T07:20+00:00\",\"2025-09-26T07:30+00:00\",\"2025-09-26T07:40+00:00\",\"2025-09-26T07:50+00:00\",\"2025-09-26T08:00+00:00\",\"2025-09-26T08:10+00:00\",\"2025-09-26T08:20+00:00\",\"2025-09-26T08:30+00:00\",\"2025-09-26T08:40+00:00\",\"2025-09-26T08:50+00:00\",\"2025-09-26T09:00+00:00\",\"2025-09-26T09:10+00:00\",\"2025-09-26T09:20+00:00\",\"2025-09-26T09:30+00:00\",\"2025-09-26T09:40+00:00\",\"2025-09-26T09:50+00:00\",\"2025-09-26T10:00+00:00\",\"2025-09-26T10:10+00:00\",\"2025-09-26T10:20+00:00\",\"2025-09-26T10:30+00:00\",\"2025-09-26T10:40+00:00\",\"2025-09-26T10:50+00:00\",\"2025-09-26T11:00+00:00\",\"2025-09-26T11:10+00:00\",\"2025-09-26T11:20+00:00\",\"2025-09-26T11:30+00:00\",\"2025-09-26T11:40+00:00\",\"2025-09-26T11:50+00:00\",\"2025-09-26T12:00+00:00\",\"2025-09-26T12:10+00:00\",\"2025-09-26T12:20+00:00\",\"2025-09-26T12:30+00:00\",\"2025-09-26T12:40+00:00\",\"2025-09-26T12:50+00:00\",\"2025-09-26T13:00+00:00\",\"2025-09-26T13:10+00:00\",\"2025-09-26T13:20+00:00\",\"2025-09-26T13:30+00:00\",\"2025-09-26T13:40+00:00\",\"2025-09-26T13:50+00:00\",\"2025-09-26T14:00+00:00\",\"2025-09-26T14:10+00:00\",\"2025-09-26T14:20+00:00\",\"2025-09-26T14:30+00:00\",\"2025-09-26T14:40+00:00\",\"2025-09-26T14:50+00:00\",\"2025-09-26T15:00+00:00\",\"2025-09-26T15:10+00:00\",\"2025-09-26T15:20+00:00\",\"2025-09-26T15:30+00:00\",\"2025-09-26T15:40+00:00\",\"2025-09-26T15:50+00:00\",\"2025-09-26T16:00+00:00\",\"2025-09-26T16:10+00:00\",\"2025-09-26T16:20+00:00\",\"2025-09-26T16:30+00:00\",\"2025-09-26T16:40+00:00\",\"2025-09-26T16:50+00:00\",\"2025-09-26T17:00+00:00\",\"2025-09-26T17:10+00:00\",\"2025-09-26T17:20+00:00\",\"2025-09-26T17:30+00:00\",\"2025-09-26T17:40+00:00\",\"2025-09-26T17:50+00:00\",\"2025-09-26T18:00+00:00\",\"2025-09-26T18:10+00:00\",\"2025-09-26T18:20+00:00\",\"2025-09-26T18:30+00:00\",\"2025-09-26T18:40+00:00\",\"2025-09-26T18:50+00:00\",\"2025-09-26T19:00+00:00\",\"2025-09-26T19:10+00:00\",\"2025-09-26T19:20+00:00\",\"2025-09-26T19:30+00:00\",\"2025-09-26T19:40+00:00\",\"2025-09-26T19:50+00:00\",\"2025-09-26T20:00+00:00\",\"2025-09-26T20:10+00:00\",\"2025-09-26T20:20+00:00\",\"2025-09-26T20:30+00:00\",\"2025-09-26T20:40+00:00\",\"2025-09-26T20:50+00:00\",\"2025-09-26T21:00+00:00\",\"2025-09-26T21:10+00:00\",\"2025-09-26T21:20+00:00\",\"2025-09-26T21:30+00:00\",\"2025-09-26T21:40+00:00\",\"2025-09-26T21:50+00:00\",\"2025-09-26T22:00+00:00\",\"2025-09-26T22:10+00:00\",\"2025-09-26T22:20+00:00\",\"2025-09-26T22:30+00:00\",\"2025-09-26T22:40+00:00\",\"2025-09-26T22:50+00:00\",\"2025-09-26T23:00+00:00\",\"2025-09-26T23:10+00:00\",\"2025-09-26T23:20+00:00\",\"2025-09-26T23:30+00:00\",\"2025-09-26T23:40+00:00\",\"2025-09-26T23:50+00:00\",\"2025-09-27T00:00+00:00\",\"2025-09-27T00:10+00:00\",\"2025-09-27T00:20+00:00\",\"2025-09-27T00:30+00:00\",\"2025-09-27T00:40+00:00\",\"2025-09-27T00:50+00:00\",\"2025-09-27T01:00+00:00\",\"2025-09-27T01:10+00:00\",\"2025-09-27T01:20+00:00\",\"2025-09-27T01:30+00:00\",\"2025-09-27T01:40+00:00\",\"2025-09-27T01:50+00:00\",\"2025-09-27T02:00+00:00\",\"2025-09-27T02:10+00:00\",\"2025-09-27T02:20+00:00\",\"2025-09-27T02:30+00:00\",\"2025-09-27T02:40+00:00\",\"2025-09-27T02:50+00:00\",\"2025-09-27T03:00+00:00\",\"2025-09-27T03:10+00:00\",\"2025-09-27T03:20+00:00\",\"2025-09-27T03:30+00:00\",\"2025-09-27T03:40+00:00\",\"2025-09-27T03:50+00:00\",\"2025-09-27T04:00+00:00\",\"2025-09-27T04:10+00:00\",\"2025-09-27T04:20+00:00\",\"2025-09-27T04:30+00:00\",\"2025-09-27T04:40+00:00\",\"2025-09-27T04:50+00:00\",\"2025-09-27T05:00+00:00\",\"2025-09-27T05:10+00:00\",\"2025-09-27T05:20+00:00\",\"2025-09-27T05:30+00:00\",\"2025-09-27T05:40+00:00\",\"2025-09-27T05:50+00:00\",\"2025-09-27T06:00+00:00\",\"2025-09-27T06:10+00:00\",\"2025-09-27T06:20+00:00\",\"2025-09-27T06:30+00:00\",\"2025-09-27T06:40+00:00\",\"2025-09-27T06:50+00:00\",\"2025-09-27T07:00+00:00\"],\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[15.410277777777779,47.04611111111111]},\"properties\":{\"parameters\":{\"TL\":{\"name\":\"Lufttemperatur\",\"unit\":\"°C\",\"data\":[13.1,13.2,13.2,13.1,13.0,13.0,12.9,13.0,13.2,13.2,13.2,13.2,13.2,13.2,13.2,13.2,13.2,13.2,13.2,13.2,13.2,13.1,13.1,13.1,13.2,13.2,13.3,13.6,13.4,13.7,13.5,13.5,13.4,13.5,13.5,13.5,13.6,13.5,13.4,13.4,13.4,13.5,13.4,13.2,13.3,13.3,13.3,13.2,13.2,13.1,13.1,13.0,13.0,13.0,13.0,13.0,13.0,13.1,13.0,13.0,12.9,12.9,12.8,12.8,12.8,12.7,12.7,12.7,12.7,12.7,12.7,12.6,12.6,12.6,12.6,12.6,12.6,12.6,12.6,12.6,12.6,12.6,12.6,12.6,12.6,12.6,12.6,12.6,12.6,12.5,12.5,12.6,12.5,12.5,12.5,12.5,12.5,12.4,12.4,12.4,12.4,12.4,12.4,12.3,12.4,12.3,12.3,12.3,12.3,12.3,12.3,12.3,12.3,12.3,12.3,12.2,12.2,12.3,12.2,12.3,12.3,12.3,12.3,12.3,12.3,12.3,12.3,12.3,12.4,12.4,12.4,12.4,12.4,12.4,12.4,12.5,12.4,12.4,12.5,12.5,12.5,12.5,12.6,21.0]},\"RR\":{\"name\":\"Niederschlag der letzten 10 Minuten\",\"unit\":\"mm\",\"data\":[0.4,0.1,0.0,0.0,0.1,0.4,0.3,0.7,0.2,0.0,0.0,0.0,0.0,0.1,0.0,0.1,0.2,0.0,0.1,0.0,0.2,0.1,0.0,0.0,0.1,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.2,0.2,0.1,0.1,0.1,0.0,0.1,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.1,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0]},\"P\":{\"name\":\"Luftdruck\",\"unit\":\"hPa\",\"data\":[980.3,980.3,980.4,980.5,980.5,980.7,980.7,980.5,980.4,980.5,980.7,980.5,980.7,980.7,980.8,980.9,981.0,981.0,981.0,981.0,981.1,981.2,981.3,981.2,981.2,981.1,981.1,981.0,980.9,980.9,980.9,981.0,981.0,981.0,981.0,981.0,981.0,981.1,981.0,981.1,981.1,981.0,981.1,981.1,981.1,981.2,981.2,981.3,981.3,981.3,981.3,981.4,981.5,981.5,981.4,981.4,981.5,981.6,981.6,981.6,981.6,981.8,981.8,981.9,981.9,981.9,981.9,981.9,981.9,981.9,982.0,982.0,982.0,982.2,982.2,982.2,982.2,982.3,982.3,982.3,982.3,982.3,982.4,982.4,982.4,982.3,982.3,982.2,982.2,982.2,982.2,982.2,982.1,982.1,982.1,982.2,982.3,982.2,982.2,982.2,982.2,982.2,982.1,982.0,982.0,982.0,982.0,981.8,981.8,981.8,981.6,981.6,981.5,981.6,981.6,981.6,981.5,981.4,981.4,981.4,981.5,981.5,981.4,981.4,981.3,981.2,981.2,981.2,981.2,981.2,981.2,981.3,981.4,981.5,981.5,981.5,981.5,981.5,981.5,981.6,981.6,981.8,981.8,981.9]},\"RF\":{\"name\":\"Relative Feuchte\",\"unit\":\"%\",\"data\":[97.0,97.0,97.0,97.0,96.0,96.0,96.0,97.0,97.0,97.0,97.0,97.0,96.0,96.0,96.0,96.0,96.0,95.0,96.0,95.0,96.0,95.0,96.0,95.0,95.0,95.0,94.0,94.0,92.0,94.0,92.0,92.0,92.0,91.0,90.0,91.0,91.0,90.0,91.0,91.0,92.0,92.0,93.0,93.0,94.0,93.0,92.0,92.0,92.0,92.0,93.0,94.0,94.0,94.0,92.0,92.0,92.0,91.0,92.0,94.0,95.0,96.0,96.0,97.0,97.0,97.0,97.0,97.0,97.0,97.0,96.0,96.0,95.0,95.0,96.0,95.0,96.0,96.0,96.0,95.0,95.0,95.0,96.0,95.0,94.0,95.0,94.0,94.0,94.0,94.0,94.0,94.0,94.0,93.0,93.0,93.0,93.0,93.0,93.0,92.0,94.0,94.0,95.0,93.0,94.0,94.0,93.0,94.0,94.0,93.0,94.0,94.0,92.0,94.0,93.0,93.0,94.0,94.0,94.0,92.0,94.0,94.0,94.0,94.0,94.0,93.0,94.0,93.0,94.0,93.0,93.0,94.0,94.0,94.0,94.0,94.0,93.0,93.0,92.0,92.0,91.0,90.0,90.0,90.0]},\"SO\":{\"name\":\"Sonnenscheindauer\",\"unit\":\"sec\",\"data\":[0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0]}},\"station\":\"11238\"}}]}";
                logger.debug("DEVELOPMENT MODE active - data replay");
            } else {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).build();
                HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
                resp_string = resp.body();
            }
            logger.debug("got response to historic weather request: " + resp_string);

            JsonElement root_node = JsonParser.parseString(resp_string);
            JsonArray features = root_node.getAsJsonObject().getAsJsonArray("features");
            JsonElement properties = features.get(0).getAsJsonObject().get("properties");
            JsonObject params = properties.getAsJsonObject().get("parameters").getAsJsonObject();
            JsonArray timestamps = root_node.getAsJsonObject().getAsJsonArray("timestamps");

            Map<String, Number> weatherData = new HashMap<>();

            for (Map.Entry<String, JsonElement> para : params.entrySet()) {
                JsonObject para_object = para.getValue().getAsJsonObject();
                JsonArray data = para_object.getAsJsonArray("data");
                String unit = para_object.get("unit").getAsString();

                Double sum = 0.0;
                Double max = Double.MIN_VALUE;
                Double min = Double.MAX_VALUE;
                Integer cnt = 0;

                Double sum_12h = 0.0;
                Double max_12h = Double.MIN_VALUE;
                Double min_12h = Double.MAX_VALUE;
                Integer cnt_12h = 0;

                for (int i = 0; i < timestamps.size(); ++i) {
                    Double value;
                    JsonElement val = data.get(i);
                    if (val != null && !val.isJsonNull())
                        value = val.getAsDouble();
                    else
                        value = 0.0;
                    sum += value;
                    cnt++;
                    if (value > max)
                        max = value;
                    if (value < min)
                        min = value;

                    ZonedDateTime zts = ZonedDateTime.parse(timestamps.get(i).getAsString());
                    Instant ts = zts.toInstant();
                    if (ts.isAfter(start_time_12h)) {
                        sum_12h += value;
                        cnt_12h++;
                        if (value > max_12h)
                            max_12h = value;
                        if (value < min_12h)
                            min_12h = value;
                    }
                }
                switch (para.getKey()) {
                    case "TL":
                    case "RF":
                    case "GLOW":
                    case "P":
                        weatherData.put(para.getKey() + "_MAX", max);
                        weatherData.put(para.getKey() + "_MIN", min);
                        weatherData.put(para.getKey() + "_AVG", sum / cnt);

                        weatherData.put(para.getKey() + "_MAX_12H", max_12h);
                        weatherData.put(para.getKey() + "_MIN_12H", min_12h);
                        weatherData.put(para.getKey() + "_AVG_12H", sum_12h / cnt_12h);
                        break;
                    case "RR":
                    case "SO":
                        weatherData.put(para.getKey() + "_ACC", sum);

                        weatherData.put(para.getKey() + "_ACC_12H", sum_12h);
                        break;
                }
            }
            logger.debug("adding data to map: " + weatherData.toString());
            return weatherData;
        } catch (Exception e) {
            // e.printStackTrace();
            logger.error(e.getMessage());
            return null;
        }
    }

    public static WeatherData getForecastWeather(String locationLatLon) {
        Logger logger = LoggerFactory.getLogger(geosphereATHandler.class);
        try {
            Instant now = Instant.now();
            Instant start_time = now.minus(1, ChronoUnit.HOURS);
            Instant end_time = now.plus(1, ChronoUnit.DAYS);
            Instant end_time_12h = now.plus(12, ChronoUnit.HOURS);

            String url = BASE_URL + "/timeseries/forecast/" + FCDATASET + "?lat_lon=" + locationLatLon
                    + "&parameters=rain_acc&parameters=t2m&parameters=rh2m&parameters=sp&parameters=tcc&parameters=sundur_acc&parameters=grad"
                    + "&start=" + start_time.toString() + "&end=" + end_time.toString() + "&output_format=geojson";
            logger.debug("send forecast request: " + url);

            String resp_string;
            if (DEVELOPMENT_MODE) {
                resp_string = "{\"reference_time\":\"2025-09-27T03:00+00:00\",\"media_type\":\"application/json\",\"type\":\"FeatureCollection\",\"version\":\"v1\",\"timestamps\":[\"2025-09-27T07:00+00:00\",\"2025-09-27T09:00+00:00\",\"2025-09-27T10:00+00:00\",\"2025-09-27T11:00+00:00\",\"2025-09-27T12:00+00:00\",\"2025-09-27T13:00+00:00\",\"2025-09-27T14:00+00:00\",\"2025-09-27T15:00+00:00\",\"2025-09-27T16:00+00:00\",\"2025-09-27T17:00+00:00\",\"2025-09-27T18:00+00:00\",\"2025-09-27T19:00+00:00\",\"2025-09-27T20:00+00:00\",\"2025-09-27T21:00+00:00\",\"2025-09-27T22:00+00:00\",\"2025-09-27T23:00+00:00\",\"2025-09-28T00:00+00:00\",\"2025-09-28T01:00+00:00\",\"2025-09-28T02:00+00:00\",\"2025-09-28T03:00+00:00\",\"2025-09-28T04:00+00:00\",\"2025-09-28T05:00+00:00\",\"2025-09-28T06:00+00:00\",\"2025-09-28T07:00+00:00\"],\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[15.465999999999852,47.049000000000156]},\"properties\":{\"parameters\":{\"rain_acc\":{\"name\":\"total rainfall amount\",\"unit\":\"kg m-2\",\"data\":[0.014,0.014,0.013,0.014,0.014,0.014,0.014,0.014,0.014,0.014,0.014,0.014,0.035,0.098,0.098,0.098,0.098,0.098,0.098,0.135,0.137,0.137,0.137,0.137]},\"t2m\":{\"name\":\"2m temperature\",\"unit\":\"degree Celsius\",\"data\":[12.9,12.9,13.0,14.1,14.4,15.4,15.9,15.7,15.1,14.5,14.3,14.0,13.7,13.4,13.5,13.3,12.9,12.7,12.7,12.6,12.6,12.8,13.0,13.2]},\"rh2m\":{\"name\":\"relative humidity 2m above ground\",\"unit\":\"%\",\"data\":[89.77,90.1,87.49,81.81,78.25,73.08,71.78,72.13,73.68,74.73,73.44,75.21,78.31,81.27,78.11,79.58,85.05,87.0,86.06,86.77,85.44,81.96,79.12,78.67]},\"sp\":{\"name\":\"surface pressure\",\"unit\":\"Pa\",\"data\":[98068.61,98091.96,98098.97,98078.53,98040.58,97976.93,97923.8,97891.1,97881.18,97879.42,97899.28,97929.64,97942.49,97935.48,97937.23,97926.14,97880.01,97815.78,97805.85,97812.28,97788.92,97798.26,97814.03,97831.55]},\"tcc\":{\"name\":\"Total cloud cover\",\"unit\":\"1\",\"data\":[1.0,1.0,1.0,1.0,0.9,0.8,0.7,0.9,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0]}}}}]}";
                logger.debug("DEVELOPMENT MODE active - data replay");
            } else {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).build();
                HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
                resp_string = resp.body();
            }
            logger.debug("got response to forecast weather request: " + resp_string);

            JsonElement root_node = JsonParser.parseString(resp_string);
            JsonArray features = root_node.getAsJsonObject().getAsJsonArray("features");
            JsonElement properties = features.get(0).getAsJsonObject().get("properties");
            JsonObject params = properties.getAsJsonObject().get("parameters").getAsJsonObject();
            JsonArray timestamps = root_node.getAsJsonObject().getAsJsonArray("timestamps");

            Map<String, Number> weatherData = new HashMap<>();
            SortedMap<Instant, Number> cloudinessData = new TreeMap<>();
            SortedMap<Instant, Number> radiationData = new TreeMap<>();

            for (Map.Entry<String, JsonElement> para : params.entrySet()) {
                JsonObject para_object = para.getValue().getAsJsonObject();
                JsonArray data = para_object.getAsJsonArray("data");
                String unit = para_object.get("unit").getAsString();

                Double sum = 0.0;
                Double max = Double.MIN_VALUE;
                Double min = Double.MAX_VALUE;
                Integer cnt = 0;

                Double sum_12h = 0.0;
                Double max_12h = Double.MIN_VALUE;
                Double min_12h = Double.MAX_VALUE;
                Integer cnt_12h = 0;

                for (int i = 0; i < timestamps.size(); ++i) {
                    Double value;
                    JsonElement val = data.get(i);
                    if (val != null && !val.isJsonNull())
                        value = val.getAsDouble();
                    else
                        value = 0.0;
                    sum += value;
                    cnt++;
                    if (value > max)
                        max = value;
                    if (value < min)
                        min = value;

                    ZonedDateTime zts = ZonedDateTime.parse(timestamps.get(i).getAsString());
                    Instant ts = zts.toInstant();
                    if (ts.isBefore(end_time_12h)) {
                        sum_12h += value;
                        cnt_12h++;
                        if (value > max_12h)
                            max_12h = value;
                        if (value < min_12h)
                            min_12h = value;
                    }

                    if ("tcc".equals(para.getKey())) {
                        logger.debug("parse cloudiness data: " + timestamps.get(i).getAsString() + " ts: "
                                + ts.toString() + " value: " + value.toString());
                        cloudinessData.put(ts, value * 100.0);
                    }
                    if ("grad".equals(para.getKey())) {
                        logger.debug("parese radiation data: " + timestamps.get(i).getAsString() + "ts: "
                                + ts.toString() + " value: " + value.toString());
                        radiationData.put(ts, value);
                    }
                }
                switch (para.getKey()) {
                    case "t2m":
                    case "rh2m":
                    case "grad":
                    case "tcc":
                        weatherData.put(para.getKey() + "_MAX", max);
                        weatherData.put(para.getKey() + "_MIN", min);
                        weatherData.put(para.getKey() + "_AVG", sum / cnt);

                        weatherData.put(para.getKey() + "_MAX_12H", max_12h);
                        weatherData.put(para.getKey() + "_MIN_12H", min_12h);
                        weatherData.put(para.getKey() + "_AVG_12H", sum_12h / cnt_12h);
                        break;
                    case "sp":
                        weatherData.put(para.getKey() + "_MAX", max / 100.0);
                        weatherData.put(para.getKey() + "_MIN", min / 100.0);
                        weatherData.put(para.getKey() + "_AVG", sum / cnt / 100.0);

                        weatherData.put(para.getKey() + "_MAX_12H", max_12h / 100.0);
                        weatherData.put(para.getKey() + "_MIN_12H", min_12h / 100.0);
                        weatherData.put(para.getKey() + "_AVG_12H", sum_12h / cnt_12h / 100.0);
                        break;
                    case "sundur_acc":
                    case "rain_acc":
                        weatherData.put(para.getKey() + "_ACC", sum);

                        weatherData.put(para.getKey() + "_ACC_12H", sum_12h);
                        break;
                }
            }
            logger.debug("adding data to map: " + weatherData.toString());
            return new WeatherData(weatherData, cloudinessData, radiationData);
        } catch (Exception e) {
            // e.printStackTrace();
            logger.error(e.getMessage());
            return null;
        }
    }
}
