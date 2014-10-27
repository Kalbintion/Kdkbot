package kdkbot.commands.messagetimer;

import java.util.ArrayList;

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
}
