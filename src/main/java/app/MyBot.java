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
    // start bot
    public MyBot() {
        this.setName("vishvakbot"); // set name
    }

    // this method runs for every message sent in the channel
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        Keys APIkeys = new Keys(); // APIkeys object to retrive the APIkeys from a second file

        String weatherKey = APIkeys.getWeatherKey(); // get weatherAPI key
        String bearer = APIkeys.getTwitterBearer(); // get twitter bearer auth token

        // if the message contains the word twitter
        if (message.contains("twitter")) {
            // array to hold 39 tags
            String tags[] = new String[39];

            try {
                // call the gettrends method for tags
                tags = getTrends("2388929", bearer);

            } catch (IOException e) {
                // if theres an error, end
                System.out.println(e);
            }

            // print out trending tags
            sendMessage(channel, "The top 5 trending tags right now are " + tags[1] + ", " + tags[2] + ", " + tags[3]
                    + ", " + tags[4] + ", " + tags[5]);
        }

        // if the message contains the word weather
        if (message.contains("weather")) {
            // var for location
            String location;
            // var to hold returned temps
            double temp[] = new double[3];

            // split the message words
            String[] city = message.split(" ");

            // runs if there are only 2 words (weather and city)
            if (city.length == 2) {
                // gets location from input
                location = city[1];

                try {
                    // call get weather function
                    temp = getWeather(location, weatherKey);
                } catch (IOException err) {
                    // error out
                    System.out.println(err);
                }

                // in all other cases if the message has the word weather
            } else if (message.contains("weather")) {
                // find all digits in the message
                location = message.replaceAll("\\D+", "");

                try {
                    // call weather function
                    temp = getWeather(location, weatherKey);
                } catch (IOException err) {
                    // error out
                    System.out.println(err);
                }
            }

            // decimal format to round doubles
            DecimalFormat df = new DecimalFormat("0.00");

            // send message with weather
            sendMessage(channel, "The weather’s going to be " + df.format(temp[0]) + " with a high of "
                    + df.format(temp[2]) + " and a low of " + df.format(temp[1]) + ".");
        }
    }

    // trends method - this will get the trending twitter hashtags in the area
    // this will only work for dallas - yahoo does not support WOEID lookup for some
    // reason
    // read - http://cagricelebi.com/blog/dear-twitter-please-stop-using-woeid/
    static String[] getTrends(String city, String bearer) throws IOException {
        // set up URL for the API call
        URL url = new URL("https://api.twitter.com/1.1/trends/place.json?id=" + city);

        // new HTTPConnection object. This objects allows me to open a connection to the
        // URL
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // open a GET request to the URL
        connection.setRequestMethod("GET");

        // set the auth property with the Bearer token as per twitter requirements
        connection.setRequestProperty("Authorization", "Bearer " + bearer);

        // new buffered reader object to read in JSON
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        // new string builder object to move the JSON to a string
        StringBuilder content = new StringBuilder();

        // string to process input
        String line;

        // while valid input is being read in
        while ((line = in.readLine()) != null) {
            // append to line
            content.append(line);
            // add a new line character
            content.append(System.lineSeparator());
        }

        // convert the string builder to a string
        String JSONContent = content.toString();

        // temp ints
        int indexSearch = 0;
        int indexFound = 0;

        // array hold most popular tags
        String tags[] = new String[39];

        int count = 0;

        // process through the JSON until all tags are found
        while ((indexFound = JSONContent.indexOf("name", indexSearch)) != -1) {
            // make substrings of the JSON with each trending tag
            JSONContent = JSONContent.substring(indexFound + 7, JSONContent.length());

            // find the index of the trending tag
            int index = JSONContent.indexOf('"');

            // add to the array
            tags[count] = JSONContent.substring(0, index);

            // increment count
            count++;
        }

        // return the tags array
        return tags;
    }

    // this function will use the OpenWeather API to return weather information for
    // any city or zip code
    // zip codes are only supported in the USA
    // this will return current temperature, high, and low
    static double[] getWeather(String city, String Key) throws IOException {
        // new HTTPConnection object. This objects allows me to open a connection to the
        // URL
        HttpURLConnection connection;

        // set up URL for the API call
        URL link = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + Key);

        // open a GET request to the URL
        connection = (HttpURLConnection) link.openConnection();

        // var to hold returned temperature values
        double temps[] = new double[3];

        try {
            // open a GET request
            connection.setRequestMethod("GET");

            // new string builder object to move the JSON to a string
            StringBuilder content = new StringBuilder();

            // new buffered reader object to read in JSON
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                // string to hold input
                String line;

                // while input is valid
                while ((line = in.readLine()) != null) {
                    // add to the string
                    content.append(line);

                    // add a new line
                    content.append(System.lineSeparator());
                }
            }

            // convert the JSON to a string
            String JSONContent = content.toString();

            // hold temperatures
            String data = " ";

            // call convertJSON 3 times, to use GSON and find the temperatures
            // then call convertTemp to convert the temperature from Kelvin to Farenheit
            data = convertJSON(JSONContent, "temp");
            temps[0] = convertTemp(Double.parseDouble(data));

            data = convertJSON(JSONContent, "temp_min");
            temps[1] = convertTemp(Double.parseDouble(data));

            data = convertJSON(JSONContent, "temp_max");
            temps[2] = convertTemp(Double.parseDouble(data));

        } finally {
            // close the connection
            connection.disconnect();
        }

        // return the temperature
        return temps;
    }

    // this function uses the GSON library to parse the string and return the data
    // value
    static String convertJSON(String content, String section) {
        // create a object and parse the string in
        JsonObject object = JsonParser.parseString(content).getAsJsonObject();

        // return the data in the second we are looking for
        return object.getAsJsonObject("main").get(section).getAsString();
    }

    // this function converts from kelvin to F
    static double convertTemp(Double temp) {
        // return converted temp
        return (temp * ((double) 9 / 5)) - 459.67;
    }
}
