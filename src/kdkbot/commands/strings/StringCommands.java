package kdkbot.commands.strings;

import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import kdkbot.Kdkbot;
import kdkbot.filemanager.Config;

public class StringCommands {
	public ArrayList<StringCommand> commands;
	private String channel;
	private Kdkbot instance;
	private Config config;
	
	public StringCommands(Kdkbot instance, String channel) {
		try {
			this.instance = instance;
			this.channel = channel;
			this.config = new Config("./cfg/stringcommands/" + channel + ".cfg", false);
			this.commands = new ArrayList<StringCommand>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadCommands() {
		try {
			// System.out.println("[DBG] [STRCMD] [LOAD] Starting load process...");
			List<String> strings = config.getConfigContents();
			// System.out.println("[DBG] [STRCMD] [LOAD] Loaded contents. Size: " + strings.size());
			Iterator<String> string = strings.iterator();
			while(string.hasNext()) {
				String str = string.next();
				// System.out.println("[DBG] [STRCMD] [LOAD] Parsing next string: " + str);
				String[] args = str.split("\\|");
				// System.out.println("[DBG] [STRCMD] [LOAD] Size of args: " + args.length);
				// System.out.println("[DBG] [STRCMD] [LOAD] args[0]: " + Integer.parseInt(args[0]));
				// System.out.println("[DBG] [STRCMD] [LOAD] args[1]: " + Boolean.parseBoolean(args[1]));
				for(int i = 0; i < args.length; i++) {
					// System.out.println("[DBG] [STRCMD] [LOAD] args[" + i + "] is " + args[i]);
				}
				commands.add(new StringCommand(this.instance, args[2], args[3], Integer.parseInt(args[0]), Boolean.parseBoolean(args[1])));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public String addCommand(String trigger, String message, String level) {	
		commands.add(new StringCommand(this.instance, trigger, message, Integer.parseInt(level), true));
		this.saveCommands();
		return "Added new command " + trigger;
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
	
	public void executeCommand(String channel, String sender, String login, String hostname, String message, int senderRank, ArrayList<String> additionalParams) {
		String[] args = message.split(" ");
		switch(args[1]) {
			case "new":
				if(senderRank >= 3 ) {
					String[] csArgs = message.split(" ", 5);
					System.out.println("[DBG] [CMD] [STRCMD] csArgs size: " + csArgs.length);
					for(int i = 0 ; i < csArgs.length; i++) {
						System.out.println("[DBG] [CMD] [STRCMD] csArgs[" + i + "] is " + csArgs[i]);
					}
					instance.sendMessage(channel, addCommand(csArgs[2], csArgs[4], csArgs[3]));
				}
				break;
			case "edit":
				if(senderRank >= 3) {
					String[] csArgs = message.split(" ", 5);
					System.out.println("[DBG] [CMD] [STRCMD] csArgs size: " + csArgs.length);
					for(int i = 0 ; i < csArgs.length; i++) {
						System.out.println("[DBG] [CMD] [STRCMD] csArgs[" + i + "] is " + csArgs[i]);
					}
				}
				break;
			case "remove":
				if(senderRank >= 3) {
					instance.sendMessage(channel, removeCommand(args[2]));
					this.saveCommands();
				}
			case "list":
				// commands list <rank>
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
					Iterator<StringCommand> strCmdIter = this.commands.iterator();
					while(strCmdIter.hasNext()) {
						StringCommand stringNext = strCmdIter.next();
						// Verify user has access to this command
						if(stringNext.getPermissionLevel() == expectedRank) {
							// System.out.println("[DBG] [CMDS] [CHK] Found usable command for " + sender + " under trigger " + stringNext.getTrigger());
							outMessage += stringNext.getTrigger() + ", ";
						} else if(expectedRank == -100) {
							outMessage += stringNext.getTrigger() + ", ";
						}
					}
					
				} else {
					outMessage += " available to user " + sender + ": ";
					// List commands based on users rank
					int userRank = senderRank;
					while(userRank > 0 ) {
						outMessage += additionalCommands[userRank--];
					}
					// Append the rest of the commands for this channel to the list
					Iterator<StringCommand> strCmdIter = this.commands.iterator();
					while(strCmdIter.hasNext()) {
						StringCommand stringNext = strCmdIter.next();
						// Verify user has access to this command
						if(senderRank >= stringNext.getPermissionLevel() &&
								stringNext.getAvailability()) {
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
}
