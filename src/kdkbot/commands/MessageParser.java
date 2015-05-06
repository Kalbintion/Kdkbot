package kdkbot.commands;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jibble.pircbot.User;

import kdkbot.Kdkbot;
import kdkbot.MessageInfo;
import kdkbot.channel.Channel;
import kdkbot.commands.counters.Counter;
import kdkbot.commands.strings.StringCommand;

public class MessageParser {
	public Kdkbot instance;
	public HashMap<String, String> LEET_MAP = new HashMap<String, String>();
	
	public MessageParser(Kdkbot instance) {
		this.instance = instance;
		
		// Initialize LEET_MAP
		LEET_MAP.put("a", "@");
		LEET_MAP.put("b", "|3");
		LEET_MAP.put("c", "(");
		LEET_MAP.put("d", "|)");
		LEET_MAP.put("e", "3");
		LEET_MAP.put("f", "|=");
		LEET_MAP.put("g", "6");
		LEET_MAP.put("h", "|-|");
		LEET_MAP.put("i", "!");
		LEET_MAP.put("j", "_|");
		LEET_MAP.put("k", "|<");
		LEET_MAP.put("l", "1");
		LEET_MAP.put("m", "|\\/|");
		LEET_MAP.put("n", "|\\|");
		LEET_MAP.put("o", "0");
		LEET_MAP.put("p", "|*");
		LEET_MAP.put("q", "0,");
		LEET_MAP.put("r", "|2");
		LEET_MAP.put("s", "$");
		LEET_MAP.put("t", "+");
		LEET_MAP.put("u", "|_|");
		LEET_MAP.put("v", "\\/");
		LEET_MAP.put("w", "|/\\|");
		LEET_MAP.put("x", "><");
		LEET_MAP.put("y", "`/");
		LEET_MAP.put("z", "`/_");
	}
	
