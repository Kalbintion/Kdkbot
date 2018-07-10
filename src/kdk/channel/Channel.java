package kdk.channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import kdk.*;
import kdk.cmds.*;
import kdk.cmds.filters.Filter;
import kdk.cmds.filters.Filters;
import kdk.dataman.DBFetcher;
import kdk.economy.Economy;
import kdk.filemanager.Config;

public class Channel {
	public Commands commands;
	public String channel;
	// private Economy economy;
	public ArrayList<Forwarder> forwarders = new ArrayList<Forwarder>();
	public String commandPrefix = "!";
	public Filters filters;
	public Stats stats;
	public Economy economy;
	
	// Path & Config locations (set by Channel() init)
	public String baseConfigLocation;
	// public Config cfgPerms;
	public Config cfgChan;
	public Config cfgTokens;
	public Config cfgSequenceMessages;
	// TODO: Optimize cfgPerms and senderRanks duplicating information in memory
	public HashMap<String, Integer> senderRanks = new HashMap<String, Integer>();
	
	// Other Vars
	public HashMap<String, Integer> filterBypass = new HashMap<String, Integer>();
	public boolean commandProcessing = true;
	public boolean logChat = true;
	public String language = "enUS";
	
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
	public Channel(Bot instance, String channel) {
		try {
			this.channel = channel;
			this.baseConfigLocation = "./cfg/" + channel + "/";
		
			this.reload();
			
			// Filters, Stats, etc
			this.filters = new Filters(channel);
			this.filters.loadFilters();
			
			this.stats = new Stats(this);
			this.stats.loadStats();
			
			this.joinChannel();
			
			this.loadSenderRanks();
			
			instance.dbg.writeln(this, "Attempting to load message sequence values.");
			cfgSequenceMessages = new Config("./cfg/" + channel + "/msgseq.cfg");
			cfgSequenceMessages.loadConfigContents();
			
			this.commands = new Commands(channel, this);
			this.language = cfgChan.getSetting("lang");
			this.economy = new Economy(this);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reloads channel configuration settings such as command prefix, tokens, etc
	 */
	public void reload() {
		cfgChan = new Config(this.baseConfigLocation + "channel.cfg");
		try {
			cfgChan.verifyExists();
		} catch (Exception e) {
			e.printStackTrace();
		}
		cfgChan.loadConfigContents();
		
		cfgTokens = new Config(this.baseConfigLocation + "tokens.cfg");
		try {
			cfgTokens.verifyExists();
		} catch (Exception e) {
			e.printStackTrace();
		}
		cfgTokens.loadConfigContents();
		
		// Command Processing?
		if(cfgChan.getSetting("commandProcessing") == null) {
			cfgChan.setSetting("commandProcessing", Boolean.TRUE.toString());
		}
		
		this.commandProcessing = Boolean.parseBoolean(cfgChan.getSetting("commandProcessing"));
		
		// Command Prefix?
		if(cfgChan.getSetting("commandPrefix") == null) {
			cfgChan.setSetting("commandPrefix", "!");
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
		
		// Lang?
		if(cfgChan.getSetting("lang") == null) {
			cfgChan.setSetting("lang", "enUS");
		}
	}
	
	/**
	 * Joins the channel this channel object is assigned to
	 */
	public void joinChannel() {
		Bot.instance.joinChannel(this.channel);
	}
	
	/**
	 * Leaves the channel this channel object is assigned to
	 */
	public void leaveChannel() {
		Bot.instance.partChannel(this.channel);
	}
	
	/**
	 * Gets the channel object
	 * @return This particular channel objects instance
	 */
	public String getChannel() {
		return this.channel;
	}
	
	/**
	 * Leaves the channel this channel object is assigned to
	 * @param reason The reason for leaving
	 */
	public void leaveChannel(String reason) {
		Bot.instance.partChannel(this.channel, reason);
	}
	
	/**
	 * Removes a user by kicking them through the normal IRC protocol
	 * @param nick The nick to kick
	 */
	public void kickUser(String nick) {
		Bot.instance.kick(this.channel, nick);
	}
	
	/**
	 * Removes a user by kicking them through the normal IRC protocol
	 * @param nick The nick to kick
	 * @param reason The reason for the kick
	 */
	public void kickUser(String nick, String reason) {
		Bot.instance.kick(this.channel, nick, reason);
	}

	/**
	 * Returns this channels command prefix used for commands
	 * @return A string containing the text of the command prefix
	 */
	public String getCommandPrefix() {
		return this.commandPrefix;
	}
	
	/**
	 * Sets this channels command prefix used for commands
	 * @param commandPrefix The new command prefix
	 */
	public void setCommandPrefix(String commandPrefix) {
		this.commandPrefix = commandPrefix;
	}
	
	/**
	 * Gets the rank (permission level) of a provided user
	 * @param user The user to look up
	 * @return an int of the users rank
	 */
	public int getUserRank(String user) {
		return this.senderRanks.get(user);
	}
	
	/**
	 * Returns this channels language setting
	 * @return
	 */
	public String getLang() {
		return this.language;
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
	 * Sets a particular users rank for this channel
	 * @param target The users name to set a rank to
	 * @param rank The rank to set the target to
	 */
	public void setSenderRank(String target, int rank) {
		target = target.toLowerCase();
		senderRanks.put(target, rank);
		DBFetcher.setChannelPerm(this.channel, target, rank);
	}
	
	/**
	 * Loads the channels ranks for users.
	 */
	public void loadSenderRanks() {
		try {
			if(senderRanks.size() > 0) { 
				// Lets reset the ranks list since apparently it was already loaded
				senderRanks.clear();
			}
			
			senderRanks = DBFetcher.getChannelPerms(this.channel);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends a message to the channel, using channel specific settings
	 * @param message The message to send
	 */
	public void sendMessage(String message) {
		if(message.startsWith("/")) {
			sendRawMessage(message);
			return;
		}
		String msgPrefix = cfgChan.getSetting("msgPrefix");
		String msgSuffix = cfgChan.getSetting("msgSuffix");
		if(msgPrefix.length() > 0) { msgPrefix += " "; } // Append a space
		if(msgSuffix.length() > 0) { msgSuffix = " " + msgSuffix; } // Prepend a space
		
		Bot.instance.sendMessage(channel, msgPrefix + message + msgSuffix);
	}
	
	/**
	 * Sends a raw message to the channel, bypassing channel specific settings
	 * @param message The message to send
	 */
	public void sendRawMessage(String message) {
		Bot.instance.sendMessage(channel, message);
	}
	
	/**
	 * Handles messages for this particular channel object
	 * @param info Information regarding the message
	 */
	public void messageHandler(MessageInfo info) {
		// User Stats
		stats.handleMessage(info);
		
		// Timer msg counters update
		commands.timers.updateTimerMessageCounters();
		
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
						Bot.instance.dbg.writeln(this, "Decreased " + info.sender + " permit bypass by 1");
						filterBypass.put(info.sender, filterBypass.get(info.sender).intValue() - 1);
						break;
					}
				}
				switch(filter.action) {
					case 1:
						Bot.instance.dbg.writeln(this, "Attempting to purge user due to filter");
						Bot.instance.log("Attempting to purge user " + info.sender + " due to filter #" + filterIndex);
						Bot.instance.sendMessage(info.channel, "/timeout " + info.sender + " 1");
						break;
					case 2:
						Bot.instance.dbg.writeln(this, "Attempting to timeout user due to filter");
						Bot.instance.log("Attempting to timeout user " + info.sender + " due to filter #" + filterIndex);
						Bot.instance.sendMessage(info.channel, "/timeout " + info.sender);
						break;
					case 3:
						Bot.instance.dbg.writeln(this, "Attempting to ban user due to filter");
						Bot.instance.log("Attempting to ban user " + info.sender + " due to filter #" + filterIndex);
						Bot.instance.sendMessage(info.channel, "/ban " + info.sender);
						break;
					case 4:
						Bot.instance.dbg.writeln(this, "Attempting to respond to user due to filter");
						Bot.instance.log("Attempting to respond to user " + info.sender + " due to filter #" + filterIndex);
						Bot.instance.sendChanMessage(info.channel, MessageParser.parseMessage(filter.actionInfo, info));
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
					Bot.instance.getChannel(fwder.getChannel()).sendRawMessage(fwder.formatMessage(info));
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
		
		// Forward it off to the economy system for handling
		this.economy.handleMessage(info.sender, info.message);
		
		// Send the message off to the channels command processor
		if(info.message.startsWith(this.commandPrefix)) {
			this.commands.commandHandler(info);
		}
		

	}
	
	public void extraHandler(MessageInfo info) {
		// User Stats
		stats.handleMessage(info);
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
		removeForwarder(fromChannel);
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
	 * Checks to see if a channel is awaiting authorization
	 * @param fromChannel The channel to check to see for awaiting response
	 * @return True if the channel is awaiting response, false otherwise
	 */
	public boolean isAwaitingForwarderResponse(String fromChannel) {
		Iterator<Forwarder> fwdIter = this.forwarders.iterator();
		while(fwdIter.hasNext()) {
			Forwarder nxt = fwdIter.next();
			if(nxt.getChannel().equalsIgnoreCase(fromChannel)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isFwdRequestor(String fromChannel) {
		Iterator<Forwarder> fwdIter = this.forwarders.iterator();
		while(fwdIter.hasNext()) {
			Forwarder nxt = fwdIter.next();
			if(nxt.getChannel().equalsIgnoreCase(fromChannel)) {
				return nxt.isRequestor();
			}
		}
		return false;
	}
	
	/**
	 * Checks to see if a channel has an active forwarder with a given channel
	 * @param fromChannel The channel to check to see for active forwarding
	 * @return True if the channel is active, false otherwise
	 */
	public boolean hasActiveForwarder(String fromChannel) {
		Iterator<Forwarder> fwdIter = this.forwarders.iterator();
		while(fwdIter.hasNext()) {
			Forwarder nxt = fwdIter.next();
			if(nxt.getChannel().equalsIgnoreCase(fromChannel)) {
				return true;
			}
		}
		return false;
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
	 * Retrieves the active forwarder list.
	 * @return A string containing every active forwarder, separated by ", " (excluding quotes), null if there are no active forwarders. 
	 */
	public String getActiveForwarders() {
		String allChannels = "";
		Iterator<Forwarder> fwdIter = this.forwarders.iterator();
		
		if(this.forwarders.size() <= 0) {
			return null;
		}
		
		while(fwdIter.hasNext()) {
			Forwarder nxt = fwdIter.next();
			allChannels += nxt.getChannel() + ", ";
		}
		
		return allChannels.substring(0, allChannels.length() - 2);
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
			userID = kdk.api.twitch.APIv5.getUserID(Bot.instance.getClientID(), channel.replace("#", ""));
			cfgTokens.setSetting("userID", userID);
		}
		return userID;
	}
	
	/**
	 * Sets and Adds (by 1) to the particular id up to a limiting value provided by max
	 * @param id The id to set
	 * @param max The max value it should ever be set to before rolling over
	 */
	public void setAddMsgSeq(String id, int max) {
		setMsgSeq(id, String.valueOf((getMsgSeq(id) + 1) % max));
	}
	
	/**
	 * Sets a particular id to a new value for sequential messages
	 * @param id The id to set
	 * @param value The new value it should be set to
	 */
	public void setMsgSeq(String id, String value) {
		cfgSequenceMessages.setSetting(id, value);
	}
	
	/**
	 * Returns an int pertaining to which part of the sequential message system it is on
	 * @param id The id to look-up
	 * @return an int containing the string index
	 */
	public int getMsgSeq(String id) {
		try {
			return Integer.parseInt(cfgSequenceMessages.getSetting(id));
		} catch(NumberFormatException e) {
			return 0;
		}
	}
}
