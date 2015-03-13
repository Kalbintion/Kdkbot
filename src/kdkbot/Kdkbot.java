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
import java.util.Map;
import java.util.regex.Pattern;

import org.jibble.pircbot.*;

import kdkbot.channel.*;
import kdkbot.filemanager.*;
import kdkbot.twitchapi.TwitchAPI;

public class Kdkbot extends PircBot {
	private String version = "0.1.0.20";
	// public static Kdkbot BOT;
	// public static ArrayList<Channel> CHANS = new ArrayList<Channel>();
	public static HashMap<String, Channel> CHANS = new HashMap<String, Channel>();
	public Config botCfg = new Config(FileSystems.getDefault().getPath("./cfg/settings.cfg"));
	public Config msgIgnoreCfg = new Config(FileSystems.getDefault().getPath("./cfg/ignores.cfg"));
	public ArrayList<String> msgIgnoreList = new ArrayList<String>();
	private boolean _verbose = false;
	private boolean _logChat = false;
	private Pattern logIgnores;
	private Log logger;
	public Debugger dbg = new Debugger(false);
	public TwitchAPI twitch;
	
	private HashMap<String, ArrayList<String>> messageDuplicatorList;
	
    /**
     * Initialization of the basic bot
     */
	public Kdkbot() throws Exception {
		// Setup log system
		botCfg.loadConfigContents();
		this._logChat = Boolean.parseBoolean(botCfg.getSetting("logChat"));
		logIgnores = Pattern.compile(botCfg.getSetting("logIgnores"));
				
		// Setup this instances chat logger
		if(_logChat) {
			this.logger = new Log();
		}
		
		// Setup this bot
		this.setEncoding("UTF-8");
		this.setName(botCfg.getSetting("nick"));
		this._verbose = Boolean.parseBoolean(botCfg.getSetting("verbose"));
		this.setVerbose(_verbose);
		this.connect(botCfg.getSetting("irc"), Integer.parseInt(botCfg.getSetting("port")), "oauth:" + botCfg.getSetting("oauth"));
		messageDuplicatorList = new HashMap<String, ArrayList<String>>();
		
		this.twitch = new TwitchAPI(botCfg.getSetting("clientId"), botCfg.getSetting("access_code"));
		
		// Get channels
		String[] cfgChannels = botCfg.getSetting("channels").split(",");
		
		// Join channels
		for(int i = 0; i < cfgChannels.length; i++) {
			CHANS.put(cfgChannels[i], new Channel(this, cfgChannels[i]));
			dbg.writeln(this, "Added new channel object for channel: " + cfgChannels[i]);
			dbg.writeln(this, "Channel object: " + getChannel(cfgChannels[i]));
		}
	}

	/**
	 * Event handler for disconnecting from a server
	 */
	@Override
	public void onDisconnect() {
		try {
			this.reconnect();
			// Iterator<Channel> chanIter = CHANS.iterator();
			Iterator chanIter = CHANS.entrySet().iterator();
			
			while(chanIter.hasNext()) {
				Map.Entry<String, Channel> chan = (Map.Entry) chanIter.next();
				chan.getValue().joinChannel();
			}
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
    		logger.logln(System.currentTimeMillis() + " " + line);
    	}
    }
    
    /**
     * Event handler for action messages received
     */
    public void onAction(String sender, String login, String hostname, String target, String action) {
    	if(messageDuplicatorList.get(target) != null) {
    		Iterator<String> msgDupeIter = messageDuplicatorList.get(target).iterator();
    		while(msgDupeIter.hasNext()) {
    			this.sendMessage(msgDupeIter.next(), "*" + sender + " " + action  + "*");
    		}
    	}
    }
    
	/**
	 * Event handler for messages received
	 */
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
    	// Message Duplicator
    	if(messageDuplicatorList.get(channel) != null && !sender.equalsIgnoreCase("coebot")) {
    		Iterator<String> msgDupeIter = messageDuplicatorList.get(channel).iterator();
    		while(msgDupeIter.hasNext()) {
        		this.sendMessage(msgDupeIter.next(), sender + ": " + message);
    		}
    	}
    	
    	// Master Commands
    	if(sender.equalsIgnoreCase("taitfox") || sender.equalsIgnoreCase("kalbintion")) {
    		if(message.equalsIgnoreCase("||msgbreakall")) {
    			messageDuplicatorList.clear();
    			this.sendMessage(channel, "Breaking all message dupe systems!");
    		} else if(message.equalsIgnoreCase("||msgbreak ")) {
    			
    		}
    	}
    	
