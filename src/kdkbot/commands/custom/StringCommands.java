package kdkbot.commands.custom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import kdkbot.Kdkbot;
import kdkbot.MessageInfo;
import kdkbot.commands.Commands;
import kdkbot.filemanager.Config;

public class StringCommands {
	public ArrayList<StringCommand> commands;
	private String channel;
	private Config config;
	private static Config defaults;
	
	public enum GetLevels {
		INCLUDE_LOWER,
		INCLUDE_HIGHER,
		INCLUDE_EQUALS,
		INCLUDE_ALL;
	}
	
	public StringCommands(String channel) {
		try {
			if(StringCommands.defaults == null) {
				StringCommands.defaults = new Config("./cfg/default/cmds.cfg");
			}
			this.channel = channel;
			this.config = new Config("./cfg/" + channel + "/cmds.cfg");
			this.commands = new ArrayList<StringCommand>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadCommands() {
		try {
			Kdkbot.instance.dbg.writeln(this, "Starting load process...");
			List<String> strings = config.getConfigContents();
			Kdkbot.instance.dbg.writeln(this, "Loaded contents. Size: " + strings.size());
			Iterator<String> string = strings.iterator();
			while(string.hasNext()) {
				String str = string.next();
				Kdkbot.instance.dbg.writeln(this, "Parsing next string: " + str);
				String[] args = str.split("\\|");
				Kdkbot.instance.dbg.writeln(this, "Size of args: " + args.length);
				Kdkbot.instance.dbg.writeln(this, "args[0]: " + Integer.parseInt(args[0]));
				Kdkbot.instance.dbg.writeln(this, "args[1]: " + Boolean.parseBoolean(args[1]));
				for(int i = 0; i < args.length; i++) {
					Kdkbot.instance.dbg.writeln(this, "args[" + i + "] is " + args[i]);
				}
				commands.add(new StringCommand(Kdkbot.instance, args[2], args[3], Integer.parseInt(args[0]), Boolean.parseBoolean(args[1])));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public String addCommand(String trigger, String message, String level) {	
		try {
			StringCommand toAdd = new StringCommand(Kdkbot.instance, trigger, message, Integer.parseInt(level), true);
			String outMsg = "Added new command " + trigger;
			if(getCommand(trigger) != null) {
				outMsg = "Added additional command " + trigger + " - All instances of this will be executed!";
			}
			commands.add(toAdd);
			this.saveCommands();
			return outMsg;
		} catch (NumberFormatException e) {
			return "Failed to add command. " + level + " is not an integer. Syntax: commands new <trigger> <rank> <message>";
		}		
	}
	
	public String removeCommand(String trigger) {
		Iterator<StringCommand> strIter = commands.iterator();
		StringCommand strCmd;
		while(strIter.hasNext()) {
			strCmd = strIter.next();
			if(strCmd.getTrigger().equalsIgnoreCase(trigger)) {
				commands.remove(strCmd);
				return "Removed command " + trigger;
			}
		}
		return "Couldn't find the command " + trigger;
	}
	
	public void saveCommands() {
		try {
			Iterator<StringCommand> strings = commands.iterator();
			List<String> toSave = new ArrayList<String>();
			
			while(strings.hasNext()) {
				StringCommand curStrCmd = strings.next();
				toSave.add(curStrCmd.getPermissionLevel() + "|" + curStrCmd.getAvailability() + "|" + curStrCmd.getTrigger() + "|" + curStrCmd.getMessage());
			}
			
			config.saveSettings(toSave);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void executeCommand(MessageInfo info) {
		String[] args = info.getSegments();
		String subCmd = "";
		if(args.length <= 1) { subCmd = "list"; } else { subCmd = args[1]; }
		switch(subCmd) {
			case "add":
			case "new":
				if(info.senderLevel >= 3 ) {
					String[] csArgs = info.message.split(" ", 5);
					Kdkbot.instance.dbg.writeln(this, "csArgs size: " + csArgs.length);
					for(int i = 0 ; i < csArgs.length; i++) {
						Kdkbot.instance.dbg.writeln(this, "csArgs[" + i + "] is " + csArgs[i]);
					}
					Kdkbot.instance.getChannel(channel).sendMessage(addCommand(csArgs[2], csArgs[4], csArgs[3]));
				}
				break;
			case "view":
				if(info.senderLevel >= 3) {
					String[] csArgs = info.message.split(" ", 4);
					Kdkbot.instance.dbg.writeln(this, "csArgs size: " + csArgs.length);
					for(int i = 0 ; i < csArgs.length; i++) {
						Kdkbot.instance.dbg.writeln(this, "csArgs[" + i + "] is " + csArgs[i]);
					}
					
					String trigger = csArgs[2];
					String type = csArgs[3];
					
					Iterator<StringCommand> strCmdIter = commands.iterator();
					while(strCmdIter.hasNext()) {
						StringCommand strCmd = strCmdIter.next();
						if(strCmd.getTrigger().equalsIgnoreCase(trigger)) {
							if(type.equalsIgnoreCase("available")) {
								Kdkbot.instance.getChannel(channel).sendMessage("The command " + strCmd.getTrigger() + "'s availability is set to " + strCmd.getAvailability());
							} else if(type.equalsIgnoreCase("level")) {
								Kdkbot.instance.getChannel(channel).sendMessage("The command " + strCmd.getTrigger() + "'s permission level is set to " + strCmd.getPermissionLevel());
							} else if(type.equalsIgnoreCase("message") || type.equalsIgnoreCase("msg")) {
								Kdkbot.instance.getChannel(channel).sendMessage("The command " + strCmd.getTrigger() + "'s message is set to: " + strCmd.messageToSend);
							}
							break;
						}
					}
				}
				break;
			case "edit":
				if(info.senderLevel >= 3) {
					String[] csArgs = info.message.split(" ", 5);
					Kdkbot.instance.dbg.writeln(this, "csArgs size: " + csArgs.length);
					for(int i = 0 ; i < csArgs.length; i++) {
						Kdkbot.instance.dbg.writeln(this, "csArgs[" + i + "] is " + csArgs[i]);
					}
					
					String trigger = csArgs[2]; // command trigger
					String type = csArgs[3]; // command edit method
					String toValue = csArgs[4];
					
					Iterator<StringCommand> strCmdIter = commands.iterator();
					while(strCmdIter.hasNext()) {
						StringCommand strCmd = strCmdIter.next();
						if(strCmd.getTrigger().equalsIgnoreCase(trigger)) {
							if(info.senderLevel >= strCmd.getPermissionLevel()) {
								if(type.equalsIgnoreCase("trigger")) {
									Kdkbot.instance.getChannel(channel).sendMessage(info.sender + " has changed " + strCmd.getTrigger() + " to " + toValue);
									strCmd.setTrigger(toValue);
								} else if(type.equalsIgnoreCase("rank")) {
									Kdkbot.instance.getChannel(channel).sendMessage(info.sender + " has changed " + strCmd.getTrigger() + "'s level from " + strCmd.getPermissionLevel() + " to " + toValue);
									strCmd.setPermissionLevel(Integer.parseInt(toValue));
								} else if(type.equalsIgnoreCase("message")) {
									Kdkbot.instance.getChannel(channel).sendMessage(info.sender + " has changed " + strCmd.getTrigger() + "'s message.");
									strCmd.messageToSend = toValue;
								} else if(type.equalsIgnoreCase("available")) {
									try {
										boolean bool = Boolean.parseBoolean(csArgs[4]);
										Kdkbot.instance.getChannel(channel).sendMessage(info.sender + " has changed " + strCmd.getTrigger() + "'s availability to " + csArgs[4] + " from " + strCmd.getAvailability());
										strCmd.setAvailability(bool);
									} catch(Exception e) {
										Kdkbot.instance.getChannel(channel).sendMessage("Unable to discern " + csArgs[4] + " as a true/false value.");
									}
								}
							} else {
								Kdkbot.instance.getChannel(channel).sendMessage(info.sender + ", you do not have the required permission to change this command.");
							}
							break;
						}
					}
					this.saveCommands();
				}
				break;
			case "remove":
				if(info.senderLevel >= 3) {
					Kdkbot.instance.getChannel(channel).sendMessage(removeCommand(info.getSegments()[2]));
					this.saveCommands();
				}
				break;
			case "list":
				Kdkbot.instance.getChannel(channel).sendMessage("You can get this channels list of commands by visiting: tfk.zapto.org/kdkbot/?p=channels&t=c&channel=" + channel.replace("#", ""));
				break;
			case "listx":
				// commands list [custom] <rank>
				ArrayList<String> commands = new ArrayList<String>();
				
				// Output message standard
				StringBuilder outMessage = new StringBuilder(400);

				// Hardcoded commands - need a better situation here
				String[] additionalCommands = {"", "ama, counter, commands list, quote get, ", "ama, counter, multi, ", "commands, quote, perm, ", "", ""};
				ArrayList<String> defaultCommands = new ArrayList<String>();
				
				try {
					List<String> defaultCommandsContents = defaults.getConfigContents();
					Iterator<String> defaultCommandsIter = defaultCommandsContents.iterator();
					while(defaultCommandsIter.hasNext()) {
						String defaultCommandsStr = defaultCommandsIter.next();
						String[] defaultCommandsParts = defaultCommandsStr.split("\\|");
						// We're only interested in storing the command name itself @ position 2
						defaultCommands.add(defaultCommandsParts[2]);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				boolean customCommandsOnly = false;
				int commandRank = 0;
				
				switch(info.getSegments().length) {
					case 2:
						// commands list (To give only user permitted commands, shows all commands usable by user by default)
						commandRank = Integer.MIN_VALUE;
						break;
					case 3:
						// commands list [custom] OR commands list <rank>
						if(info.getSegments()[2].equalsIgnoreCase("c") || info.getSegments()[2].equalsIgnoreCase("custom")) {
							// commands list [custom]
							customCommandsOnly = true;
							commandRank = Integer.MIN_VALUE;
						} else {
							// commands list <rank>
							String commandRankStr = info.getSegments()[2];
							commandRank = Commands.rankNameToInt(commandRankStr);
						}
						break;
					case 4:
						// commands list [custom] <rank>
						if(info.getSegments()[2].equalsIgnoreCase("c") || info.getSegments()[2].equalsIgnoreCase("custom")) {
							customCommandsOnly = true;
						} else {
							Kdkbot.instance.sendMessage(info.channel, info.sender + ": You did not specify a valid custom (or 'c') arguments! Given: " + info.getSegments()[2]);
							break;
						}
						String commandRankStr = info.getSegments()[3];
						commandRank = Commands.rankNameToInt(commandRankStr);
				}
				
				// How should the rest of the message be formatted to indicate what was requested?
				if(customCommandsOnly) {
					// Showing custom commands
					outMessage.append("Custom commands ");
				} else {
					outMessage.append("Commands ");
				}
				
				// for <user> @ rank?
				if(commandRank == Integer.MAX_VALUE) {
					// We're showing all commands
					outMessage.append(" at all ranks: ");
				} else if(commandRank == Integer.MIN_VALUE) {
					// We're showing commands available to the sender
					outMessage.append(" available to " + info.sender + ": ");
				} else {
					// We're showing commands @ a particular rank
					outMessage.append(" available @ rank " + commandRank + ": ");
				}
				
				// Do we add the built-in commands to the list or no?
				if(!customCommandsOnly) {
					int senderRankTemp = commandRank;
					while(senderRankTemp > 0) {
						if(senderRankTemp >= additionalCommands.length){
							senderRankTemp--;
						} else {
							outMessage.append(additionalCommands[senderRankTemp--]);
						}
					}
				}
				
				// Now we need to get the rest of the commands
				if(commandRank == Integer.MAX_VALUE) {
					// Grab all the commands
					commands = this.getListOfCommands(0, GetLevels.INCLUDE_ALL);
				} else if(commandRank == Integer.MIN_VALUE) {
					// Get commands for user
					commands = this.getListOfCommands(info.senderLevel, GetLevels.INCLUDE_LOWER);
				} else {
					// We are getting commands for a particular rank
					commands = this.getListOfCommands(commandRank, GetLevels.INCLUDE_EQUALS);
				}
				
				// Sort the list
				Collections.sort(commands);
				
				// Lets iterate through that now.
				Iterator<String> commandIter = commands.iterator();
				while(commandIter.hasNext()) {
					String nextCommand = commandIter.next();
					if(!defaultCommands.contains(nextCommand)) {
						if(outMessage.length() + nextCommand.length() > 400) {
							// Length too long, send message and reset string
							Kdkbot.instance.getChannel(channel).sendMessage(outMessage.toString());
							outMessage = new StringBuilder(400);
						} else {
							outMessage.append(nextCommand + ", ");
						}
					}
				}
				
				// Trim off last two characters and 
				// Finally send the last bit of info to the channel
				if(outMessage.length() > 2) {
					Kdkbot.instance.getChannel(channel).sendMessage(outMessage.substring(0, outMessage.length() - 2));
				} else {
					// We shouldnt have a message to send
				}
				break;
			}
	}
	
	/**
	 * Returns an array list of type string containing a list of commands found in the channel for a given permLevel
	 * @param senderLevel The senders permission level
	 * @param permLevel Which permission levels to compare against, see GetLevels enum
	 * @return An ArrayList<String> containing the commands triggers
	 */
	public ArrayList<String> getListOfCommands(int senderLevel, GetLevels permLevel) {
		HashSet<String> hs = new HashSet<String>();
		Iterator<StringCommand> strCmds = this.commands.iterator();
		while(strCmds.hasNext()) {
			StringCommand strCmd = strCmds.next();
			if((permLevel == GetLevels.INCLUDE_ALL) ||
					(permLevel == GetLevels.INCLUDE_EQUALS && strCmd.getPermissionLevel() == senderLevel) ||
					(permLevel == GetLevels.INCLUDE_LOWER && strCmd.getPermissionLevel() <= senderLevel) ||
					(permLevel == GetLevels.INCLUDE_HIGHER && strCmd.getPermissionLevel() >= senderLevel)
					) {
				hs.add(strCmd.getTrigger());
			}
		}
		
		ArrayList<String> listOfCommands = new ArrayList<String>();
		listOfCommands.addAll(hs);
		return listOfCommands;
	}
	
	/**
	 * Returns an array list of type string containing a list of commands found in the channel excluding the ones found in filter
	 * @param senderLevel The senders permission level
	 * @param permLevel
	 * @param filters
	 * @return An ArrayList<String> containing the commands triggers
	 */
	public ArrayList<String> getListOfCommands(int senderLevel, GetLevels permLevel, ArrayList<String> filter) {
		ArrayList<String> commandList = getListOfCommands(senderLevel, permLevel);
		
		if(filter == null) { return commandList; }
		
		Iterator<String> strCmds = commandList.iterator();
		while(strCmds.hasNext()) {
			String strCmd = strCmds.next();
			Iterator<String> strFilters = filter.iterator();
			while(strFilters.hasNext()) {
				String strFilter = strFilters.next();
				if(strCmd.equalsIgnoreCase(strFilter)) {
					strCmds.remove();
				}
			}
		}
		return commandList;
		
	}
	
	/**
	 * Returns the StringCommand object with a given trigger
	 * @param command The trigger to look for
	 * @return null if the command cannot be found, otherwise the StringCommand instance
	 */
	public StringCommand getCommand(String command) {
		Iterator<StringCommand> iter = this.commands.iterator();
		while(iter.hasNext()) {
			StringCommand strCmd = iter.next();
			if(strCmd.getTrigger().equals(command)) {
				return strCmd;
			}
		}
		
		return null;
	}
}
