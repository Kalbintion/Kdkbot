package kdkbot.commands;

import java.util.ArrayList;

import kdkbot.Kdkbot;

public abstract class Command {
	private Kdkbot instance;
	private String trigger;
	private CommandPermissionLevel cpl;
	private boolean isAvailable;
	
	public Command() {
		this.trigger = "";
		this.cpl = new CommandPermissionLevel(0);
	}
	
	public void setBotInstance(Kdkbot instance) {
		this.instance = instance;
	}
	
	public Kdkbot getBotInstance() {
		return this.instance;
	}
	
	public void setPermissionLevel(int level) {
		this.cpl.setLevel(level);
	}
	
	public int getPermissionLevel() {
		return this.cpl.getLevel();
	}
	
	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}
	
	public String getTrigger() {
		return this.trigger;
	}
	
	public void setAvailability(boolean availability) {
		this.isAvailable = availability;
	}
	
	public boolean getAvailability() {
		return this.isAvailable;
	}
}
