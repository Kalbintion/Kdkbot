package kdk.cmds.custom;

import kdk.*;
import kdk.cmds.*;

public class StringCommand extends Command {
	// Class Specific
	public String messageToSend;
	
	public StringCommand(String trigger, Bot instance) {
		this.setBotInstance(instance);
		this.setTrigger(trigger);
	}
	
	public StringCommand(Bot instance, String trigger, String message, int level, boolean active) {
		this.setBotInstance(instance);
		this.setTrigger(trigger);
		this.messageToSend = message;
		this.setPermissionLevel(level);
		this.setAvailability(active);
	}
	
	public void executeCommand(MessageInfo info) {
		Bot.instance.dbg.writeln(this, "Attempting to execute command " + this.getTrigger() + " to channel " + info.channel);
		Bot.instance.getChannel(info.channel).sendMessage(MessageParser.parseMessage(this.messageToSend, info));
	}
	
	public String getMessage() {
		return this.messageToSend;
	}
}
