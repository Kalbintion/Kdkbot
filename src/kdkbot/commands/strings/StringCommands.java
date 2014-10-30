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
					if(args[2] == "*") {
						// Doesn't matter what rank we send with this, as it'll grab all commands.
						commands = this.getListOfCommands(0, GetLevels.INCLUDE_ALL);
					} else {
						commands = this.getListOfCommands(Integer.parseInt(args[2]), GetLevels.INCLUDE_EQUALS);
					}
				} else {
					outMessage.append(" available to " + sender +": ");
					// List commands based on users rank
					int senderRankTemp = senderRank;
					while(senderRankTemp > 0 ) {
						outMessage.append(additionalCommands[senderRankTemp--]);
					}
					commands = this.getListOfCommands(senderRank, GetLevels.INCLUDE_LOWER);
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
	
	public ArrayList<String> getListOfCommands(int senderLevel, GetLevels permLevel) {
		ArrayList<String> listOfCommands = new ArrayList<String>();
		Iterator<StringCommand> strCmds = this.commands.iterator();
		while(strCmds.hasNext()) {
			StringCommand strCmd = strCmds.next();
			if((permLevel == GetLevels.INCLUDE_ALL) ||
					(strCmd.getPermissionLevel() == senderLevel && permLevel == GetLevels.INCLUDE_EQUALS) ||
					(strCmd.getPermissionLevel() <= senderLevel && permLevel == GetLevels.INCLUDE_LOWER) ||
					(strCmd.getPermissionLevel() >= senderLevel && permLevel == GetLevels.INCLUDE_HIGHER)
					) {
				listOfCommands.add(strCmd.getTrigger());
			}
		}
		return listOfCommands;
	}
}
