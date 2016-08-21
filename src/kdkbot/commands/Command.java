package kdkbot.commands;

import kdkbot.Kdkbot;

public abstract class Command {
	private Kdkbot instance;
	private String trigger;
	private CommandPermissionLevel cpl;
	private boolean isAvailable;
	private String helpMessage;
	private int lastCall;
	
	/**
	 * Initializes a new command with nothing defaulted
	 */
	public Command() {
		this("", 0, true, "");
	}
	
	/**
	 * Initializes a new command with a given trigger, permission level, availability, and help message
	 * @param trigger The commands trigger word
	 * @param cpl The CommandPermissionLevels integer rank
	 * @param isAvailable Whether or not this command is enabled
	 * @param helpMessage The help message to go along with this command
	 */
	public Command(String trigger, int cpl, boolean isAvailable, String helpMessage) {
		this(trigger, new CommandPermissionLevel(cpl), isAvailable, helpMessage);
	}
	
	/**
	 * Initializes a new command with a given trigger, permission level, availability, and help message
	 * @param trigger The commands trigger word
	 * @param cpl The CommandPermissionLevels instance
	 * @param isAvailable Whether or not this command is enabled
	 * @param helpMessage The help message to go along with this command
	 */
	public Command(String trigger, CommandPermissionLevel cpl, boolean isAvailable, String helpMessage) {
		this.trigger = trigger;
		this.cpl = cpl;
		this.isAvailable = isAvailable;
		this.helpMessage = helpMessage;
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
	
	/**
	 * Gets this commands last call time, otherwise returns 0
	 * @return The time this command was last called
	 */
	public int getLastCall() {
		return lastCall;
	}
	
	/**
	 * Sets this commands last call time
	 * @param time The time to set the last call to
	 */
	public void setLastCall(int time) {
		lastCall = time;
	}
}