	public String parseMessage(String toParse, MessageInfo info) {
		String args[] = info.message.split(" ");
		// Static message replacements
		toParse = toParse.replace("%USER%", info.sender);
		toParse = toParse.replace("%CHAN%", info.channel);
		toParse = toParse.replace("%LOGIN%", info.login);
		toParse = toParse.replace("%HOSTNAME%", info.hostname);
		if(args[0].length() + 1 < info.message.length()) {
			if(info.message.substring(args[0].length() + 1).startsWith("/")) {
				toParse = toParse.replace("%ARGS%", info.message.substring(args[0].length() + 2));
			} else {
				toParse = toParse.replace("%ARGS%", info.message.substring(args[0].length() + 1));
			}
		}
		
		// Command replacement
		Pattern PATTERN_CMD_REPLACE = Pattern.compile("%CMD:.*?%");
		Matcher pattern_cmd_matches = PATTERN_CMD_REPLACE.matcher(toParse);
		
		while(pattern_cmd_matches.find()) {
			String result = pattern_cmd_matches.group();
			
			String argID = result.substring("%CMD:".length(), result.length()-1);
			
			Channel chan = instance.getChannel(info.channel);
			StringCommand cmdID = chan.commands.commandStrings.getCommand(argID);
			if(cmdID != null) {
				toParse = toParse.replace(result, cmdID.messageToSend);
			} else {
				toParse = toParse.replace(result, "No command found for " + argID);
			}
		}
		
		// Arg specificity
		Pattern PATTERN_ARGS_REPLACE = Pattern.compile("%ARGS:\\d*?%");
		Matcher pattern_args_matches = PATTERN_ARGS_REPLACE.matcher(toParse);

		while(pattern_args_matches.find()) {
			String result = pattern_args_matches.group();
			
			String argID = result.substring("%ARGS:".length(), result.length()-1);
			int argIDInt = Integer.parseInt(argID);
			
			if(args[argIDInt].startsWith("/")) {
				args[argIDInt] = args[argIDInt].substring(1);
			}
			toParse = toParse.replace("%ARGS:" + argID + "%", args[argIDInt]);
		}
		
		// Counter specificity
		Pattern PATTERN_CNTR_REPLACE = Pattern.compile("%CNTR:.*?%");
		Matcher pattern_cntr_matches = PATTERN_CNTR_REPLACE.matcher(toParse);
		while(pattern_cntr_matches.find()) {
			String result = pattern_cntr_matches.group();
			
			String cntrID = result.substring("%CNTR:".length(), result.length()-1);
			
			Channel chan = this.instance.getChannel(info.channel);
			Iterator<Counter> cntrIter = chan.commands.counters.counters.iterator();
			Counter cntr = null;
			while(cntrIter.hasNext()) {
				cntr = cntrIter.next();
				if(cntr.name.equalsIgnoreCase(cntrID)) {
					break;
				}
			}
			
			toParse = toParse.replace("%CNTR:" + cntrID + "%", Integer.toString(cntr.value));
		}		
		
		// Random Number Generator Variables
		Random rnd = new Random();
		
		// Basic replacement
		toParse = toParse.replace("%RND%", Integer.toString(rnd.nextInt()));
		
		// Advanced replacement (specifying max value)
		Pattern PATTERN_RND_MAX_REPLACE = Pattern.compile("%RND:\\d*?%");
		Matcher pattern_rnd_max_matches = PATTERN_RND_MAX_REPLACE.matcher(toParse);
		debugPatternMatcher(PATTERN_RND_MAX_REPLACE, pattern_rnd_max_matches);
		
		while(pattern_rnd_max_matches.find()) {
			String result = pattern_rnd_max_matches.group();
			
			String argValues = result.substring("%RND:".length(), result.length()-1);
			
			int maxValue = Integer.parseInt(argValues);

			toParse = toParse.replace(result, Integer.toString(rnd.nextInt(maxValue)));
		}
		
		// Advanced replacement (specifying min and max values)
		Pattern PATTERN_RND_MIN_MAX_REPLACE = Pattern.compile("%RND:\\d*?,\\d*?%");
		Matcher pattern_rnd_min_max_matches = PATTERN_RND_MIN_MAX_REPLACE.matcher(toParse);
		debugPatternMatcher(PATTERN_RND_MIN_MAX_REPLACE, pattern_rnd_min_max_matches);
				
		while(pattern_rnd_min_max_matches.find()) {
			String result = pattern_rnd_min_max_matches.group();

			String argValues = result.substring("%RND:".length(), result.length()-1);
			String[] argParts = argValues.split(",");
			
			int minValue = Integer.parseInt(argParts[0]);
			int maxValue = Integer.parseInt(argParts[1]);

			toParse = toParse.replace(result, Integer.toString(rnd.nextInt(maxValue - minValue + 1) + minValue));
		}
		
		// Channel Users
		Pattern PATTERN_CHANUSER_REPLACE = Pattern.compile("%CHANUSER%");
		Matcher pattern_chanuser_replace_matches = PATTERN_CHANUSER_REPLACE.matcher(toParse);
		while(pattern_chanuser_replace_matches.find()) {
			String result = pattern_chanuser_replace_matches.group();
			
			User[] users = instance.getUsers(info.channel);
			for(int i = 0; i < users.length; i++) {
				System.out.println(users[i].getNick());
			}
			User randomUser = users[rnd.nextInt(users.length)];
			
			toParse = toParse.replace(result, randomUser.getNick());
		}
		
		Pattern PATTERN_CHANUSER_IDX_REPLACE = Pattern.compile("%CHANUSER:\\d*?%");
		Matcher pattern_chanuser_idx_replace_matches = PATTERN_CHANUSER_IDX_REPLACE.matcher(toParse);
		while(pattern_chanuser_idx_replace_matches.find()) {
			String result = pattern_chanuser_idx_replace_matches.group();
			
			User[] users = instance.getUsers(info.channel);
			int idx = Integer.parseInt(result.substring("%CHANUSER:".length(), result.length() -1));
			
			toParse = toParse.replace(result, users[idx].getNick());
		}
		
		
		// Case methods
		// Upper
		Pattern PATTERN_UPPER_REPLACE = Pattern.compile("%UPPER:.*?%");
		Matcher pattern_upper_replace_matches = PATTERN_UPPER_REPLACE.matcher(toParse);
		while(pattern_upper_replace_matches.find()) {
			String result = pattern_upper_replace_matches.group();
			
			toParse = toParse.replace(result, result.substring("%UPPER:".length(), result.length()-1).toUpperCase());
		}
		
		// Lower
		Pattern PATTERN_LOWER_REPLACE = Pattern.compile("%LOWER:.*?%");
		Matcher pattern_lower_replace_matches = PATTERN_LOWER_REPLACE.matcher(toParse);
		while(pattern_lower_replace_matches.find()) {
			String result = pattern_lower_replace_matches.group();
			
			toParse = toParse.replace(result, result.substring("%LOWER:".length(), result.length()-1).toLowerCase());
		}
		
		// Leet
		Pattern PATTERN_LEET_REPLACE = Pattern.compile("%LEET:.*?%");
		Matcher pattern_leet_replace_matches = PATTERN_LEET_REPLACE.matcher(toParse);
		while(pattern_leet_replace_matches.find()) {
			String result = pattern_leet_replace_matches.group();
			String messagePiece = result.substring("%LEET:".length(), result.length()-1);
			messagePiece = charTransform(LEET_MAP, messagePiece);
			toParse = toParse.replace(result, messagePiece);
		}

		return toParse;
	}
	
	public void debugPatternMatcher(Pattern pattern, Matcher matcher) {
		this.instance.dbg.writeln(this, "Pattern: " + pattern.toString());
		this.instance.dbg.writeln(this, "Matcher: " + matcher.toString());
	}
	
	public String charTransform(HashMap<String, String> characterTable, String message) {
		String modifiedMessage = "";
		
		for(int i = 0; i < message.length(); i++) {
			if(characterTable.get(String.valueOf(message.toLowerCase().charAt(i))) == null) {
				modifiedMessage += message.charAt(i);
			} else {
				modifiedMessage += characterTable.get(String.valueOf(message.toLowerCase().charAt(i)));
			}
			
		}
		
		return modifiedMessage;
	}
}
