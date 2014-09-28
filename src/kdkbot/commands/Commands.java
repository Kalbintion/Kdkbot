package kdkbot.commands;

import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.*;
import kdkbot.commands.*;
import kdkbot.commands.quotes.*;
import kdkbot.commands.channel.*;
import kdkbot.commands.counters.*;
import kdkbot.filemanager.Config;

public class Commands {
	public HashMap<Command, Method> commands;
	public Path permissionPath;
	public Path commandListPath;
	
	private static Pattern commandConfig = Pattern.compile("(?<Class>([A-Za-z.]*\\.?)*?)\\.(?<Method>(.*?))\\((?<Params>.*)*?\\)");
	
	public Commands() {

	}

	/**
	 * Loads the commands for this particular commands instance from a given channel
	 * @param channel
	 */
	public void loadCommandsFromFile(String channel) throws Exception {
		// Setup the config for this particular channel
		Config cfg = new Config(FileSystems.getDefault().getPath("./cfg/channels/" + channel + ".cmd"));
		List<String> contents = cfg.getConfigContents();
		
		// Init the command params
		String lineContents;	// Holds the current line
		String[] lineArgs;		// Holds the split up line contents
		String commandTrigger;
		CommandPermissionLevel commandPermissionLevel;
		Method method_target;
		
		// Parse the string list
		Iterator<String> iter = contents.iterator();
		while(iter.hasNext()) {
			lineContents = iter.next();			// Grab the next line in the list
			lineArgs = lineContents.split("|");	// Grab the individual information, per ./help/Command_Config_Setup.txt
			commandTrigger = lineArgs[1];
			commandPermissionLevel = new CommandPermissionLevel(Integer.parseInt(lineArgs[0]));
			Matcher matcher = commandConfig.matcher(lineArgs[2]);
			if(matcher.matches()) {
				String tClass = matcher.group("Class");
				String tMethod = matcher.group("Method");
				String[] tParams = matcher.group("Params").split(",");
			} else {
				throw new IllegalArgumentException();
			}
		}
	}
	
	public void commandHandler(String channel, String sender, String login, String hostname, String message) {
		
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
	
	public int getCommandCount() {
		return commands.size();
	}
	
	/**
	 * Gives some basic commands to this particular command group, in the event none exist.
	 */
	public void initializeBasicCommands() {
		try {
			commands.put(new Update(), Update.class.getDeclaredMethod("executeCommand", String[].class));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
	}
}
