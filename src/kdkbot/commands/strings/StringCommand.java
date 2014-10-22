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
	
	public void executeCommand(String channel, String sender, String login, String hostname, String message, ArrayList<String> additionalParams) {
		// System.out.println("[DBG] [STRCMD] [EXEC] Attempting to execute command " + this.getTrigger() + " to channel " + channel);
		this.getBotInstance().sendMessage(channel, parseMessage(this.messageToSend, channel, sender, login, hostname, message, additionalParams));
	}
	
	public String getMessage() {
		return this.messageToSend;
	}
	
	public String parseMessage(String message, String channel, String sender, String login, String hostname, String sentMessage, ArrayList<String> additionalParams) {
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
			
			Iterator<Channel> chanIter = this.getBotInstance().CHANS.iterator();
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
		
		// Random Number Generator Variables
		Random rnd = new Random();
		
		// Basic replacement
		message = message.replace("%RND%", Integer.toString(rnd.nextInt()));
		
		// Advanced replacement (specifying max value)
		Pattern PATTERN_RND_MAX_REPLACE = Pattern.compile("%RND:\\d*?%");
		System.out.println("[DBG] [STRCMD] [PARSE] " + PATTERN_RND_MAX_REPLACE.toString());
		Matcher pattern_rnd_max_matches = PATTERN_RND_MAX_REPLACE.matcher(message);
		System.out.println("[DBG] [STRCMD] [PARSE] Post Matcher RegEx: " + pattern_rnd_max_matches.toString());
		
		while(pattern_rnd_max_matches.find()) {
			String result = pattern_rnd_max_matches.group();
			System.out.println("[DBG] [STRCMD] [PARSE] " + result);

			String argValues = result.substring("%RND:".length(), result.length()-1);
			
			int maxValue = Integer.parseInt(argValues);

			message = message.replace(result, Integer.toString(rnd.nextInt(maxValue)));
		}
		
		// Advanced replacement (specifying min and max values)
		Pattern PATTERN_RND_MIN_MAX_REPLACE = Pattern.compile("%RND:\\d*?,\\d*?%");
		System.out.println("[DBG] [STRCMD] [PARSE] " + PATTERN_RND_MIN_MAX_REPLACE.toString());
		Matcher pattern_rnd_min_max_matches = PATTERN_RND_MIN_MAX_REPLACE.matcher(message);
		System.out.println("[DBG] [STRCMD] [PARSE] Post Matcher RegEx: " + pattern_rnd_min_max_matches.toString());
		
		
		while(pattern_rnd_min_max_matches.find()) {
			String result = pattern_rnd_min_max_matches.group();
			System.out.println("[DBG] [STRCMD] [PARSE] " + result);

			String argValues = result.substring("%RND:".length(), result.length()-1);
			String[] argParts = argValues.split(",");
			
			int minValue = Integer.parseInt(argParts[0]);
			int maxValue = Integer.parseInt(argParts[1]);

			message = message.replace(result, Integer.toString(rnd.nextInt(maxValue - minValue + 1) + minValue));
		}
		
		return message;
	}
}
