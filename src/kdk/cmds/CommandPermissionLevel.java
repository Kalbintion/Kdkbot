package kdk.cmds;

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
	 * Sets the permission level
	 * @param level The integer value to use
	 */
	public void setLevel(int level) {
		this.commandLevel = level;
	}
	
	/**
	 * Gets the permission level
	 * @return The permission level
	 */
	public int getLevel() {
		return this.commandLevel;
	}
}
