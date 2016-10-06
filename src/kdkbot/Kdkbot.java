package kdkbot;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.jibble.pircbot.*;

import kdkbot.channel.*;
import kdkbot.commands.MessageParser;
import kdkbot.filemanager.*;
import kdkbot.urbanapi.UrbanAPI;
import kdkbot.youtubeapi.YoutubeAPI;

public class Kdkbot extends PircBot {
	public static HashMap<String, Channel> CHANS = new HashMap<String, Channel>();
	public static Kdkbot instance;
	
	public Config botCfg = new Config(FileSystems.getDefault().getPath("./cfg/settings.cfg"));
	public ArrayList<String> msgIgnoreList = new ArrayList<String>();
	private boolean _verbose = false;
	private boolean _logChat = false;
	private Pattern logIgnores;
	private Log logger;
	public Debugger dbg;
	
	private HashMap<String, ArrayList<String>> messageDuplicatorList;
	
    /**
     * Initialization of the basic bot
     */
	public Kdkbot() throws Exception {
		if(instance == null) { // Protection against initializing the bot more than once - singleton!
			instance = this;
		} else {
			throw new Exception("Bot instance already created!");
		}
		
		// Setup log system
		botCfg.loadConfigContents();
		this._logChat = Boolean.parseBoolean(botCfg.getSetting("logChat"));
		logIgnores = Pattern.compile(botCfg.getSetting("logIgnores"));
		
		// Setup this instances chat logger
		if(_logChat) {
			this.logger = new Log();
		}
		
		// Setup the debugger instance
		this.dbg = new Debugger(false);
		
		// Setup this bot
		this.setEncoding("UTF-8");
		this.setName(botCfg.getSetting("nick"));
		this._verbose = Boolean.parseBoolean(botCfg.getSetting("verbose"));
		this.setVerbose(_verbose);
		
		boolean connectionSent = false;
		
		do {
			try {
				this.connect(botCfg.getSetting("irc"), Integer.parseInt(botCfg.getSetting("port")), "oauth:" + botCfg.getSetting("oauth"));
				connectionSent = true;
			} catch(UnknownHostException e) {
				logger.logln("Failed to resolve host for " + botCfg.getSetting("irc") + ". Retrying in 10 seconds.");
				Thread.sleep(10 * 1000); // 10s * 1000ms
			}
		} while (connectionSent = false);
		
		messageDuplicatorList = new HashMap<String, ArrayList<String>>();

		// Get channels
		String[] cfgChannels = botCfg.getSetting("channels").split(",");
		
		// Join channels
		for(int i = 0; i < cfgChannels.length; i++) {
			CHANS.put(cfgChannels[i], new Channel(this, cfgChannels[i]));
			dbg.writeln(this, "Added new channel object for channel: " + cfgChannels[i]);
			dbg.writeln(this, "Channel object: " + getChannel(cfgChannels[i]));
		}
		
		// Instantiate a MessageParser
		new MessageParser();
	}

