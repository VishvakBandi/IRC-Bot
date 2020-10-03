package app;

import org.jibble.pircbot.*;

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