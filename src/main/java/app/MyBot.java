package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jibble.pircbot.PircBot;

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
            String location;
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
            DecimalFormat df = new DecimalFormat("0.00");

            sendMessage(channel, "The weather’s going to be " + df.format(temp[0]) + " with a high of "
                    + df.format(temp[2]) + " and a low of " + df.format(temp[1]) + ".");
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
            temps[0] = convertTemp(Double.parseDouble(data));

            data = convertJSON(JSONContent, "temp_min");
            temps[1] = convertTemp(Double.parseDouble(data));

            data = convertJSON(JSONContent, "temp_max");
            temps[2] = convertTemp(Double.parseDouble(data));

            return temps;
        } finally {
            connection.disconnect();
        }
    }

    static String convertJSON(String content, String section) {
        JsonObject object = JsonParser.parseString(content).getAsJsonObject();
        return object.getAsJsonObject("main").get(section).getAsString();
    }

    static double convertTemp(Double temp) {
        return (temp * ((double) 9 / 5)) - 459.67;
    }
}
