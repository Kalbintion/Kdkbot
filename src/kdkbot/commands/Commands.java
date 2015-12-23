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
		System.out.println("Sender Level: " + info.senderLevel);
		if(info.senderLevel >= 5 &&
				coreWord.equalsIgnoreCase("channel")) {
			// Channel settings
			if(args[1].equalsIgnoreCase("commandProcessing")) {
				chan.cfgChan.setSetting("commandProcessing", String.valueOf(Boolean.parseBoolean(args[2])));
				chan.commandProcessing = Boolean.parseBoolean(args[2]);
				Kdkbot.instance.sendMessage(info.channel, "Channel's command processing set to " + String.valueOf(chan.commandProcessing));
			} else if(args[1].equalsIgnoreCase("commandPrefix")) {
				chan.cfgChan.setSetting("commandPrefix", args[2]);
				chan.commandPrefix = args[2];
				Kdkbot.instance.sendMessage(info.channel, "Channel's command prefix set to " + args[2]);
			}
		}
		
		// Command Processing Breaking
		if(!chan.commandProcessing) { return; }
		
		// Permission Ranks
		if (info.senderLevel >= 3 &&
				coreWord.equalsIgnoreCase("perm")) {
			switch(args[1]) {
				case "set":
					int toRank;
					try {
						toRank = Integer.parseInt(args[3]);
					} catch(NumberFormatException e) {
						// May mean we need to translate it from a rank to integer before adding
						switch(args[3]) {
							case "nobody":
								args[3] = "0";
								break;
							case "normal":
								args[3] = "1";
								break;
							case "regular":
								args[3] = "2";
								break;
							case "moderator":
							case "mod":
								args[3] = "3";
								break;
							case "supermoderator":
							case "smod":
							case "supermod":
								args[3] = "4";
								break;
							case "channeloperator":
							case "chanop":
								args[3] = "5";
								break;
							default:
								args[3] = "0";
						}
						toRank = Integer.parseInt(args[3]);
					}
					
					if(info.senderLevel <= toRank) {
						chan.setSenderRank(args[2], Integer.parseInt(args[3]));
						Kdkbot.instance.sendMessage(info.channel, "Set " + args[2] + " to level " + args[3] + " permission.");
					} else {
						Kdkbot.instance.sendMessage(info.channel, info.sender + ": You cannot set someones rank to a higher one than your own");
					}
					break;
				case "get":
					Kdkbot.instance.sendMessage(info.channel, "The user " + args[2] + " is set to " + chan.getSenderRank(args[2]));
					break;
			}
		}
		// Forwarders
		else if(info.senderLevel >= 5 &&
					(coreWord.equalsIgnoreCase("fwd")
				  || coreWord.equalsIgnoreCase("forward"))) {
			String toChan = args[1];
			this.chan.forwarders.add(new Forwarder(toChan));
		}
		// Filter Bypass
		else if(info.senderLevel >= 3 &&
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
		// Regulars
		
		// Help
		else if(info.senderLevel >= 1 &&
				coreWord.equalsIgnoreCase("help")) {
			if(args.length <= 1) {
				// Send link to channel for wiki
				Kdkbot.instance.sendMessage(info.channel, "You can see standard commands and get bot help @ https://github.com/kalbintion/kdkbot/wiki");
			} else {
				// Get information for command help
			}
		}
		// Quotes
		else if (info.senderLevel >= quotes.getPermissionLevel() &&
					quotes.getAvailability() &&
					quotes.getTrigger().equalsIgnoreCase(coreWord)) {
			quotes.executeCommand(info);
		}
		// Raid
		else if (info.senderLevel >= 3 &&
				coreWord.equalsIgnoreCase("raid")) {
			Kdkbot.instance.sendMessage(info.channel, "Raid http://www.twitch.tv/" + args[1]);
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
		else if(info.senderLevel >= 1 &&
				coreWord.equalsIgnoreCase("counter")) {
			counters.executeCommand(info);
		}
		// Magic 8-Ball / Conch
		else if(info.senderLevel >= 1 &&
					(coreWord.equalsIgnoreCase("conch") ||
					 coreWord.equalsIgnoreCase("8ball"))) {
			Random conchRnd = new Random();
			String[] conchResponses = {"It is certain", "It is decidedly so", "Without a doubt", "Yes definitely", "You may rely on it", "As I see it, yes", "Most likely", "Outlook good", "Yes", "Signs point to yes", "Reply hazy, try again", "Ask again later", "Better not tell you now", "Cannot predict now", "Concentrate and ask again", "Don't count on it", "My reply is no", "My sources say no", "Outlook not so good", "Very doubtful"};
			Kdkbot.instance.sendMessage(info.channel, conchResponses[conchRnd.nextInt(conchResponses.length)]);
		}
		// Coin Flip
		else if(info.senderLevel >= 1 &&
				coreWord.equalsIgnoreCase("coin")) {
			Random coinRnd = new Random();
			String[] coinResponses = {"Heads", "Tails"};
			Kdkbot.instance.sendMessage(info.channel, coinResponses[coinRnd.nextInt(coinResponses.length)]);
		} else if(info.senderLevel >= 5 &&
				  coreWord.equalsIgnoreCase("fwd")) {
		
		} else if(info.senderLevel >= 3 &&
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
}
