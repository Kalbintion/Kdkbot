package kdkbot;

import java.lang.reflect.Array;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;

import org.jibble.pircbot.*;
import kdkbot.channel.*;
import kdkbot.filemanager.*;

public class Kdkbot extends PircBot {
	private String version = "0.1.0.18";
	public static Kdkbot BOT;
	public static ArrayList<Channel> CHANS = new ArrayList<Channel>();
	public Config botCfg = new Config(FileSystems.getDefault().getPath("./cfg/settings.cfg"));
	
    /**
     * Initialization of the basic bot
     */
	public Kdkbot() throws Exception {
		BOT = this;
		BOT.setName(botCfg.getSetting("nick"));
		BOT.setVerbose(Boolean.parseBoolean(botCfg.getSetting("verbose")));
		BOT.connect(botCfg.getSetting("irc"), Integer.parseInt(botCfg.getSetting("port")), "oauth:" + botCfg.getSetting("oauth"));

		// Get channels
		String[] cfgChannels = botCfg.getSetting("channels").split(",");
		
		// Join channels
		for(int i = 0; i < cfgChannels.length; i++) {
			CHANS.add(new Channel(BOT, cfgChannels[i]));
		}
	}
	
	/**
	 * Event handler for messages received
	 */
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
    	// Temp Section
    	if(channel.equalsIgnoreCase("#kalbintion")) {
	    	if(message.equalsIgnoreCase("!mumble")) {
	    		BOT.sendMessage(channel, "Mumble IP: mumble.thespawn.net Port: 6667");
	    	} else if (message.equalsIgnoreCase("!pmc")) {
	    		BOT.sendMessage(channel,  "The server url is us.playmindcrack.com");
	    	} else if (message.equalsIgnoreCase("!dvz")) {
	    		BOT.sendMessage(channel, "DvZ stands for Dwarves Vs Zombies, a mini-game on the playmindcrack server, developed by BruceWillakers.");
	    	} else if (message.startsWith("!raid")) {
	    		String args = message.substring(("!raid ").length(), message.length());
	    		BOT.sendMessage(channel, "Go raid http://www.twitch.tv/" + args);
	    	} else if (message.equalsIgnoreCase("!hivemc")) {
	    		BOT.sendMessage(channel, "The server url is hivemc.eu");
	    	}
    	}
    	if (message.startsWith("!multi")) {
    		String[] args = message.split(" ");
    		String out = "http://multitwitch.tv/";
    		for(int i = 1; i < args.length; i++) {
    			out += args[i] + "/";
    		}
    		BOT.sendMessage(channel, out);
    	} else if (message.equalsIgnoreCase("!mumble get")) {
    		BOT.sendMessage(channel, "You can download mumble @ http://wiki.mumble.info/wiki/Main_Page");
    	}
    	/*
    	// Send info off to correct channel
    	Iterator<Channel> iter = CHANS.iterator();
    	while(iter.hasNext()) {
    		Channel curChan = iter.next();
    		if(curChan.getChannel().equalsIgnoreCase(channel)) {
    			curChan.commands.commandHandler(channel, sender, login, hostname, message);
    			break;
    		}
    	}
    	*/
	}
}
