package kdk;

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
import java.util.Timer;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.jibble.pircbot.*;

import kdk.channel.*;
import kdk.cmds.MessageParser;
import kdk.cmds.fwd.LiveChecker;
import kdk.dataman.DBFetcher;
import kdk.dataman.DBMan;
import kdk.filemanager.*;
import kdk.language.*;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import sx.blah.discord.api.IDiscordClient;

public class Bot extends PircBot {
	public static HashMap<String, Channel> CHANS = new HashMap<String, Channel>();
	public static Bot inst;
	public static String botLanguage = "enUS";
	public static DBMan dbm;
	
	public Config botCfg = new Config(FileSystems.getDefault().getPath("./cfg/settings.cfg"));
	public ArrayList<String> msgIgnoreList = new ArrayList<String>();
	private boolean _verbose = false;
	private boolean _logChat = false;
	private Pattern logIgnores;
	private Log logger;
	public Debugger dbg;
	private WebInterfaceWatcher webWatcher;
	private IDiscordClient platform_discord;
	private static LiveChecker fwdLiveChecker = new LiveChecker();
	
	private HashMap<String, ArrayList<String>> messageDuplicatorList;
	
	// Twitter related variables
	public boolean useTwitter = true;
	public static Twitter status;
	
    /**
     * Initialization of the basic bot
     */
	public Bot() throws Exception {
		if(inst == null) { // Protection against initializing the bot more than once - singleton!
			inst = this;
		} else {
			throw new Exception("Bot instance already created!");
		}


		// Setup log system
		botCfg.loadConfigContents();
		
		// Setup the debugger instance
		this.dbg = new Debugger(false);
		if(botCfg.getSetting("enableDebug", "0").equalsIgnoreCase("0")) {
			dbg.disable();
		} else {
			dbg.enable();
		}
		
		// Setup the DBMan
		dbm = new DBMan(botCfg.getSetting("sqlHost"), botCfg.getSetting("sqlUser"), botCfg.getSetting("sqlPass"));
		if(!DBMan.connected) {
			System.out.println("Could not establish database connection. Terminating with code 503.");
			System.exit(503);
		}
		DBFetcher._mgr = dbm; // Set DBFetcher DBMan instance
		
		this._verbose = Boolean.parseBoolean(botCfg.getSetting("verbose"));

		this._logChat = Boolean.parseBoolean(DBFetcher.getSetting("log_chat"));
		System.out.println("logging chat? " + _logChat + " [FROM: " + DBFetcher.getSetting("log_chat") + "]");
		logIgnores = Pattern.compile(DBFetcher.getSetting("log_ignores"));
		System.out.println("logging ignors? " + logIgnores.toString()+ " [FROM: " + DBFetcher.getSetting("log_ignores") + "]");
		
		// Setup this instances chat logger
		this.logger = new Log(DBFetcher.getSetting("log_chat_loc"));

		// Setup this bot
		this.setEncoding("UTF-8");
		this.setName(DBFetcher.getTwitchNick(dbm));
		this.setVerbose(_verbose);

		@SuppressWarnings("unused") // Is it really unused? Is it really?
		boolean connectionSent = false;
		
		do {
			try {
				this.connect(DBFetcher.getTwitchIRC(), Integer.parseInt(DBFetcher.getTwitchIRCPort()), "oauth:" + DBFetcher.getTwitchOAuth());
				connectionSent = true;
			} catch(UnknownHostException e) {
				logger.logln(String.format(Translate.getTranslate("log.failedToResolveDelay", botLanguage), DBFetcher.getTwitchIRC()));
				Thread.sleep(10 * 1000); // 10s * 1000ms
			}
		} while (connectionSent = false);
		
		messageDuplicatorList = new HashMap<String, ArrayList<String>>();

		// Get channels
		ArrayList<String> twitchChannels = DBFetcher.getTwitchChannels();
		Iterator<String> iter = twitchChannels.iterator();
		while(iter.hasNext()) {
			String nxt = iter.next();
			if(!nxt.startsWith("#")) {
				nxt = "#" + nxt;
			}
			
			CHANS.put(nxt, new Channel(this, nxt));
			dbg.writeln(this, "Added new channel object for channel: " + nxt);
			dbg.writeln(this, "Channel object: " + getChannel(nxt));
		}
		
		// Instantiate a MessageParser
		new MessageParser();
		
		// Setup forwarder live checker
		Timer fwdLiveChk = new Timer("fwdLiveChk", true);  // 5 Minutes      5 Minutes
		fwdLiveChk.schedule(fwdLiveChecker, 5 * 60 * 1000, 5 * 60 * 1000);
		
		// Setup Twitter interface
		if (useTwitter) {
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true)
			  .setOAuthConsumerKey(DBFetcher.getTwitterOAuthConsumer())
			  .setOAuthConsumerSecret(DBFetcher.getTwitterSecretConsumer())
			  .setOAuthAccessToken(DBFetcher.getTwitterOAuth())
			  .setOAuthAccessTokenSecret(DBFetcher.getTwitterSecret());
			TwitterFactory tf = new TwitterFactory(cb.build());
			status = tf.getInstance();
		}
		
		// Setup Discord interface
		platform_discord = kdk.discord.Core.createClient(DBFetcher.getSetting("discord_user_pass"), true);
		
