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

import org.jibble.pircbot.*;
import org.jibble.*;

public class App extends PircBot {
    public static void main(String[] args) throws Exception {
        // start bot function
        // this function needs to start the bot
        // read in user input from bot
        // pass input to getWeather
        // send data back to bot
        // print data

        MyBot bot = new MyBot();

        bot.connect("irc.freenode.net");

        bot.joinChannel("#khan-bot");
    }

}