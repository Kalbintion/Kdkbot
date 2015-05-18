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
import kdkbot.commands.strings.*;

public class Commands {
	// Necessary variable for instance referencing
	public Kdkbot instance;
	public Channel chan;
	
	// Sub-system commands managers
	public Quotes quotes;
	public StringCommands commandStrings;
	public Counters counters;
	public AMA amas;
	
	public Commands(Kdkbot instance, String channel, Channel chanInst) {
		this.instance = instance;
		this.chan = chanInst;
		try {			
			this.commandStrings = new StringCommands(this.instance, channel);
			this.commandStrings.loadCommands();
			
			this.quotes = new Quotes(this.instance, channel);
			this.quotes.loadQuotes();
			
			this.counters = new Counters(this.instance, channel);
			this.counters.loadCounters();
			
			this.amas = new AMA(this.instance, channel);
			this.amas.loadQuestions();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void commandHandler(MessageInfo info) {
		instance.dbg.writeln(this, "Attempting to parse last message for channel " + info.channel);
		
		instance.dbg.writeln(this, "Got past filter section.");
		
		// Start command checking
		String coreWord = info.getSegments()[0].substring(1);
		String args[] = info.getSegments();
		
		instance.dbg.writeln(this, "Core Command detected as '" + coreWord + "'");
		instance.dbg.writeln(this, "Senders level detected as " + info.senderLevel + " for " + info.sender);
		
		// Enforce senders name to be lowercased - prevents case sensitive issues later on
		info.sender = info.sender.toLowerCase();
		
		// Permission Ranks
		if (info.senderLevel >= 3 &&
				coreWord.equalsIgnoreCase("perm")) {
			switch(args[1]) {
				case "set":
					chan.setSenderRank(args[2], Integer.parseInt(args[3]));
					instance.sendMessage(info.channel, "Set " + args[2] + " to level " + args[3] + " permission.");
					break;
				case "get":
					instance.sendMessage(info.channel, "The user " + args[2] + " is set to " + chan.getSenderRank(args[2]));
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
		// Filters
		else if(info.senderLevel >= 3 &&
				coreWord.equalsIgnoreCase("filters")) {
			// filters.executeCommand(channel, sender, login, hostname, message, additionalParams)
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
				instance.sendMessage(info.channel, info.sender + " has permitted user " + args[1] + " to bypass all filters " + args[2] + " time(s)");
			} catch (NumberFormatException e) {
				instance.sendMessage(info.channel, info.sender + ": " + args[2] + " is not a valid number to permit user " + args[1]);
			}
			chan.filterBypass.put(info.sender, Integer.parseInt(args[2]));
		}
		// Help
		else if(info.senderLevel >= 1 &&
				coreWord.equalsIgnoreCase("help")) {
			if(args.length <= 1) {
				// Send link to channel for wiki
				instance.sendMessage(info.channel, "You can see standard commands and get bot help @ https://github.com/kalbintion/kdkbot/wiki");
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
			instance.sendMessage(info.channel, "Raid http://www.twitch.tv/" + args[1]);
		}
		// Multitwitch
		else if (info.senderLevel >= 2 &&
				coreWord.equalsIgnoreCase("multi")) {
			String multiOut = "";
			for(int i = 1; i < args.length; i++) {
				multiOut += args[i] + "/";
			}
			instance.sendMessage(info.channel, "http://www.multitwitch.tv/" + multiOut);
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
			instance.sendMessage(info.channel, conchResponses[conchRnd.nextInt(conchResponses.length)]);
		}
		// Coin Flip
		else if(info.senderLevel >= 1 &&
				coreWord.equalsIgnoreCase("coin")) {
			Random coinRnd = new Random();
			String[] coinResponses = {"Heads", "Tails"};
			instance.sendMessage(info.channel, coinResponses[coinRnd.nextInt(coinResponses.length)]);
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
