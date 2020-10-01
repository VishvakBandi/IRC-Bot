package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.jar.JarFile;

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
        if (message.contains("weather")) {
            String location = "dallas";
            double temp[] = new double[3];

            String[] city = message.split(" ");

            if (city.length == 2) {
                location = city[1];
                try {
                    temp = getWeather(location);
                } catch (IOException err) {
                    System.out.println(err);
                }
            }

            // sendMessage(channel, sender + " the temperature in " + city[1] + " is now " +
            // temp);
            sendMessage(channel, "The weather’s going to be " + temp[0] + " with a high of " + temp[2]
                    + " and a low of " + temp[1] + ".");
        }
    }

    static double[] getWeather(String city) throws IOException {
        HttpURLConnection connection;

        String APIKey = "deecdb003a627c10b15a99c60d4bb45f";

        URL link = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + APIKey);

        connection = (HttpURLConnection) link.openConnection();

        try {
            connection.setRequestMethod("GET");

            // modify URL
            // Hit API
            // Print contents

            StringBuilder content;

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

                String line;
                content = new StringBuilder();

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
