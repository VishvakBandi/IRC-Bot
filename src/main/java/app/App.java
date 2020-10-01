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

        String server = "irc.freenode.net";
        String nick = "simple_bot";
        String login = "simple_bot";

        String channel = "#khan-bot";

        // double temp = getWeather("austin");
        /// System.out.println(temp);

        MyBot bot = new MyBot();

        bot.connect("irc.freenode.net");

        bot.joinChannel("#khan-bot");

        // bot.onMessage(channel, "sender", login, server, "time");

        /*
         * // bot
         * 
         * String server = "irc.freenode.net"; String nick = "simple_bot"; String login
         * = "simple_bot";
         * 
         * String channel = "#khan-bot";
         * 
         * Socket socket = new Socket(server, 6667);
         * 
         * BufferedWriter writer = new BufferedWriter(new
         * OutputStreamWriter(socket.getOutputStream()));
         * 
         * BufferedReader reader = new BufferedReader(new
         * InputStreamReader(socket.getInputStream()));
         * 
         * writer.write("NICK " + nick + "\r\n"); writer.write("USER " + login +
         * " 8 * : Java IRC Hacks Bot\r\n"); writer.flush();
         * 
         * String line = ""; while ((line = reader.readLine()) != null) { if
         * (line.contains("004")) { break; } else if (line.contains("433")) {
         * System.out.println("Nickname is already in use."); return; } }
         * 
         * writer.write("JOIN " + channel + "\r\n");
         * 
         * writer.flush();
         * 
         * while ((line = reader.readLine()) != null) { if
         * (line.toLowerCase().startsWith("PING ")) { writer.write("PONG " +
         * line.substring(5) + "\r\n"); writer.write("PRIVMSG " + channel +
         * "I got pinged!\r\n"); writer.flush(); bot.sendMessage(channel, "hello"); //
         * System.out.println("pong"); } else { if (line.contains("hi")) {
         * bot.sendMessage(channel, "esdfsfdg"); } System.out.println(line); //
         * bot.sendMessage(channel, "hello1"); // System.out.println("ping "); } }
         * System.out.println("done");
         */
    }

}