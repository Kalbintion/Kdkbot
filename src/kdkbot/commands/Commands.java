package kdkbot.commands;

import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.*;

import org.jibble.pircbot.User;

import kdkbot.Kdkbot;
import kdkbot.MessageInfo;
import kdkbot.channel.Channel;
import kdkbot.channel.Forwarder;
import kdkbot.commands.*;
import kdkbot.commands.quotes.*;
import kdkbot.commands.ama.AMA;
import kdkbot.commands.counters.*;
import kdkbot.commands.filters.Filter;
import kdkbot.commands.filters.Filters;
import kdkbot.commands.strings.*;
import kdkbot.filemanager.Config;

public class Commands {
	// Necessary variable for instance referencing
	public Kdkbot instance;
	public Channel chan;
	
	// Command prefix of this particular command set
	public String commandPrefix = "|";
	
	// Sub-system commands managers
	public Quotes quotes;
	public StringCommands commandStrings;
	public Counters counters;
	public AMA amas;
	public Filters filters;
	
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
			
			this.filters = new Filters(this.instance, channel);
			this.filters.loadFilters();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void commandHandler(MessageInfo info) {
		instance.dbg.writeln(this, "Attempting to parse last message for channel " + info.channel);
		
		// Begin filtering first before checking for command validity
		ArrayList<Filter> fList = this.filters.getFilters();
		Iterator<Filter> fIter = fList.iterator();
		instance.dbg.writeln(this, "Filter count: " + fList.size());
		while(fIter.hasNext()) {
			Filter filter = fIter.next();
			if(filter.contains(info.message)) {
				switch(filter.action) {
					case 1:
						instance.dbg.writeln(this, "Attempting to purge user due to filter");
						instance.sendMessage(info.channel, "/timeout " + info.sender + " 1");
						break;
					case 2:
						instance.dbg.writeln(this, "Attempting to timeout user due to filter");
						instance.sendMessage(info.channel, "/timeout " + info.sender);
						break;
					case 3:
						instance.dbg.writeln(this, "Attempting to ban user due to filter");
						instance.sendMessage(info.channel, "/ban " + info.sender);
						break;
					case 4:
						instance.dbg.writeln(this, "Attempting to respond to user due to filter");
						instance.sendMessage(info.channel, info.sender + ": " + filter.actionInfo);
						break;
				}
			}
		}

		instance.dbg.writeln(this, "Got past filter section.");
		
		// Start command checking
		if(info.message.startsWith(commandPrefix)) {
			instance.dbg.writeln(this, "Previous line detected as a command");
			String args[] = info.getSegments();
			String coreCommand = args[0].substring(commandPrefix.length()); // Snag the core command from the message
			String coreMessage = "";
			if(args.length > 1)
				coreMessage = info.message.substring(args[0].length() + 1);
			
			instance.dbg.writeln(this, "Core Command detected as '" + coreCommand + "'");
			instance.dbg.writeln(this, "Senders level detected as " + info.senderLevel + " for " + info.sender);
			
			// Enforce senders name to be lowercased - prevents case sensitive issues later on
			info.sender = info.sender.toLowerCase();
			ArrayList<String> additionalParams = new ArrayList<String>();

			// Permission Ranks
			if (info.senderLevel >= 3 &&
						coreCommand.equalsIgnoreCase("perm")) {
				switch(args[1]) {
					case "set":
						instance.sendMessage(info.channel, "Set " + args[2] + " to level " + args[3] + " permission.");
						break;
					case "get":
						instance.sendMessage(info.channel, "The user " + args[2] + " is set to " + chan.getSenderRank(args[2]));
						break;
				}
			}
			// Forwarders
			else if(info.senderLevel >= 5 &&
						(coreCommand.equalsIgnoreCase("fwd")
					  || coreCommand.equalsIgnoreCase("forward"))) {
				String toChan = args[1];
				this.chan.forwarders.add(new Forwarder(toChan));
			}
			// Filters
			else if(info.senderLevel >= 3 &&
						coreCommand.equalsIgnoreCase("filters")) {
				// filters.executeCommand(channel, sender, login, hostname, message, additionalParams)
			}
			// Help
			else if(info.senderLevel >= 1 &&
						coreCommand.equalsIgnoreCase("help")) {
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
						quotes.getTrigger().equalsIgnoreCase(coreCommand)) {
				quotes.executeCommand(info);
			}
			// Raid
			else if (info.senderLevel >= 3 &&
						coreCommand.equalsIgnoreCase("raid")) {
				instance.sendMessage(info.channel, "Raid http://www.twitch.tv/" + args[1]);
			}
			// Multitwitch
			else if (info.senderLevel >= 2 &&
						coreCommand.equalsIgnoreCase("multi")) {
				String multiOut = "";
				for(int i = 1; i < args.length; i++) {
					multiOut += args[i] + "/";
				}
				instance.sendMessage(info.channel, "http://www.multitwitch.tv/" + multiOut);
			}
			// Custom Commands
			else if(info.senderLevel >= 1 &&
						coreCommand.equalsIgnoreCase("commands")) {
				commandStrings.executeCommand(info);
			}
			// AMA
			else if(info.senderLevel >= amas.getPermissionLevel() &&
						coreCommand.equalsIgnoreCase(amas.getTrigger()) &&
						amas.getAvailability()) {
				instance.dbg.writeln(this, "Sending message to AMA handler.");
				amas.executeCommand(info);
			}
			// Counters
			else if(info.senderLevel >= 1 &&
						coreCommand.equalsIgnoreCase("counter")) {
				counters.executeCommand(info);
			}
			// Magic 8-Ball / Conch
			else if(info.senderLevel >= 1 &&
						(coreCommand.equalsIgnoreCase("conch") ||
						 coreCommand.equalsIgnoreCase("8ball"))) {
				Random conchRnd = new Random();
				String[] conchResponses = {"It is certain", "It is decidedly so", "Without a doubt", "Yes definitely", "You may rely on it", "As I see it, yes", "Most likely", "Outlook good", "Yes", "Signs point to yes", "Reply hazy, try again", "Ask again later", "Better not tell you now", "Cannot predict now", "Concentrate and ask again", "Don't count on it", "My reply is no", "My sources say no", "Outlook not so good", "Very doubtful"};
				instance.sendMessage(info.channel, conchResponses[conchRnd.nextInt(conchResponses.length)]);
			}
			// Coin Flip
			else if(info.senderLevel >= 1 &&
						coreCommand.equalsIgnoreCase("coin")) {
				Random coinRnd = new Random();
				String[] coinResponses = {"Heads", "Tails"};
				instance.sendMessage(info.channel, coinResponses[coinRnd.nextInt(coinResponses.length)]);
			} else if(info.senderLevel >= 5 &&
						coreCommand.equalsIgnoreCase("fwd")) {
			
			} else if(info.senderLevel >= 3 &&
						coreCommand.equalsIgnoreCase("filter")) {
				filters.executeCommand(info);
			}
			// Custom String Commands
			Iterator<StringCommand> stringIter = commandStrings.commands.iterator();
			while(stringIter.hasNext()) {
				instance.dbg.writeln(this, "[DBG] [CMDS] [CHK] Testing next iteration of custom string commands");
				StringCommand stringNext = stringIter.next();
				instance.dbg.writeln(this, "[DBG] [CMDS] [CHK] Testing coreCommand '" + coreCommand + "' against '" + stringNext.getTrigger() + "'");
				// Verify user has access to this command
				instance.dbg.writeln(this, "[DBG] [CMDS] [CHK] Current commands level: " + stringNext.getPermissionLevel());
				instance.dbg.writeln(this, "[DBG] [CMDS] [CHK] Current command is available: " + stringNext.getAvailability());
				if(info.senderLevel >= stringNext.getPermissionLevel() &&
						coreCommand.equalsIgnoreCase(stringNext.getTrigger()) &&
						stringNext.getAvailability()) {
					instance.dbg.writeln(this, "[DBG] [CMDS] [CHK] Found usable command for " + info.sender + " under trigger " + stringNext.getTrigger());
					stringNext.executeCommand(info);
				}
			}
		}
	}
}
