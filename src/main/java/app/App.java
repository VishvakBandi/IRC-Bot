package app;

import org.jibble.pircbot.*;

public class App extends PircBot {
    public static void main(String[] args) throws Exception {
        // start bot
        MyBot bot = new MyBot();

        // connect to freenode
        bot.connect("irc.freenode.net");

        // join the right channel
        bot.joinChannel("#khan-bot");

        // send initial message
        bot.sendMessage("#khan-bot",
                "To use this bot, type 'weather <zipcode>' 'weather <city> or 'twitter' to see the weather, or trending twitter tags.");
    }
}