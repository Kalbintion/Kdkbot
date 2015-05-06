package kdkbot.commands.messagetimer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;

import kdkbot.Kdkbot;
import kdkbot.MessageInfo;
import kdkbot.commands.strings.StringCommand;
import kdkbot.commands.strings.StringCommands.GetLevels;
import kdkbot.filemanager.Config;

public class Timers {
	private ArrayList<MessageTimer> timers;
	private Config config;
	private String channel;
	
	public Timers(String channel) {
		timers = new ArrayList<MessageTimer>();
		this.channel = channel;
		try {
			this.config = new Config("./cfgs/timers/" + channel + ".cfg" );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadTimers() {
		
	}
	
	public void executeCommand(MessageInfo info) {
		String[] args = info.message.split(" ");
		if(args.length > 1) {
			switch(args[1]) {
				case "new":
					MessageTimer newTimer = new MessageTimer(Kdkbot.instance, info.channel, args[1], args[3], new Timer());
					timers.add(newTimer);
					break;
				case "remove":
				case "delete":
					break;
			}
		}
	}
}
