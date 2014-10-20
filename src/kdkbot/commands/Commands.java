package kdkbot.commands;

import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
					channelUpdater.isAvailable() &&
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
			// Quotes
			else if (getSenderRank(sender) >= quotes.getPermissionLevel() &&
						quotes.isAvailable() &&
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
				switch(args[1]) {
					case "new":
						if(getSenderRank(sender) >= 3 ) {
							String[] csArgs = message.split(" ", 5);
							System.out.println("[DBG] [CMD] [STRCMD] csArgs size: " + csArgs.length);
							for(int i = 0 ; i < csArgs.length; i++) {
								System.out.println("[DBG] [CMD] [STRCMD] csArgs[" + i + "] is " + csArgs[i]);
							}
							commandStrings.addCommand(csArgs[2], csArgs[4], csArgs[3]);
							commandStrings.saveCommands();
						}
						break;
					case "edit":
						if(getSenderRank(sender) >= 3) {
							String[] csArgs = message.split(" ", 5);
							System.out.println("[DBG] [CMD] [STRCMD] csArgs size: " + csArgs.length);
							for(int i = 0 ; i < csArgs.length; i++) {
								System.out.println("[DBG] [CMD] [STRCMD] csArgs[" + i + "] is " + csArgs[i]);
							}
						}
						break;
					case "list":
						// |commands list <rank>
						String outMessage = "Commands for " + channel + " ";
						// Hardcoded commands - need a better situation here
						String[] additionalCommands = {"", "ama, ama next, ama get, counter add, counter sub, counter mult, counter divide, counter get, commands list, quote get, ", "ama add, counter new, counter delete, multi, ", "commands new, commands edit, commands remove, raid, quote add, perm get, perm set, ", "", ""};
						
						if(args.length == 3) {
							// We are expecting a rank to list for
							outMessage += " @ rank " + args[2] +": ";
							
							int expectedRank = 0;
							if(args[2] == "*") {
								// Show all - Using expectedRank ID -1000
								expectedRank = -1000;
							} else {
								// Grab expected rank
								expectedRank = Integer.parseInt(args[2]);
							}
							
							if(expectedRank != -1000) {
								outMessage += additionalCommands[expectedRank];
							}
							// Append the rest of the commands for this channel to the list
							Iterator<StringCommand> strCmdIter = this.commandStrings.commands.iterator();
							while(strCmdIter.hasNext()) {
								StringCommand stringNext = strCmdIter.next();
								// Verify user has access to this command
								if(stringNext.cpl.getLevel() == expectedRank) {
									// System.out.println("[DBG] [CMDS] [CHK] Found usable command for " + sender + " under trigger " + stringNext.getTrigger());
									outMessage += stringNext.getTrigger() + ", ";
								} else if(expectedRank == -100) {
									outMessage += stringNext.getTrigger() + ", ";
								}
							}
							
						} else {
							outMessage += " available to user " + sender + ": ";
							// List commands based on users rank
							int userRank = getSenderRank(sender);
							while(userRank > 0 ) {
								outMessage += additionalCommands[userRank--];
							}
							// Append the rest of the commands for this channel to the list
							Iterator<StringCommand> strCmdIter = this.commandStrings.commands.iterator();
							while(strCmdIter.hasNext()) {
								StringCommand stringNext = strCmdIter.next();
								// Verify user has access to this command
								if(getSenderRank(sender) >= stringNext.cpl.getLevel() &&
										stringNext.isAvailable()) {
									Pattern patternCheck = Pattern.compile(stringNext.getTrigger() + ", ");
									if(patternCheck.matcher(outMessage).find()) {
										
									} else {
										outMessage += stringNext.getTrigger() + ", ";
									}
									
								}
							}
						}

						// Trim off last two characters
						outMessage = outMessage.substring(0, outMessage.length() - 2);
						
						System.out.println("[DBG] [CMDS] [LIST] " + outMessage);
						// Finally we should have compiled a list of commands, send to chat
						if(outMessage.length() >= 400) {
							System.out.println("[DBG] [CMDS] [LIST] Detected >400 outgoing message");
							// outgoing message is too long, split it up into multiple messages
							int offset = 0;
							int endset = offset + 400;
							String[] items = outMessage.split(", ");
							String newMessageOut = "";
							for(int i = 0; i < items.length; i++) {
								System.out.println("[DBG] [CMDS] [LIST] On item " + i + " of " + items.length);
								newMessageOut += items[i] + ", ";
								if(newMessageOut.length() >= 400) {
									System.out.println("[DBG] [CMDS] [LIST] Outputting a new message: " + newMessageOut);
									instance.sendMessage(channel, newMessageOut);
									newMessageOut = "";
								}
							}
							// Finally send the last bit of info to the channel
							instance.sendMessage(channel, newMessageOut);
						} else {
							instance.sendMessage(channel, outMessage);
						}
						
						break;
				}

			}
			// AMA
			else if(getSenderRank(sender) >= amas.getPermissionLevel() &&
						coreCommand.startsWith(amas.getTrigger()) &&
						amas.isAvailable()) {
				amas.executeCommand(channel, sender, login, hostname, message, additionalParams);
			}
			// Counters
			else if(getSenderRank(sender) >= 1 &&
						coreCommand.startsWith("counter")) {
				Iterator<Counter> cntrIter = this.counters.counters.iterator();
				Counter cntr;
				
				switch(args[1]) {
					case "new":
						if(getSenderRank(sender) >= 2) {
							if(args.length >= 3) {
								this.counters.addCounter(args[2], Integer.parseInt(args[3]));
								instance.sendMessage(channel, "Added new counter called " + args[2] + " with value of " + args[3]);
							} else {
								this.counters.addCounter(args[2], 0);
								instance.sendMessage(channel, "Added new counter called " + args[2] + " with value of 0");
							}
						}
						break;
					case "delete":
					case "remove":
						if(getSenderRank(sender) >= 2)
							this.counters.removeCounter(args[2]);
						break;
					case "+":
					case "add":
						while(cntrIter.hasNext()) {
							cntr = cntrIter.next();
							if(cntr.name.equalsIgnoreCase(args[2])) {
								if(args.length >= 3) {
									cntr.addValue(Integer.parseInt(args[3]));
									instance.sendMessage(channel, "Incremented " + args[2] + " by " + args[3] + ". Value is now " + cntr.value);
								} else {
									cntr.addValue(1);
									instance.sendMessage(channel, "Incremented " + args[2] + " by " + args[3] + ". Value is now " + cntr.value);
								}
							}
						}
						break;
					case "-":
					case "sub":
						while(cntrIter.hasNext()) {
							cntr = cntrIter.next();
							if(cntr.name.equalsIgnoreCase(args[2])) {
								cntr.subtractValue(Integer.parseInt(args[3]));
								instance.sendMessage(channel, "Decremented " + args[2] + " by " + args[3] + ". Value is now " + cntr.value);
							}
						}
						break;
					case "*":
					case "mult":
						while(cntrIter.hasNext()) {
							cntr = cntrIter.next();
							if(cntr.name.equalsIgnoreCase(args[2])) {
								cntr.multiplyValue(Integer.parseInt(args[3]));
								instance.sendMessage(channel, "Multiplied " + args[2] + " by " + args[3] + ". Value is now " + cntr.value);
							}
						}
						break;
					case "/":
					case "divide":
						while(cntrIter.hasNext()) {
							cntr = cntrIter.next();
							if(cntr.name.equalsIgnoreCase(args[2])) {
								cntr.divideValue(Integer.parseInt(args[3]));
								instance.sendMessage(channel, "Divided " + args[2] + " by " + args[3] + ". Value is now " + cntr.value);
							}
						}
						break;
					case "=":
					case "get":
						while(cntrIter.hasNext()) {
							cntr = cntrIter.next();
							if(cntr.name.equalsIgnoreCase(args[2])) {
								instance.sendMessage(channel, "Counter " + cntr.name + " is set to " + cntr.value);
							}
						}
				}
				this.counters.saveCounters();
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
				if(getSenderRank(sender) >= stringNext.cpl.getLevel() &&
						coreCommand.equalsIgnoreCase(stringNext.getTrigger()) &&
						stringNext.isAvailable()) {
					// System.out.println("[DBG] [CMDS] [CHK] Found usable command for " + sender + " under trigger " + stringNext.getTrigger());
					stringNext.executeCommand(channel, sender, login, hostname, message, additionalParams);
				}
			}
		}
	}
	
	public void setPermissionPath(Path filePath) {
		this.permissionPath = filePath;
	}
	
	public Path getPermissionPath() {
		return this.permissionPath;
	}
	
	public void setCommandListPath(Path filePath) {
		this.commandListPath = filePath;
	}
	
	public Path getCommandListPath() {
		return this.commandListPath;
	}
	
	public int getSenderRank(String sender) {
		if(this.senderRanks.containsKey(sender.toLowerCase())) {
			return this.senderRanks.get(sender.toLowerCase());
		} else {
			return 0;
		}
	}
	
	public void setSenderRank(String target, int rank) {
		senderRanks.put(target.toLowerCase(), rank);
		this.saveSenderRanks(true);
	}
	
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
	
	public void saveSenderRanks() {
		cfgRanks.saveSettings();
	}
	
	public void saveSenderRanks(boolean sendReferenceMap) {
		cfgRanks.saveSettings(this.senderRanks);
	}
}