    	if(sender.equalsIgnoreCase("kalbintion")) {
    		if(message.equalsIgnoreCase("||leavechan")) {
    			// Leave channel
    			this.sendMessage(channel, "Leaving by the order of the king, Kalbintion!");
    			this.partChannel(channel, "By order of the king!");
    			
    			// Remove it from setting list
    			String prevChanSetting = botCfg.getSetting("channels");
    			// Remove it from the setting
    			prevChanSetting = prevChanSetting.replace(channel, "");
    			// Remove duplicated commas that can result from removing from channel
    			prevChanSetting = prevChanSetting.replace(",,", ",");
    			
    			botCfg.saveSettings();
    		} else if(message.startsWith("||debug disable")) {
    			dbg.disable();
    			this.sendMessage(channel, "Disabled internal debug messages");
    		} else if(message.startsWith("||msgdupe ")) {
    			String[] chanArgs = message.split(" ");
    			if(messageDuplicatorList.get(chanArgs[1]) == null) {
    				messageDuplicatorList.put(chanArgs[1], new ArrayList<String>());
    			}
    			messageDuplicatorList.get(chanArgs[1]).add(chanArgs[2]);
    			this.sendMessage(chanArgs[1], "Now sending all messages from this channel to " + chanArgs[2]);
    			this.sendMessage(chanArgs[2], "Now receiving all messages from " + chanArgs[1]);
    		} else if(message.startsWith("||msgdupeto ")) {
    			String[] chanArgs = message.split(" ");
    			if(messageDuplicatorList.get(channel) == null) {
    				messageDuplicatorList.put(channel, new ArrayList<String>());
    			}
    			messageDuplicatorList.get(channel).add(chanArgs[1]);
    			this.sendMessage(channel, "Now sending all messages from this channel to " + chanArgs[1]);
    			this.sendMessage(chanArgs[1], "Now receiving all messages from " + channel);
    		} else if(message.startsWith("||debug enable")) {
    			dbg.enable();
    			this.sendMessage(channel, "Enabled internal debug messages");
    		} else if(message.startsWith("||stop")) {
    			this.disconnect();
    			System.exit(0);
    		} else if(message.startsWith("||echo " )) {
    			String messageToSend = message.substring("||echo ".length());
    			this.sendMessage(channel, messageToSend);
    		} else if(message.startsWith("||echoto ")) {
    			String messageArgs[] = message.split(" ", 3);
    			this.sendMessage(messageArgs[1], messageArgs[2]);
    		} else if(message.startsWith("||echotoall ")) {
    			String messageArgs[] = message.split(" ", 2);
    			Iterator chanIter = CHANS.entrySet().iterator();
    			while(chanIter.hasNext()) {
    				Map.Entry pairs = (Map.Entry) chanIter.next();
    				this.sendMessage(pairs.getKey().toString(), messageArgs[1]);
    			}
    			
    		} else if(message.startsWith("||joinchan ")) {
    			String[] args = message.split(" ");
    			
    			// Join channel
    			String channelToJoin = message.substring("||joinchan ".length());
    			this.sendMessage(channel, "Joining channel " + channelToJoin);
    			CHANS.put(channelToJoin, new Channel(this, channelToJoin));
    			
    			if(!(args.length < 3) && args[2].equalsIgnoreCase("false")) {
    				this.sendMessage(channelToJoin, "Hello chat! I am Kdkbot, a bot authored by Kalbintion.");
    			}
    			
    			// Add channel to settings cfg
    			botCfg.setSetting("channels", botCfg.getSetting("channels") + "," + channelToJoin);
    		} else if(message.startsWith("||color ")) {
    			String colorArgs[] = message.split(" ");
    			this.sendMessage(channel, "/color " + colorArgs[1]);
    			this.sendMessage(channel, "Changed color to " + colorArgs[1]);
    		} else if(message.equalsIgnoreCase("||initchan")) {
    			Channel chan = getChannel(channel);
    			chan.setSenderRank("kalbintion", 5);
    			chan.setSenderRank(channel.substring(1), 5);
    			this.sendMessage(channel, "Initialized channel by giving user " + channel.substring(1) + " and Kalbintion permission level 5");
    		}
    	}
    	
    	if(!this.msgIgnoreList.contains(sender)) {
	    	// Send info off to correct channel
    		
	    	Channel curChan = getChannel(channel);
	    	MessageInfo msgInfo = new MessageInfo(channel, sender, message, login, hostname,  curChan.getSenderRank(sender));
    		curChan.commands.commandHandler(msgInfo);
    	}
	}
    
    public Channel getChannel(String channel) {
    	dbg.writeln(this, "Requested for channel object for channel " + channel);

    	return this.CHANS.get(channel);
    }
}