		// Setup web related info
		if (DBFetcher.getWebEnabled()) {
			dbg.writeln("Enabled Web Watcher.");
			dbg.writeln("Watching @ " + DBFetcher.getWatcherLoc());
			webWatcher = new WebInterfaceWatcher(DBFetcher.getWatcherLoc(), DBFetcher.getSetting("watcher_file"));
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
				platform_discord.login();
				Iterator<Entry<String, Channel>> chanIter = CHANS.entrySet().iterator();
				
				while(chanIter.hasNext()) {
					Map.Entry<String, Channel> chan = chanIter.next();
					chan.getValue().joinChannel();
				}
				
				hasReconnected = true;
			} catch (NickAlreadyInUseException e) {
				logger.logln(Translate.getTranslate("log.nickInUse", botLanguage));
			} catch (UnknownHostException e) {
				logger.logln(String.format(Translate.getTranslate("log.failedToResolve", botLanguage), DBFetcher.getSetting("twitch_irc")));
			} catch (IrcException e) {
				logger.logln(Translate.getTranslate("log.ircException", botLanguage));
			} catch (TwitterException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
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
    	if(_logChat && !logIgnores.matcher(line).find()) {
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
    	
    	return Bot.CHANS.get(channel);
    }
        
    public ArrayList<Channel> getAllChannels() {
    	ArrayList<Channel> c = new ArrayList<Channel>();
    	
    	Iterator<Map.Entry<String, Channel>> iter = CHANS.entrySet().iterator();
    	while(iter.hasNext()) {
    		Map.Entry<String, Channel> pair = (Map.Entry<String, Channel>) iter.next();
    		c.add(pair.getValue());
    	}
    	
    	return c;
    }
    
    /**
     * Sets up and joins a particular channel
     * @param channel The channel to join
     * @return -1 if it is already in the channel, 1 if successful, any other value means unsuccessful
     */
    public int enterChannel(String channel) {
    	Bot.inst.dbg.writeln(this, "Entering channel: " + channel);
    	if (! channel.startsWith("#")) {
    		channel = "#" + channel;
    	}
    	
		if(inst.getChannel(channel) != null) {
			Bot.inst.dbg.writeln(this, "Channel already entered: " + channel);
			inst.getChannel(channel).joinChannel();
			return -1;
		} else {
			Bot.inst.dbg.writeln(this, "Channel not yet entered: " + channel);
			// Join channel
			this.sendMessage(channel, String.format(Translate.getTranslate("channel.join", botLanguage), channel));
			CHANS.put(channel, new Channel(this, channel));
			
			// Add channel to settings cfg
			DBFetcher.joinTwitchChannel(dbm, channel);
			
			Channel chan = getChannel(channel);
			Bot.inst.dbg.writeln(this, "Adding users " + botCfg.getSetting("masterCommands") + " and " + channel.substring(1)  + " to sender rank 5");
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
					// chan.sendMessage(Translate.getTranslate("channel.badInitialize", botLanguage));
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
    }
    
    public void exitChannelDB(String channel) {
    	exitChannel(channel);
    	
		// Remove it from being joined
		DBFetcher.leaveTwitchChannel(dbm, channel);
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
    		} else if(info.message.startsWith("&&ReloadNodeData")) {
    			kdk.api.warframe.InternalTranslator.reloadNodeData();
    			this.sendMessage(info.channel, "Reloaded node data for Warframe");
    		} else if(info.message.startsWith("&&stop")) {
    			this.disconnect();
    			System.exit(0);
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
					Bot.status.updateStatus(info.message.substring("&&status ".length()));
					Bot.inst.sendMessage(info.channel, Translate.getTranslate("bot.mastercommands.status", botLanguage));
				} catch (TwitterException e) {
					Bot.inst.sendMessage(info.channel, String.format(Translate.getTranslate("bot.mastercommands.statusFail", botLanguage), e.getMessage()));
					e.printStackTrace();
				}
    		} else if(info.message.startsWith("&&ram?")) {
    			Bot.inst.sendMessage(info.channel, String.format(Translate.getTranslate("bot.mastercommands.ram", botLanguage), (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024));
    		} else if(info.message.startsWith("&&gc")) {
    			Bot.inst.sendMessage(info.channel, Translate.getTranslate("bot.mastercommands.gc", botLanguage));
    			System.gc();
    			System.gc();
    		} else if(info.message.startsWith("&&sql ")) {
    			System.out.println(Bot.dbm.queryDBStr(info.getSegments(2)[1]));
    		} else if(info.message.startsWith("&&sql2 ")) {
    			System.out.println(DBFetcher.getSetting(dbm, "settings", "value", info.getSegments(2)[1]));
    		}
    	}
    }
    
    /**
     * Gets the client ID for twitch
     * @return The Client ID in plain text for twitch
     */
    public String getClientID() {
    	return DBFetcher.getSetting("twitch_client_id");
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
     * Returns a MessageInfo instance containing a particular message for a channel for the purposes of standardized internal command spoofing.
     * @param channel The channel to be the target of the spoof
     * @param message The message to be spoofed
     * @return a new MessageInfo object containing bot data with spoofed message and channel
     */
    public static MessageInfo spoofMessage(String channel, String message) {
    	return new MessageInfo(channel, inst.getNick(), message, "self", "localhost", Integer.MAX_VALUE);
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
