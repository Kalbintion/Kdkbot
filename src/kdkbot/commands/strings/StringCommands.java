package kdkbot.commands.strings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import kdkbot.Kdkbot;
import kdkbot.MessageInfo;
import kdkbot.filemanager.Config;

public class StringCommands {
	public ArrayList<StringCommand> commands;
	private String channel;
	private Kdkbot instance;
	private Config config;
	
	public enum GetLevels {
		INCLUDE_LOWER,
		INCLUDE_HIGHER,
		INCLUDE_EQUALS,
		INCLUDE_ALL;
	}
	
	public StringCommands(Kdkbot instance, String channel) {
		try {
			this.instance = instance;
			this.channel = channel;
			this.config = new Config("./cfg/" + channel + "/cmds.cfg");
			this.commands = new ArrayList<StringCommand>();
		} catch (Exception e) {
			e.printStackTrace();
		}
		instance.dbg.writeln(this, "Test");
	}
	
	public void loadCommands() {
		try {
			instance.dbg.writeln(this, "Starting load process...");
			List<String> strings = config.getConfigContents();
			instance.dbg.writeln(this, "Loaded contents. Size: " + strings.size());
			Iterator<String> string = strings.iterator();
			while(string.hasNext()) {
				String str = string.next();
				instance.dbg.writeln(this, "Parsing next string: " + str);
				String[] args = str.split("\\|");
				instance.dbg.writeln(this, "Size of args: " + args.length);
				instance.dbg.writeln(this, "args[0]: " + Integer.parseInt(args[0]));
				instance.dbg.writeln(this, "args[1]: " + Boolean.parseBoolean(args[1]));
				for(int i = 0; i < args.length; i++) {
					instance.dbg.writeln(this, "args[" + i + "] is " + args[i]);
				}
				commands.add(new StringCommand(this.instance, args[2], args[3], Integer.parseInt(args[0]), Boolean.parseBoolean(args[1])));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public String addCommand(String trigger, String message, String level) {	
		try {
			commands.add(new StringCommand(this.instance, trigger, message, Integer.parseInt(level), true));
			this.saveCommands();
			return "Added new command " + trigger;
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
		String[] args = info.message.split(" ");
		if(args.length > 1) {
			switch(args[1]) {
				case "new":
					if(info.senderLevel >= 3 ) {
						String[] csArgs = info.message.split(" ", 5);
						instance.dbg.writeln(this, "csArgs size: " + csArgs.length);
						for(int i = 0 ; i < csArgs.length; i++) {
							instance.dbg.writeln(this, "csArgs[" + i + "] is " + csArgs[i]);
						}
						instance.sendMessage(channel, addCommand(csArgs[2], csArgs[4], csArgs[3]));
					}
					break;
				case "view":
					if(info.senderLevel >= 3) {
						String[] csArgs = info.message.split(" ", 4);
						instance.dbg.writeln(this, "csArgs size: " + csArgs.length);
						for(int i = 0 ; i < csArgs.length; i++) {
							instance.dbg.writeln(this, "csArgs[" + i + "] is " + csArgs[i]);
						}
						
						String trigger = csArgs[2];
						String type = csArgs[3];
						
						Iterator<StringCommand> strCmdIter = commands.iterator();
						while(strCmdIter.hasNext()) {
							StringCommand strCmd = strCmdIter.next();
							if(strCmd.getTrigger().equalsIgnoreCase(trigger)) {
								if(type.equalsIgnoreCase("available")) {
									instance.sendMessage(channel, "The command " + strCmd.getTrigger() + "'s availability is set to " + strCmd.getAvailability());
								} else if(type.equalsIgnoreCase("level")) {
									instance.sendMessage(channel, "The command " + strCmd.getTrigger() + "'s permission level is set to " + strCmd.getPermissionLevel());
								} else if(type.equalsIgnoreCase("message")) {
									instance.sendMessage(channel, "The command " + strCmd.getTrigger() + "'s message is set to: " + strCmd.messageToSend);
								}
								break;
							}
						}
					}
					break;
				case "edit":
					if(info.senderLevel >= 3) {
						String[] csArgs = info.message.split(" ", 5);
						instance.dbg.writeln(this, "csArgs size: " + csArgs.length);
						for(int i = 0 ; i < csArgs.length; i++) {
							instance.dbg.writeln(this, "csArgs[" + i + "] is " + csArgs[i]);
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
										instance.sendMessage(channel, info.sender + " has changed " + strCmd.getTrigger() + " to " + toValue);
										strCmd.setTrigger(toValue);
									} else if(type.equalsIgnoreCase("level")) {
										instance.sendMessage(channel, info.sender + " has changed " + strCmd.getTrigger() + "'s level from " + strCmd.getPermissionLevel() + " to " + toValue);
										strCmd.setPermissionLevel(Integer.parseInt(toValue));
									} else if(type.equalsIgnoreCase("message")) {
										instance.sendMessage(channel, info.sender + " has changed " + strCmd.getTrigger() + "'s message.");
										strCmd.messageToSend = toValue;
									} else if(type.equalsIgnoreCase("available")) {
										try {
											boolean bool = Boolean.parseBoolean(csArgs[4]);
											instance.sendMessage(channel, info.sender + " has changed " + strCmd.getTrigger() + "'s availability to " + csArgs[4] + " from " + strCmd.getAvailability());
											strCmd.setAvailability(bool);
										} catch(Exception e) {
											instance.sendMessage(channel, "Unable to discern " + csArgs[4] + " as a true/false value.");
										}
									}
								} else {
									instance.sendMessage(channel, info.sender + ", you do not have the required permission to change this command.");
								}
								break;
							}
						}
						this.saveCommands();
					}
					break;
				case "remove":
					if(info.senderLevel >= 3) {
						instance.sendMessage(channel, removeCommand(args[2]));
						this.saveCommands();
					}
					break;
				case "list":
					// commands list <rank>
					ArrayList<String> commands = new ArrayList<String>();
					
					// Output message standard
					StringBuilder outMessage = new StringBuilder(400);
					outMessage.append("Commands for " + channel + " ");
					
					// Hardcoded commands - need a better situation here
					// TODO: Implement better solution
					String[] additionalCommands = {"", "ama, counter, commands list, quote get, ", "ama, counter, multi, ", "commands, raid, quote, perm, perm, ", "", ""};
									
					// Are we expecting a particular rank to look at? If so, grab those, otherwise, get commands user can use
					if(args.length == 3) {
						// We are expecting a rank to list for
						outMessage.append(" @ rank " + args[2] + ": ");
						if(args[2].equalsIgnoreCase("*")) {
							// Doesn't matter what rank we send with this, as it'll grab all commands.
							commands = this.getListOfCommands(0, GetLevels.INCLUDE_ALL);
						} else {
							commands = this.getListOfCommands(Integer.parseInt(args[2]), GetLevels.INCLUDE_EQUALS);
						}
					} else {
						outMessage.append(" available to " + info.sender +": ");
						// List commands based on users rank
						int senderRankTemp = info.senderLevel;
						while(senderRankTemp > 0 ) {
							if(senderRankTemp >= additionalCommands.length) {
								senderRankTemp--;
							} else {
								outMessage.append(additionalCommands[senderRankTemp--]);
							}
						}
						commands = this.getListOfCommands(info.senderLevel, GetLevels.INCLUDE_LOWER);
					}
		
					// Loop over command list and add it to the outMessage
					Iterator<String> commandIter = commands.iterator();
					while(commandIter.hasNext()) {
						String nextCommand = commandIter.next();
						if(outMessage.length() + nextCommand.length() > 400) {
							// Length too long, send message and reset string
							instance.sendMessage(channel, outMessage.toString());
							outMessage = new StringBuilder(400);
						} else {
							outMessage.append(nextCommand + ", ");
						}
					}
					
					// Trim off last two characters and 
					// Finally send the last bit of info to the channel
					instance.sendMessage(channel, outMessage.substring(0, outMessage.length() - 2));
					
					break;
			}
		}
	}
	
	public ArrayList<String> getListOfCommands(int senderLevel, GetLevels permLevel) {
		HashSet hs = new HashSet();
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
