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
import kdkbot.channel.Channel;
import kdkbot.commands.*;
import kdkbot.commands.quotes.*;
import kdkbot.commands.ama.AMA;
import kdkbot.commands.counters.*;
import kdkbot.commands.strings.*;
import kdkbot.filemanager.Config;

public class Commands {
	// Necessary variable for instance referencing
	public Kdkbot instance;
	public Channel chan;
	
	// Path & Config locations (set by Commands() init)
	public Path permissionPath;
	public Path commandListPath;
	public Config cfgRanks;
	
	// Command prefix of this particular command set
	public String commandPrefix = "|";
	
	// Sub-system commands managers
	public Quotes quotes;
	public StringCommands commandStrings;
	public Counters counters;
	public AMA amas;
	
	public HashMap<String, Integer> senderRanks = new HashMap<String, Integer>();
	
	public Commands(Kdkbot instance, String channel) {
		this.instance = instance;
		try {
			instance.dbg.writeln(this, "Attempting to load config ranks.");
			cfgRanks = new Config("./cfg/perms/" + channel + ".cfg");
			List<String> cfgContents = cfgRanks.getConfigContents();
			Iterator<String> iter = cfgContents.iterator();
			while(iter.hasNext()) {
				instance.dbg.writeln(this, "Parsing next line of cfgArgs.");
				String cfgArgs[] = iter.next().split("=");
				instance.dbg.writeln(this, "Size of cfgArgs is " + cfgArgs.length + " a value of 2 is expected.");
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
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void commandHandler(String channel, String sender, String login, String hostname, String message) {
		instance.dbg.writeln(this, "Attempting to parse last message for channel " + channel);
		if(message.startsWith(commandPrefix)) {
			instance.dbg.writeln(this, "Previous line detected as a command");
			String args[] = message.split(" ");
			String coreCommand = args[0].substring(commandPrefix.length()); // Snag the core command from the message
			String coreMessage = message.substring(args[0].length());
			
			instance.dbg.writeln(this, "Core Command detected as '" + coreCommand + "'");
			instance.dbg.writeln(this, "Senders level detected as " + getSenderRank(sender) + " for value " + sender);
			
			// Enforce senders name to be lowercased - prevents case sensitive issues later on
			sender = sender.toLowerCase();
			ArrayList<String> additionalParams = new ArrayList<String>();
			
			// Permission Ranks
			if (getSenderRank(sender) >= 3 &&
						coreCommand.equalsIgnoreCase("perm")) {
				instance.dbg.writeln(this, "DBG: Detected command perm, checking for sub-command.");
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
			// Channel - Game
			else if(getSenderRank(sender) >= 5 &&
						coreCommand.equalsIgnoreCase("game")) {
				instance.twitch.setChannelProperty(channel, "game", coreMessage);
				instance.sendMessage(channel, "Sent update message for game to: " + coreMessage);
			}
			// Channel - Status (Title)
			else if(getSenderRank(sender) >= 5 &&
						coreCommand.equalsIgnoreCase("title")) {
				instance.twitch.setChannelProperty(channel, "status", coreMessage);
				instance.sendMessage(channel, "Sent update message for title to: " + coreMessage);
			}
			// Twitch API Testing
			else if(getSenderRank(sender) >= 6 &&
						coreCommand.equalsIgnoreCase("twitchapi")) {
				switch(args[1]) {
					case "header":
						switch(args[2]) {
							case "list":
								String outputMessage = "";
								Iterator headerIter = this.instance.twitch.getHeaders().entrySet().iterator();
								while(headerIter.hasNext()) {
									Map.Entry pairs = (Map.Entry)headerIter.next();
									outputMessage += pairs.getKey() + "=" + pairs.getValue() + ", ";
								}
								instance.sendMessage(channel, outputMessage);
								break;
							case "add":
								instance.twitch.addHeader(args[2], args[3]);
								instance.sendMessage(channel, "Added header named '" + args[2] + "' with value '" + args[3] +"'");
						}
					break;
					case "get":
						instance.sendMessage(channel, instance.twitch.getChannelProperty(channel, args[2]).getAsString());
						break;
					case "set":
						String[] setArgs = message.split(" ", 4);
						instance.sendMessage(channel, instance.twitch.setChannelProperty(channel, setArgs[2], setArgs[3]));
						break;
					case "raw":
						String[] rawArgs = message.split(" ", 5);
						instance.sendMessage(channel, instance.twitch.sendRawData(rawArgs[2], rawArgs[3], rawArgs[4]).toString());
				}
			}
			// Help
			else if(getSenderRank(sender) >= 1 &&
						coreCommand.equalsIgnoreCase("help")) {
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
						quotes.getTrigger().equalsIgnoreCase(coreCommand)) {
				additionalParams.add("sender_rank=" + getSenderRank(sender));
				quotes.executeCommand(channel, sender, login, hostname, message, additionalParams);
			}
			// Raid
			else if (getSenderRank(sender) >= 3 &&
						coreCommand.equalsIgnoreCase("raid")) {
				instance.sendMessage(channel, "Raid http://www.twitch.tv/" + args[1]);
			}
			// Multitwitch
			else if (getSenderRank(sender) >= 2 &&
						coreCommand.equalsIgnoreCase("multi")) {
				String multiOut = "";
				for(int i = 1; i < args.length; i++) {
					multiOut += args[i] + "/";
				}
				instance.sendMessage(channel, "http://www.multitwitch.tv/" + multiOut);
			}
			// Custom Commands
			else if(getSenderRank(sender) >= 1 &&
						coreCommand.equalsIgnoreCase("commands")) {
				commandStrings.executeCommand(channel, sender, login, hostname, message, getSenderRank(sender), additionalParams);
			}
			// AMA
			else if(getSenderRank(sender) >= amas.getPermissionLevel() &&
						coreCommand.equalsIgnoreCase(amas.getTrigger()) &&
						amas.getAvailability()) {
				instance.dbg.writeln(this, "Sending message to AMA handler.");
				amas.executeCommand(channel, sender, login, hostname, message, additionalParams);
			}
			// Counters
			else if(getSenderRank(sender) >= 1 &&
						coreCommand.equalsIgnoreCase("counter")) {
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
			else if(getSenderRank(sender) >= 5 &&
						coreCommand.equalsIgnoreCase("fwd")) {
			
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
				if(getSenderRank(sender) >= stringNext.getPermissionLevel() &&
						coreCommand.equalsIgnoreCase(stringNext.getTrigger()) &&
						stringNext.getAvailability()) {
					instance.dbg.writeln(this, "[DBG] [CMDS] [CHK] Found usable command for " + sender + " under trigger " + stringNext.getTrigger());
					stringNext.executeCommand(channel, sender, login, hostname, message, additionalParams);
					break;
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
