package kdkbot.commands;

import java.util.ArrayList;

import kdkbot.Kdkbot;

public abstract class Command {
	private Kdkbot instance;
	private String trigger;
	private CommandPermissionLevel cpl;
	private boolean isAvailable;
	private String helpMessage;
	
	/**
	 * Initializes a new command with nothing defaulted
	 */
	public Command() {
		this.trigger = "";
		this.cpl = new CommandPermissionLevel(0);
	}
	
	/**
	 * Initializes a new command with a trigger and commandpermissionlevel object
	 * @param trigger The commands trigger word
	 * @param cpl The permission level object to use for the rank required to use this command
	 */
	public Command(String trigger, CommandPermissionLevel cpl) {
		this.trigger = trigger;
		this.cpl = cpl;
	}
	
	/**
	 * Initializes a new command with a trigger and permission level integer value
	 * @param trigger The commands trigger word
	 * @param cpl The integer permission level to use for the rank required to use this command
	 */
	public Command(String trigger, int cpl) {
		this.trigger = trigger;
		this.cpl = new CommandPermissionLevel(cpl);
	}
	
	/**
	 * Sets the instance of the bot to use in reference with this command
	 * @param instance The bots instance to use
	 */
	public void setBotInstance(Kdkbot instance) {
		this.instance = instance;
	}
	
	/**
	 * Gets the instance of the bot to use in reference with this command
	 * @return The bots instance to use
	 */
	public Kdkbot getBotInstance() {
		return this.instance;
	}
	
	/**
	 * Sets the commands permission level
	 * @param level The level to use for this command
	 */
	public void setPermissionLevel(int level) {
		this.cpl.setLevel(level);
	}
	
	/**
	 * Sets the commands permission level through a CPL object
	 * @param cpl The object to set this commands level to
	 */
	public void setPermissionLevel(CommandPermissionLevel cpl) {
		this.cpl = cpl;
	}
	
	/**
	 * Gets the commands permission level
	 * @return The integer level of the commands permission level
	 */
	public int getPermissionLevel() {
		return this.cpl.getLevel();
	}
	
	/**
	 * Sets this commands trigger word
	 * @param trigger The trigger word to use
	 */
	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}
	
	/**
	 * Gets this commands trigger word
	 * @return The trigger word to use
	 */
	public String getTrigger() {
		return this.trigger;
	}
	
	/**
	 * Sets this commands ability to be executed at all
	 * @param availability True if it can be used, false otherwise
	 */
	public void setAvailability(boolean availability) {
		this.isAvailable = availability;
	}
	
	/**
	 * Gets this commands ability to be executed at all
	 * @return True if available, false otherwise
	 */
	public boolean getAvailability() {
		return this.isAvailable;
	}
	
	/**
	 * Gets this commands help message, if one exists.
	 * @return The message string for using this particular command.
	 */
	public String getHelpMessage() {
		return this.helpMessage;
	}
}
