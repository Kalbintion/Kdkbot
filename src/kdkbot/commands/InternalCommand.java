package kdkbot.commands;

/*
 * Class wrapper for the Command interface
 */
public class InternalCommand extends Command {
	private String settingName;
	private String settingDefault;
	
	public InternalCommand(String settingName, String settingDefault) {
		super();
		this.settingName = settingName;
		this.settingDefault = settingDefault;
	}
	
	public String getSettingName() {
		return settingName;
	}
	
	public void setSettingName(String settingName) {
		this.settingName = settingName;
	}
	
	public String getSettingDefault() {
		return this.settingDefault;
	}
	
	public void setSettingDefault(String settingDefault) {
		this.settingDefault = settingDefault;
	}
}
