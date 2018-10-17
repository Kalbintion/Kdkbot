package kdk.cmds.custom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import kdk.Bot;
import kdk.MessageInfo;
import kdk.cmds.Commands;
import kdk.dataman.DBFetcher;
import kdk.filemanager.Config;
import kdk.language.Translate;

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
		this.commands = DBFetcher.getChannelCommands("twitch", this.channel.replaceAll("#", ""));
		
		// NOTE: Deprecated
		// If we cannot find any commands in database, we will need to convert from local copy
		// Backwards compatibility
		if(commands == null || commands.size() <= 0) {
			loadCommandsCfg();
			for(StringCommand cmd : commands) {
				boolean res = DBFetcher.addChannelCommand("twitch", channel.replaceAll("#", ""), cmd);
				if(res == false) {
					Bot.inst.dbg.writeln("COMMAND ADD FAILURE! twitch, " + channel.replaceAll("#", "") + " for command " + cmd.getTrigger());
				}
			}
		}
	}
	
	/**
	 * Load the channels custom commands from a local file.
	 * @deprecated Local copies of commands are no longer supported. Database integration.
	 */
	@Deprecated
	public void loadCommandsCfg() {
		try {
			if(this.commands != null && this.commands.size() > 0) {
				// We need to reset the list before reloading
				this.commands.clear();
			}
			
			Bot.inst.dbg.writeln(this, "Starting load process...");
			List<String> strings = config.getConfigContents();
			Bot.inst.dbg.writeln(this, "Loaded contents. Size: " + strings.size());
			Iterator<String> string = strings.iterator();
			while(string.hasNext()) {
				String str = string.next();
				Bot.inst.dbg.writeln(this, "Parsing next string: " + str);
				String[] args = str.split("\\|");
				Bot.inst.dbg.writeln(this, "Size of args: " + args.length);
				Bot.inst.dbg.writeln(this, "args[0]: " + Integer.parseInt(args[0]));
				Bot.inst.dbg.writeln(this, "args[1]: " + Boolean.parseBoolean(args[1]));
				for(int i = 0; i < args.length; i++) {
					Bot.inst.dbg.writeln(this, "args[" + i + "] is " + args[i]);
				}
				commands.add(new StringCommand(args[2], args[3], Integer.parseInt(args[0]), Boolean.parseBoolean(args[1])));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public String addCommand(String trigger, String message, String level) {	
		try {
			StringCommand toAdd = new StringCommand(trigger, message, Integer.parseInt(level), true);
			String outMsg = String.format(Translate.getTranslate("custom.add", Bot.inst.getChannel(this.channel).getLang()), trigger);
			if(getCommand(trigger) != null) {
				outMsg = String.format(Translate.getTranslate("custom.add.dupe", Bot.inst.getChannel(this.channel).getLang()), trigger);
			}
			DBFetcher.addChannelCommand("twitch", this.channel, toAdd);
			commands.add(toAdd);
			return outMsg;
		} catch (NumberFormatException e) {
			return String.format(Translate.getTranslate("custom.add.failed", Bot.inst.getChannel(this.channel).getLang()), trigger);
		}
	}
	
	public String removeCommand(String trigger) {
		Iterator<StringCommand> strIter = commands.iterator();
		StringCommand strCmd;
		while(strIter.hasNext()) {
			strCmd = strIter.next();
			if(strCmd.getTrigger().equalsIgnoreCase(trigger)) {
				strIter.remove();
				DBFetcher.removeCommand(channel.replaceAll("#", ""), trigger);
				return String.format(Translate.getTranslate("custom.del", Bot.inst.getChannel(this.channel).getLang()), trigger);
			}
		}
		return String.format(Translate.getTranslate("custom.del.failed", Bot.inst.getChannel(this.channel).getLang()), trigger);
	}
	
	/**
	 * @deprecated Old system for saving commands to local file. No longer supported. Database migration.
	 */
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
		System.out.println("Detected Sub-cmd: " + subCmd);
		
		switch(subCmd) {
			case "add":
			case "new":
				if(info.senderLevel >= 3 ) {
					String[] csArgs = info.message.split(" ", 5);
					Bot.inst.dbg.writeln(this, "csArgs size: " + csArgs.length);
					for(int i = 0 ; i < csArgs.length; i++) {
						Bot.inst.dbg.writeln(this, "csArgs[" + i + "] is " + csArgs[i]);
					}
					Bot.inst.getChannel(channel).sendMessage(addCommand(csArgs[2], csArgs[4], csArgs[3]));
				}
				break;
			case "view":
				if(info.senderLevel >= 3) {
					String[] csArgs = info.message.split(" ", 4);
					Bot.inst.dbg.writeln(this, "csArgs size: " + csArgs.length);
					for(int i = 0 ; i < csArgs.length; i++) {
						Bot.inst.dbg.writeln(this, "csArgs[" + i + "] is " + csArgs[i]);
					}
					
					String trigger = csArgs[2];
					String type = csArgs[3];
					
					Iterator<StringCommand> strCmdIter = commands.iterator();
					while(strCmdIter.hasNext()) {
						StringCommand strCmd = strCmdIter.next();
						if(strCmd.getTrigger().equalsIgnoreCase(trigger)) {
							if(type.equalsIgnoreCase("available")) {
								Bot.inst.sendChanMessageTrans(channel, "custom.view.available", strCmd.getTrigger(), strCmd.getAvailability());
							} else if(type.equalsIgnoreCase("level")) {
								Bot.inst.sendChanMessageTrans(channel, "custom.view.level", strCmd.getTrigger(), strCmd.getPermissionLevel());
							} else if(type.equalsIgnoreCase("message") || type.equalsIgnoreCase("msg")) {
								Bot.inst.sendChanMessageTrans(channel, "custom.view.msg", strCmd.getTrigger(), strCmd.messageToSend);
							}
							break;
						}
					}
				}
				break;
			case "edit":
				Bot.inst.dbg.writeln("Checking for perm against " + info.senderLevel);
				if(info.senderLevel >= 3) {
					Bot.inst.dbg.writeln("Editing custom command.");
					String[] csArgs = info.message.split(" ", 5);
					Bot.inst.dbg.writeln(this, "csArgs size: " + csArgs.length);
					for(int i = 0 ; i < csArgs.length; i++) {
						Bot.inst.dbg.writeln(this, "csArgs[" + i + "] is " + csArgs[i]);
					}
					
					String trigger = csArgs[2]; // command trigger
					String type = csArgs[3]; // command edit method
					String toValue = csArgs[4];
					
					Bot.inst.dbg.writeln("Cmd to find - Trigger: " + trigger + ", type: " + type + ", toValue: " + toValue);
					
					Iterator<StringCommand> strCmdIter = commands.iterator();
					while(strCmdIter.hasNext()) {
						StringCommand strCmd = strCmdIter.next();
						if(strCmd.getTrigger().equalsIgnoreCase(trigger)) {
							Bot.inst.dbg.writeln(this, "Found command to edit");
							if(type.equalsIgnoreCase("trigger")) {
								Bot.inst.sendChanMessageTrans(channel, "custom.mod.trigger", info.sender, strCmd.getTrigger(), toValue);
								Bot.inst.dbg.writeln(this, "Edited cmd trigger");
								DBFetcher.setCommandValue("twitch", channel, trigger, "trigger", toValue);
								strCmd.setTrigger(toValue);
							} else if(type.equalsIgnoreCase("rank")) {
								Bot.inst.sendChanMessageTrans(channel, "custom.mod.rank", info.sender, strCmd.getTrigger(), strCmd.getPermissionLevel(), toValue);
								Bot.inst.dbg.writeln(this, "Edited cmd level");
								DBFetcher.setCommandValue("twitch", channel, trigger, "level", toValue);
								strCmd.setPermissionLevel(Integer.parseInt(toValue));
							} else if(type.equalsIgnoreCase("message") || type.equalsIgnoreCase("msg")) {
								System.out.println("Editing command message.");
								Bot.inst.sendChanMessageTrans(channel, "custom.mod.msg", info.sender, strCmd.getTrigger());
								Bot.inst.dbg.writeln(this, "Edited cmd message");
								DBFetcher.setCommandValue("twitch", channel, trigger, "message", toValue);
								strCmd.messageToSend = toValue;
							} else if(type.equalsIgnoreCase("available")) {
								try {
									boolean bool = Boolean.parseBoolean(csArgs[4]);
									Bot.inst.sendChanMessageTrans(channel, "custom.mod.available",info.sender, strCmd.getTrigger(), csArgs[4], strCmd.getAvailability());
									Bot.inst.dbg.writeln(this, "Edited cmd availability");
									DBFetcher.setCommandValue("twitch", channel, trigger, "available", toValue);
									strCmd.setAvailability(bool);
								} catch(Exception e) {
									Bot.inst.sendChanMessageTrans(channel, "custom.mod.available.fail", csArgs[4]);
								}
							} else if(type.equalsIgnoreCase("reactive") || type.equalsIgnoreCase("re")) {
								try {
									long time = Long.parseLong(csArgs[4]) * 1000;
									Bot.inst.sendChanMessageTrans(channel, "custom.mod.reactive", info.sender, strCmd.getTrigger(), csArgs[4], strCmd.getReactiveOffset());
									DBFetcher.setCommandValue("twitch", channel, trigger, "reactive_value", String.valueOf(time));
									strCmd.setReactiveOffset(time);
								} catch(Exception e) {
									Bot.inst.sendChanMessageTrans(channel, "custom.mod.reactive.fail", csArgs[4]);
								}
							} else {
								Bot.inst.sendChanMessageTrans(channel, "custom.mod.badedit", csArgs[3]);
							}
							break;
						}
					}
				}
				break;
			case "remove":
				if(info.senderLevel >= 3) {
					Bot.inst.sendChanMessage(channel, removeCommand(info.getSegments()[2]));
				}
				break;
			case "list":
				Bot.inst.sendChanMessageTrans(channel, "custom.list", channel.replace("#", ""));
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
							Bot.inst.sendMessage(info.channel, String.format(Translate.getTranslate("custom.listx.fail", info.getChannel().getLang()), info.sender, info.getSegments()[2]));
							break;
						}
						String commandRankStr = info.getSegments()[3];
						commandRank = Commands.rankNameToInt(commandRankStr);
				}
				
				// How should the rest of the message be formatted to indicate what was requested?
				if(customCommandsOnly) {
					// Showing custom commands
					outMessage.append(Translate.getTranslate("custom.listx.prefix.a.1", info.getChannel().getLang()));
				} else {
					outMessage.append(Translate.getTranslate("custom.listx.prefix.a.2", info.getChannel().getLang()));
				}
				
				// for <user> @ rank?
				if(commandRank == Integer.MAX_VALUE) {
					// We're showing all commands
					outMessage.append(Translate.getTranslate("custom.listx.prefix.b.1", info.getChannel().getLang()));
				} else if(commandRank == Integer.MIN_VALUE) {
					// We're showing commands available to the sender
					outMessage.append(String.format(Translate.getTranslate("custom.listx.prefix.b.2", info.getChannel().getLang()), info.sender));
				} else {
					// We're showing commands @ a particular rank
					outMessage.append(String.format(Translate.getTranslate("custom.listx.prefix.b.3", info.getChannel().getLang()), commandRank));
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
							Bot.inst.getChannel(channel).sendMessage(outMessage.toString());
							outMessage = new StringBuilder(400);
						} else {
							outMessage.append(nextCommand + ", ");
						}
					}
				}
				
				// Trim off last two characters and 
				// Finally send the last bit of info to the channel
				if(outMessage.length() > 2) {
					Bot.inst.getChannel(channel).sendMessage(outMessage.substring(0, outMessage.length() - 2));
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
