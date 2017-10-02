package kdkbot.commands;

import java.util.Iterator;

import kdkbot.Kdkbot;
import kdkbot.MessageInfo;
import kdkbot.api.urban.API;
import kdkbot.channel.Channel;
import kdkbot.channel.Forwarder;
import kdkbot.commands.messagetimer.Timers;
import kdkbot.commands.quotes.*;
import kdkbot.commands.ama.AMA;
import kdkbot.commands.counters.*;
import kdkbot.commands.custom.*;
import kdkbot.commands.giveaway.Giveaway;
import kdkbot.commands.stats.Stats;

// TODO: Change Commands list to level 1


public class Commands {
	// Necessary variable for instance referencing
	public Channel chan;
	
	// Sub-system commands managers
	public Quotes quotes;
	public Stats stats;
	public StringCommands commandStrings;
	public Counters counters;
	public AMA amas;
	public Timers timers;
	public Giveaway giveaway;
	
	// Additional Commands
	private InternalCommand cmdChannel = new InternalCommand("channel", 5);
	private InternalCommand cmdPerm = new InternalCommand("perm", 3);
	private InternalCommand cmdPermit = new InternalCommand("permit", 3);
	private InternalCommand cmdForward = new InternalCommand("fwd", 4);
	private InternalCommand cmdAForward = new InternalCommand("a" + cmdForward.getDefaultTrigger(), cmdForward.getDefaultLevel());
	private InternalCommand cmdDForward = new InternalCommand("d" + cmdForward.getDefaultTrigger(), cmdForward.getDefaultLevel());
	private InternalCommand cmdSForward = new InternalCommand("s" + cmdForward.getDefaultTrigger(), cmdForward.getDefaultLevel());
	private InternalCommand cmdFilter = new InternalCommand("filter", 5);
	private InternalCommand cmdHost = new InternalCommand("host", 3);
	private InternalCommand cmdUnhost = new InternalCommand("unhost", 3);
	private InternalCommand cmdStatus = new InternalCommand("status", 1);
	private InternalCommand cmdGame = new InternalCommand("game", 1);
	private InternalCommand cmdGiveaway = new InternalCommand("giveaway", 3);
	private InternalCommand cmdQuotes = new InternalCommand("quote", 1);
	private InternalCommand cmdAma = new InternalCommand("ama", 1);
	private InternalCommand cmdTimers = new InternalCommand("timers", 3);
	private InternalCommand cmdUrban = new InternalCommand("urban", 1);
	private InternalCommand cmdTime = new InternalCommand("time", 0);
	private InternalCommand cmdStats = new InternalCommand("stats", 0);
	private InternalCommand cmdMsges = new InternalCommand("msges", 0);
	private InternalCommand cmdBits = new InternalCommand("bits", 0);
	private InternalCommand cmdSeen = new InternalCommand("seen", 1);
	private InternalCommand cmdCommands = new InternalCommand("commands", 1);
	private InternalCommand cmdUptime = new InternalCommand("uptime", 0);
	private InternalCommand cmdViewers = new InternalCommand("viewers", 1);
	private InternalCommand cmdCounters = new InternalCommand("counter", 1);
	private InternalCommand[] cmdList = {cmdCounters, cmdChannel, cmdPerm, cmdPermit, cmdTimers, cmdForward, cmdAForward, cmdDForward, cmdSForward, cmdFilter, cmdHost, cmdUnhost, cmdStatus, cmdQuotes, cmdGame, cmdGiveaway, cmdUrban, cmdTime, cmdStats, cmdMsges, cmdBits, cmdSeen, cmdCommands, cmdUptime, cmdViewers};
	
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
			
			this.giveaway = new Giveaway(channel);
			