	/**
	 * Event handler for disconnecting from a server
	 */
	@Override
	public void onDisconnect() {
		logger.log("Bot has disconnected. Will be attempting to re-join.");
		boolean hasReconnected = false;
		int retryAttempts = 1;
		
		do {
			logger.logln("Reconnection retry #" + retryAttempts);
			try {
				this.reconnect();
				// Iterator<Channel> chanIter = CHANS.iterator();
				Iterator<Entry<String, Channel>> chanIter = CHANS.entrySet().iterator();
				
				while(chanIter.hasNext()) {
					Map.Entry<String, Channel> chan = chanIter.next();
					chan.getValue().joinChannel();
				}
				
				hasReconnected = true;
			} catch (NickAlreadyInUseException e) {
				logger.logln("Could not re-connect due to nickname already in use.");
			} catch (UnknownHostException e) {
				logger.logln("Failed to resolve host for " + botCfg.getSetting("irc") + ".");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IrcException e) {
				logger.logln("Could not reconnect to the irc server, disallowed.");
			}
			
			// Only sleep if we havent reconnected, otherwise we can safely exit this function.
			if(!hasReconnected) {
				try {
					// 10s * 1000ms
					Thread.sleep(10 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			retryAttempts++;
			
		} while(!hasReconnected);
	}
	
	/**
	 * Event handler for connecting (successfully) to a server
	 */
	@Override
	public void onConnect() {
		// Re-establishes JOIN/LEAVE msges per Twitch IRCv3 implementation
		sendRawLine("CAP REQ :twitch.tv/membership");
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
     * Event handler for join messages received
     */
    public void onJoin(String channel, String sender, String login, String hostname) {
    	Channel curChan = CHANS.get(channel);
    	MessageInfo info = new MessageInfo(channel, sender, "#JOIN", login, hostname, curChan.getSenderRank(sender));
    	curChan.messageHandler(info);
    }
    
    /**
     * Event handler for part messages received
     */
    public void onPart(String channel, String sender, String login, String hostname) {
    	Channel curChan = CHANS.get(channel);
    	MessageInfo info = new MessageInfo(channel, sender, "#PART", login, hostname, curChan.getSenderRank(sender));
    	curChan.messageHandler(info);
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
    	CHANS.get(target).messageHandler(new MessageInfo(target, sender, action, login, hostname, CHANS.get(target).getSenderRank(sender)));
    }
    
    public void onUnknown(String msg) {
    	System.out.println("UNKNOWN MESSAGE: " + msg);
    }
    
	/**
	 * Event handler for messages received
	 */
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
    	// Message Duplicator
    	if(messageDuplicatorList.get(channel) != null && !sender.equalsIgnoreCase("coebot") && !sender.equalsIgnoreCase("jtv") && !sender.equalsIgnoreCase("monstercat")) {
    		Iterator<String> msgDupeIter = messageDuplicatorList.get(channel).iterator();
    		while(msgDupeIter.hasNext()) {
        		this.sendMessage(msgDupeIter.next(), sender + ": " + message);
    		}
    	}
    	
    	MessageInfo info = new MessageInfo(channel, sender, message, login, hostname, CHANS.get(channel).getSenderRank(sender));
    	
    	// Master Commands
    	// TODO: Remove master commands and write web interface
    	if(sender.equalsIgnoreCase(botCfg.getSetting("masterCommands")) && info.message.startsWith("||")) {
    		if(message.equalsIgnoreCase("||leavechan")) {
    			// Leave channel
    			this.partChannel(channel, "By order of " + sender + "!");
    			
    			// Remove it from setting list
    			String prevChanSetting = botCfg.getSetting("channels");
    			// Remove it from the setting
    			prevChanSetting = prevChanSetting.replace(channel, "");
    			// Remove duplicated commas that can result from removing from channel
    			prevChanSetting = prevChanSetting.replace(",,", ",");
    			
    			botCfg.saveSettings();
    		} else if(message.startsWith("||yttest ")) {
    			Kdkbot.instance.sendMessage(channel, "Linked video: " + YoutubeAPI.getVideoTitle(info.getSegments()[1]));
    		} else if(message.startsWith("||ytidtest ")) {
    			Kdkbot.instance.sendMessage(channel, "Linked video: " + YoutubeAPI.getVideoTitleFromID(info.getSegments()[1]));
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
    			
    			if(messageDuplicatorList.get(chanArgs[1]) == null) {
    				messageDuplicatorList.put(chanArgs[1], new ArrayList<String>());
    			}
    			messageDuplicatorList.get(chanArgs[1]).add(channel);
    			
    			this.sendMessage(channel, "Now sending & receiving all messages from this channel to " + chanArgs[1]);
    			this.sendMessage(chanArgs[1], "Now sending & receiving all messages from " + channel);
    		} else if(message.equalsIgnoreCase("||msgbreakall")) {
        			messageDuplicatorList.clear();
        			this.sendMessage(channel, "Breaking all message dupe systems!");
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
    			Iterator<Entry<String, Channel>> chanIter = CHANS.entrySet().iterator();
    			while(chanIter.hasNext()) {
    				Map.Entry<String, Channel> pairs = chanIter.next();
    				this.sendMessage(pairs.getKey().toString(), messageArgs[1]);
    			}
    		} else if(message.startsWith("||joinchan ")) {
    			String channelToJoin = message.substring("||joinchan ".length());
    			
    			if(this.botCfg.getSetting("channels").contains(channelToJoin)) {
    				instance.sendMessage(channel, "I am already in " + channelToJoin);
    			} else {
        			// Join channel
        			this.sendMessage(channel, "Joining channel " + channelToJoin);
        			CHANS.put(channelToJoin, new Channel(this, channelToJoin));
        			
        			// Add channel to settings cfg
        			botCfg.setSetting("channels", botCfg.getSetting("channels") + "," + channelToJoin);
    			}

    		} else if(message.startsWith("||color ")) {
    			String colorArgs[] = message.split(" ");
    			this.sendMessage(channel, "/color " + colorArgs[1]);
    			this.sendMessage(channel, "Changed color to " + colorArgs[1]);
    		} else if(message.equalsIgnoreCase("||initchan")) {
    			Channel chan = getChannel(channel);
    			chan.setSenderRank(botCfg.getSetting("masterCommands"), 5);
    			chan.setSenderRank(channel.substring(1), 5);
    			this.sendMessage(channel, "Initialized channel by giving user " + channel.substring(1) + " and " + botCfg.getSetting("masterCommands") + " permission level 5");
    			
    			try {
    				
        			FileInputStream cmdIn = new FileInputStream(FileSystems.getDefault().getPath("./cfg/default/cmds.cfg").toAbsolutePath().toString());
        			FileOutputStream cmdOut = new FileOutputStream(FileSystems.getDefault().getPath("./cfg/" + channel + "/cmds.cfg").toAbsolutePath().toString());
					cmdOut.getChannel().transferFrom(cmdIn.getChannel(), 0, cmdIn.getChannel().size());
					cmdIn.close();
	    			cmdOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
    		}
    	}
    	
    	CHANS.get(channel).messageHandler(info);
	}
    
    public Channel getChannel(String channel) {
    	dbg.writeln(this, "Requested for channel object for channel " + channel);

    	return Kdkbot.CHANS.get(channel);
    }
    
    
}
