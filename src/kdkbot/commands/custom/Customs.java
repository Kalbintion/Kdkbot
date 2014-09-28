package kdkbot.commands.custom;

import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kdkbot.commands.CommandPermissionLevel;
import kdkbot.commands.channel.Update;
import kdkbot.filemanager.Config;

/**
 * Collection of the Custom command classes that initializes custom commands
 */
public class Customs {
	public ArrayList<Custom> commands;
	public Path permissionPath;
	public Path commandListPath;
	
	public Customs() {

	}

	/**
	 * Loads the commands for this particular commands instance from a given channel
	 * @param channel
	 */
	public void loadCommandsFromFile(String channel) throws Exception {
		// Setup the config for this particular channel
		Config cfg = new Config(FileSystems.getDefault().getPath("./cfg/channels/" + channel + ".ccl"));
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
}
