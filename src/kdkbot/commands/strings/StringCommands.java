package kdkbot.commands.strings;

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
		this.instance = instance;
		this.channel = channel;
	}
	
	public void loadCommands() {
		try {
			List<String> strings = config.getConfigContents();
			Iterator<String> string = strings.iterator();
			while(string.hasNext()) {
				String[] args = string.next().split("|");
				commands.add(new StringCommand(this.instance, args[0], args[1], Integer.parseInt(args[2])));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
