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
		this.getBotInstance().sendMessage(info.channel, parseMessage(info, this.messageToSend));
	}
	
	public String getMessage() {
		return this.messageToSend;
	}
	
	public String parseMessage(MessageInfo info, String msgToParse) {
		String args[] = info.message.split(" ");
		// Static message replacements
		msgToParse = msgToParse.replace("%USER%", info.sender);
		msgToParse = msgToParse.replace("%CHAN%", info.channel);
		msgToParse = msgToParse.replace("%LOGIN%", info.login);
		msgToParse = msgToParse.replace("%HOSTNAME%", info.hostname);
		if(args[0].length() + 1 < info.message.length()) {
			if(info.message.substring(args[0].length() + 1).startsWith("/")) {
				msgToParse = msgToParse.replace("%ARGS%", info.message.substring(args[0].length() + 2));
			} else {
				msgToParse = msgToParse.replace("%ARGS%", info.message.substring(args[0].length() + 1));
			}
		}
		
		// Arg specificity
		Pattern PATTERN_ARGS_REPLACE = Pattern.compile("%ARGS:\\d*?%");
		Matcher pattern_args_matches = PATTERN_ARGS_REPLACE.matcher(msgToParse);

		while(pattern_args_matches.find()) {
			String result = pattern_args_matches.group();
			// System.out.println("" + result);
			
			String argID = result.substring("%ARGS:".length(), result.length()-1);
			int argIDInt = Integer.parseInt(argID);
			
			// System.out.println("args[" + argID + "] = " + args[argIDInt]);
			if(args[argIDInt].startsWith("/")) {
				// System.out.println("Detected a / for " + argID);
				args[argIDInt] = args[argIDInt].substring(1);
			}
			msgToParse = msgToParse.replace("%ARGS:" + argID + "%", args[argIDInt]);
		}
		
		// Counter specificity
		Pattern PATTERN_CNTR_REPLACE = Pattern.compile("%CNTR:.*?%");
		System.out.println("" + PATTERN_CNTR_REPLACE.toString());
		Matcher pattern_cntr_matches = PATTERN_CNTR_REPLACE.matcher(msgToParse);
		System.out.println("" + msgToParse);
		System.out.println("Post Matcher RegEx: " + pattern_cntr_matches.toString());
		while(pattern_cntr_matches.find()) {
			String result = pattern_cntr_matches.group();
			System.out.println("" + result);
			
			String cntrID = result.substring("%CNTR:".length(), result.length()-1);
			System.out.println("" + cntrID);
			
			Channel chan = this.getBotInstance().getChannel(info.channel);
			Iterator<Counter> cntrIter = chan.commands.counters.counters.iterator();
			Counter cntr = null;
			while(cntrIter.hasNext()) {
				cntr = cntrIter.next();
				if(cntr.name.equalsIgnoreCase(cntrID)) {
					break;
				}
			}
			
			msgToParse = msgToParse.replace("%CNTR:" + cntrID + "%", Integer.toString(cntr.value));
		}		
		
		// Random Number Generator Variables
		Random rnd = new Random();
		
		// Basic replacement
		msgToParse = msgToParse.replace("%RND%", Integer.toString(rnd.nextInt()));
		
		// Advanced replacement (specifying max value)
		Pattern PATTERN_RND_MAX_REPLACE = Pattern.compile("%RND:\\d*?%");
		System.out.println("" + PATTERN_RND_MAX_REPLACE.toString());
		Matcher pattern_rnd_max_matches = PATTERN_RND_MAX_REPLACE.matcher(msgToParse);
		System.out.println("Post Matcher RegEx: " + pattern_rnd_max_matches.toString());
		
		while(pattern_rnd_max_matches.find()) {
			String result = pattern_rnd_max_matches.group();
			System.out.println("" + result);

			String argValues = result.substring("%RND:".length(), result.length()-1);
			
			int maxValue = Integer.parseInt(argValues);

			msgToParse = msgToParse.replace(result, Integer.toString(rnd.nextInt(maxValue)));
		}
		
		// Advanced replacement (specifying min and max values)
		Pattern PATTERN_RND_MIN_MAX_REPLACE = Pattern.compile("%RND:\\d*?,\\d*?%");
		System.out.println("" + PATTERN_RND_MIN_MAX_REPLACE.toString());
		Matcher pattern_rnd_min_max_matches = PATTERN_RND_MIN_MAX_REPLACE.matcher(msgToParse);
		System.out.println("Post Matcher RegEx: " + pattern_rnd_min_max_matches.toString());
				
		while(pattern_rnd_min_max_matches.find()) {
			String result = pattern_rnd_min_max_matches.group();
			System.out.println("" + result);

			String argValues = result.substring("%RND:".length(), result.length()-1);
			String[] argParts = argValues.split(",");
			
			int minValue = Integer.parseInt(argParts[0]);
			int maxValue = Integer.parseInt(argParts[1]);

			msgToParse = msgToParse.replace(result, Integer.toString(rnd.nextInt(maxValue - minValue + 1) + minValue));
		}
		
		return msgToParse;
	}
}
