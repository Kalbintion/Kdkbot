package kdk.cmds.custom;

import kdk.*;
import kdk.cmds.*;

public class StringCommand extends Command {
	// Class Specific
	public String messageToSend;
	
	public StringCommand(String trigger, Bot instance) {
		this(0, trigger, "", 0, false);
	}
	
	public StringCommand(String trigger, String message, int level, boolean active) {
		this(0, trigger, message, level, active);
	}
	
	public StringCommand(int id, String trigger, String message, int level, boolean active) {
		this.setID(id);
		this.setTrigger(trigger);
		this.messageToSend = message;
		this.setPermissionLevel(level);
		this.setAvailability(active);
	}
	
	public void executeCommand(MessageInfo info) {
		System.out.println("Last Call: " + getLastCall() + "\nOffset: " + getReactiveOffset() + "\n Time: " + System.currentTimeMillis());
		if(this.getLastCall() + this.getReactiveOffset() < System.currentTimeMillis()) {
			Bot.inst.dbg.writeln(this, "Attempting to execute command " + this.getTrigger() + " to channel " + info.channel);
			Bot.inst.getChannel(info.channel).sendMessage(MessageParser.parseMessage(this.messageToSend, info));
			setLastCall(System.currentTimeMillis());
		} else {
			Bot.inst.dbg.writeln("Could not activate command. Not yet re-activated.\n" + info.message);
		}
	}
	
	public String getMessage() {
		return this.messageToSend;
	}
}
