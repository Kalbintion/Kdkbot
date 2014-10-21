package kdkbot;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.jibble.pircbot.*;

import kdkbot.channel.*;
import kdkbot.filemanager.*;

public class Kdkbot extends PircBot {
	private String version = "0.1.0.18";
	public static Kdkbot BOT;
	public static ArrayList<Channel> CHANS = new ArrayList<Channel>();
	public Config botCfg = new Config(FileSystems.getDefault().getPath("./cfg/settings.cfg"));
	public Config msgIgnoreCfg = new Config(FileSystems.getDefault().getPath("./cfg/ignores.cfg"));
	public ArrayList<String> msgIgnoreList = new ArrayList<String>();
	private boolean _verbose = false;
	private boolean _logChat = false;
	private Pattern logIgnores;
	
    /**
     * Initialization of the basic bot
     */
	public Kdkbot() throws Exception {
		// Setup log system
		this._logChat = Boolean.parseBoolean(botCfg.getSetting("logChat"));
		logIgnores = Pattern.compile(botCfg.getSetting("logIgnores"));
		
		// Setup this bot
		BOT = this;
		BOT.setEncoding("UTF-8");
		BOT.setName(botCfg.getSetting("nick"));
		this._verbose = Boolean.parseBoolean(botCfg.getSetting("verbose"));
		BOT.setVerbose(_verbose);
		BOT.connect(botCfg.getSetting("irc"), Integer.parseInt(botCfg.getSetting("port")), "oauth:" + botCfg.getSetting("oauth"));

		// Get channels
		String[] cfgChannels = botCfg.getSetting("channels").split(",");
		
		// Join channels
		for(int i = 0; i < cfgChannels.length; i++) {
			CHANS.add(new Channel(BOT, cfgChannels[i]));
		}
	}
	
	/**
	 * Event handler for disconnecting from a server
	 */
	@Override
	public void onDisconnect() {
		try {
			this.reconnect();
		} catch (NickAlreadyInUseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IrcException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Overrides the PIRC implementation of logging to console for purposes of logging to file as well.
	 * @param line The line in which will be logged
	 */
    @Override
	public void log(String line) {
    	super.log(line);
        
    	// Ensure we're logging chat, and if we are, ensure there isnt a line that needs to be ignored
    	if(this._logChat && !this.logIgnores.matcher(line).find()) {
    		// Location To Store Logs
    		String logPath = "./logs/";
    		
    		// Log contents to a file
    		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
    		Date date = new Date();
    		String logFileName = dateFormat.format(date) + ".log";
    		
    		// Compile log path
    		Path path = Paths.get(logPath + logFileName);
    		
    		// Open File
    		PrintWriter out;
			try {
				out = new PrintWriter(new BufferedWriter(new FileWriter(path.toAbsolutePath().toString(), true)));
				out.println(System.currentTimeMillis() + " " + line);
	    		out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}
    }
    
	/**
	 * Event handler for messages received
	 */
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
    	// Temp Section
    	/* if(channel.equalsIgnoreCase("#kalbintion")) {
	    	String args = message.substring(("!raid ").length(), message.length());
	    	BOT.sendMessage(channel, "Go raid http://www.twitch.tv/" + args);
    	} */
    	/* if (message.startsWith("!multi")) {
    		String[] args = message.split(" ");
    		String out = "http://multitwitch.tv/";
    		for(int i = 1; i < args.length; i++) {
    			out += args[i] + "/";
    		}
    		BOT.sendMessage(channel, out);
    	} else if (message.equalsIgnoreCase("!mumble get")) {
    		BOT.sendMessage(channel, "You can download mumble @ http://wiki.mumble.info/wiki/Main_Page");
    	} */
    	// Master Commands
    	if(sender.equalsIgnoreCase("kalbintion")) {
    		if(message.equalsIgnoreCase("||leavechan")) {
    			BOT.sendMessage(channel, "Leaving by the order of the king, Kalbintion!");
    			BOT.partChannel(channel, "By order of the king!");
    		} else if(message.startsWith("||echo " )) {
    			String messageToSend = message.substring("||echo ".length());
    			BOT.sendMessage(channel, messageToSend);
    		} else if(message.startsWith("||joinchan ")) {
    			String channelToJoin = message.substring("||joinchan ".length());
    			BOT.sendMessage(channel, "Joining channel " + channelToJoin);
    			Channel channelToAdd = new Channel(this, channelToJoin);
    			CHANS.add(channelToAdd);
    			BOT.sendMessage(channelToJoin, "Hello chat! I am Kdkbot, a bot authored by Kalbintion.");
    		} else if(message.startsWith("||ignoreuser ")) {
    			String userToIgnore = message.substring("||ignoreuser ".length());
    			msgIgnoreList.add(userToIgnore);
    		} else if(message.startsWith("||userignored ")) {
    			String userToFind = message.substring("||userignored ".length());
    			if(msgIgnoreList.contains(userToFind)) {
    				BOT.sendMessage(channel, userToFind + " is unable to globally use the bot.");
    			} else {
    				BOT.sendMessage(channel, userToFind + " is able to globally use the bot.");
    			}
    		} else if(message.startsWith("||color")) {
    			String colorArgs[] = message.split(" ");
    			BOT.sendMessage(channel, "/color " + colorArgs[1]);
    			BOT.sendMessage(channel, "Changed color to " + colorArgs[1]);
    		} else if(message.equalsIgnoreCase("||listallperms")) {
    			Iterator<Channel> chan = CHANS.iterator();
    			while(chan.hasNext()) {
    				Channel curChan = chan.next();
    				try {
	    				List<String> cfgContents = curChan.commands.cfgRanks.getConfigContents();
	    				Iterator<String> cfgContentsIter = cfgContents.iterator();
	    				String outMsg = curChan.getChannel() + "=";
	    				while(cfgContentsIter.hasNext()) {
	    					outMsg += cfgContentsIter.next() + " && ";
	    				}
	    				
	    				this.sendMessage(channel, outMsg);
    				} catch(Exception e) {
    					e.printStackTrace();
    				}
    			}
    		} else if(message.equalsIgnoreCase("||listquotessize")) {
    			Iterator<Channel> chanIter = CHANS.iterator();
    			while(chanIter.hasNext()) {
    				Channel chan = chanIter.next();
    				if(chan.getChannel().equalsIgnoreCase(channel)) {
    					HashMap<String, String> quotes = chan.commands.quotes.quotes;
						BOT.sendMessage(channel, "Quote list size: " + quotes.size());
    				}
    			}
    		} else if(message.equalsIgnoreCase("||listallcustomstrings")) {
    			
    		} else if(message.equalsIgnoreCase("||listallcustomstringssize")) {
    			Iterator<Channel> chanIter = CHANS.iterator();
    			while(chanIter.hasNext()) {
    				Channel chan = chanIter.next();
    				if(chan.getChannel().equalsIgnoreCase(channel)) {
						BOT.sendMessage(channel, "Custom Strings list size: " + chan.commands.commandStrings.commands.size());
    				}
    			}
    		}
    	}
    	
    	if(!this.msgIgnoreList.contains(sender)) {
	    	// Send info off to correct channel
	    	Iterator<Channel> iter = CHANS.iterator();
	    	while(iter.hasNext()) {
	    		Channel curChan = iter.next();
	    		if(curChan.getChannel().equalsIgnoreCase(channel)) {
	    			curChan.commands.commandHandler(channel, sender, login, hostname, message);
	    			break;
	    		}
	    	}
    	}
	}
}
