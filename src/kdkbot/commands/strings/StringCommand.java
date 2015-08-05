package kdkbot.commands.strings;

import kdkbot.*;
import kdkbot.commands.*;

public class StringCommand extends Command {
	// Class Specific
	public String messageToSend;
	
	public StringCommand(String trigger, Kdkbot instance) {
		this.setBotInstance(instance);
		this.setTrigger(trigger);
	}
	
	public StringCommand(Kdkbot instance, String trigger, String message, int level, boolean active) {
		this.setBotInstance(instance);
		this.setTrigger(trigger);
		this.messageToSend = message;
		this.setPermissionLevel(level);
		this.setAvailability(active);
	}
	
	public void executeCommand(MessageInfo info) {
		Kdkbot.instance.dbg.writeln(this, "Attempting to execute command " + this.getTrigger() + " to channel " + info.channel);
		Kdkbot.instance.sendMessage(info.channel, new MessageParser(this.getBotInstance()).parseMessage(this.messageToSend, info));
	}
	
	public String getMessage() {
		return this.messageToSend;
	}
}
