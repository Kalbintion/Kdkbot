package kdkbot.commands;

import java.util.Iterator;

import kdkbot.Kdkbot;
import kdkbot.MessageInfo;
import kdkbot.api.urban.UrbanAPI;
import kdkbot.channel.Channel;
import kdkbot.channel.Forwarder;
import kdkbot.commands.messagetimer.Timers;
import kdkbot.commands.quotes.*;
import kdkbot.commands.ama.AMA;
import kdkbot.commands.counters.*;
import kdkbot.commands.stats.Stats;
import kdkbot.commands.strings.*;

public class Commands {
	// Necessary variable for instance referencing
	public Channel chan;
	
	// Sub-system commands managers
	// #TODO: Figure out way to add these to the InternalCommand list since these internal commands are a bit more in-depth then the "Additional Commands" comment section
	public Quotes quotes;
	public Stats stats;
	public StringCommands commandStrings;
	public Counters counters;
	public AMA amas;
	public Timers timers;
	
	// Additional Commands
	private InternalCommand cmdChannel = new InternalCommand("rankChannel", "5", Integer.class);
	private InternalCommand cmdPerm = new InternalCommand("rankPerm", "3", Integer.class);
	private InternalCommand cmdForward = new InternalCommand("rankForward", "4", Integer.class);
	private InternalCommand cmdPermit = new InternalCommand("rankPermit", "3", Integer.class);
	private InternalCommand cmdFilter = new InternalCommand("rankFilter", "5", Integer.class);
	private InternalCommand cmdTimer = new InternalCommand("rankTimer", "3", Integer.class);
	
