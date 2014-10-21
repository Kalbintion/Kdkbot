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
	
	public void addCommand(String trigger, String message, String level) {	
		commands.add(new StringCommand(this.instance, trigger, message, Integer.parseInt(level), true));
		this.saveCommands();
	}
	
	public void saveCommands() {
		try {
			Iterator<StringCommand> strings = commands.iterator();
			List<String> toSave = new ArrayList<String>();
			
			while(strings.hasNext()) {
				StringCommand curStrCmd = strings.next();
				toSave.add(curStrCmd.getTrigger() + "|" + curStrCmd.getPermissionLevel() + "|" + curStrCmd.getMessage());
			}
			
			config.saveSettings(toSave);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void executeCommand(String channel, String sender, String login, String hostname, String message, String[] additionalParams) {

	}
}
