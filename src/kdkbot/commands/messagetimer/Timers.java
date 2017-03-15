package kdkbot.commands.messagetimer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kdkbot.Kdkbot;
import kdkbot.MessageInfo;
import kdkbot.commands.Command;
import kdkbot.filemanager.Config;

public class Timers extends Command {
	private ArrayList<MessageTimer> timers;
	private Config cfg;
	private String channel;
	
	public Timers(String channel) {
		timers = new ArrayList<MessageTimer>();
		this.channel = channel;
		try {
			this.cfg = new Config("./cfg/" + channel + "/timers.cfg" );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadTimers() {
		try {
			List<String> lines = cfg.getConfigContents();
			Iterator<String> lineItero = lines.iterator();
			while(lineItero.hasNext()) {
				String line = lineItero.next();
				String args[] = line.split("|", 3);
				MessageTimer newTimer = new MessageTimer(channel, args[0], args[2], Long.parseLong(args[1]));
				timers.add(newTimer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveTimers() {
		List<String> timerData = new ArrayList<String>();
		for(MessageTimer t : timers) {
			timerData.add(t.toString());
		}
		try {
			cfg.saveSettings(timerData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void executeCommand(MessageInfo info) {
		String[] args = info.message.split(" ");
		boolean found = false;
		if(args.length > 1) {
			switch(args[1]) {
				case "new":
					args = info.message.split(" ", 5);
					MessageTimer newTimer = new MessageTimer(info.channel, args[2], args[4], Long.parseLong(args[3]));
					timers.add(newTimer);
					Kdkbot.instance.sendMessage(info.channel, "Added new timer with id " + args[2] + " with a delay of " + args[3] + " seconds and a message of " + args[4]);
					break;
				case "remove":
				case "delete":
					String timerID = args[2];
					// Lets find that timer!
					for(MessageTimer t : timers) {
						if(t.timerID.equalsIgnoreCase(timerID)) {
							t.stop();
							timers.remove(t);
							Kdkbot.instance.sendMessage(info.channel, "Deleted timer with id " + timerID);
							found = true;
							break;
						}
					}
					if(!found)
						Kdkbot.instance.sendMessage(info.channel, "Couldn't find timer with id " + timerID);
					break;
				case "edit":
					timerID = args[2];
					// Lets find that timer!
					for(MessageTimer t : timers) {
						if(t.timerID.equalsIgnoreCase(timerID)) {
							// Timer found, need to stop it, create a new timer, and then add a new MessageTimer
							t.stop();
							newTimer = new MessageTimer(info.channel, timerID, args[2], t.delay);
							timers.remove(t);
							timers.add(newTimer);
							Kdkbot.instance.sendMessage(info.channel, "Edited timer with id " + timerID);
						}
					}
					break;
			}
			this.saveTimers();
		}
	}
}
