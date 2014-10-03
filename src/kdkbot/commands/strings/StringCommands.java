package kdkbot.commands.strings;

import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
			this.config = new Config("./cfg/stringcommands/" + channel + ".cfg");
			this.commands = new ArrayList<StringCommand>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadCommands() {
		try {
			List<String> strings = config.getConfigContents();
			Iterator<String> string = strings.iterator();
			while(string.hasNext()) {
				String[] args = string.next().split("|");
				commands.add(new StringCommand(this.instance, args[0], args[1], Integer.parseInt(args[2]), Boolean.parseBoolean(args[3])));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addCommand(String trigger, String message, String level) {	
		commands.add(new StringCommand(this.instance, trigger, message, Integer.parseInt(level), true));
	}
	
	public void saveCommands() {
		try {
			Iterator<StringCommand> strings = commands.iterator();
			List<String> toSave = new ArrayList<String>();
			
			while(strings.hasNext()) {
				StringCommand curStrCmd = strings.next();
				curStrCmd.getTrigger();
				curStrCmd.getMessage();
				curStrCmd.cpl.getLevel();
				toSave.add("");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void executeCommand(String channel, String sender, String login, String hostname, String message, String[] additionalParams) {
		String[] args = message.split(" ", 2);

	}
}
