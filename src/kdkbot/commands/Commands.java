package kdkbot.commands;

import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.*;

import kdkbot.Kdkbot;
import kdkbot.commands.*;
import kdkbot.commands.quotes.*;
import kdkbot.commands.channel.*;
import kdkbot.commands.counters.*;
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
	public Update channelUpdater = new Update();
	public Quotes quotes = new Quotes();
	
	public HashMap<String, Integer> senderRanks = new HashMap<String, Integer>();
	
	public Commands(Kdkbot instance, String channel) {
		this.instance = instance;
		try {
			System.out.println("[DBG] [CMDS] [INIT] Attempting to load config ranks.");
			cfgRanks = new Config("./cfg/perms/" + channel + ".cfg");
			List<String> cfgContents = cfgRanks.getConfigContents();
			Iterator<String> iter = cfgContents.iterator();
			while(iter.hasNext()) {
				System.out.println("[DBG] [CMDS] [INIT] Parsing next line of cfgArgs.");
				String cfgArgs[] = iter.next().split("=");
				System.out.println("[DBG] [CMDS] [INIT] Size of cfgArgs is " + cfgArgs.length + " a value of 2 is expected.");
				try {
					senderRanks.put(cfgArgs[0], Integer.parseInt(cfgArgs[1]));
				} catch(NumberFormatException e) {
					e.printStackTrace();
				}
			}
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
			
			// Channel
			if(getSenderRank(sender) >= channelUpdater.getPermissionLevel() &&
					channelUpdater.isAvailable() &&
					channelUpdater.getTrigger().equalsIgnoreCase(coreCommand)) {
				String[] additionalParams = {instance.botCfg.getSetting("oauth")};
				channelUpdater.executeCommand(channel, sender, login, hostname, message, additionalParams);
			}
			// Permission Ranks
			else if (getSenderRank(sender) >= 3 &&
						coreCommand.equalsIgnoreCase("perm")) {
				// System.out.println("DBG: Detected command perm, checking for sub-command.");
				switch(args[1]) {
					case "set":
						this.setSenderRank(args[2], Integer.parseInt(args[3]));
						instance.sendMessage(channel, "Set " + args[2] + " to level " + args[3] + "permission.");
						break;
					case "get":
						instance.sendMessage(channel, "The user " + args[2] + " is set to " + this.getSenderRank(args[2]));
						break;
				}
			}
			// Ban
			else if(getSenderRank(sender) >= 3 &&
						coreCommand.startsWith("ban")) {
				instance.ban(channel, args[1]);
				instance.sendMessage(channel, "Banned user " + args[1]);
			}
			// Devoice
			else if(getSenderRank(sender) >= 3 && (
						coreCommand.startsWith("devoice") ||
						coreCommand.startsWith("dv"))) {
				instance.deVoice(channel, args[1]);
				instance.sendMessage(channel, "Devoiced user " + args[1]);
			}
			// Quotes
			else if (getSenderRank(sender) >= quotes.getPermissionLevel() &&
						quotes.isAvailable() &&
						quotes.getTrigger().equalsIgnoreCase(coreCommand)) {
				quotes.executeCommand(channel, sender, login, hostname, message, new String[0]);
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
		if(this.senderRanks.containsKey(sender)) {
			return this.senderRanks.get(sender);
		} else {
			return 0;
		}
	}
	
	public void setSenderRank(String target, int rank) {
		senderRanks.put(target, rank);
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
}
