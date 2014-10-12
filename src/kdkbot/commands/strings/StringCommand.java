package kdkbot.commands.strings;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kdkbot.*;
import kdkbot.channel.Channel;
import kdkbot.commands.*;
import kdkbot.commands.counters.Counter;

public class StringCommand implements Command {
	// Standard Vars
	public Kdkbot instance;
	public String trigger;
	public boolean isAvailable = true;
	public CommandPermissionLevel cpl = new CommandPermissionLevel(0);
	
	// Class Specific
	public String messageToSend;
	
	public StringCommand(String trigger, Kdkbot instance) {
		this.init(trigger, instance);
	}
	
	public StringCommand(Kdkbot instance, String trigger, String message, int level, boolean active) {
		this.init(trigger, instance);
		this.messageToSend = message;
		this.cpl.setLevel(level);
		this.setAvailability(active);
	}
	
	@Override
	public void init(String trigger, Kdkbot instance) {
		this.setTrigger(trigger);
		this.instance = instance;
	}
	
	@Override
	public void executeCommand(String channel, String sender, String login, String hostname, String message, String[] additionalParams) {
		// System.out.println("[DBG] [STRCMD] [EXEC] Attempting to execute command " + this.getTrigger() + " to channel " + channel);
		instance.sendMessage(channel, parseMessage(this.messageToSend, channel, sender, login, hostname, message, additionalParams));
	}
	
	public String getMessage() {
		return this.messageToSend;
	}
	
	@Override
	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}
	
	@Override
	public String getTrigger() {
		return this.trigger;
	}
	
	@Override
	public boolean isAvailable() {
		return this.isAvailable;
	}
	
	@Override
	public void setAvailability(boolean available) {
		this.isAvailable = available;
	}
	
	@Override
	public int getPermissionLevel() {
		return this.cpl.getLevel();
	}
	
	@Override
	public void setPermissionLevel(int level) {
		this.cpl.setLevel(level);
	}
	
	public String parseMessage(String message, String channel, String sender, String login, String hostname, String sentMessage, String[] additionalParams) {
		String args[] = sentMessage.split(" ");
		// Static message replacements
		message = message.replace("%USER%", sender);
		message = message.replace("%CHAN%", channel);
		message = message.replace("%LOGIN%", login);
		message = message.replace("%HOSTNAME%", hostname);
		if(args[0].length() + 1 < sentMessage.length()) {
			if(sentMessage.substring(args[0].length() + 1).startsWith("/")) {
				message = message.replace("%ARGS%", sentMessage.substring(args[0].length() + 2));
			} else {
				message = message.replace("%ARGS%", sentMessage.substring(args[0].length() + 1));
			}
		}
		
		// Arg specificity
		Pattern PATTERN_ARGS_REPLACE = Pattern.compile("%ARGS:\\d*%");
		// System.out.println("[DBG] [STRCMD] [PARSE] " + PATTERN_ARGS_REPLACE.toString());
		Matcher pattern_args_matches = PATTERN_ARGS_REPLACE.matcher(message);
		// System.out.println("[DBG] [STRCMD] [PARSE] " + message);
		// System.out.println("[DBG] [STRCMD] [PARSE] Post Matcher RegEx: " + pattern_args_matches.toString());
		while(pattern_args_matches.find()) {
			String result = pattern_args_matches.group();
			System.out.println("[DBG] [STRCMD] [PARSE] " + result);
			
			String argID = result.substring("%ARGS:".length(), result.length()-1);
			int argIDInt = Integer.parseInt(argID);
			
			System.out.println("[DBG] [STRCMD] [PARSE] args[" + argID + "] = " + args[argIDInt]);
			if(args[argIDInt].startsWith("/")) {
				System.out.println("[DBG] [STRCMD] [PARSE] Detected a / for " + argID);
				args[argIDInt] = args[argIDInt].substring(1);
			}
			message = message.replace("%ARGS:" + argID + "%", args[argIDInt]);
		}
		
		// Counter specificity
		Pattern PATTERN_CNTR_REPLACE = Pattern.compile("%CNTR:.*?%");
		System.out.println("[DBG] [STRCMD] [PARSE] " + PATTERN_CNTR_REPLACE.toString());
		Matcher pattern_cntr_matches = PATTERN_CNTR_REPLACE.matcher(message);
		System.out.println("[DBG] [STRCMD] [PARSE] " + message);
		System.out.println("[DBG] [STRCMD] [PARSE] Post Matcher RegEx: " + pattern_cntr_matches.toString());
		while(pattern_cntr_matches.find()) {
			String result = pattern_cntr_matches.group();
			System.out.println("[DBG] [STRCMD] [PARSE] " + result);
			
			String cntrID = result.substring("%CNTR:".length(), result.length()-1);
			System.out.println("[DBG] [STRCMD] [PARSE] " + cntrID);
			
			Iterator<Channel> chanIter = instance.CHANS.iterator();
			Channel chan = null;
			while(chanIter.hasNext()) {
				chan = chanIter.next();
				if(chan.getChannel().equalsIgnoreCase(channel)) {
					break;
				}
			}
			
			Iterator<Counter> cntrIter = chan.commands.counters.counters.iterator();
			Counter cntr = null;
			while(cntrIter.hasNext()) {
				cntr = cntrIter.next();
				if(cntr.name.equalsIgnoreCase(cntrID)) {
					break;
				}
			}
			
			message = message.replace("%CNTR:" + cntrID + "%", Integer.toString(cntr.value));
		}
			
		return message;
	}
}
