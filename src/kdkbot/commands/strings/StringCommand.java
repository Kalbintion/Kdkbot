package kdkbot.commands.strings;

import kdkbot.*;
import kdkbot.commands.*;

public class StringCommand implements Command {
	// Standard Vars
	public Kdkbot instance;
	public String trigger;
	public boolean isAvailable = true;
	public CommandPermissionLevel cpl = new CommandPermissionLevel(0);
	
	// Class Specific
	public String messageToSend;
	
	public StringCommand(String trigger, Kdkbot instance) {
		this.init(trigger, instance);
	}
	
	public StringCommand(Kdkbot instance, String trigger, String message, int level, boolean active) {
		this.init(trigger, instance);
		this.messageToSend = message;
		this.cpl.setLevel(level);
		this.setAvailability(active);
	}
	
	@Override
	public void init(String trigger, Kdkbot instance) {
		this.setTrigger(trigger);
		this.instance = instance;
	}
	
	@Override
	public void executeCommand(String channel, String sender, String login, String hostname, String message, String[] additionalParams) {
		instance.sendMessage(channel, parseMessage(this.messageToSend));
	}
	
	public String getMessage() {
		return this.messageToSend;
	}
	
	@Override
	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}
	
	@Override
	public String getTrigger() {
		return this.trigger;
	}
	
	@Override
	public boolean isAvailable() {
		return this.isAvailable();
	}
	
	@Override
	public void setAvailability(boolean available) {
		this.isAvailable = available;
	}
	
	@Override
	public int getPermissionLevel() {
		return this.cpl.getLevel();
	}
	
	@Override
	public void setPermissionLevel(int level) {
		this.cpl.setLevel(level);
	}
	
	private String parseMessage(String message) {
		return message;
	}
}
