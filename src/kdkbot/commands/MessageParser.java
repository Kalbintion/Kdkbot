package kdkbot.commands;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jibble.pircbot.User;

import kdkbot.Kdkbot;
import kdkbot.MessageInfo;
import kdkbot.channel.Channel;
import kdkbot.commands.counters.Counter;
import kdkbot.commands.strings.StringCommand;

/**
 * Class responsible for parsing custom command (StringCommand) messages
 * @author KDK
 *
 */
public class MessageParser {
	private static HashMap<String, String> LEET_MAP = new HashMap<String, String>();
	private static HashMap<String, String> FLIP_MAP = new HashMap<String, String>();
	private static HashMap<String, Pattern> PATTERN_MAP = new HashMap<String, Pattern>();
	
	/**
	 * Necessary information for regex patterns & character mappings
	 */
	static {
		PATTERN_MAP.put("cmd", Pattern.compile("%CMD:.*?%"));
		PATTERN_MAP.put("argN", Pattern.compile("%ARGS:\\d*?%"));
		PATTERN_MAP.put("args", Pattern.compile("%ARGS%"));
		PATTERN_MAP.put("cntr", Pattern.compile("%CNTR:.*?%"));
		PATTERN_MAP.put("cntr++", Pattern.compile("%CNTR\\+\\+:.*?%"));
		PATTERN_MAP.put("cntr--", Pattern.compile("%CNTR--:.*?%"));
		PATTERN_MAP.put("rndMax", Pattern.compile("%RND:\\d*?%"));
		PATTERN_MAP.put("rndMinMax", Pattern.compile("%RND:\\d*?,\\d*?%"));
		PATTERN_MAP.put("chanuser", Pattern.compile("%CHANUSER%"));
		PATTERN_MAP.put("chanuserIdx", Pattern.compile("%CHANUSER:\\d*?%"));
		PATTERN_MAP.put("upper", Pattern.compile("%UPPER:.*?%"));
		PATTERN_MAP.put("lower", Pattern.compile("%LOWER:.*?%"));
		PATTERN_MAP.put("leet", Pattern.compile("%LEET:.*?%"));
		PATTERN_MAP.put("flip", Pattern.compile("%FLIP:.*?%"));
		PATTERN_MAP.put("hash", Pattern.compile("%HASH:.*?%"));
		PATTERN_MAP.put("reverse", Pattern.compile("%REVERSE:.*?%"));
		PATTERN_MAP.put("pick", Pattern.compile("%PICK:.*?%"));
		
		
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

		// Initialize Flip Map
		FLIP_MAP.put("a", "ɐ");
		FLIP_MAP.put("b", "q");
		FLIP_MAP.put("c", "ɔ");
		FLIP_MAP.put("d", "p");
		FLIP_MAP.put("e", "ǝ");
		FLIP_MAP.put("f", "ɟ");
		FLIP_MAP.put("g", "b");
		FLIP_MAP.put("h", "ɥ");
		FLIP_MAP.put("i", "ı");
		FLIP_MAP.put("j", "ظ");
		FLIP_MAP.put("k", "ʞ");
		FLIP_MAP.put("l", "ן");
		FLIP_MAP.put("m", "ɯ");
		FLIP_MAP.put("n", "u");
		FLIP_MAP.put("o", "o");
		FLIP_MAP.put("p", "d");
		FLIP_MAP.put("q", "b");
		FLIP_MAP.put("r", "ɹ");
		FLIP_MAP.put("s", "s");
		FLIP_MAP.put("t", "ʇ");
		FLIP_MAP.put("u", "n");
		FLIP_MAP.put("v", "ʌ");
		FLIP_MAP.put("w", "ʍ");
		FLIP_MAP.put("x", "x");
		FLIP_MAP.put("y", "ʎ");
		FLIP_MAP.put("z", "z");
		FLIP_MAP.put("?", "¿");
		FLIP_MAP.put("!", "¡");
		FLIP_MAP.put("6", "9");
		FLIP_MAP.put("9", "6");
	}
	
