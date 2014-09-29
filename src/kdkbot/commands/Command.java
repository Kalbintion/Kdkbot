package kdkbot.commands;

import kdkbot.Kdkbot;

public interface Command {
	/**
	 * Initializes the command
	 * @param trigger defaults the command trigger.
	 * @param instance the bot instance
	 */
	void init(String trigger, Kdkbot instance);
	
	
    /**
     * Executes a particular commands code
     * 
     * @param args The list of command arguments to use for this.
     */
	void executeCommand(String channel, String sender, String login, String hostname, String message, String[] additionalParams);
	
	/**
	 * Sets the command trigger for this command
	 * 
	 * @param trigger the command string to check for
	 */
	public void setTrigger(String trigger);
	
	/**
	 * Gets the trigger string for this command
	 * 
	 * @return the command trigger.
	 */
	public String getTrigger();

}
