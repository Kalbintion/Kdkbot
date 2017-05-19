package kdkbot.commands;

import kdkbot.MessageInfo;

/*
 * Class wrapper for the Command interface
 */
public class InternalCommand extends Command {
	private String settingSuffix;
	private boolean defaultAvailable = false;
	private int defaultLevel = 5;
	private String defaultTrigger = "";
	
	public InternalCommand(String settingSuffix, String defaultTrigger, int defaultLevel, boolean defaultAvailable) {
		super();
		this.settingSuffix = settingSuffix;
		this.defaultAvailable = defaultAvailable;
		this.defaultLevel = defaultLevel;
		this.defaultTrigger = defaultTrigger;
	}
	
	public InternalCommand(String settingSuffix, int defaultLevel) {
		this(settingSuffix, settingSuffix, defaultLevel, true);
	}
	
	public InternalCommand(String settingSuffix) {
		this(settingSuffix, 5);
	}

	public String getSettingSuffix() {
		return this.settingSuffix;
	}
	
	public void setSettingSuffix(String settingSuffix) {
		this.settingSuffix = settingSuffix;
	}
	
	public boolean getDefaultAvailable() {
		return this.defaultAvailable;
	}
	
	public int getDefaultLevel() {
		return this.defaultLevel;
	}
	
	public String getDefaultTrigger() {
		return this.defaultTrigger;
	}
	
	public int getDefaultTriggerInt() {
		return Integer.parseInt(this.defaultTrigger);
	}
	
	/**
	 * Message handler for command parsing
	 * @param info
	 */
	public void handleMessage(MessageInfo info) {
		return;
	}
}
