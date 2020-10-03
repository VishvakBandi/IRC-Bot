package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.jar.JarFile;

import java.io.*;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jibble.*;
import org.jibble.pircbot.*;

public class MyBot extends PircBot {
    public MyBot() {
        this.setName("vishvakbot");
    }

    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        Keys APIkeys = new Keys();

        String weatherKey = APIkeys.getWeatherKey();
        String bearer = APIkeys.getTwitterBearer();

        if (message.contains("twitter")) {
            String tags[] = new String[39];

            try {
                tags = getTrends("2388929", bearer);

            } catch (IOException e) {
                System.out.println(e);
            }

            sendMessage(channel, "The top 5 trending tags right now are " + tags[1] + ", " + tags[2] + ", " + tags[3]
                    + ", " + tags[4] + ", " + tags[5]);
        }

        if (message.contains("weather")) {
            String location = "dallas";
            double temp[] = new double[3];

            String[] city = message.split(" ");

            if (city.length == 2) {
                location = city[1];
                try {
                    temp = getWeather(location, weatherKey);
                } catch (IOException err) {
                    System.out.println(err);
                }
            } else if (message.contains("weather")) {
                location = message.replaceAll("\\D+", ""); // remove non-digits
                try {
                    temp = getWeather(location, weatherKey);
                } catch (IOException err) {
                    System.out.println(err);
                }
            }

            sendMessage(channel, "The weather’s going to be " + temp[0] + " with a high of " + temp[2]
                    + " and a low of " + temp[1] + ".");
        }
    }

    static String[] getTrends(String city, String bearer) throws IOException {
        URL url = new URL("https://api.twitter.com/1.1/trends/place.json?id=" + city);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        connection.setRequestProperty("Authorization", "Bearer " + bearer);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder content = new StringBuilder();

        String line;

        while ((line = in.readLine()) != null) {
            content.append(line);
            content.append(System.lineSeparator());

        }
        String JSONContent = content.toString();

        int indexSearch = 0;
        int indexFound = 0;

        String tags[] = new String[39];

        int count = 0;

        while ((indexFound = JSONContent.indexOf("name", indexSearch)) != -1) {
            JSONContent = JSONContent.substring(indexFound + 7, JSONContent.length());
            int index = JSONContent.indexOf('"');
            tags[count] = JSONContent.substring(0, index);
            count++;
        }
        return tags;
    }

    static double[] getWeather(String city, String Key) throws IOException {
        HttpURLConnection connection;

        URL link = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + Key);

        connection = (HttpURLConnection) link.openConnection();

        try {
            connection.setRequestMethod("GET");

            // modify URL
            // Hit API
            // Print contents

            StringBuilder content = new StringBuilder();

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

                String line;

                while ((line = in.readLine()) != null) {

                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }
            String JSONContent = content.toString();

            // convert the JSON to an object
            double temps[] = new double[3];
            String data = " ";

            data = convertJSON(JSONContent, "temp");
            temps[0] = Double.parseDouble(data);

            data = convertJSON(JSONContent, "temp_min");
            temps[1] = Double.parseDouble(data);

            data = convertJSON(JSONContent, "temp_max");
            temps[2] = Double.parseDouble(data);

            return temps;
        } finally {
            connection.disconnect();
        }
    }

    static String convertJSON(String content, String section) {
        JsonObject object = JsonParser.parseString(content).getAsJsonObject();
        String data = object.getAsJsonObject("main").get(section).getAsString();

        return data;
    }
}
