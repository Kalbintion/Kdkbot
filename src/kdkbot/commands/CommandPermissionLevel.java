package kdkbot.commands;

public class CommandPermissionLevel {
	public int commandLevel;
	
	public CommandPermissionLevel() {
		commandLevel = 0;
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
