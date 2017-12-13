package kdkbot;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.jibble.pircbot.*;

import com.google.gson.JsonObject;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import kdkbot.channel.*;
import kdkbot.commands.MessageParser;
import kdkbot.filemanager.*;
import kdkbot.language.*;

public class Kdkbot extends PircBot {
	public static HashMap<String, Channel> CHANS = new HashMap<String, Channel>();
	public static Kdkbot instance;
	public static String botLanguage = "enUS";
	
	public Config botCfg = new Config(FileSystems.getDefault().getPath("./cfg/settings.cfg"));
	public ArrayList<String> msgIgnoreList = new ArrayList<String>();
	private boolean _verbose = false;
	private boolean _logChat = false;
	private Pattern logIgnores;
	private Log logger;
	public Debugger dbg;
	private WebInterfaceWatcher webWatcher;
	
	private HashMap<String, ArrayList<String>> messageDuplicatorList;
	
	// Twitter related variables
	boolean useTwitter = true;
	static Twitter status;
	
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
		if(botCfg.getSetting("enableDebug", "0").equalsIgnoreCase("0")) {
			dbg.disable();
		} else { 
			dbg.enable();
		}
		
		// Setup this bot
		this.setEncoding("UTF-8");
		this.setName(botCfg.getSetting("nick"));
		this._verbose = Boolean.parseBoolean(botCfg.getSetting("verbose"));
		this.setVerbose(_verbose);

		@SuppressWarnings("unused") // Is it really unused? Is it really?
		boolean connectionSent = false;
		
