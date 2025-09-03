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
import java.util.concurrent.locks.ReentrantLock;

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
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            logger.debug("got response to weather station id request: " + resp.body());

            JsonElement root_node = JsonParser.parseString(resp.body());
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
                    + "&parameters=TL&parameters=RR&parameters=P&parameters=RF&parameters=SO"
                    + "&output_format=geojson";
            logger.debug("send current request: " + url);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            logger.debug("got response to current weather request: " + resp.body());

            JsonElement root_node = JsonParser.parseString(resp.body());
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
            Instant end_time = now;

            String url = BASE_URL + "/station/historical/" + DATASET + "?station_ids="
                    + weather_station_ids.get(stationName)
                    + "&parameters=TL&parameters=RR&parameters=P&parameters=RF&parameters=SO" + "&start="
                    + start_time.toString() + "&end=" + end_time.toString() + "&output_format=geojson";
            logger.debug("send historic request: " + url);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            logger.debug("got response to historic weather request: " + resp.body());

            JsonElement root_node = JsonParser.parseString(resp.body());
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
            logger.debug("adding data to map: " + weatherData.toString());
            return weatherData;
        } catch (Exception e) {
            // e.printStackTrace();
            logger.error(e.getMessage());
            return null;
        }
    }

    public static Map<String, Number> getForecastWeather(String locationLatLon) {
        Logger logger = LoggerFactory.getLogger(geosphereATHandler.class);
        try {
            Instant now = Instant.now();
            Instant start_time = now;
            Instant end_time = now.plus(1, ChronoUnit.DAYS);

            String url = BASE_URL + "/timeseries/forecast/" + FCDATASET + "?lat_lon=" + locationLatLon
                    + "&parameters=rain_acc&parameters=t2m&parameters=rh2m&parameters=sp" + "&start="
                    + start_time.toString() + "&end=" + end_time.toString() + "&output_format=geojson";
            logger.debug("send forecast request: " + url);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            logger.debug("got response to forecast weather request: " + resp.body());

            JsonElement root_node = JsonParser.parseString(resp.body());
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
                }
                switch (para.getKey()) {
                    case "t2m":
                    case "rh2m":
                        weatherData.put(para.getKey() + "_MAX", max);
                        weatherData.put(para.getKey() + "_MIN", min);
                        weatherData.put(para.getKey() + "_AVG", sum / cnt);
                        break;
                    case "sp":
                        weatherData.put(para.getKey() + "_MAX", max / 100.0);
                        weatherData.put(para.getKey() + "_MIN", min / 100.0);
                        weatherData.put(para.getKey() + "_AVG", sum / cnt / 100.0);
                        break;
                    case "rain_acc":
                        weatherData.put(para.getKey() + "_ACC", sum);
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
}
