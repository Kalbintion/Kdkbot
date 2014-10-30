package kdkbot.commands;

import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.*;

import org.jibble.pircbot.User;

import kdkbot.Kdkbot;
import kdkbot.commands.*;
import kdkbot.commands.quotes.*;
import kdkbot.commands.ama.AMA;
import kdkbot.commands.channel.*;
import kdkbot.commands.counters.*;
import kdkbot.commands.strings.*;
import kdkbot.filemanager.Config;

public class Commands {
	// Necessary variable for instance referencing
	public Kdkbot instance;
	
	// Path & Config locations (set by Commands() init)
	public Path permissionPath;
	public Path commandListPath;
	public Config cfgRanks;
	
	// Command prefix of this particular command set
	public String commandPrefix = "|";
	
	// Sub-system commands managers
	public Update channelUpdater;
	public Quotes quotes;
	public StringCommands commandStrings;
	public Counters counters;
	public AMA amas;
	
	public HashMap<String, Integer> senderRanks = new HashMap<String, Integer>();
	
	public Commands(Kdkbot instance, String channel) {
		this.instance = instance;
		try {
			// System.out.println("[DBG] [CMDS] [INIT] Attempting to load config ranks.");
			cfgRanks = new Config("./cfg/perms/" + channel + ".cfg");
			List<String> cfgContents = cfgRanks.getConfigContents();
			Iterator<String> iter = cfgContents.iterator();
			while(iter.hasNext()) {
				// System.out.println("[DBG] [CMDS] [INIT] Parsing next line of cfgArgs.");
				String cfgArgs[] = iter.next().split("=");
				// System.out.println("[DBG] [CMDS] [INIT] Size of cfgArgs is " + cfgArgs.length + " a value of 2 is expected.");
				try {
					senderRanks.put(cfgArgs[0], Integer.parseInt(cfgArgs[1]));
				} catch(NumberFormatException e) {
					e.printStackTrace();
				}
			}
			
			this.commandStrings = new StringCommands(this.instance, channel);
			this.commandStrings.loadCommands();
			
			this.quotes = new Quotes(this.instance, channel);
			this.quotes.loadQuotes();
			
			this.counters = new Counters(this.instance, channel);
			this.counters.loadCounters();
			
			this.amas = new AMA(this.instance, channel);
			this.amas.loadQuestions();
			
			this.channelUpdater = new Update();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void commandHandler(String channel, String sender, String login, String hostname, String message) {
		// System.out.println("[DBG] [CMD] [HND] Attempting to parse last message for channel " + channel);
		if(message.startsWith(commandPrefix)) {
			// System.out.println("DBG: Previous line detected as a command");
			String args[] = message.split(" ");
			String coreCommand = args[0].substring(commandPrefix.length()); // Snag the core command from the message
			
			// System.out.println("[DBG] [CMD] [HND] Core Command detected as '" + coreCommand + "'");
			// System.out.println("[DBG] [CMD] [HND] Senders level detected as " + getSenderRank(sender) + " for value " + sender);
			
			// Enforce senders name to be lowercased - prevents case sensitive issues later on
			sender = sender.toLowerCase();
			ArrayList<String> additionalParams = new ArrayList<String>();
			
			// Channel
			if(getSenderRank(sender) >= channelUpdater.getPermissionLevel() &&
					channelUpdater.getAvailability() &&
					channelUpdater.getTrigger().equalsIgnoreCase(coreCommand)) {
				additionalParams.add(instance.botCfg.getSetting("oauth"));
				channelUpdater.executeCommand(channel, sender, login, hostname, message, additionalParams);
			}
			// Permission Ranks
			else if (getSenderRank(sender) >= 3 &&
						coreCommand.equalsIgnoreCase("perm")) {
				// System.out.println("DBG: Detected command perm, checking for sub-command.");
				switch(args[1]) {
					case "set":
						this.setSenderRank(args[2].toLowerCase(), Integer.parseInt(args[3]));
						instance.sendMessage(channel, "Set " + args[2] + " to level " + args[3] + " permission.");
						break;
					case "get":
						instance.sendMessage(channel, "The user " + args[2] + " is set to " + this.getSenderRank(args[2]));
						break;
				}
			}
			// Help
			else if(getSenderRank(sender) >= 1 &&
						coreCommand.startsWith("help")) {
				if(args.length <= 1) {
					// Send link to channel for wiki
					instance.sendMessage(channel, "You can see standard commands and get bot help @ https://github.com/kalbintion/kdkbot/wiki");
				} else {
					// Get information for command help
				}
			}
			// Quotes
			else if (getSenderRank(sender) >= quotes.getPermissionLevel() &&
						quotes.getAvailability() &&
						quotes.getTrigger().startsWith(coreCommand)) {
				additionalParams.add("sender_rank=" + getSenderRank(sender));
				quotes.executeCommand(channel, sender, login, hostname, message, additionalParams);
			}
			// Raid
			else if (getSenderRank(sender) >= 3 &&
						coreCommand.startsWith("raid")) {
				instance.sendMessage(channel, "Raid http://www.twitch.tv/" + args[1]);
			}
			// Multitwitch
			else if (getSenderRank(sender) >= 2 &&
						coreCommand.startsWith("multi")) {
				String multiOut = "";
				for(int i = 1; i < args.length; i++) {
					multiOut += args[i] + "/";
				}
				instance.sendMessage(channel, "http://www.multitwitch.tv/" + multiOut);
			}
			// Custom Commands
			else if(getSenderRank(sender) >= 1 &&
						coreCommand.startsWith("commands")) {
				commandStrings.executeCommand(channel, sender, login, hostname, message, getSenderRank(sender), additionalParams);
			}
			// AMA
			else if(getSenderRank(sender) >= amas.getPermissionLevel() &&
						coreCommand.equalsIgnoreCase(amas.getTrigger()) &&
						amas.getAvailability()) {
				System.out.println("[DBG] [CMDHND] [AMA] Sending message to AMA handler.");
				amas.executeCommand(channel, sender, login, hostname, message, additionalParams);
			}
			// Counters
			else if(getSenderRank(sender) >= 1 &&
						coreCommand.startsWith("counter")) {
				counters.executeCommand(channel, sender, login, hostname, message, getSenderRank(sender), additionalParams);
			}
			// Magic 8-Ball / Conch
			else if(getSenderRank(sender) >= 1 &&
						(coreCommand.equalsIgnoreCase("conch") ||
						 coreCommand.equalsIgnoreCase("8ball"))) {
				Random conchRnd = new Random();
				String[] conchResponses = {"It is certain", "It is decidedly so", "Without a doubt", "Yes definitely", "You may rely on it", "As I see it, yes", "Most likely", "Outlook good", "Yes", "Signs point to yes", "Reply hazy, try again", "Ask again later", "Better not tell you now", "Cannot predict now", "Concentrate and ask again", "Don't count on it", "My reply is no", "My sources say no", "Outlook not so good", "Very doubtful"};
				instance.sendMessage(channel, conchResponses[conchRnd.nextInt(conchResponses.length)]);
			}
			// Coin Flip
			else if(getSenderRank(sender) >= 1 &&
						coreCommand.equalsIgnoreCase("coin")) {
				Random coinRnd = new Random();
				String[] coinResponses = {"Heads", "Tails"};
				instance.sendMessage(channel, coinResponses[coinRnd.nextInt(coinResponses.length)]);
			}
			// Custom String Commands
			Iterator<StringCommand> stringIter = commandStrings.commands.iterator();
			while(stringIter.hasNext()) {
				// System.out.println("[DBG] [CMDS] [CHK] Testing next iteration of custom string commands");
				StringCommand stringNext = stringIter.next();
				// System.out.println("[DBG] [CMDS] [CHK] Testing coreCommand '" + coreCommand + "' against '" + stringNext.getTrigger() + "'");
				// Verify user has access to this command
				// System.out.println("[DBG] [CMDS] [CHK] Current commands level: " + stringNext.cpl.getLevel());
				// System.out.println("[DBG] [CMDS] [CHK] Current command is available: " + stringNext.isAvailable);
				if(getSenderRank(sender) >= stringNext.getPermissionLevel() &&
						coreCommand.equalsIgnoreCase(stringNext.getTrigger()) &&
						stringNext.getAvailability()) {
					// System.out.println("[DBG] [CMDS] [CHK] Found usable command for " + sender + " under trigger " + stringNext.getTrigger());
					stringNext.executeCommand(channel, sender, login, hostname, message, additionalParams);
				}
			}
		}
	}
	
	/**
	 * Sets the permission file's path for this command object
	 * @param filePath The path to the file 
	 */
	public void setPermissionPath(Path filePath) {
		this.permissionPath = filePath;
	}
	
	/**
	 * Gets the permission file's path for this command object
	 * @return The path to the file
	 */
	public Path getPermissionPath() {
		return this.permissionPath;
	}
	
	/**
	 * Sets the command list file's path for this command object
	 * @param filePath The command list path object to use
	 */
	public void setCommandListPath(Path filePath) {
		this.commandListPath = filePath;
	}
	
	/**
	 * Gets the command list file's path for this command object
	 * @return The command list path object
	 */
	public Path getCommandListPath() {
		return this.commandListPath;
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
			strings = (ArrayList<String>) cfgRanks.getConfigContents();
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
			List<String> strings = cfgRanks.getConfigContents();
			Iterator<String> string = strings.iterator();
			while(string.hasNext()) {
				String[] args = string.next().split("=");
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
		cfgRanks.saveSettings();
	}
	
	/**
	 * 
	 * @param sendReferenceMap
	 */
	public void saveSenderRanks(boolean sendReferenceMap) {
		cfgRanks.saveSettings(this.senderRanks);
	}
}