			for (InternalCommand cmd : cmdList) {
				cmd.setAvailability(Boolean.parseBoolean(getInternalCommandSetting(cmd.getSettingSuffix(), "availability", cmd.getDefaultAvailable()).toString()));
				cmd.setPermissionLevel(Integer.parseInt(getInternalCommandSetting(cmd.getSettingSuffix(), "rank", cmd.getDefaultLevel()).toString()));
				cmd.setTrigger(getInternalCommandSetting(cmd.getSettingSuffix(), "trigger", cmd.getDefaultTrigger()).toString());
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
				coreWord.equalsIgnoreCase(cmdChannel.getTrigger())) {
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
				} else if(args[1].equalsIgnoreCase("get")) {
					chan.sendMessage(chan.cfgChan.getSetting(args[2].toLowerCase()));
				} else if(args[1].startsWith("rank") || args[1].startsWith("trigger") || args[1].startsWith("active")) {
						// May be looking for internal command setting
						for(InternalCommand cmd : cmdList) {
							args[1] = args[1].toLowerCase();
							if(args[1].contains(cmd.getTrigger().toLowerCase())) {
								if(args[1].startsWith("rank")) {
									args[2] = String.valueOf(rankNameToInt(args[2]));
									chan.cfgChan.setSetting(args[1], args[2]);
									cmd.setPermissionLevel(rankNameToInt(args[2]));
								} else if(args[1].startsWith("trigger")) {
									chan.cfgChan.setSetting(args[1], args[2]);
									cmd.setTrigger(args[2]);
								} else if(args[1].startsWith("active")) {
									args[2] = String.valueOf(Boolean.parseBoolean(args[2]));
									chan.cfgChan.setSetting(args[1], args[2]);
									System.out.println("Setting " + args[1] + " to " + args[2] + " parsed as " + String.valueOf(Boolean.parseBoolean(args[2])));
									cmd.setAvailability(Boolean.parseBoolean(args[2]));
								}
								validCommand = true;
								break;
							} else {
								validCommand = false;
							}
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
				cmdPerm.getAvailability() && 
				coreWord.equalsIgnoreCase(cmdPerm.getTrigger())) {
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
					int iRank = chan.getSenderRank(args[2]);
					String rank = rankIntToName(chan.getSenderRank(args[2]));
					try {
						Integer.parseInt(rank);
						chan.sendMessage("The user " + args[2] + " is set to " + rank);
					} catch(NumberFormatException e) {
						chan.sendMessage("The user " + args[2] + " is set to " + rank + " [" + iRank + "]");
					}
					
					break;
			}
		}
		
		// Forwarders
		else if(info.senderLevel >= cmdForward.getPermissionLevel() &&
				    cmdForward.getAvailability() &&
					coreWord.equalsIgnoreCase(cmdForward.getTrigger())) {
			if(args.length >= 2) {
				String toChan = args[1].toLowerCase();
				if(!toChan.startsWith("#")) {
					toChan = "#" + toChan;
				}
				
				if(Kdkbot.instance.isInChannel(toChan)) {
					Channel toChanObj = Kdkbot.instance.getChannel(toChan);
					toChanObj.sendMessage(info.channel + " has requested forwarding permissions. Type '" + toChanObj.cfgChan.getSetting("commandPrefix") + "afwd " + info.channel.replaceAll("#", "") + "' to authorize or '" + toChanObj.cfgChan.getSetting("commandPrefix") + "dfwd " + info.channel.replaceAll("#", "") + "' to deny.");
					chan.sendMessage("Sent forward request, awaiting reply.");
					
					// Add forwarder to both channels
					chan.forwarders.add(new Forwarder(toChan));
					toChanObj.forwarders.add(new Forwarder(info.channel));
				} else {
					chan.sendMessage(info.sender + ": This bot is not in that channel. Have them join my channel and type !join");
				}
				
			} else {
				chan.sendMessage(info.sender + ": You did not provide a channel name.");
			}
		}
		