	/**
	 * Parses a provided message with supplied information
	 * @param toParse The string to parse through
	 * @param info The message information to substitute some options information
	 * @return Finalized string containing a fully parsed message
	 */
	public static String parseMessage(String toParse, MessageInfo info) {
		String args[] = info.message.split(" ");
		// Static message replacements
		toParse = toParse.replace("%USER%", info.sender);
		toParse = toParse.replace("%CHAN%", info.channel);
		toParse = toParse.replace("%LOGIN%", info.login);
		toParse = toParse.replace("%HOSTNAME%", info.hostname);
		
		// Replace %ARGS% based information
		if(args[0].length() + 1 < info.message.length()) {
			if(info.message.substring(args[0].length() + 1).startsWith("/")) {
				toParse = toParse.replace("%ARGS%", info.message.substring(args[0].length() + 2));
			} else {
				toParse = toParse.replace("%ARGS%", info.message.substring(args[0].length() + 1));
			}
		}
		
		// If it was all replaced, we can default to removing them completely (should remove stray %ARGS%)
		toParse = toParse.replace("%ARGS%", "");
		
		// Command replacement
		Matcher pattern_cmd_matches = PATTERN_MAP.get("cmd").matcher(toParse);
		
		while(pattern_cmd_matches.find()) {
			String result = pattern_cmd_matches.group();
			
			String argID = result.substring("%CMD:".length(), result.length()-1);
			
			Channel chan = Kdkbot.instance.getChannel(info.channel);
			StringCommand cmdID = chan.commands.commandStrings.getCommand(argID);
			if(cmdID != null) {
				toParse = toParse.replace(result, cmdID.messageToSend);
			} else {
				toParse = toParse.replace(result, "No command found for " + argID);
			}
		}
		
		// Arg specificity
		Matcher pattern_args_matches = PATTERN_MAP.get("argN").matcher(toParse);
		
		while(pattern_args_matches.find()) {
			String result = pattern_args_matches.group();
			
			String argID = result.substring("%ARGS:".length(), result.length()-1);
			int argIDInt = Integer.parseInt(argID);
			
			if(args.length > argIDInt) {
				if(args[argIDInt].startsWith("/")) {
					args[argIDInt] = args[argIDInt].substring(1);
				}
				
			
				toParse = toParse.replace("%ARGS:" + argID + "%", args[argIDInt]);
			}
		}
				
		// Counter specificity
		Matcher pattern_cntr_matches = PATTERN_MAP.get("cntr").matcher(toParse);
		
		while(pattern_cntr_matches.find()) {
			String result = pattern_cntr_matches.group();
			
			String cntrID = result.substring("%CNTR:".length(), result.length()-1);
			
			Channel chan = Kdkbot.instance.getChannel(info.channel);
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
		
		// Counter++ Specificity
		Matcher pattern_cntrpp_matches = PATTERN_MAP.get("cntr++").matcher(toParse);
		while(pattern_cntrpp_matches.find()) {
			System.out.println("Hit CNTR++");
			String result = pattern_cntrpp_matches.group();
			
			String cntrID = result.substring("%CNTR++:".length(), result.length()-1);
			
			Channel chan = Kdkbot.instance.getChannel(info.channel);
			Iterator<Counter> cntrIter = chan.commands.counters.counters.iterator();
			Counter cntr = null;
			while(cntrIter.hasNext()) {
				cntr = cntrIter.next();
				if(cntr.name.equalsIgnoreCase(cntrID)) {
					break;
				}
			}
			
			toParse = toParse.replace("%CNTR++:" + cntrID + "%", Integer.toString(cntr.value));
			cntr.value++;
			
			// Force a counter save
			Kdkbot.CHANS.get(info.channel).commands.counters.saveCounters();
		}
		
		// Counter-- Specificity
		Matcher pattern_cntrmm_matches = PATTERN_MAP.get("cntr--").matcher(toParse);
		while(pattern_cntrmm_matches.find()) {
			String result = pattern_cntrmm_matches.group();
			
			String cntrID = result.substring("%CNTR--:".length(), result.length()-1);
			
			Channel chan = Kdkbot.instance.getChannel(info.channel);
			Iterator<Counter> cntrIter = chan.commands.counters.counters.iterator();
			Counter cntr = null;
			while(cntrIter.hasNext()) {
				cntr = cntrIter.next();
				if(cntr.name.equalsIgnoreCase(cntrID)) {
					break;
				}
			}
			
			toParse = toParse.replace("%CNTR--:" + cntrID + "%", Integer.toString(cntr.value));
			cntr.value--;
			
			// Force a counter save
			Kdkbot.CHANS.get(info.channel).commands.counters.saveCounters();
		}
		
		// Random Number Generator Variables
		Random rnd = new Random();
		
		// Basic replacement
		toParse = toParse.replace("%RND%", Integer.toString(rnd.nextInt()));
		
		// Advanced replacement (specifying max value)
		Matcher pattern_rnd_max_matches = PATTERN_MAP.get("rndMax").matcher(toParse);
		
		while(pattern_rnd_max_matches.find()) {
			String result = pattern_rnd_max_matches.group();
			
			String argValues = result.substring("%RND:".length(), result.length()-1);
			
			int maxValue = Integer.parseInt(argValues);

			toParse = toParse.replace(result, Integer.toString(rnd.nextInt(maxValue)));
		}
		
		// Advanced replacement (specifying min and max values)
		Matcher pattern_rnd_min_max_matches = PATTERN_MAP.get("rndMinMax").matcher(toParse);
				
		while(pattern_rnd_min_max_matches.find()) {
			String result = pattern_rnd_min_max_matches.group();

			String argValues = result.substring("%RND:".length(), result.length()-1);
			String[] argParts = argValues.split(",");
			
			int minValue = Integer.parseInt(argParts[0]);
			int maxValue = Integer.parseInt(argParts[1]);

			toParse = toParse.replace(result, Integer.toString(rnd.nextInt(maxValue - minValue + 1) + minValue));
		}
		
		// Channel Users
		Matcher pattern_chanuser_replace_matches = PATTERN_MAP.get("chanuser").matcher(toParse);
		while(pattern_chanuser_replace_matches.find()) {
			String result = pattern_chanuser_replace_matches.group();
			
			User[] users = Kdkbot.instance.getUsers(info.channel);
			User randomUser = users[rnd.nextInt(users.length)];
			
			toParse = toParse.replace(result, randomUser.getNick());
		}
		
		Matcher pattern_chanuser_idx_replace_matches = PATTERN_MAP.get("chanuserIdx").matcher(toParse);
		while(pattern_chanuser_idx_replace_matches.find()) {
			String result = pattern_chanuser_idx_replace_matches.group();
			
			User[] users = Kdkbot.instance.getUsers(info.channel);
			int idx = Integer.parseInt(result.substring("%CHANUSER:".length(), result.length() -1));
			
			toParse = toParse.replace(result, users[idx].getNick());
		}
		
		
		// Case methods
		// Upper
		Matcher pattern_upper_replace_matches = PATTERN_MAP.get("upper").matcher(toParse);
		while(pattern_upper_replace_matches.find()) {
			String result = pattern_upper_replace_matches.group();
			
			toParse = toParse.replace(result, result.substring("%UPPER:".length(), result.length()-1).toUpperCase());
		}
		
		// Lower
		Matcher pattern_lower_replace_matches = PATTERN_MAP.get("lower").matcher(toParse);
		while(pattern_lower_replace_matches.find()) {
			String result = pattern_lower_replace_matches.group();
			
			toParse = toParse.replace(result, result.substring("%LOWER:".length(), result.length()-1).toLowerCase());
		}
		
		// Leet
		Matcher pattern_leet_replace_matches = PATTERN_MAP.get("leet").matcher(toParse);
		while(pattern_leet_replace_matches.find()) {
			String result = pattern_leet_replace_matches.group();
			String messagePiece = result.substring("%LEET:".length(), result.length()-1);
			messagePiece = charTransform(LEET_MAP, messagePiece);
			toParse = toParse.replace(result, messagePiece);
		}
		
		// Flip
		Matcher pattern_flip_replace_matches = PATTERN_MAP.get("flip").matcher(toParse);
		while(pattern_flip_replace_matches.find()) {
			String result = pattern_flip_replace_matches.group();
			String messagePiece = result.substring("%FLIP:".length(), result.length()-1);
			messagePiece = charTransform(FLIP_MAP, messagePiece);
			toParse = toParse.replace(result, messagePiece);
		}
		
		// Hash
		Matcher pattern_hash_replace_matches = PATTERN_MAP.get("hash").matcher(toParse);
		while(pattern_hash_replace_matches.find()) {
			String result = pattern_hash_replace_matches.group();
			String messagePiece = result.substring("%HASH:".length(), result.length()-1);
			String argParts[] = messagePiece.split(",", 2);
			
			try {
				MessageDigest md = MessageDigest.getInstance(argParts[1]);
				toParse = toParse.replace(result, new String(md.digest(argParts[2].getBytes("UTF-8"))));
			} catch (Exception e) {
				toParse = toParse.replace(result, e.getMessage());
			}
		}
		
		// Reverse String (ie: abc, cba)
		Matcher pattern_reverse_replace_matches = PATTERN_MAP.get("reverse").matcher(toParse);
		while(pattern_reverse_replace_matches.find()) {
			String result = pattern_reverse_replace_matches.group();
			String messagePiece = result.substring("%REVERSE:".length(), result.length()-1);
			toParse = toParse.replace(result, new StringBuilder(messagePiece).reverse().toString());
		}
		
		// Pick
		Matcher pattern_pick_replace_matches = PATTERN_MAP.get("pick").matcher(toParse);
		while(pattern_pick_replace_matches.find()) {
			String result = pattern_pick_replace_matches.group();
			String messagePiece = result.substring("%PICK:".length(), result.length()-1);
			
			String messageParts[] = messagePiece.split(",");
			
			toParse = toParse.replace(result, messageParts[new Random().nextInt(messageParts.length)]);
		}
		
		// We've gone through all of the percent-args, we can clear the remaining ones now
		Iterator<Entry<String, Pattern>> patternItr = PATTERN_MAP.entrySet().iterator();
		while (patternItr.hasNext()) {
			Map.Entry<String, Pattern> pair = patternItr.next();
			Pattern obj = (Pattern) pair.getValue();
			toParse = toParse.replaceAll(obj.toString(), "");
		}

		return toParse;
	}
	
	public static void debugPatternMatcher(Pattern pattern, Matcher matcher) {
		Kdkbot.instance.dbg.writeln(MessageParser.class, "Pattern: " + pattern.toString());
		Kdkbot.instance.dbg.writeln(MessageParser.class, "Matcher: " + matcher.toString());
	}
	
	/**
	 * Uses the maps defined in this classes static section to translate a message based on these mappings
	 * @param characterTable The character table to use containig the mappings
	 * @param message Message to transform
	 * @return The finalized transformed message, leaving unfound letters untouched
	 */
	public static String charTransform(HashMap<String, String> characterTable, String message) {
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
