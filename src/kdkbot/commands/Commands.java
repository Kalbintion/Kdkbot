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
	public Kdkbot instance;
	public Path permissionPath;
	public Path commandListPath;
	public String commandPrefix = "";
	
	public Update channelUpdater = new Update();
	public Quotes quotes = new Quotes();
	public Config cfg;
	
	public HashMap<String, Integer> senderRanks = new HashMap<String, Integer>();
	
	public Commands(Kdkbot instance, String channel) {
		this.instance = instance;
		try {
			cfg = new Config("./cfg/perms/" + channel + ".cfg");
			List<String> cfgContents = cfg.getConfigContents();
			Iterator<String> iter = cfgContents.iterator();
			while(iter.hasNext()) {
				String cfgArgs[] = iter.next().split("=");
				try {
					senderRanks.put(cfgArgs[1], Integer.parseInt(cfgArgs[2]));
				} catch(NumberFormatException e) {
					e.printStackTrace();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void commandHandler(String channel, String sender, String login, String hostname, String message) {
		if(message.startsWith(commandPrefix)) {
			String args[] = message.split(" ");
			String coreCommand = args[0].substring(commandPrefix.length()); // Snag the core command from the message
			
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
		// return this.senderRanks.get(sender);
		return 0;
	}
	
	public void setSenderRank(String target, int rank) {
		senderRanks.put(target, rank);
	}
	
	public void loadSenderRanks() {
		try {
			List<String> strings = cfg.getConfigContents();
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
		cfg.saveSettings();
	}
}
