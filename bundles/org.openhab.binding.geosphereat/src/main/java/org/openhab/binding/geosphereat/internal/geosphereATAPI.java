// GeosphereWeatherAPI.java
package org.openhab.binding.geosphereat.internal;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class geosphereATAPI {
    private static final String BASE_URL = "https://dataset.api.hub.geosphere.at/v1";
    private static final String DATASET = "tawes-v1-10min";
    private static final String FCDATASET = "nwp-v1-1h-2500m";

    private static final Map<String, Integer> weather_station_ids = new HashMap<>();

    public static boolean getWeatherStations(String locationName) {
        try {
            Logger logger = LoggerFactory.getLogger(geosphereATHandler.class);
            String url = BASE_URL + "/station/current/" + DATASET + "/filter?name=" + locationName;
            logger.info("send weather stations request: " + url);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            JsonParser parser = new JsonParser();
            JsonElement rootNode = parser.parse(resp.body());
            JsonArray stations = rootNode.getAsJsonObject().getAsJsonArray("matching_stations");

            if (stations.size() == 0)
                return false;

            weather_station_ids.clear();

            for (JsonElement station : stations) {
                Integer stationId = station.getAsJsonObject().get("id").getAsInt();
                if (station.getAsJsonObject().get("is_active").getAsBoolean()) {
                    weather_station_ids.put(station.getAsJsonObject().get("name").getAsString(), stationId);
                }
            }
            logger.info("weather_station_ids: " + weather_station_ids.toString());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Map<String, Number> getCurrentWeather(String stationName) {
        try {
            Logger logger = LoggerFactory.getLogger(geosphereATHandler.class);
            String url = BASE_URL + "/station/current/" + DATASET + "?station_ids="
                    + weather_station_ids.get(stationName)
                    + "&parameters=TL&parameters=RR&parameters=P&parameters=RF&parameters=SO"
                    + "&output_format=geojson";
            logger.info("send current request: " + url);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            logger.info("got response to current weather request: " + resp.body());
            // String resp =
            // "{\"media_type\":\"application/json\",\"type\":\"FeatureCollection\",\"version\":\"v1\",\"timestamps\":[\"2025-05-23T22:20+00:00\"],\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[15.410277777777779,47.04611111111111]},\"properties\":{\"parameters\":{\"TL\":{\"name\":\"Lufttemperatur\",\"unit\":\"°C\",\"data\":[10.0]},\"RR\":{\"name\":\"Niederschlag
            // der letzten 10
            // Minuten\",\"unit\":\"mm\",\"data\":[0.0]},\"P\":{\"name\":\"Luftdruck\",\"unit\":\"hPa\",\"data\":[975.5]},\"RF\":{\"name\":\"Relative
            // Feuchte\",\"unit\":\"%\",\"data\":[85.0]},\"SO\":{\"name\":\"Sonnenscheindauer\",\"unit\":\"sec\",\"data\":[0.0]}},\"station\":\"11238\"}}]}";
            // logger.info("fix response " + resp);

            JsonParser parser = new JsonParser();
            JsonElement root_node = parser.parse(resp.body());
            // JsonElement root_node = parser.parse(resp);
            JsonArray features = root_node.getAsJsonObject().getAsJsonArray("features");
            JsonElement properties = features.get(0).getAsJsonObject().get("properties");
            JsonObject params = properties.getAsJsonObject().get("parameters").getAsJsonObject();

            Map<String, Number> weatherData = new HashMap<>();

            for (Map.Entry<String, JsonElement> para : params.entrySet()) {
                JsonObject para_object = para.getValue().getAsJsonObject();
                JsonArray data = para_object.getAsJsonArray("data");
                String unit = para_object.get("unit").getAsString();
                if (data.size() > 0) {
                    weatherData.put(para.getKey(), data.get(0).getAsNumber());
                }
            }
            logger.info("adding data to map: " + weatherData.toString());
            return weatherData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, Number> getHistoricWeather(String stationName) {
        try {
            Logger logger = LoggerFactory.getLogger(geosphereATHandler.class);
            Instant now = Instant.now();
            Instant start_time = now.minus(1, ChronoUnit.DAYS);
            Instant end_time = now;

            String url = BASE_URL + "/station/historical/" + DATASET + "?station_ids="
                    + weather_station_ids.get(stationName)
                    + "&parameters=TL&parameters=RR&parameters=P&parameters=RF&parameters=SO" + "&start="
                    + start_time.toString() + "&end=" + end_time.toString() + "&output_format=geojson";
            logger.info("send historic request: " + url);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            logger.info("got response to historic weather request: " + resp.body());
            // String resp =
            // "{\"media_type\":\"application/json\",\"type\":\"FeatureCollection\",\"version\":\"v1\",\"timestamps\":[\"2025-05-23T13:50+00:00\",\"2025-05-23T14:00+00:00\",\"2025-05-23T14:10+00:00\",\"2025-05-23T14:20+00:00\",\"2025-05-23T14:30+00:00\",\"2025-05-23T14:40+00:00\",\"2025-05-23T14:50+00:00\",\"2025-05-23T15:00+00:00\",\"2025-05-23T15:10+00:00\",\"2025-05-23T15:20+00:00\",\"2025-05-23T15:30+00:00\",\"2025-05-23T15:40+00:00\",\"2025-05-23T15:50+00:00\",\"2025-05-23T16:00+00:00\",\"2025-05-23T16:10+00:00\",\"2025-05-23T16:20+00:00\",\"2025-05-23T16:30+00:00\",\"2025-05-23T16:40+00:00\",\"2025-05-23T16:50+00:00\",\"2025-05-23T17:00+00:00\",\"2025-05-23T17:10+00:00\",\"2025-05-23T17:20+00:00\",\"2025-05-23T17:30+00:00\",\"2025-05-23T17:40+00:00\",\"2025-05-23T17:50+00:00\",\"2025-05-23T18:00+00:00\",\"2025-05-23T18:10+00:00\",\"2025-05-23T18:20+00:00\",\"2025-05-23T18:30+00:00\",\"2025-05-23T18:40+00:00\",\"2025-05-23T18:50+00:00\",\"2025-05-23T19:00+00:00\",\"2025-05-23T19:10+00:00\",\"2025-05-23T19:20+00:00\",\"2025-05-23T19:30+00:00\",\"2025-05-23T19:40+00:00\",\"2025-05-23T19:50+00:00\",\"2025-05-23T20:00+00:00\",\"2025-05-23T20:10+00:00\",\"2025-05-23T20:20+00:00\",\"2025-05-23T20:30+00:00\",\"2025-05-23T20:40+00:00\",\"2025-05-23T20:50+00:00\",\"2025-05-23T21:00+00:00\",\"2025-05-23T21:10+00:00\",\"2025-05-23T21:20+00:00\",\"2025-05-23T21:30+00:00\",\"2025-05-23T21:40+00:00\",\"2025-05-23T21:50+00:00\",\"2025-05-23T22:00+00:00\",\"2025-05-23T22:10+00:00\",\"2025-05-23T22:20+00:00\",\"2025-05-23T22:30+00:00\",\"2025-05-23T22:40+00:00\",\"2025-05-23T22:50+00:00\",\"2025-05-23T23:00+00:00\",\"2025-05-23T23:10+00:00\",\"2025-05-23T23:20+00:00\",\"2025-05-23T23:30+00:00\",\"2025-05-23T23:40+00:00\",\"2025-05-23T23:50+00:00\",\"2025-05-24T00:00+00:00\",\"2025-05-24T00:10+00:00\",\"2025-05-24T00:20+00:00\",\"2025-05-24T00:30+00:00\",\"2025-05-24T00:40+00:00\",\"2025-05-24T00:50+00:00\",\"2025-05-24T01:00+00:00\",\"2025-05-24T01:10+00:00\",\"2025-05-24T01:20+00:00\",\"2025-05-24T01:30+00:00\",\"2025-05-24T01:40+00:00\",\"2025-05-24T01:50+00:00\",\"2025-05-24T02:00+00:00\",\"2025-05-24T02:10+00:00\",\"2025-05-24T02:20+00:00\",\"2025-05-24T02:30+00:00\",\"2025-05-24T02:40+00:00\",\"2025-05-24T02:50+00:00\",\"2025-05-24T03:00+00:00\",\"2025-05-24T03:10+00:00\",\"2025-05-24T03:20+00:00\",\"2025-05-24T03:30+00:00\",\"2025-05-24T03:40+00:00\",\"2025-05-24T03:50+00:00\",\"2025-05-24T04:00+00:00\",\"2025-05-24T04:10+00:00\",\"2025-05-24T04:20+00:00\",\"2025-05-24T04:30+00:00\",\"2025-05-24T04:40+00:00\",\"2025-05-24T04:50+00:00\",\"2025-05-24T05:00+00:00\",\"2025-05-24T05:10+00:00\",\"2025-05-24T05:20+00:00\",\"2025-05-24T05:30+00:00\",\"2025-05-24T05:40+00:00\",\"2025-05-24T05:50+00:00\",\"2025-05-24T06:00+00:00\",\"2025-05-24T06:10+00:00\",\"2025-05-24T06:20+00:00\",\"2025-05-24T06:30+00:00\",\"2025-05-24T06:40+00:00\",\"2025-05-24T06:50+00:00\",\"2025-05-24T07:00+00:00\",\"2025-05-24T07:10+00:00\",\"2025-05-24T07:20+00:00\",\"2025-05-24T07:30+00:00\",\"2025-05-24T07:40+00:00\",\"2025-05-24T07:50+00:00\",\"2025-05-24T08:00+00:00\",\"2025-05-24T08:10+00:00\",\"2025-05-24T08:20+00:00\",\"2025-05-24T08:30+00:00\",\"2025-05-24T08:40+00:00\",\"2025-05-24T08:50+00:00\",\"2025-05-24T09:00+00:00\",\"2025-05-24T09:10+00:00\",\"2025-05-24T09:20+00:00\",\"2025-05-24T09:30+00:00\",\"2025-05-24T09:40+00:00\",\"2025-05-24T09:50+00:00\",\"2025-05-24T10:00+00:00\",\"2025-05-24T10:10+00:00\",\"2025-05-24T10:20+00:00\",\"2025-05-24T10:30+00:00\",\"2025-05-24T10:40+00:00\",\"2025-05-24T10:50+00:00\",\"2025-05-24T11:00+00:00\",\"2025-05-24T11:10+00:00\",\"2025-05-24T11:20+00:00\",\"2025-05-24T11:30+00:00\",\"2025-05-24T11:40+00:00\",\"2025-05-24T11:50+00:00\",\"2025-05-24T12:00+00:00\",\"2025-05-24T12:10+00:00\",\"2025-05-24T12:20+00:00\",\"2025-05-24T12:30+00:00\",\"2025-05-24T12:40+00:00\",\"2025-05-24T12:50+00:00\",\"2025-05-24T13:00+00:00\",\"2025-05-24T13:10+00:00\",\"2025-05-24T13:20+00:00\",\"2025-05-24T13:30+00:00\",\"2025-05-24T13:40+00:00\"],\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[15.410277777777779,47.04611111111111]},\"properties\":{\"parameters\":{\"TL\":{\"name\":\"Lufttemperatur\",\"unit\":\"Â°C\",\"data\":[13.3,13.5,13.9,13.7,14.0,13.6,14.1,13.9,14.2,13.9,14.0,14.0,13.8,13.8,13.3,13.0,13.0,12.9,12.9,12.8,12.6,12.6,12.7,12.5,12.2,12.1,12.1,11.7,11.4,11.2,11.0,10.9,10.7,10.4,10.3,10.2,10.1,10.1,10.2,10.1,9.9,9.8,9.8,10.0,10.1,10.2,10.1,10.1,10.1,10.1,10.1,10.0,10.0,9.7,9.4,9.0,8.7,8.4,8.1,7.8,7.6,7.5,7.4,7.5,7.6,7.5,7.1,6.7,6.6,6.4,6.6,6.3,6.1,6.0,5.9,6.2,6.4,6.2,5.9,5.7,5.5,5.3,5.2,5.1,5.3,5.5,5.7,6.0,6.4,7.0,7.4,7.8,8.1,8.4,9.6,10.1,10.4,10.5,11.1,12.0,12.3,12.5,12.6,13.6,14.7,15.1,15.0,15.7,15.4,15.5,15.7,15.1,14.8,14.5,16.0,15.2,16.0,16.2,15.9,15.9,16.0,16.1,16.9,16.6,16.4,16.3,17.0,16.7,16.5,17.4,17.1,17.3,17.5,17.4,17.7,18.2,17.9,19.4,17.8,17.7,17.8,17.9,18.6,17.8]},\"RR\":{\"name\":\"Niederschlag
            // der letzten 10
            // Minuten\",\"unit\":\"mm\",\"data\":[0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.1,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0]},\"P\":{\"name\":\"Luftdruck\",\"unit\":\"hPa\",\"data\":[972.1,972.0,972.0,972.0,971.9,971.8,971.9,971.9,971.9,972.0,972.0,972.1,972.1,972.1,972.4,972.3,972.6,972.6,972.7,972.9,973.1,973.1,973.1,973.3,973.4,973.6,973.6,973.7,973.8,974.0,974.2,974.3,974.3,974.5,974.7,974.8,974.9,974.9,975.1,975.1,975.2,975.2,975.3,975.4,975.4,975.5,975.5,975.5,975.6,975.6,975.5,975.5,975.5,975.5,975.5,975.5,975.5,975.6,975.6,975.5,975.6,975.7,975.6,975.6,975.5,975.4,975.4,975.4,975.5,975.5,975.5,975.4,975.3,975.3,975.4,975.4,975.5,975.4,975.4,975.5,975.5,975.5,975.5,975.6,975.7,975.8,975.8,975.9,975.9,975.8,975.9,976.0,976.0,976.0,976.0,976.0,976.2,976.2,976.3,976.3,976.2,976.3,976.2,976.2,976.2,976.2,976.0,975.9,975.9,975.9,975.9,976.0,976.0,976.0,976.0,975.9,976.0,976.0,975.9,975.9,975.9,975.9,975.7,975.8,975.7,975.7,975.6,975.6,975.6,975.6,975.6,975.5,975.3,975.3,975.4,975.3,975.3,975.3,975.3,975.3,975.4,975.3,975.3,975.3]},\"RF\":{\"name\":\"Relative
            // Feuchte\",\"unit\":\"%\",\"data\":[63.0,64.0,65.0,63.0,62.0,62.0,63.0,63.0,62.0,62.0,64.0,62.0,64.0,65.0,68.0,67.0,66.0,65.0,65.0,64.0,64.0,66.0,63.0,65.0,67.0,69.0,68.0,73.0,76.0,77.0,78.0,78.0,80.0,81.0,82.0,82.0,84.0,84.0,83.0,82.0,83.0,83.0,84.0,83.0,84.0,82.0,83.0,83.0,84.0,84.0,85.0,85.0,84.0,86.0,87.0,89.0,90.0,90.0,91.0,92.0,92.0,92.0,92.0,91.0,90.0,90.0,92.0,92.0,92.0,93.0,91.0,92.0,91.0,92.0,92.0,90.0,89.0,89.0,90.0,91.0,91.0,92.0,92.0,93.0,92.0,91.0,91.0,91.0,90.0,84.0,83.0,79.0,79.0,76.0,74.0,70.0,70.0,69.0,68.0,61.0,60.0,56.0,58.0,52.0,51.0,47.0,42.0,37.0,35.0,36.0,32.0,34.0,33.0,34.0,32.0,31.0,33.0,32.0,30.0,30.0,31.0,27.0,28.0,27.0,28.0,27.0,28.0,27.0,28.0,26.0,27.0,25.0,25.0,26.0,24.0,24.0,25.0,28.0,26.0,25.0,24.0,23.0,25.0,24.0]},\"SO\":{\"name\":\"Sonnenscheindauer\",\"unit\":\"sec\",\"data\":[0.0,0.0,0.0,0.0,0.0,0.0,33.0,204.0,597.0,304.0,102.0,236.0,0.0,36.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,131.0,543.0,205.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,508.0,600.0,600.0,600.0,600.0,600.0,600.0,600.0,600.0,600.0,600.0,600.0,600.0,600.0,594.0,557.0,498.0,580.0,582.0,194.0,0.0,0.0,515.0,132.0,439.0,513.0,258.0,324.0,327.0,600.0,600.0,553.0,356.0,240.0,478.0,416.0,163.0,575.0,326.0,511.0,599.0,406.0,527.0,547.0,516.0,594.0,180.0,461.0,557.0,558.0,600.0,406.0]}},\"station\":\"11238\"}}]}";
            // logger.info("fix response " + resp);

            JsonParser parser = new JsonParser();
            JsonElement root_node = parser.parse(resp.body());
            // JsonElement root_node = parser.parse(resp);
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
                for (int i = 0; i < timestamps.size(); ++i) {
                    Double value = data.get(i).getAsDouble();
                    sum += value;
                    cnt++;
                    if (value > max)
                        max = value;
                    if (value < min)
                        min = value;
                }
                switch (para.getKey()) {
                    case "TL":
                    case "RF":
                    case "P":
                        weatherData.put(para.getKey() + "_MAX", max);
                        weatherData.put(para.getKey() + "_MIN", min);
                        weatherData.put(para.getKey() + "_AVG", sum / cnt);
                        break;
                    case "RR":
                    case "SO":
                        weatherData.put(para.getKey() + "_ACC", sum);
                        break;
                }
            }
            logger.info("adding data to map: " + weatherData.toString());
            return weatherData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, Number> getForecastWeather(String locationLatLon) {
        try {
            Logger logger = LoggerFactory.getLogger(geosphereATHandler.class);
            Instant now = Instant.now();
            Instant start_time = now;
            Instant end_time = now.plus(1, ChronoUnit.DAYS);

            String url = BASE_URL + "/timeseries/forecast/" + FCDATASET + "?lat_lon=" + locationLatLon
                    + "&parameters=rain_acc&parameters=t2m&parameters=rh2m&parameters=sp" + "&start="
                    + start_time.toString() + "&end=" + end_time.toString() + "&output_format=geojson";
            logger.info("send forecast request: " + url);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            logger.info("got response to forecast weather request: " + resp.body());
            // String resp =
            // "{\"reference_time\":\"2025-05-24T09:00+00:00\",\"media_type\":\"application/json\",\"type\":\"FeatureCollection\",\"version\":\"v1\",\"timestamps\":[\"2025-05-24T15:00+00:00\",\"2025-05-24T16:00+00:00\",\"2025-05-24T17:00+00:00\",\"2025-05-24T18:00+00:00\",\"2025-05-24T19:00+00:00\",\"2025-05-24T20:00+00:00\",\"2025-05-24T21:00+00:00\",\"2025-05-24T22:00+00:00\",\"2025-05-24T23:00+00:00\",\"2025-05-25T00:00+00:00\",\"2025-05-25T01:00+00:00\",\"2025-05-25T02:00+00:00\",\"2025-05-25T03:00+00:00\",\"2025-05-25T04:00+00:00\",\"2025-05-25T05:00+00:00\",\"2025-05-25T06:00+00:00\",\"2025-05-25T07:00+00:00\",\"2025-05-25T08:00+00:00\",\"2025-05-25T09:00+00:00\",\"2025-05-25T10:00+00:00\",\"2025-05-25T11:00+00:00\",\"2025-05-25T12:00+00:00\",\"2025-05-25T13:00+00:00\",\"2025-05-25T14:00+00:00\"],\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[15.465999999999852,47.049000000000156]},\"properties\":{\"parameters\":{\"rain_acc\":{\"name\":\"total
            // rainfall amount\",\"unit\":\"kg
            // m-2\",\"data\":[0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0]},\"t2m\":{\"name\":\"2m
            // temperature\",\"unit\":\"degree
            // Celsius\",\"data\":[17.1,16.2,15.0,14.2,13.0,11.4,10.2,9.5,9.1,8.6,8.0,7.4,6.4,6.5,9.2,12.6,15.1,16.5,17.5,18.2,18.5,19.0,19.4,19.0]},\"rh2m\":{\"name\":\"relative
            // humidity 2m above
            // ground\",\"unit\":\"%\",\"data\":[44.54,49.71,56.4,60.38,66.61,74.59,77.66,78.98,78.19,79.67,79.22,76.76,75.93,72.34,61.28,49.57,41.31,34.92,34.01,33.32,34.09,34.66,37.92,43.87]},\"sp\":{\"name\":\"surface
            // pressure\",\"unit\":\"Pa\",\"data\":[97344.64,97363.14,97394.95,97433.7,97504.25,97547.04,97569.59,97570.75,97562.66,97560.34,97541.26,97523.91,97515.24,97523.33,97525.64,97529.69,97514.08,97489.21,97468.97,97423.86,97370.08,97325.55,97282.18,97260.79]}}}}]}";
            // logger.info("fix response " + resp);

            JsonParser parser = new JsonParser();
            JsonElement root_node = parser.parse(resp.body());
            // JsonElement root_node = parser.parse(resp);
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
                for (int i = 0; i < timestamps.size(); ++i) {
                    Double value = data.get(i).getAsDouble();
                    sum += value;
                    cnt++;
                    if (value > max)
                        max = value;
                    if (value < min)
                        min = value;
                }
                switch (para.getKey()) {
                    case "t2m":
                    case "rh2m":
                    case "sp":
                        weatherData.put(para.getKey() + "_MAX", max);
                        weatherData.put(para.getKey() + "_MIN", min);
                        weatherData.put(para.getKey() + "_AVG", sum / cnt);
                        break;
                    case "rain_acc":
                        weatherData.put(para.getKey() + "_ACC", sum);
                        break;
                }
            }
            logger.info("adding data to map: " + weatherData.toString());
            return weatherData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