		do {
			try {
				this.connect(botCfg.getSetting("irc"), Integer.parseInt(botCfg.getSetting("port")), "oauth:" + botCfg.getSetting("oauth"));
				connectionSent = true;
			} catch(UnknownHostException e) {
				logger.logln(String.format(Translate.getTranslate("log.failedToResolveDelay", botLanguage), botCfg.getSetting("irc")));
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
		
		// Setup Twitter interface
		if (useTwitter) {
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true)
			  .setOAuthConsumerKey(botCfg.getSetting("twitterOAuthConsumer"))
			  .setOAuthConsumerSecret(botCfg.getSetting("twitterOAuthConsumerSecret"))
			  .setOAuthAccessToken(botCfg.getSetting("twitterOAuth"))
			  .setOAuthAccessTokenSecret(botCfg.getSetting("twitterOAuthSecret"));
			TwitterFactory tf = new TwitterFactory(cb.build());
			status = tf.getInstance();
		}
		
		// Setup web related info
		if (Boolean.parseBoolean(botCfg.getSetting("webEnabled"))) {
			System.out.println("Enabled Web Watcher.");
			System.out.println("Watching @ " + botCfg.getSetting("watcherLocation"));
			webWatcher = new WebInterfaceWatcher(botCfg.getSetting("watcherLocation"), botCfg.getSetting("watcherFile"));
			webWatcher.watch();
		}
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
				if((retryAttempts - 1) % 100 == 0) {
					status.updateStatus(Translate.getTranslate("twitter.disconnectStatus", botLanguage));
				}
				
				this.reconnect();
				// Iterator<Channel> chanIter = CHANS.iterator();
				Iterator<Entry<String, Channel>> chanIter = CHANS.entrySet().iterator();
				
				while(chanIter.hasNext()) {
					Map.Entry<String, Channel> chan = chanIter.next();
					chan.getValue().joinChannel();
				}
				
				hasReconnected = true;
			} catch (NickAlreadyInUseException e) {
				logger.logln(Translate.getTranslate("log.nickInUse", botLanguage));
			} catch (UnknownHostException e) {
				logger.logln(String.format(Translate.getTranslate("log.failedToResolve", botLanguage), botCfg.getSetting("irc")));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IrcException e) {
				logger.logln(Translate.getTranslate("log.ircException", botLanguage));
			} catch (TwitterException e) {
				
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
		
		try {
			status.updateStatus(Translate.getTranslate("twitter.reconnectStatus", botLanguage));
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Event handler for connecting (successfully) to a server
	 */
	public void onConnect() {
		// Re-establishes JOIN/LEAVE msges per Twitch IRCv3 implementation
		sendRawLine("CAP REQ :twitch.tv/membership");
	}
	
	/**
	 * Overrides the PIRC implementation of logging to console for purposes of logging to file as well.
	 * @param line The line in which will be logged
	 */
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
    	Channel curChan = getChannel(channel);
    	int senderRank = 0;
    	if(curChan != null) { senderRank = curChan.getSenderRank(sender); }
    	MessageInfo info = new MessageInfo(channel, sender, "#JOIN", login, hostname, senderRank);
    	if(curChan != null) { curChan.extraHandler(info); }
    }
    
    /**
     * Event handler for part messages received
     */
    public void onPart(String channel, String sender, String login, String hostname) {
    	Channel curChan = getChannel(channel);
    	int senderRank = 0;
    	if(curChan != null) { senderRank = curChan.getSenderRank(sender); }
    	MessageInfo info = new MessageInfo(channel, sender, "#JOIN", login, hostname, senderRank);
    	curChan.extraHandler(info);
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
    	
    	// Master Commands Handler
    	handleMasterCommands(info);
    	
    	CHANS.get(channel).messageHandler(info);
	}
    
    public Channel getChannel(String channel) {
    	dbg.writeln(this, "Requested for channel object for channel " + channel);
    		
    	if(!channel.startsWith("#")) {
    		channel = "#" + channel;
    	}
    	
    	return Kdkbot.CHANS.get(channel);
    }
    
    /**
     * Sets up and joins a particular channel
     * @param channel The channel to join
     * @return -1 if it is already in the channel, 1 if successful, any other value means unsuccessful
     */
    public int enterChannel(String channel) {
    	if (! channel.startsWith("#")) {
    		channel = "#" + channel;
    	}
    	
		if(this.botCfg.getSetting("channels").contains(channel)) {
			return -1;
		} else {
			// Join channel
			this.sendMessage(channel, String.format(Translate.getTranslate("channel.join", botLanguage), channel));
			CHANS.put(channel, new Channel(this, channel));
			
			// Add channel to settings cfg
			botCfg.setSetting("channels", botCfg.getSetting("channels") + "," + channel);
			
			Channel chan = getChannel(channel);
			chan.setSenderRank(botCfg.getSetting("masterCommands"), 5);
			chan.setSenderRank(channel.substring(1), 5);

			// Initialize new commands list for channel if the channel info doesn't exist
			Path path = FileSystems.getDefault().getPath("./cfg/" + channel).toAbsolutePath();
			if(Files.notExists(path)) {
				try {
					FileInputStream cmdIn = new FileInputStream(FileSystems.getDefault().getPath("./cfg/default/cmds.cfg").toAbsolutePath().toString());
					FileOutputStream cmdOut = new FileOutputStream(FileSystems.getDefault().getPath("./cfg/" + channel + "/cmds.cfg").toAbsolutePath().toString());
					cmdOut.getChannel().transferFrom(cmdIn.getChannel(), 0, cmdIn.getChannel().size());
					cmdIn.close();
					cmdOut.close();
				} catch (IOException e) {
					chan.sendMessage(Translate.getTranslate("channel.badInitialize", botLanguage));
				}
			} // else we don't need to attempt to create a new instance for the channels commands
			
			return 1;
		}
    }
    
    /**
     * Exits a given channel
     * @param channel The channel to leave
     */
    public void exitChannel(String channel) {
    	if (! channel.startsWith("#")) {
    		channel = "#" + channel;
    	}
    	
		// Leave channel
		this.partChannel(channel);
		
		// Remove it from setting list
		String prevChanSetting = botCfg.getSetting("channels");
		
		// Remove it from the setting
		prevChanSetting = prevChanSetting.replace(channel, "");
		
		// Remove duplicated commas that can result from removing from channel
		prevChanSetting = prevChanSetting.replace(",,", ",");
		
		botCfg.setSetting("channels", prevChanSetting);
		
		botCfg.saveSettings();
    }
    
    /**
     * Determine if the bot exists in a particular channel
     * @param channel The channel to look up
     * @return True if the bot is in the channel, false otherwise
     */
    public boolean isInChannel(String channel) {
    	return (this.getChannel(channel) == null) ? false : true;
    }
    
    /**
     * Master commands: These commands are designated to be used for debugging purposes or otherwise control the bot that has not been fully implemented in other ways.
     * @param info The message information to use for parsing
     */
    public void handleMasterCommands(MessageInfo info) {
       	// Master Commands
    	if(info.sender.equalsIgnoreCase(botCfg.getSetting("masterCommands")) && info.message.startsWith("&&")) {
    		if(info.message.startsWith("&&debug disable")) {
    			dbg.disable();
    			this.sendMessage(info.channel, "Disabled internal debug messages");
    		} else if(info.message.startsWith("&&debug enable")) {
    			dbg.enable();
    			this.sendMessage(info.channel, "Enabled internal debug messages");
    		} else if (info.message.startsWith("&&ClrLangCache")) { 
    			Translate.resetLanguageCache();
    			this.sendMessage(info.channel, "Reset language cache");
    		} else if(info.message.startsWith("&&stop")) {
    			this.disconnect();
    			System.exit(0);
    		} else if(info.message.startsWith("&&wfdata")) {
    			kdkbot.api.warframe.API.Warframe.getData();
    		} else if(info.message.startsWith("&&wfdaily")) {
    			this.sendMessage(info.channel, kdkbot.api.warframe.API.Warframe.getDailyDeal());
    		} else if(info.message.startsWith("&&wfddtest")) {
    			this.sendMessage(info.channel, kdkbot.api.warframe.API.Warframe.getDailyDealReadable());
    		} else if(info.message.startsWith("&&echo " )) {
    			String messageToSend = info.message.substring("&&echo ".length());
    			this.sendMessage(info.channel, messageToSend);
    		} else if(info.message.startsWith("&&echoto ")) {
    			String messageArgs[] = info.message.split(" ", 3);
    			this.sendMessage(messageArgs[1], messageArgs[2]);
    		} else if(info.message.startsWith("&&echotoall ")) {
    			String messageArgs[] = info.message.split(" ", 2);
    			Iterator<Entry<String, Channel>> chanIter = CHANS.entrySet().iterator();
    			while(chanIter.hasNext()) {
    				Map.Entry<String, Channel> pairs = chanIter.next();
    				this.sendMessage(pairs.getKey().toString(), messageArgs[1]);
    			}
    		} else if(info.message.startsWith("&&color ")) {
    			String colorArgs[] = info.message.split(" ");
    			this.sendMessage(info.channel, "/color " + colorArgs[1]);
    			this.sendMessage(info.channel, String.format(Translate.getTranslate("bot.mastercommands.color", botLanguage), colorArgs[1]));
    		} else if(info.message.startsWith("&&status ")) {
    			try {
					Kdkbot.status.updateStatus(info.message.substring("&&status ".length()));
					Kdkbot.instance.sendMessage(info.channel, Translate.getTranslate("bot.mastercommands.status", botLanguage));
				} catch (TwitterException e) {
					Kdkbot.instance.sendMessage(info.channel, String.format(Translate.getTranslate("bot.mastercommands.statusFail", botLanguage), e.getMessage()));
					e.printStackTrace();
				}
    		} else if(info.message.startsWith("&&ram?")) {
    			Kdkbot.instance.sendMessage(info.channel, String.format(Translate.getTranslate("bot.mastercommands.ram", botLanguage), (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024));
    		} else if(info.message.startsWith("&&gc")) {
    			Kdkbot.instance.sendMessage(info.channel, Translate.getTranslate("bot.mastercommands.gc", botLanguage));
    			System.gc();
    			System.gc();
    		} else if(info.message.startsWith("&&ytviews ")) {
    			Kdkbot.instance.sendMessage(info.channel, "Video views: " + kdkbot.api.youtube.API.getNumberOfVideoViews(info.getSegments(2)[1]));
    		} else if(info.message.startsWith("&&formattest")) {
    			Kdkbot.instance.sendMessage(info.channel, String.format("%1$s", 3));
    		} else if(info.message.startsWith("&&wfmtest ")) {
    			Kdkbot.instance.sendMessage(info.channel, kdkbot.api.warframe.API.Market.getSellStatsDynamic(info.getSegments(2)[1]).toString());
    		} else if(info.message.startsWith("&&jsontest")) {
    			JsonObject tempObj = new JsonObject();
    			tempObj.addProperty("stream", "null");
    			Kdkbot.instance.sendMessage(info.channel, "Json Test: " + tempObj.get("stream").toString());
    			Kdkbot.instance.sendMessage(info.channel, "Json Test: " + tempObj.get("stream").toString().equalsIgnoreCase("\"null\""));
    		} else if(info.message.startsWith("&&livejsontest ")) {
    			kdkbot.api.twitch.APIv5.isStreamerLive(getClientID(), getChannel("#taitfox").getUserID());
    		}
    	}
    }
    
    /**
     * Gets the client ID for twitch
     * @return The Client ID in plain text for twitch
     */
    public String getClientID() {
    	return botCfg.getSetting("clientId");
    }
    
    /**
     * Sends a message through a channels sendMessage function
     * @param channel The channel to find, and consequently send the message through
     * @param message The message to be sent
     */
    public void sendChanMessage(String channel, String message) {
    	try {
	    	Channel chan = getChannel(channel);
	    	chan.sendMessage(message);
    	} catch(NullPointerException e) {
    		// In the event an invalid channel is provided, we don't care, toss away the request.
    		return;
    	}
    }
    
    /**
     * Sends a message through a channels sendMessage function
     * @param channel The channel to find, and consequently send the message through
     * @param key The key to find in a channels language file
     * @param formatArgs The format args
     */
    public void sendChanMessageTrans(String channel, String key, Object... formatArgs) {
    	try {
    		Channel chan = getChannel(channel);
	    	chan.sendMessage(String.format(Translate.getTranslate(key, chan.getLang()), formatArgs));
    	} catch(NullPointerException e) {
    		// In the event an invalid channel is provided, we don't care, toss away the request.
    		return;
    	}
    	
    }
}
