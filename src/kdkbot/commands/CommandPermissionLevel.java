package kdkbot.commands;

public class CommandPermissionLevel {
	public int commandLevel;
	
	/**
	 * Initializes this CPL with a level of 0
	 */
	public CommandPermissionLevel() {
		commandLevel = 0;
	}
	
	/**
	 * Initializes this CPL with a particular given level
	 * @param level the integer value, provided as a string, to set this to
	 */
	public CommandPermissionLevel(String level) {
		try {
			this.commandLevel = Integer.parseInt(level);
		} catch(NumberFormatException e) {
			e.printStackTrace();
		} finally {
			this.commandLevel = 0;
		}
	}
	
	/**
	 * Initializes this CPL with a particular given level
	 * @param level the integer value to set this level to
	 */
	public CommandPermissionLevel(int level) {
		this.commandLevel = level;
	}
	
	/**
	 * Determines if this command can be used with the supplied command level
	 * @param commandLevel The value of the permission level to use to be checked against.
	 * @return A boolean indicating if the user can (true) or cannot (false) execute a particular command
	 */
	public boolean canUseCommand(int commandLevel) {
		if(this.commandLevel <= commandLevel) { return true; }
		return false;
	}
	
	public void setLevel(int level) {
		this.commandLevel = level;
	}
	
	public int getLevel() {
		return this.commandLevel;
	}
}
