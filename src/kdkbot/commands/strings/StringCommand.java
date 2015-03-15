package kdkbot.commands.strings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kdkbot.*;
import kdkbot.channel.Channel;
import kdkbot.commands.*;
import kdkbot.commands.counters.Counter;

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
		this.getBotInstance().dbg.writeln(this, "Attempting to execute command " + this.getTrigger() + " to channel " + info.channel);
		this.getBotInstance().sendMessage(info.channel, new MessageParser(this.getBotInstance()).parseMessage(this.messageToSend, info));
	}
	
	public String getMessage() {
		return this.messageToSend;
	}
}
