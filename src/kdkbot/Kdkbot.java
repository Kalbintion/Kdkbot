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

public class Kdkbot extends PircBot {
	private String version = "0.1.0.18";
	public static Kdkbot BOT;
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
	
	private HashMap<String, String> messageDuplicatorList;
	
    /**
     * Initialization of the basic bot
     */
	public Kdkbot() throws Exception {
		// Setup log system
		this._logChat = Boolean.parseBoolean(botCfg.getSetting("logChat"));
		logIgnores = Pattern.compile(botCfg.getSetting("logIgnores"));
				
		// Setup this instances chat logger
		if(_logChat) {
			this.logger = new Log();
		}
		
		// Setup this bot
		BOT = this;
		BOT.setEncoding("UTF-8");
		BOT.setName(botCfg.getSetting("nick"));
		this._verbose = Boolean.parseBoolean(botCfg.getSetting("verbose"));
		BOT.setVerbose(_verbose);
		BOT.connect(botCfg.getSetting("irc"), Integer.parseInt(botCfg.getSetting("port")), "oauth:" + botCfg.getSetting("oauth"));
		messageDuplicatorList = new HashMap<String, String>();
		
		// Get channels
		String[] cfgChannels = botCfg.getSetting("channels").split(",");
		
		// Join channels
		for(int i = 0; i < cfgChannels.length; i++) {
			CHANS.put(cfgChannels[i], new Channel(BOT, cfgChannels[i]));
			// CHANS.add(new Channel(BOT, cfgChannels[i]));
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
    		BOT.sendMessage(messageDuplicatorList.get(target), "*" + sender + " " + action  + "*");
    	}
    }
    
	/**
	 * Event handler for messages received
	 */
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
    	// Message Duplicator
    	if(messageDuplicatorList.get(channel) != null) {
    		BOT.sendMessage(messageDuplicatorList.get(channel), sender + ": " + message);
    	}
    	
    	// Master Commands
    	if(sender.equalsIgnoreCase("kalbintion")) {
    		if(message.equalsIgnoreCase("||leavechan")) {
    			// Leave channel
    			BOT.sendMessage(channel, "Leaving by the order of the king, Kalbintion!");
    			BOT.partChannel(channel, "By order of the king!");
    			
    			// Remove it from setting list
    			String prevChanSetting = botCfg.getSetting("channels");
    			// Remove it from the setting
    			prevChanSetting = prevChanSetting.replace(channel, "");
    			// Remove duplicated commas that can result from removing from channel
    			prevChanSetting = prevChanSetting.replace(",,", ",");
    		} else if(message.startsWith("||debug disable")) {
    			dbg.disable();
    			BOT.sendMessage(channel, "Disabled internal debug messages");
    		} else if(message.startsWith("||msgdupe ")) {
    			String[] chanArgs = message.split(" ");
    			messageDuplicatorList.put(chanArgs[1], chanArgs[2]);
    			BOT.sendMessage(chanArgs[1], "Now sending all messages from this channel to " + chanArgs[2]);
    			BOT.sendMessage(chanArgs[2], "Now receiving all messages from " + chanArgs[1]);
    		} else if(message.startsWith("||debug enable")) {
    			dbg.enable();
    			BOT.sendMessage(channel, "Enabled internal debug messages");
    		} else if(message.startsWith("||stop")) {
    			BOT.disconnect();
    			System.exit(0);
    		} else if(message.startsWith("||echo " )) {
    			String messageToSend = message.substring("||echo ".length());
    			BOT.sendMessage(channel, messageToSend);
    		} else if(message.startsWith("||echoto ")) {
    			String messageArgs[] = message.split(" ", 3);
    			BOT.sendMessage(messageArgs[1], messageArgs[2]);
    		} else if(message.startsWith("||joinchan ")) {
    			String[] args = message.split(" ");
    			
    			// Join channel
    			String channelToJoin = message.substring("||joinchan ".length());
    			BOT.sendMessage(channel, "Joining channel " + channelToJoin);
    			Channel channelToAdd = new Channel(this, channelToJoin);
    			CHANS.put(channelToJoin, channelToAdd);
    			
    			if(!(args.length < 3) && args[2].equalsIgnoreCase("false")) {
    				BOT.sendMessage(channelToJoin, "Hello chat! I am Kdkbot, a bot authored by Kalbintion.");
    			}
    			
    			// Add channel to settings cfg
    			botCfg.setSetting("channels", botCfg.getSetting("channels") + "," + channelToJoin);
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
    		} else if(message.startsWith("||color ")) {
    			String colorArgs[] = message.split(" ");
    			BOT.sendMessage(channel, "/color " + colorArgs[1]);
    			BOT.sendMessage(channel, "Changed color to " + colorArgs[1]);
    		}
    	}
    	
    	if(!this.msgIgnoreList.contains(sender)) {
	    	// Send info off to correct channel
	    	Channel curChan = getChannel(channel);
    		curChan.commands.commandHandler(channel, sender, login, hostname, message);
    	}
	}
    
    public Channel getChannel(String channel) {
    	return this.CHANS.get(channel);
    }
}
