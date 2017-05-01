package kdkbot.channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import kdkbot.*;
import kdkbot.commands.*;
import kdkbot.commands.filters.Filter;
import kdkbot.commands.filters.Filters;
import kdkbot.filemanager.Config;

public class Channel {
	public Commands commands;
	public String channel;
	// private Economy economy;
	public ArrayList<Forwarder> forwarders;
	public String commandPrefix = "|";
	public Filters filters;
	public Stats stats;
	
	// Path & Config locations (set by Channel() init)
	public String baseConfigLocation;
	public Config cfgPerms;
	public Config cfgChan;
	public Config cfgTokens;
	public HashMap<String, Integer> senderRanks = new HashMap<String, Integer>();
	
	// Other Vars
	public HashMap<String, Integer> filterBypass = new HashMap<String, Integer>();
	public boolean commandProcessing = true;
	public boolean logChat = true;
	
	/**
	 * Constructs a new channel instance with no references
	 */
	public Channel() {
		this(null, null);
	}
	
	/**
	 * Constructs a new channel instance
	 * @param instance the instance of the bot to be assigned for this channel
	 * @param channel the channel name in which to join and reign control over
	 */
	public Channel(Kdkbot instance, String channel) {
		try {
			this.channel = channel;
			this.baseConfigLocation = "./cfg/" + channel + "/";
		
			cfgChan = new Config(this.baseConfigLocation + "channel.cfg");
			cfgChan.verifyExists();
			cfgChan.loadConfigContents();
			
			cfgTokens = new Config(this.baseConfigLocation + "tokens.cfg");
			cfgTokens.verifyExists();
			cfgTokens.loadConfigContents();
			
			// Command Processing?
			if(cfgChan.getSetting("commandProcessing") == null) {
				cfgChan.setSetting("commandProcessing", Boolean.TRUE.toString());
			}
			
			this.commandProcessing = Boolean.parseBoolean(cfgChan.getSetting("commandProcessing"));
			
			// Command Prefix?
			if(cfgChan.getSetting("commandPrefix") == null) {
				cfgChan.setSetting("commandPrefix", "|");
			}
			
			this.commandPrefix = cfgChan.getSetting("commandPrefix");
			
			// Log Chat?
			if(cfgChan.getSetting("logChat") == null) {
				cfgChan.setSetting("logChat", "true");
			}
			
			this.logChat = Boolean.parseBoolean(cfgChan.getSetting("logChat"));
			
			// Message Prefix?
			if(cfgChan.getSetting("msgPrefix") == null) {
				cfgChan.setSetting("msgPrefix", "");
			}
			
			// Mesage Suffix?
			if(cfgChan.getSetting("msgSuffix") == null) {
				cfgChan.setSetting("msgSuffix", "");
			}
			
			// Filters, Stats, etc
			this.filters = new Filters(channel);
			this.filters.loadFilters();
			
			this.stats = new Stats(this);
			this.stats.loadStats();
			
			this.joinChannel();
			
			instance.dbg.writeln(this, "Attempting to load config ranks.");
			cfgPerms = new Config("./cfg/" + channel + "/perms.cfg");
			this.loadSenderRanks();

			this.commands = new Commands(channel, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void joinChannel() {
		Kdkbot.instance.joinChannel(this.channel);
	}
	
	public void leaveChannel() {
		Kdkbot.instance.partChannel(this.channel);
	}
	
	public String getChannel() {
		return this.channel;
	}
	
	public void leaveChannel(String reason) {
		Kdkbot.instance.partChannel(this.channel, reason);
	}
	
	public void kickUser(String nick) {
		Kdkbot.instance.kick(this.channel, nick);
	}
	
	public void kickUser(String nick, String reason) {
		Kdkbot.instance.kick(this.channel, nick, reason);
	}

	public String getCommandPrefix() {
		return this.commandPrefix;
	}
	
	public void setCommandPrefix(String commandPrefix) {
		this.commandPrefix = commandPrefix;
	}
	
	public int getUserRank(String user) {
		return this.senderRanks.get(user);
	}
	
	/**
	 * Gets a particular users rank for this channel.
	 * @param sender The sender to lookup
	 * @return An integer value representing the users rank for this channel.
	 */
	public int getSenderRank(String sender) {
		if(this.senderRanks.containsKey(sender.toLowerCase())) {
			return this.senderRanks.get(sender.toLowerCase());
		} else {
			return 0;
		}
	}
	
	/**
	 * Gets all of this channels ranks
	 * @return An Array List of users and their ranks
	 */
	public ArrayList<String> getSenderRanks() {
		ArrayList<String> strings = null;
		try {
			strings = (ArrayList<String>) cfgPerms.getConfigContents();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return strings;
	}
	
	/**
	 * Sets a particular users rank for this channel
	 * @param target The users name to set a rank to
	 * @param rank The rank to set the target to
	 */
	public void setSenderRank(String target, int rank) {
		senderRanks.put(target.toLowerCase(), rank);
		this.saveSenderRanks(true);
	}
	
	/**
	 * Loads the channels ranks for users.
	 */
	public void loadSenderRanks() {
		try {
			List<String> strings = cfgPerms.getConfigContents();
			Iterator<String> string = strings.iterator();
			while(string.hasNext()) {
				Kdkbot.instance.dbg.writeln(this, "Parsing next line of perm list.");
				String[] args = string.next().split("=");
				Kdkbot.instance.dbg.writeln(this, "Size of args is " + args.length + ". a value of 2 is expected.");
				this.senderRanks.put(args[0], Integer.parseInt(args[1]));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Saves the channels ranks for users.
	 */
	public void saveSenderRanks() {
		cfgPerms.saveSettings();
	}
	
	/**
	 * 
	 * @param sendReferenceMap
	 */
	public void saveSenderRanks(boolean sendReferenceMap) {
		cfgPerms.saveSettings(this.senderRanks);
	}
	
	public void sendMessage(String message) {
		String msgPrefix = cfgChan.getSetting("msgPrefix");
		String msgSuffix = cfgChan.getSetting("msgSuffix");
		if(msgPrefix.length() > 0) { msgPrefix += " "; } // Append a space
		if(msgSuffix.length() > 0) { msgSuffix = " " + msgSuffix; } // Prepend a space
		
		Kdkbot.instance.sendMessage(channel, msgPrefix + message + msgSuffix);
	}
	
	public void sendRawMessage(String message) {
		Kdkbot.instance.sendMessage(channel, message);
	}
	
	/**
	 * 
	 * @param info
	 */
	public void messageHandler(MessageInfo info) {
		// User Stats
		stats.handleMessage(info);
		
		// Begin filtering first before checking for command validity
		ArrayList<Filter> fList = this.filters.getFilters();
		Iterator<Filter> fIter = fList.iterator();
		int filterIndex = 0;
		while(fIter.hasNext()) {
			Filter filter = fIter.next();
			filterIndex++;
			if(filter.contains(info.message)) {
				if(filter.ignoresPermit == false && this.filterBypass.containsKey(info.sender)) {
					if(filterBypass.get(info.sender).intValue() > 0) {
						Kdkbot.instance.dbg.writeln(this, "Decreased " + info.sender + " permit bypass by 1");
						filterBypass.put(info.sender, filterBypass.get(info.sender).intValue() - 1);
						break;
					}
				}
				switch(filter.action) {
					case 1:
						Kdkbot.instance.dbg.writeln(this, "Attempting to purge user due to filter");
						Kdkbot.instance.log("Attempting to purge user " + info.sender + " due to filter #" + filterIndex);
						Kdkbot.instance.sendMessage(info.channel, "/timeout " + info.sender + " 1");
						break;
					case 2:
						Kdkbot.instance.dbg.writeln(this, "Attempting to timeout user due to filter");
						Kdkbot.instance.log("Attempting to timeout user " + info.sender + " due to filter #" + filterIndex);
						Kdkbot.instance.sendMessage(info.channel, "/timeout " + info.sender);
						break;
					case 3:
						Kdkbot.instance.dbg.writeln(this, "Attempting to ban user due to filter");
						Kdkbot.instance.log("Attempting to ban user " + info.sender + " due to filter #" + filterIndex);
						Kdkbot.instance.sendMessage(info.channel, "/ban " + info.sender);
						break;
					case 4:
						Kdkbot.instance.dbg.writeln(this, "Attempting to respond to user due to filter");
						Kdkbot.instance.log("Attempting to respond to user " + info.sender + " due to filter #" + filterIndex);
						Kdkbot.instance.sendMessage(info.channel, MessageParser.parseMessage(filter.actionInfo, info));
						break;
				}
			}
		}
		
		// Do we have any message forwarders
		if(forwarders != null && forwarders.size() > 0) {
			// We do! Lets check that they're validated forwarders
			Iterator<Forwarder> fwdIter = forwarders.iterator();
			while(fwdIter.hasNext()) {
				Forwarder fwder = fwdIter.next();
				if(fwder.isAuthorized()) {
					Kdkbot.instance.getChannel(fwder.getChannel()).sendMessage(fwder.formatMessage(info));
				}
			}
		}
		
		// ORDER IMPORTANT HERE: Channel starting giveaway through the command caused this to add the person starting the giveaway to add themselves to it automatically
		// Do we have a giveaway active? If so, does the message contain the keyword?
		if(this.commands.giveaway.hasStarted() && info.message.toLowerCase().contains(this.commands.giveaway.getTriggerWord().toLowerCase())) {
			if(!this.commands.giveaway.hasEntry(info.sender)) {
				this.commands.giveaway.addEntry(info.sender);
			}
		}
		
		// Send the message off to the channels command processor
		if(info.message.startsWith(this.commandPrefix)){
			this.commands.commandHandler(info);
		}
		

	}
	
	/**
	 * Authorizes a forwarder for this channel
	 * @param fromChannel The channel to accept the request from
	 */
	public void authorizeForwarder(String fromChannel) {
		setForwarderAuthorization(fromChannel, true);
	}
	
	/**
	 * Denies a forwarder for this channel
	 * @param fromChannel The channel to deny the request from
	 */
	public void denyForwarder(String fromChannel) {
		setForwarderAuthorization(fromChannel, false);
	}
	
	/**
	 * Sets authorization status for a forwarder based on the channel accepting or denying the forwarder
	 * @param fromChannel The channel to accept/deny the request from
	 * @param status The authorization status. True for accept, False for deny.
	 */
	private void setForwarderAuthorization(String fromChannel, boolean status) {
		Iterator<Forwarder> fwdIter = this.forwarders.iterator();
		while(fwdIter.hasNext()) {
			Forwarder nxt = fwdIter.next();
			if(nxt.getChannel().equalsIgnoreCase(fromChannel)) {
				if(status) { nxt.authorize(); } else { nxt.unauthorize(); }
			}
		}
	}
	
	/**
	 * Removes a forwarder from this channel
	 * @param fromChannel The channel to remove the forwarder to
	 */
	public void removeForwarder(String fromChannel) {
		Iterator<Forwarder> fwdIter = this.forwarders.iterator();
		while(fwdIter.hasNext()) {
			Forwarder nxt = fwdIter.next();
			if(nxt.getChannel().equalsIgnoreCase(fromChannel)) {
				fwdIter.remove();
			}
		}
	}
	
	/**
	 * Gets this channels access token if its available
	 * @return Twitch access token in unencrypted form
	 */
	public String getAccessToken() {
		String token = cfgTokens.getSetting("accessToken");
		if(token == null) {
			sendMessage("This channel has not authorized kdkbot to access stream information!");
			return "";
		}
		return cfgTokens.getSetting("accessToken");
	}
	
	/**
	 * Gets the channel owners user ID. If it does not exist, it will retrieve and store it before returning the value.
	 * @return The ID of the channel's owner.
	 */
	public String getUserID() {
		String userID = cfgTokens.getSetting("userID");
		if(userID == null || userID.equalsIgnoreCase("null")) {
			userID = kdkbot.api.twitch.APIv5.getUserID(Kdkbot.instance.getClientID(), channel.replace("#", ""));
			cfgTokens.setSetting("userID", userID);
		}
		return userID;
	}
}
