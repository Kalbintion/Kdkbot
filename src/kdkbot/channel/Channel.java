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
	private static Kdkbot instance;
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
	public HashMap<String, Integer> senderRanks = new HashMap<String, Integer>();
	
	// Other Vars
	public HashMap<String, Integer> filterBypass = new HashMap<String, Integer>();
	
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
			Channel.instance = instance;
			this.channel = channel;
			this.commands = new Commands(channel, this);
			// this.economy = new Economy(instance, channel);
			this.baseConfigLocation = "./cfg/" + channel + "/";
		
			this.cfgChan = new Config(this.baseConfigLocation + "channel.cfg");

			this.filters = new Filters(Channel.instance, channel);
			this.filters.loadFilters();
			
			this.stats = new Stats(this);
			this.stats.loadStats();
			
			this.joinChannel();
			
			instance.dbg.writeln(this, "Attempting to load config ranks.");
			cfgPerms = new Config("./cfg/" + channel + "/perms.cfg");
			this.loadSenderRanks();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void joinChannel() {
		Channel.instance.joinChannel(this.channel);
	}
	
	public void leaveChannel() {
		Channel.instance.partChannel(this.channel);
	}
	
	public String getChannel() {
		return this.channel;
	}
	
	public void leaveChannel(String reason) {
		Channel.instance.partChannel(this.channel, reason);
	}
	
	public void kickUser(String nick) {
		Channel.instance.kick(this.channel, nick);
	}
	
	public void kickUser(String nick, String reason) {
		Channel.instance.kick(this.channel, nick, reason);
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
						instance.dbg.writeln(this, "Decreased " + info.sender + " permit bypass by 1");
						filterBypass.put(info.sender, filterBypass.get(info.sender).intValue() - 1);
						break;
					}
				}
				switch(filter.action) {
					case 1:
						instance.dbg.writeln(this, "Attempting to purge user due to filter");
						instance.log("Attempting to purge user " + info.sender + " due to filter #" + filterIndex);
						instance.sendMessage(info.channel, "/timeout " + info.sender + " 1");
						break;
					case 2:
						instance.dbg.writeln(this, "Attempting to timeout user due to filter");
						instance.log("Attempting to timeout user " + info.sender + " due to filter #" + filterIndex);
						instance.sendMessage(info.channel, "/timeout " + info.sender);
						break;
					case 3:
						instance.dbg.writeln(this, "Attempting to ban user due to filter");
						instance.log("Attempting to ban user " + info.sender + " due to filter #" + filterIndex);
						instance.sendMessage(info.channel, "/ban " + info.sender);
						break;
					case 4:
						instance.dbg.writeln(this, "Attempting to respond to user due to filter");
						instance.log("Attempting to respond to user " + info.sender + " due to filter #" + filterIndex);
						instance.sendMessage(info.channel, info.sender + ": " + filter.actionInfo);
						break;
				}
			}
		}
		
		if(info.message.startsWith(this.commandPrefix)) {
			this.commands.commandHandler(info);
		}
	}
}
