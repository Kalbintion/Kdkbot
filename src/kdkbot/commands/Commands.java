package kdkbot.commands;

import java.util.Iterator;
import java.util.Random;

import kdkbot.Kdkbot;
import kdkbot.MessageInfo;
import kdkbot.channel.Channel;
import kdkbot.channel.Forwarder;
import kdkbot.commands.quotes.*;
import kdkbot.commands.ama.AMA;
import kdkbot.commands.counters.*;
import kdkbot.commands.stats.Stats;
import kdkbot.commands.strings.*;

public class Commands {
	// Necessary variable for instance referencing
	public Channel chan;
	
	// Sub-system commands managers
	public Quotes quotes;
	public Stats stats;
	public StringCommands commandStrings;
	public Counters counters;
	public AMA amas;
	
	// Additional Commands
	private CommandHolder cmdChannel = new CommandHolder();
	private CommandHolder cmdPerm = new CommandHolder();
	private CommandHolder cmdForward = new CommandHolder();
	private CommandHolder cmdPermit = new CommandHolder();
	private CommandHolder cmdFilter = new CommandHolder();
	
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
			
			if(chan.cfgChan.getSetting("rankChannel") == null) {
				chan.cfgChan.setSetting("rankChannel", "5");
			}
			
			try {
				this.cmdChannel.setPermissionLevel(Integer.parseInt(chan.cfgChan.getSetting("rankChannel")));
			} catch(NumberFormatException e) {
				Kdkbot.instance.sendMessage(channel, "This channels rankChannel setting is invalid! Got " + chan.cfgChan.getSetting("rankChannel"));
				this.cmdChannel.setAvailability(false);
			}
			
			if(chan.cfgChan.getSetting("rankPerm") == null) {
				chan.cfgChan.setSetting("rankPerm", "5");
			}
			
			try {
				this.cmdPerm.setPermissionLevel(Integer.parseInt(chan.cfgChan.getSetting("rankPerm")));
			} catch(NumberFormatException e) {
				Kdkbot.instance.sendMessage(channel, "This channels rankPerm setting is invalid! Got " + chan.cfgChan.getSetting("rankPerm"));
				this.cmdPerm.setAvailability(false);
			}
			
			if(chan.cfgChan.getSetting("rankPermit") == null) {
				chan.cfgChan.setSetting("rankPermit", "3");
			}
			
			try {
				this.cmdPermit.setPermissionLevel(Integer.parseInt(chan.cfgChan.getSetting("rankPermit")));
			} catch(NumberFormatException e) {
				Kdkbot.instance.sendMessage(channel, "This channels rankPermit setting is invalid! Got " + chan.cfgChan.getSetting("rankPermit"));
				this.cmdPermit.setAvailability(false);
			}
			
			if(chan.cfgChan.getSetting("rankForward") == null) {
				chan.cfgChan.setSetting("rankForward", "4");
			}
			
			try {
				this.cmdForward.setPermissionLevel(Integer.parseInt(chan.cfgChan.getSetting("rankForward")));
			} catch (NumberFormatException e) {
				Kdkbot.instance.sendMessage(channel, "This channels rankForward settig is invalid! Got " + chan.cfgChan.getSetting("rankForward"));
				this.cmdForward.setAvailability(false);
			}
			
			if(chan.cfgChan.getSetting("rankQuotes") == null) {
				chan.cfgChan.setSetting("rankQuotes", "1");
			}
			
			try {
				this.quotes.setPermissionLevel(Integer.parseInt(chan.cfgChan.getSetting("rankQuotes")));
			} catch (NumberFormatException e) {
				Kdkbot.instance.sendMessage(channel, "This channels rankQuotes setting is invalid! Got " + chan.cfgChan.getSetting("rankQuotes"));
				this.quotes.setAvailability(false);
			}
			
			if(chan.cfgChan.getSetting("rankCounters") == null) {
				chan.cfgChan.setSetting("rankCounters", "1");
			}
			
