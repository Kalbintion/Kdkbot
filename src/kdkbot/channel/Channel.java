package kdkbot.channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import kdkbot.*;
import kdkbot.commands.*;
import kdkbot.filemanager.Config;

public class Channel {
	private static Kdkbot instance;
	public Commands commands;
	private String channel;
	// private Economy economy;
	private boolean repeatMessages;
	public ArrayList<Forwarder> forwarders;
	public Config channelConfig;
	public String commandPrefix;
	
	// Path & Config locations (set by Channel() init)
	public String baseConfigLocation;
	public Config cfgPerms;
	public Config cfgChan;
	public HashMap<String, Integer> senderRanks = new HashMap<String, Integer>();
	
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
			this.instance = instance;
			this.channel = channel;
			this.commands = new Commands(instance, channel, this);
			// this.economy = new Economy(instance, channel);
			this.baseConfigLocation = "./cfg/" + channel + "/";
		
			this.channelConfig = new Config(this.baseConfigLocation + "channel.cfg");
			
			
			this.joinChannel();
			
			instance.dbg.writeln(this, "Attempting to load config ranks.");
			cfgPerms = new Config("./cfg/" + channel + "/perms.cfg");
			this.loadSenderRanks();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void joinChannel() {
		this.instance.joinChannel(this.channel);
	}
	
	public void leaveChannel() {
		this.instance.partChannel(this.channel);
	}
	
	public String getChannel() {
		return this.channel;
	}
	
	public void leaveChannel(String reason) {
		this.instance.partChannel(this.channel, reason);
	}
	
	public void kickUser(String nick) {
		this.instance.kick(this.channel, nick);
	}
	
	public void kickUser(String nick, String reason) {
		this.instance.kick(this.channel, nick, reason);
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
				instance.dbg.writeln(this, "Parsing next line of perm list.");
				String[] args = string.next().split("=");
				instance.dbg.writeln(this, "Size of args is " + args.length + ". a value of 2 is expected.");
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
	
	public void messageHandler(MessageInfo info) {
		
	}
}