		// Forwarder - Authorization - Accept
		else if(info.senderLevel >= cmdForward.getPermissionLevel() &&
				     cmdForward.getAvailability() && 
					(coreWord.equalsIgnoreCase("afwd")
				  || coreWord.equalsIgnoreCase("acceptforward"))) {
			if(args.length >= 2) {
				String toAuthorize = args[1].toLowerCase();
				if(!toAuthorize.startsWith("#")) { toAuthorize = "#" + toAuthorize; }
				
				Channel fromChan = Kdkbot.instance.getChannel(toAuthorize);
				fromChan.sendMessage("Forwarding authorization request accepted.");
				chan.sendMessage("Forwarding authorization request accepted.");
				fromChan.authorizeForwarder(info.channel);
				chan.authorizeForwarder(toAuthorize);
			} else {
				chan.sendMessage("You did not specify a channel to accept the forward request from.");
			}
		}

		// Forwarder - Authorization - Deny
		else if(info.senderLevel >= cmdForward.getPermissionLevel() &&
				     cmdForward.getAvailability() && 
					coreWord.equalsIgnoreCase("dfwd")) {
			if(args.length >= 2) {
				String toDeny = args[1].toLowerCase();
				if(!toDeny.startsWith("#")) { toDeny = "#" + toDeny; }
				
				Channel fromChan = Kdkbot.instance.getChannel(toDeny);
				fromChan.sendMessage("Forwarding authorization request denied.");
				chan.sendMessage("Forwarding authorization request denied.");
				fromChan.denyForwarder(info.channel);
				chan.denyForwarder(toDeny);
			} else {
				chan.sendMessage("You did not specify a channel to deny the forward request from.");
			}
		}
		
		// Forwarder - Stop
		else if(info.senderLevel >= cmdForward.getPermissionLevel() &&
				     cmdForward.getAvailability() && 
					coreWord.equalsIgnoreCase(cmdSForward.getTrigger())) {
			if(args.length >= 2) {
				String toStop = args[1].toLowerCase();
				if(!toStop.startsWith("#")) { toStop = "#" + toStop; }
				
				Channel fromChan = Kdkbot.instance.getChannel(toStop);
				fromChan.sendMessage("Stopping message forwarding.");
				chan.sendMessage("Stopping message forwarding.");
				
				fromChan.removeForwarder(info.channel);
				chan.removeForwarder(toStop);
			}
		}
		
