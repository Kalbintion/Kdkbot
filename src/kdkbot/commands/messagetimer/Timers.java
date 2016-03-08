package kdkbot.commands.messagetimer;

import java.util.ArrayList;
import java.util.Timer;

import kdkbot.MessageInfo;
import kdkbot.filemanager.Config;

public class Timers {
	private ArrayList<MessageTimer> timers;
	private Config config;
	private String channel;
	
	public Timers(String channel) {
		timers = new ArrayList<MessageTimer>();
		this.channel = channel;
		try {
			this.config = new Config("./cfg/" + channel + "/timers.cfg" );
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
					MessageTimer newTimer = new MessageTimer(info.channel, args[1], args[3], new Timer());
					timers.add(newTimer);
					break;
				case "remove":
				case "delete":
					break;
			}
		}
	}
}
