package kdkbot.commands;

import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import kdkbot.filemanager.Config;

public class Commands {
	public HashMap<Command, Method> commands;
	public Path permissionPath;
	public Path commandListPath;
	
	public Commands() {

	}

	public void loadCommandsFromFile(String channel) throws Exception {
		Config cfg = new Config(FileSystems.getDefault().getPath("./cfg/channels/" + channel + ".cmd"));
		cfg.loadConfigContents();
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