			try {
				this.counters.setPermissionLevel(Integer.parseInt(chan.cfgChan.getSetting("rankCounters")));
			} catch (NumberFormatException e) {
				Kdkbot.instance.sendMessage(channel, "This channels rankCounters setting is invalid! Got " + chan.cfgChan.getSetting("rankCounters"));
				this.counters.setAvailability(false);
			}
			
			if(chan.cfgChan.getSetting("rankFilter") == null) {
				chan.cfgChan.setSetting("rankFilter", "3");
			}
			
			try {
				this.cmdFilter.setPermissionLevel(Integer.parseInt(chan.cfgChan.getSetting("rankFilter")));
			} catch(NumberFormatException e) {
				Kdkbot.instance.sendMessage(channel, "This channels rankFilter setting is invalid! Got " + chan.cfgChan.getSetting("rankFilter"));
				this.cmdFilter.setAvailability(false);
			}
			
			if(chan.cfgChan.getSetting("rankAMA") == null) {
				chan.cfgChan.setSetting("rankAMA", "1");
			}
			
			try {
				this.amas.setPermissionLevel(Integer.parseInt(chan.cfgChan.getSetting("rankAMA")));
			} catch (NumberFormatException e) {
				Kdkbot.instance.sendMessage(channel, "This channels rankAMA setting is invalid! Got " + chan.cfgChan.getSetting("rankAMA"));
				this.amas.setAvailability(false);
			}
			

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void commandHandler(MessageInfo info) {
		Kdkbot.instance.dbg.writeln(this, "Attempting to parse last message for channel " + info.channel);
		
		// Enforce senders name to be lowercased - prevents case sensitive issues later on
		info.sender = info.sender.toLowerCase();
		
		// Start command checking
		String coreWord = info.getSegments()[0].substring(chan.commandPrefix.length());
		String args[] = info.getSegments();
		
		Kdkbot.instance.dbg.writeln(this, "Core Command detected as '" + coreWord + "'");
		Kdkbot.instance.dbg.writeln(this, "Senders level detected as " + info.senderLevel + " for " + info.sender);
		
		// These commands supersede command processing toggling
		if(info.senderLevel >= cmdChannel.getPermissionLevel() &&
				coreWord.equalsIgnoreCase("channel")) {
			// Channel settings
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
			}
			
			Kdkbot.instance.sendMessage(info.channel, "Channel setting " + args[1] + " set to " + args[2]);
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
						Kdkbot.instance.sendMessage(info.channel, info.sender + ": You cannot set someones rank to a higher one than your own");
					} else {
						chan.setSenderRank(args[2], toRank);
						Kdkbot.instance.sendMessage(info.channel, "Set " + args[2] + " to permission rank: " + args[3] + ".");
					}
					break;
				case "get":
					Kdkbot.instance.sendMessage(info.channel, "The user " + args[2] + " is set to " + chan.getSenderRank(args[2]));
					break;
			}
		}
		// Forwarders
		else if(info.senderLevel >= cmdForward.getPermissionLevel() &&
					(coreWord.equalsIgnoreCase("fwd")
				  || coreWord.equalsIgnoreCase("forward"))) {
			String toChan = args[1];
			this.chan.forwarders.add(new Forwarder(toChan));
		}
		// Filter Bypass
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
				Kdkbot.instance.sendMessage(info.channel, info.sender + " has permitted user " + args[1] + " to bypass all filters " + args[2] + " time(s)");
			} catch (NumberFormatException e) {
				Kdkbot.instance.sendMessage(info.channel, info.sender + ": " + args[2] + " is not a valid number to permit user " + args[1]);
			}
			chan.filterBypass.put(info.sender, Integer.parseInt(args[2]));
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
			stats.executeCommand(info);
		}
		// Multitwitch
		else if (info.senderLevel >= 2 &&
				coreWord.equalsIgnoreCase("multi")) {
			String multiOut = "";
			for(int i = 1; i < args.length; i++) {
				multiOut += args[i] + "/";
			}
			Kdkbot.instance.sendMessage(info.channel, "http://www.multitwitch.tv/" + multiOut);
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
				try {
					return Integer.parseInt(rank);
				} catch (NumberFormatException e) {
					return 0;
				}
		}
	}
}