		// Filter Bypass
		else if(info.senderLevel >= cmdPermit.getPermissionLevel() &&
				cmdPermit.getAvailability() && 
				coreWord.equalsIgnoreCase(cmdPermit.getTrigger())) {
			try {
				int bypassLimit = 0;
				String user = args[1];
				if(args.length > 2) {
					bypassLimit = Integer.parseInt(args[2]);
				} else {
					bypassLimit = 1;
				}
				chan.filterBypass.put(user, bypassLimit);
				chan.sendMessage(info.sender + " has permitted user " + args[1] + " to bypass all filters " + bypassLimit + " time(s)");
			} catch (NumberFormatException e) {
				chan.sendMessage(info.sender + ": " + args[2] + " is not a valid number to permit user " + args[1]);
			}
		}
		// Urban Look-up
		else if (info.senderLevel >= cmdUrban.getPermissionLevel() &&
					cmdUrban.getAvailability() &&
					coreWord.equalsIgnoreCase(cmdUrban.getTrigger())) {
			chan.sendMessage(API.getTopDefinition(info.getSegments(2)[1]));
		}
		// Quotes
		else if (info.senderLevel >= cmdQuotes.getPermissionLevel() &&
					cmdQuotes.getAvailability() &&
					coreWord.equalsIgnoreCase(cmdQuotes.getTrigger())) {
			quotes.executeCommand(info);
		}
		// Stats - Time
		else if(info.senderLevel >= cmdTime.getPermissionLevel() &&
					coreWord.equalsIgnoreCase(cmdTime.getTrigger())) {
			info.message = "stats time " + info.message;
			stats.executeCommand(info);
		}
		// Stats - Stats
		else if(info.senderLevel >= cmdStats.getPermissionLevel() &&
				cmdStats.getAvailability() &&
				coreWord.equalsIgnoreCase(cmdStats.getTrigger())) {
			info.message = "stats all " + info.message;
			stats.executeCommand(info);
		}
		// Stats - Msges
		else if(info.senderLevel >= cmdMsges.getPermissionLevel() &&
				cmdMsges.getAvailability() &&
				coreWord.equalsIgnoreCase(cmdMsges.getTrigger())) {
			info.message ="stats msges " + info.message;
			stats.executeCommand(info);
		}
		// Stats - Bits
		else if(info.senderLevel >= cmdBits.getPermissionLevel() &&
				cmdBits.getAvailability() &&
				coreWord.equalsIgnoreCase(cmdBits.getTrigger())) {
			info.message = "stats bits " + info.message;
			stats.executeCommand(info);
		}
		// Stats - Seen
		else if(info.senderLevel >= cmdSeen.getPermissionLevel() &&
				cmdSeen.getAvailability() &&
				coreWord.equalsIgnoreCase(cmdSeen.getTrigger())) {
			String date = stats.getLastLeaveDate(info.getSegments(2)[1]);
			if(date == null) {
				chan.sendMessage(info.sender + ": I have not seen " + info.getSegments(2)[1]);
			} else {
				chan.sendMessage(info.sender +": " + info.getSegments(2)[1] + " was last seen on " + date);
			}
		}
		// Custom Commands
		else if(info.senderLevel >= cmdCommands.getPermissionLevel() &&
				cmdCommands.getAvailability() &&
				coreWord.equalsIgnoreCase(cmdCommands.getTrigger())) {
			commandStrings.executeCommand(info);
		}
		// AMA
		else if(info.senderLevel >= cmdAma.getPermissionLevel() &&
				coreWord.equalsIgnoreCase(cmdAma.getTrigger()) &&
				cmdAma.getAvailability()) {
			amas.executeCommand(info);
		}
		// Counters
		else if(info.senderLevel >= cmdCounters.getPermissionLevel() &&
				coreWord.equalsIgnoreCase(cmdCounters.getTrigger())) {
			counters.executeCommand(info);
		}
		// Filters
		else if(info.senderLevel >= cmdFilter.getPermissionLevel() &&
				  coreWord.equalsIgnoreCase(cmdFilter.getTrigger())) {
			this.chan.filters.executeCommand(info);
		}
		// Timers
		else if(info.senderLevel >= cmdTimers.getPermissionLevel() &&
				coreWord.equalsIgnoreCase(cmdTimers.getTrigger())) {
			System.out.println("Triggered timers section");
			timers.executeCommand(info);
		}
		// Game
		else if(info.senderLevel >= 1 &&
				cmdGame.getAvailability() &&
				coreWord.equalsIgnoreCase(cmdGame.getTrigger())) {
			if(info.message.contains(" ") && info.senderLevel >= 3) {
				// We are updating game
				if(kdkbot.api.twitch.APIv5.setChannelGame(chan.getAccessToken(), chan.getUserID(), info.getSegments(2)[1])) {
					chan.sendMessage("Sent game update request.");
				} else {
					chan.sendMessage("Failed to send game update request.");
				}
			} else {
				// We are requesting game
				chan.sendMessage("Current game: " + kdkbot.api.twitch.APIv5.getChannelGame(chan.getAccessToken(), chan.getUserID()));
			}
		// Status (Title)
		} else if(info.senderLevel >= 1 &&
				cmdStatus.getAvailability() &&
				coreWord.equalsIgnoreCase(cmdStatus.getTrigger())) {
			if(info.message.contains(" ") && info.senderLevel >= 3) {
				// We are updating status
				if(kdkbot.api.twitch.APIv5.setChannelStatus(chan.getAccessToken(), chan.getUserID(), info.getSegments(2)[1])) {
					chan.sendMessage("Sent status update request.");
				} else {
					chan.sendMessage("Failed to send status update request.");
				}
			} else {
				// We are requesting status
				chan.sendMessage("Current title: " + kdkbot.api.twitch.APIv5.getChannelStatus(chan.getAccessToken(), chan.getUserID()));
			}
			
		}
		// Hosting
		else if(info.senderLevel >= 3 &&
				cmdHost.getAvailability() &&
				coreWord.equalsIgnoreCase(cmdHost.getTrigger())) {
			String[] parts = info.getSegments(2);
			if(parts.length > 1) {
				String channel = parts[1];
				if(kdkbot.api.twitch.APIv5.isEditorOf(chan.getAccessToken(), chan.getUserID())) {
					chan.sendRawMessage("/host " + channel);
					chan.sendMessage("Now hosting: " + channel);
				} else {
					chan.sendMessage("Cannot send host request! Bot isn't an editor of this channel.");
				}				
			} else {
				// We are looking up who the channel is hosting, we need to get user id then get the host target
				String hostTarget = kdkbot.api.twitch.APIv5.getHostTarget(Kdkbot.instance.getClientID(), chan.getUserID());
				if(hostTarget == null) {
					chan.sendMessage("Currently not hosting anyone.");
				} else {
					chan.sendMessage("Currently hosting " + hostTarget);
				}
			}
		}
		// Unhosting
		else if(info.senderLevel >= cmdUnhost.getPermissionLevel() &&
				coreWord.equalsIgnoreCase(cmdUnhost.getTrigger())) {
			if(kdkbot.api.twitch.APIv5.isEditorOf(chan.getAccessToken(), chan.getUserID())) {
				chan.sendRawMessage("/unhost");
			} else {
				chan.sendMessage("Cannot send unhost request! Bot isn't an editor of this channel.");
			}
		}
		// Stream Uptime
		else if(info.senderLevel >= 1 &&
				coreWord.equalsIgnoreCase("uptime")) {
			String getChan = chan.channel;
			String[] parts = info.getSegments(2);
			if(parts.length > 1) {
				getChan = parts[1];
			}
			
			String userID;
			if(chan.channel.equalsIgnoreCase(getChan)) {
				userID = chan.getUserID();
			} else {
				userID = kdkbot.api.twitch.APIv5.getUserID(Kdkbot.instance.getClientID(), getChan);
			}
			
			String res = kdkbot.api.twitch.APIv5.getStreamUptime(Kdkbot.instance.getClientID(), userID);
			if(res != null) {
				chan.sendMessage("Stream has been going for " + res);
			} else {
				chan.sendMessage("Stream is not currently live!");
			}
		}
		// Viewers
		else if(info.senderLevel >= 1 &&
				coreWord.equalsIgnoreCase("viewers")) {
			String res = kdkbot.api.twitch.APIv5.getStreamViewers(Kdkbot.instance.getClientID(), chan.getUserID());
			if(res == null) {
				chan.sendMessage("Stream is not currently live!");
			} else {
				chan.sendMessage("There are " + res + " viewers.");
			}
		}
		// Giveaway
		else if(info.senderLevel >= giveaway.getPermissionLevel() &&
				coreWord.equalsIgnoreCase(giveaway.getTrigger()) &&
				info.getSegments().length > 1) {
			giveaway.executeCommand(info);
		} else {
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
	
	public static String rankIntToName(int rank) {
		switch(rank) {
			case 1:
				return "normal";
			case 2:
				return "regular";
			case 3:
				return "moderator";
			case 4:
				return "super moderator";
			case 5:
				return "channel operator";
			case Integer.MAX_VALUE:
				return "max";
			case Integer.MIN_VALUE:
				return "min";
			case 0:
				return "nobody";
			default:
				return String.valueOf(rank);
		}
	}
	
	private Object getInternalCommandSetting(String settingSuffix, String settingType, Object defaultValue) {
		if(chan.cfgChan.getSetting(settingType + settingSuffix.toLowerCase()) == null) {
			chan.cfgChan.setSetting(settingType + settingSuffix.toLowerCase(), defaultValue.toString());
		}
		
		return chan.cfgChan.getSetting(settingType + settingSuffix.toLowerCase());
	}
}


