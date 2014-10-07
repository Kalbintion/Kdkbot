package kdkbot.commands.counters;

import kdkbot.Kdkbot;
import kdkbot.commands.Command;

public class Counter implements Command {

	@Override
	public void init(String trigger, Kdkbot instance) {
		
	}

	@Override
	public void executeCommand(String channel, String sender, String login, String hostname, String message, String[] additionalParams) {

	}

	@Override
	public void setTrigger(String trigger) {

	}

	@Override
	public String getTrigger() {
		return null;
	}

	@Override
	public boolean isAvailable() {
		return false;
	}

	@Override
	public void setAvailability(boolean available) {
		
	}

	@Override
	public int getPermissionLevel() {
		return 0;
	}

	@Override
	public void setPermissionLevel(int level) {
		
	}

}