	/**
	 * Creates a new Commands class with a given channel assignment and channel instance
	 * @param channel The name of the channel these commands are to belong
	 * @param chanInst The instance of the channel object to use
	 */
	public Commands(String channel, Channel chanInst) {
		this.chan = chanInst;
		
		try {
			this.commandStrings = new StringCommands(channel);
			this.commandStrings.loadCommands();
			
			this.quotes = new Quotes(channel);
			this.quotes.loadQuotes();
			
			this.counters = new Counters(channel);
			this.counters.loadCounters();
			
			this.stats = new Stats(channel);
			
			this.amas = new AMA(channel);
			this.amas.loadQuestions();
			
			this.timers = new Timers(channel);
			this.timers.loadTimers();

			InternalCommand[] internalCommands = {cmdChannel, cmdPerm, cmdForward, cmdPermit, cmdFilter};
			for (InternalCommand cmd : internalCommands) {
				Object ret = verifyGetSetting(cmd.getSettingName(), cmd.getSettingDefault(), cmd.getSettingType());
				if(ret == null) {
					setCommandStatus(cmd, "availability", false);
					setCommandStatus(cmd, "permission", cmd.getSettingDefault());
				} else {
					setCommandStatus(cmd, "availability", true);
					setCommandStatus(cmd, "permission", ret);
				}
			}
			
			if(chan.cfgChan.getSetting("rankQuotes") == null) {
				chan.cfgChan.setSetting("rankQuotes", "1");
			}
			
			try {
				this.quotes.setPermissionLevel(Integer.parseInt(chan.cfgChan.getSetting("rankQuotes")));
			} catch (NumberFormatException e) {
				chan.sendMessage("This channels rankQuotes setting is invalid! Got " + chan.cfgChan.getSetting("rankQuotes"));
				this.quotes.setAvailability(false);
			}
			
			if(chan.cfgChan.getSetting("rankCounters") == null) {
				chan.cfgChan.setSetting("rankCounters", "1");
			}
			
			try {
				this.cmdFilter.setPermissionLevel(Integer.parseInt(chan.cfgChan.getSetting("rankFilter")));
			} catch(NumberFormatException e) {
				chan.sendMessage("This channels rankFilter setting is invalid! Got " + chan.cfgChan.getSetting("rankFilter"));
				this.cmdFilter.setAvailability(false);
			}
			
			if(chan.cfgChan.getSetting("rankAMA") == null) {
				chan.cfgChan.setSetting("rankAMA", "1");
			}
			
			try {
				this.amas.setPermissionLevel(Integer.parseInt(chan.cfgChan.getSetting("rankAMA")));
			} catch (NumberFormatException e) {
				chan.sendMessage("This channels rankAMA setting is invalid! Got " + chan.cfgChan.getSetting("rankAMA"));
				this.amas.setAvailability(false);
			}
			
			if(chan.cfgChan.getSetting("rankTimer") == null) {
				chan.cfgChan.setSetting("rankTimer",  "3");
			}
			
			try {
				this.timers.setPermissionLevel(Integer.parseInt(chan.cfgChan.getSetting("rankTimer")));
			} catch (NumberFormatException e) {
				chan.sendMessage("this channels rankTimer setting is invalid! Got " + chan.cfgChan.getSetting("rankTimer"));
				this.timers.setAvailability(false);
			}
			

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Command handler for processing messages
	 * @param info MessageInfo containing the message to process
	 */
	public void commandHandler(MessageInfo info) {
		Kdkbot.instance.dbg.writeln(this, "Attempting to parse last message for channel " + info.channel);
		
		// Enforce senders name to be lowercased - prevents case sensitive issues later on
		info.sender = info.sender.toLowerCase();
		
		// Start command checking
		String coreWord = info.getSegments()[0].substring(chan.commandPrefix.length());
		String args[] = info.getSegments();
		
		Kdkbot.instance.dbg.writeln(this, "Core Command detected as '" + coreWord + "'");
		Kdkbot.instance.dbg.writeln(this, "Senders level detected as " + info.senderLevel + " for " + info.sender);
		
		// These commands are bot-room specific
		Kdkbot.instance.dbg.writeln(this, "Kdkbot nick is: " + Kdkbot.instance.getNick());
		Kdkbot.instance.dbg.writeln(this, "Channel is: " + info.channel);
		if(info.channel.equalsIgnoreCase("#" + Kdkbot.instance.getNick())) {
			Kdkbot.instance.dbg.writeln(this, "Running through bot specific channel commands");
			if (coreWord.equalsIgnoreCase("join")) {
				switch(Kdkbot.instance.enterChannel(info.sender)) {
					case -1:
						Kdkbot.instance.getChannel(info.channel).sendMessage("Already in " + info.sender);
						break;
					case 1:
						Kdkbot.instance.getChannel(info.channel).sendMessage("Joined channel " + info.sender);
						break;
					default:
						Kdkbot.instance.getChannel(info.channel).sendMessage("Unknown error occured attempting to join channel " + info.sender);
						break;
				}
			} else if (coreWord.equalsIgnoreCase("leave")) {
				Kdkbot.instance.exitChannel(info.sender);
			}
		}
		
		// These commands supersede command processing toggling
		if(info.senderLevel >= cmdChannel.getPermissionLevel() &&
				coreWord.equalsIgnoreCase("channel")) {
			// Channel settings
			
			try {
				boolean validCommand = true;
				if(args[1].equalsIgnoreCase("commandProcessing")) {
					chan.cfgChan.setSetting("commandProcessing", String.valueOf(Boolean.parseBoolean(args[2])));
					chan.commandProcessing = Boolean.parseBoolean(args[2]);
				} else if(args[1].equalsIgnoreCase("commandPrefix")) {
					chan.cfgChan.setSetting("commandPrefix", args[2]);
					chan.commandPrefix = args[2];
				} else if(args[1].equalsIgnoreCase("logChat")) {
					chan.cfgChan.setSetting("logChat", args[2]);
				} else if(args[1].equalsIgnoreCase("rankQuotes")) {
					chan.cfgChan.setSetting("rankQuotes", String.valueOf(rankNameToInt(args[2])));
					quotes.setPermissionLevel(rankNameToInt(args[2]));
				} else if(args[1].equalsIgnoreCase("rankAMA")) {
					chan.cfgChan.setSetting("rankAMA", String.valueOf(rankNameToInt(args[2])));
					amas.setPermissionLevel(rankNameToInt(args[2]));
				} else if(args[1].equalsIgnoreCase("rankCounters")) {
					chan.cfgChan.setSetting("rankCounters", String.valueOf(rankNameToInt(args[2])));
					counters.setPermissionLevel(rankNameToInt(args[2]));
				} else if(args[1].equalsIgnoreCase("rankChannel")) {
					chan.cfgChan.setSetting("rankChannel", String.valueOf(rankNameToInt(args[2])));
				} else if(args[1].equalsIgnoreCase("msgPrefix")) {
					if(args.length < 3) { 
						chan.cfgChan.setSetting("msgPrefix", "");
					} else {
						chan.cfgChan.setSetting("msgPrefix", args[2]);
					}
				} else if(args[1].equalsIgnoreCase("msgSuffix")) {
					if(args.length < 3) { 
						chan.cfgChan.setSetting("msgSuffix", "");
					} else {
						chan.cfgChan.setSetting("msgSuffix", args[2]);
					}
				} else {
					validCommand = false;
				}
				
				if(validCommand) {
					if(args.length < 3) {
						chan.sendMessage("Channel setting " + args[1] + " set to nothing");
					} else {
						chan.sendMessage("Channel setting " + args[1] + " set to " + args[2]);
					}
				} else {
					chan.sendMessage("Channel setting " + args[1] + " was not recognized.");
				}
			} catch(ArrayIndexOutOfBoundsException e) {
				chan.sendMessage("Could not successfully set channel setting. Please double-check input.");
			}
		}
		
		// Command Processing Breaking
		if(!chan.commandProcessing) { return; }
		
		// Permission Ranks
		if (info.senderLevel >= cmdPerm.getPermissionLevel() &&
				coreWord.equalsIgnoreCase("perm")) {
			switch(args[1]) {
				case "set":
					int toRank;
					toRank = rankNameToInt(args[3]);
					
					if(info.senderLevel < toRank) {
						chan.sendMessage(info.sender + ": You cannot set someones rank to a higher one than your own");
					} else {
						chan.setSenderRank(args[2], toRank);
						chan.sendMessage("Set " + args[2] + " to permission rank: " + args[3] + ".");
					}
					break;
				case "get":
					chan.sendMessage("The user " + args[2] + " is set to " + chan.getSenderRank(args[2]));
					break;
			}
		}
		
		// Forwarders
		else if(info.senderLevel >= cmdForward.getPermissionLevel() &&
					(coreWord.equalsIgnoreCase("fwd")
				  || coreWord.equalsIgnoreCase("forward"))) {
			if(args.length >= 2) {
				String toChan = args[1];
				if(!toChan.startsWith("#")) {
					toChan = "#" + toChan;
				}
				
				if(Kdkbot.instance.isInChannel(toChan)) {
					Channel toChanObj = Kdkbot.instance.getChannel(toChan);
					toChanObj.sendMessage(info.channel + " has requested forwarding permissions. Type '" + toChanObj.cfgChan.getSetting("commandPrefix") + "afwd " + info.channel + "' to authorize or '" + toChanObj.cfgChan.getSetting("commandPRefix") + "dfwd " + info.channel + "' to deny.");
					this.chan.forwarders.add(new Forwarder(toChan));
				} else {
					chan.sendMessage(info.sender + ": This bot is not in that channel.");
				}
				
			} else {
				chan.sendMessage(info.sender + ": You did not provide a channel name.");
			}
		}
		
		// Forwarder - Authorization - Accept
		else if(info.senderLevel >= cmdForward.getPermissionLevel() &&
					(coreWord.equalsIgnoreCase("afwd")
				  || coreWord.equalsIgnoreCase("acceptforward"))) {
			if(args.length >= 2) {
				String toAuthorize = args[1];
				if(!toAuthorize.startsWith("#")) { toAuthorize = "#" + toAuthorize; }
				
				Channel fromChan = Kdkbot.instance.getChannel(toAuthorize);
				fromChan.sendMessage("Forwarding authorization request accepted.");
			} else {
				chan.sendMessage("You did not specify a channel to accept the forward request from.");
			}
		}

		// Forwarder - Authorization - Deny
		else if(info.senderLevel >= cmdForward.getPermissionLevel() &&
					(coreWord.equalsIgnoreCase("dfwd")
				  || coreWord.equalsIgnoreCase("denyforward"))) {
			if(args.length >= 2) {
				String toDeny = args[1];
				if(!toDeny.startsWith("#")) { toDeny = "#" + toDeny; }
				
				Channel fromChan = Kdkbot.instance.getChannel(toDeny);
				fromChan.sendMessage("Forwarding authorization request denied.");
			} else {
				chan.sendMessage("You did not specify a channel to deny the forward request from.");
			}
			
		}
		
		// Filter Bypass
		// TODO: Errors on _permit <username>_ but not _permit <username> <times>_
		else if(info.senderLevel >= cmdPermit.getPermissionLevel() &&
				coreWord.equalsIgnoreCase("permit")) {
			try {
				int bypassLimit = 0;
				String user = args[1];
				if(args.length > 2) {
					bypassLimit = Integer.parseInt(args[2]);
				} else {
					bypassLimit = 1;
				}
				chan.filterBypass.put(user, bypassLimit);
				chan.sendMessage(info.sender + " has permitted user " + args[1] + " to bypass all filters " + args[2] + " time(s)");
			} catch (NumberFormatException e) {
				chan.sendMessage(info.sender + ": " + args[2] + " is not a valid number to permit user " + args[1]);
			}
			chan.filterBypass.put(info.sender, Integer.parseInt(args[2]));
		}
		// Urban Look-up
		else if (info.senderLevel >= 2 &&
					true &&
					coreWord.equalsIgnoreCase("urban")) {
			chan.sendMessage(UrbanAPI.getTopDefinition(info.getSegments(2)[1]));
		}
		// Quotes
		else if (info.senderLevel >= quotes.getPermissionLevel() &&
					quotes.getAvailability() &&
					quotes.getTrigger().equalsIgnoreCase(coreWord)) {
			quotes.executeCommand(info);
		}
		// Stats
		else if(coreWord.equalsIgnoreCase("time")) {
			info.message = "stats time " + info.message;
			stats.executeCommand(info);
		}
		else if(coreWord.equalsIgnoreCase("stats")) {
			info.message = "stats all " + info.message;
			stats.executeCommand(info);
		}
		else if(coreWord.equalsIgnoreCase("msges")) {
			info.message ="stats msges " + info.message;
			stats.executeCommand(info);
		}
		// Custom Commands
		else if(info.senderLevel >= 1 &&
				coreWord.equalsIgnoreCase("commands")) {
			commandStrings.executeCommand(info);
		}
		// AMA
		else if(info.senderLevel >= amas.getPermissionLevel() &&
				coreWord.equalsIgnoreCase(amas.getTrigger()) &&
					amas.getAvailability()) {
			amas.executeCommand(info);
		}
		// Counters
		else if(info.senderLevel >= counters.getPermissionLevel() &&
				coreWord.equalsIgnoreCase("counter")) {
			counters.executeCommand(info);
		} else if(info.senderLevel >= cmdForward.getPermissionLevel() &&
				  coreWord.equalsIgnoreCase("fwd")) {
		} else if(info.senderLevel >= cmdFilter.getPermissionLevel() &&
				  coreWord.equalsIgnoreCase("filter")) {
			this.chan.filters.executeCommand(info);
		}
		// Timers
		else if(info.senderLevel >= timers.getPermissionLevel() &&
				coreWord.equalsIgnoreCase("timer")) {
			timers.executeCommand(info);	
		}
		// Custom String Commands
		Iterator<StringCommand> stringIter = commandStrings.commands.iterator();
		while(stringIter.hasNext()) {
			StringCommand stringNext = stringIter.next();
			// Verify user has access to this command
			if(info.senderLevel >= stringNext.getPermissionLevel() &&
					coreWord.equalsIgnoreCase(stringNext.getTrigger()) &&
					stringNext.getAvailability()) {
				stringNext.executeCommand(info);
			}
		}
	}
	
	/**
	 * Translates human readable names into their respective rank integer
	 * @param rank The human readable name to translate from
	 * @return An integer representing the rank provided
	 */
	public static int rankNameToInt(String rank) {
		switch(rank) {
			case "normal":
			case "n":
				return 1;
			case "regular":
			case "r":
				return 2;
			case "moderator":
			case "mod":
			case "m":
				return 3;
			case "supermoderator":
			case "smod":
			case "supermod":
			case "sm":
				return 4;
			case "channeloperator":
			case "chanop":
			case "co":
			case "op":
			case "owner":
				return 5;
			case "max":
			case "*":
				return Integer.MAX_VALUE;
			case "/":
			case "min":
				return Integer.MIN_VALUE;
			case "nobody":
			default:
				// If we hit here, try to just parse the rank as an int, upon failure just return 0 for an invalid rank name
				try {
					return Integer.parseInt(rank);
				} catch (NumberFormatException e) {
					return 0;
				}
		}
	}
	
	/*
	 * 			if(chan.cfgChan.getSetting("rankQuotes") == null) {
				chan.cfgChan.setSetting("rankQuotes", "1");
			}
			
			try {
				this.quotes.setPermissionLevel(Integer.parseInt(chan.cfgChan.getSetting("rankQuotes")));
			} catch (NumberFormatException e) {
				chan.sendMessage("This channels rankQuotes setting is invalid! Got " + chan.cfgChan.getSetting("rankQuotes"));
				this.quotes.setAvailability(false);
			}
	 */
	private <T> Object verifyGetSetting(String settingName, String settingDefault, Class<T> settingType) {
		if(chan.cfgChan.getSetting(settingName) == null) {
			chan.cfgChan.setSetting(settingName, settingDefault);
		}
		
		try {
			String configValue = chan.cfgChan.getSetting(settingName);
			switch(settingType.getCanonicalName()) {
				case "java.lang.Integer":
					return Integer.parseInt(configValue);
				case "java.lang.String":
					return configValue;
			}

			System.out.println("::: " + settingType.getCanonicalName());
		} catch (Exception e) {
			return null;
		}
		
		return settingDefault;
	}
	
	private boolean setCommandStatus(Command command, String settingName, Object value) {
		switch(settingName) {
			case "availability":
				command.setAvailability((boolean) value);
				return true;
			case "permission":
				command.setPermissionLevel((int) value);
				return true;
		}
		return false;
	}
	
}


