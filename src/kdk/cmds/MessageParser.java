package kdk.cmds;

import java.nio.file.FileSystems;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jibble.pircbot.User;

import com.google.gson.JsonObject;

import kdk.Bot;
import kdk.MessageInfo;
import kdk.channel.Channel;
import kdk.cmds.counters.Counter;
import kdk.cmds.custom.StringCommand;
import kdk.filemanager.Config;

/**
 * Class responsible for parsing custom command (StringCommand) messages
 * @author KDK
 *
 */
public class MessageParser {
	private static HashMap<String, String> LEET_MAP = new HashMap<String, String>();
	private static HashMap<String, String> FLIP_MAP = new HashMap<String, String>();
	private static HashMap<String, Pattern> PATTERN_MAP = new HashMap<String, Pattern>();
	private static HashMap<String, String> STEAM_MAP = new HashMap<String, String>();
	private static HashMap<String, String> MORSE_MAP = new HashMap<String, String>();
	
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
		PATTERN_MAP.put("rndMinMax", Pattern.compile("%RND:-?\\d*?,-?\\d*?%"));
		PATTERN_MAP.put("chanuser", Pattern.compile("%CHANUSER%"));
		PATTERN_MAP.put("chanuserIdx", Pattern.compile("%CHANUSER:\\d*?%"));
		PATTERN_MAP.put("upper", Pattern.compile("%UPPER:.*?%"));
		PATTERN_MAP.put("lower", Pattern.compile("%LOWER:.*?%"));
		PATTERN_MAP.put("leet", Pattern.compile("%LEET:.*?%"));
		PATTERN_MAP.put("flip", Pattern.compile("%FLIP:.*?%"));
		PATTERN_MAP.put("hash", Pattern.compile("%HASH:.*?%"));
		PATTERN_MAP.put("reverse", Pattern.compile("%REVERSE:.*?%"));
		PATTERN_MAP.put("pick", Pattern.compile("%PICK:.*?%"));
		PATTERN_MAP.put("pagetitle", Pattern.compile("%PAGETITLE:.*?%"));
		PATTERN_MAP.put("yturltoken", Pattern.compile("%YTURL%"));
		PATTERN_MAP.put("urltoken", Pattern.compile("%URL%"));
		PATTERN_MAP.put("join", Pattern.compile("%JOIN:.*?,.*?,.*?%"));
		PATTERN_MAP.put("math", Pattern.compile("%MATH:.*?%"));
		PATTERN_MAP.put("seq", Pattern.compile("%SEQ:(.*?),.*?%"));
		PATTERN_MAP.put("replace", Pattern.compile("%REPLACE:(.*?),(.{1,}),(.*?)%"));
		PATTERN_MAP.put("ytviews_id", Pattern.compile("%YTVIEWS:.*?"));
		
		// Warframe Patterns
		PATTERN_MAP.put("wfm", Pattern.compile("%WFM:.*?%")); // Warframe.Market API
		PATTERN_MAP.put("wfs", Pattern.compile("%WFS:.*?%")); // Warframe Scale API
		PATTERN_MAP.put("wfn", Pattern.compile("%WFN%")); // Warframe News (Events)
		PATTERN_MAP.put("wfnf", Pattern.compile("%WFNF%")); // Warframe News (Events) - First Only
		PATTERN_MAP.put("wfa", Pattern.compile("%WFA%")); // Warframe Alerts
		PATTERN_MAP.put("wfso", Pattern.compile("%WFSO%")); // Warframe Sorties
		PATTERN_MAP.put("wfsy", Pattern.compile("%WFSY%")); // Warframe Syndicate
		PATTERN_MAP.put("wfv", Pattern.compile("%WFV%")); // Warframe Active Missions (Void Fissures)
		PATTERN_MAP.put("wfss", Pattern.compile("%WFSS%")); // Warframe Flash Sales (Market Sales)
		PATTERN_MAP.put("wfi", Pattern.compile("%WFI%")); // Waframe Invasions
		PATTERN_MAP.put("wfbad", Pattern.compile("%WFBAD%")); // Warframe Badlands (Dark Sectors)
		PATTERN_MAP.put("wfb", Pattern.compile("%WFB%")); // Warframe Baro
		PATTERN_MAP.put("wfd", Pattern.compile("%WFD%")); // Warframe Darvo
		PATTERN_MAP.put("wfdr", Pattern.compile("%WFDR%")); // Warframe Darvo - Readable
		
		PATTERN_MAP.put("game", Pattern.compile("%GAME:.*?%"));
		PATTERN_MAP.put("title", Pattern.compile("%TITLE:.*?%"));
		PATTERN_MAP.put("steam", Pattern.compile("%STEAM:.*?%"));
		
		// %YTURL% special condition pattern
		PATTERN_MAP.put("yturl", Pattern.compile("(?:https?\\:(?://|\\\\\\\\))?(?:www\\.)?(?:youtu\\.be(?:\\\\|/)|youtube\\.com(?:\\\\|/)watch\\?v=)(?<VidID>[a-zA-Z0-9-_]*)"));
		PATTERN_MAP.put("url", Pattern.compile("(https?\\:(?://|\\\\\\\\)[A-Za-z0-9\\./-]*)((?:/|\\\\\\\\)?|\\.(html?|php|asp))"));
		
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
		FLIP_MAP.put("a", "�?");
		FLIP_MAP.put("b", "q");
		FLIP_MAP.put("c", "ɔ");
		FLIP_MAP.put("d", "p");
		FLIP_MAP.put("e", "�?");
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
		FLIP_MAP.put("w", "�?");
		FLIP_MAP.put("x", "x");
		FLIP_MAP.put("y", "ʎ");
		FLIP_MAP.put("z", "z");
		FLIP_MAP.put("?", "¿");
		FLIP_MAP.put("!", "¡");
		FLIP_MAP.put("6", "9");
		FLIP_MAP.put("9", "6");
		FLIP_MAP.put("¯", "_");
		FLIP_MAP.put("_", "¯");
		
		MORSE_MAP.put("A", ".-");
		MORSE_MAP.put("B", "-...");
		MORSE_MAP.put("C", "-.-.");
		MORSE_MAP.put("D", "-..");
		MORSE_MAP.put("E", ".");
		MORSE_MAP.put("F", "..-.");
		MORSE_MAP.put("G", "--.");
		MORSE_MAP.put("H", "....");
		MORSE_MAP.put("I", "..");
		MORSE_MAP.put("J", ".---");
		MORSE_MAP.put("K", "-.-");
		MORSE_MAP.put("L", ".-..");
		MORSE_MAP.put("M", "--");
		MORSE_MAP.put("N", "-.");
		MORSE_MAP.put("O", "---");
		MORSE_MAP.put("P", ".--.");
		MORSE_MAP.put("Q", "--.-");
		MORSE_MAP.put("R", ".-.");
		MORSE_MAP.put("S", "...");
		MORSE_MAP.put("T", "-");
		MORSE_MAP.put("U", "..-");
		MORSE_MAP.put("V", "...-");
		MORSE_MAP.put("W", ".--");
		MORSE_MAP.put("X", "-..-");
		MORSE_MAP.put("Y", "-.--");
		MORSE_MAP.put("Z", "--..");
		MORSE_MAP.put("1", ".----");
		MORSE_MAP.put("2", "..---");
		MORSE_MAP.put("3", "...--");
		MORSE_MAP.put("4", "....-");
		MORSE_MAP.put("5", ".....");
		MORSE_MAP.put("6", "-....");
		MORSE_MAP.put("7", "--...");
		MORSE_MAP.put("8", "---..");
		MORSE_MAP.put("9", "----.");
		MORSE_MAP.put("0", "-----");
		
		// STEAM MAP
		// TODO: There has got to a better way to look up game id (appid) from name
		Config steam_game_cfg = new Config(FileSystems.getDefault().getPath("./cfg/steam_map.cfg"));
		try {
			List<String> contents = steam_game_cfg.getConfigContents();
			Iterator<String> iter = contents.iterator();
			while(iter.hasNext()) {
				String line = iter.next();
				// Data format is: APPID=NAME
				String[] parts = line.split("=", 2);
				STEAM_MAP.put(parts[1], parts[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Parses a provided message with supplied information
	 * @param toParse The string to parse through
	 * @param info The message information to substitute some options information
	 * @return Finalized string containing a fully parsed message
	 */
	public static String parseMessage(String toParse, MessageInfo info) {
		String args[] = info.message.split(" ");
		Matcher matches;
		
		// Static message replacements
		toParse = toParse.replace("%USER%", info.sender);
		toParse = toParse.replace("%CHAN%", info.channel);
		toParse = toParse.replace("%LOGIN%", info.login);
		toParse = toParse.replace("%HOSTNAME%", info.hostname);

		// Random Number Generator Variables
		Random rnd = new Random();
		
		// Random Basic replacement
		toParse = toParse.replace("%RND%", Integer.toString(rnd.nextInt()));
		
		Bot.instance.dbg.writeln(MessageParser.class, "toParse = " + toParse);
		
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
		Bot.instance.dbg.writeln(MessageParser.class, "toParse = " + toParse);
		
		// Command replacement
		matches = PATTERN_MAP.get("cmd").matcher(toParse);
		
		while(matches.find()) {
			String result = matches.group();
			
			String argID = result.substring("%CMD:".length(), result.length()-1);
			
			Channel chan = Bot.instance.getChannel(info.channel);
			StringCommand cmdID = chan.commands.commandStrings.getCommand(argID);
			if(cmdID != null) {
				toParse = toParse.replace(result, cmdID.messageToSend);
			} else {
				toParse = toParse.replace(result, "No command found for " + argID);
			}
		}
		Bot.instance.dbg.writeln(MessageParser.class, "toParse = " + toParse);
		
		// Arg specificity
		matches = PATTERN_MAP.get("argN").matcher(toParse);
		
		while(matches.find()) {
			String result = matches.group();
			
			String argID = result.substring("%ARGS:".length(), result.length()-1);
			int argIDInt = Integer.parseInt(argID);
			
			if(args.length > argIDInt) {
				if(args[argIDInt].startsWith("/")) {
					args[argIDInt] = args[argIDInt].substring(1);
				}
				
			
				toParse = toParse.replace("%ARGS:" + argID + "%", args[argIDInt]);
			}
		}
		Bot.instance.dbg.writeln(MessageParser.class, "toParse = " + toParse);
		
		// Counter specificity
		matches = PATTERN_MAP.get("cntr").matcher(toParse);
		
		while(matches.find()) {
			String result = matches.group();
			
			String cntrID = result.substring("%CNTR:".length(), result.length()-1);
			
			Channel chan = Bot.instance.getChannel(info.channel);
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
		Bot.instance.dbg.writeln(MessageParser.class, "toParse = " + toParse);
		
		// Counter++ Specificity
		matches = PATTERN_MAP.get("cntr++").matcher(toParse);
		while(matches.find()) {
			String result = matches.group();
			
			String cntrID = result.substring("%CNTR++:".length(), result.length()-1);
			
			Channel chan = Bot.instance.getChannel(info.channel);
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
			Bot.CHANS.get(info.channel).commands.counters.saveCounters();
		}
		Bot.instance.dbg.writeln(MessageParser.class, "toParse = " + toParse);
		
		// Counter-- Specificity
		matches = PATTERN_MAP.get("cntr--").matcher(toParse);
		while(matches.find()) {
			String result = matches.group();
			
			String cntrID = result.substring("%CNTR--:".length(), result.length()-1);
			
			Channel chan = Bot.instance.getChannel(info.channel);
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
			Bot.CHANS.get(info.channel).commands.counters.saveCounters();
		}
		Bot.instance.dbg.writeln(MessageParser.class, "toParse = " + toParse);
		
		// Channel Users
		matches = PATTERN_MAP.get("chanuser").matcher(toParse);
		while(matches.find()) {
			String result = matches.group();
			
			User[] users = Bot.instance.getUsers(info.channel);
			User randomUser = users[rnd.nextInt(users.length)];
			
			toParse = toParse.replace(result, randomUser.getNick());
		}
		Bot.instance.dbg.writeln(MessageParser.class, "toParse = " + toParse);
		
		// Channel Users IDX
		matches = PATTERN_MAP.get("chanuserIdx").matcher(toParse);
		while(matches.find()) {
			String result = matches.group();
			
			User[] users = Bot.instance.getUsers(info.channel);
			int idx = Integer.parseInt(result.substring("%CHANUSER:".length(), result.length() -1));
			
			toParse = toParse.replace(result, users[idx].getNick());
		}
		Bot.instance.dbg.writeln(MessageParser.class, "toParse = " + toParse);
		
		
		// Case methods
		// Upper
		matches = PATTERN_MAP.get("upper").matcher(toParse);
		while(matches.find()) {
			String result = matches.group();
			
			toParse = toParse.replace(result, result.substring("%UPPER:".length(), result.length()-1).toUpperCase());
		}
		Bot.instance.dbg.writeln(MessageParser.class, "toParse = " + toParse);
		
		// Lower
		matches = PATTERN_MAP.get("lower").matcher(toParse);
		while(matches.find()) {
			String result = matches.group();
			
			toParse = toParse.replace(result, result.substring("%LOWER:".length(), result.length()-1).toLowerCase());
		}
		Bot.instance.dbg.writeln(MessageParser.class, "toParse = " + toParse);
		
		// Sequence (Seq)
		matches = PATTERN_MAP.get("seq").matcher(toParse);
		while(matches.find()) {
			String result = matches.group();
			String seqID = matches.group(1);
			String[] seqParts = result.substring("%SEQ:".length(), result.length() - 1).split(",");
			int seqCur = info.getChannel().getMsgSeq(seqID) + 1;
			if(seqCur >= seqParts.length) { seqCur %= seqParts.length; }
			String out = seqParts[seqCur];
			info.getChannel().setAddMsgSeq(seqID, seqParts.length - 1);
			
			toParse = toParse.replace(result, out);
		}
		
		// Leet
		matches = PATTERN_MAP.get("leet").matcher(toParse);
		while(matches.find()) {
			String result = matches.group();
			String messagePiece = result.substring("%LEET:".length(), result.length()-1);
			messagePiece = charTransform(LEET_MAP, messagePiece);
			toParse = toParse.replace(result, messagePiece);
		}
		Bot.instance.dbg.writeln(MessageParser.class, "toParse = " + toParse);
		
		// Flip
		matches = PATTERN_MAP.get("flip").matcher(toParse);
		while(matches.find()) {
			String result = matches.group();
			String messagePiece = result.substring("%FLIP:".length(), result.length()-1);
			messagePiece = charTransform(FLIP_MAP, messagePiece);
			toParse = toParse.replace(result, messagePiece);
		}
		Bot.instance.dbg.writeln(MessageParser.class, "toParse = " + toParse);
		
		// Hash
		matches = PATTERN_MAP.get("hash").matcher(toParse);
		while(matches.find()) {
			String result = matches.group();
			String messagePiece = result.substring("%HASH:".length(), result.length()-1);
			String argParts[] = messagePiece.split(",", 2);
			
			try {
				MessageDigest md = MessageDigest.getInstance(argParts[1]);
				toParse = toParse.replace(result, new String(md.digest(argParts[2].getBytes("UTF-8"))));
			} catch (Exception e) {
				toParse = toParse.replace(result, e.getMessage());
			}
		}
		Bot.instance.dbg.writeln(MessageParser.class, "toParse = " + toParse);
		
		// Reverse String (ie: abc, cba)
		matches = PATTERN_MAP.get("reverse").matcher(toParse);
		while(matches.find()) {
			String result = matches.group();
			String messagePiece = result.substring("%REVERSE:".length(), result.length()-1);
			toParse = toParse.replace(result, new StringBuilder(messagePiece).reverse().toString());
		}
		Bot.instance.dbg.writeln(MessageParser.class, "toParse = " + toParse);
		
		// Pick
		matches = PATTERN_MAP.get("pick").matcher(toParse);
		while(matches.find()) {
			String result = matches.group();
			String messagePiece = result.substring("%PICK:".length(), result.length()-1);
			
			String messageParts[] = messagePiece.split(",");
			
			toParse = toParse.replace(result, messageParts[new Random().nextInt(messageParts.length)]);
		}
		Bot.instance.dbg.writeln(MessageParser.class, "toParse = " + toParse);
		
		// Replace specificity
		matches = PATTERN_MAP.get("replace").matcher(toParse);
		
		while(matches.find()) {
			String result = matches.group();
			
			System.out.println("result: " + result);
			
			String[] messageParts = {matches.group(1), matches.group(2), matches.group(3)};

			System.out.println("messageParts: " + Arrays.toString(messageParts));
			
			System.out.println("messageParts[2]: " + messageParts[2]);
			System.out.println("messageParts[0]: " + messageParts[0]);
			System.out.println("messageParts[1]: " + messageParts[1]);
			System.out.println("Replace: " + messageParts[2].replace(messageParts[0], messageParts[1]));
			toParse = toParse.replace(result, messageParts[2].replace(messageParts[0], messageParts[1]));
			
			System.out.println("toParse: " + toParse);
		}
		Bot.instance.dbg.writeln(MessageParser.class, "toParse = " + toParse);
				
		// Join
		matches = PATTERN_MAP.get("join").matcher(toParse);
		while(matches.find()) {
			String result = matches.group();
			String messageParts[] = result.substring("%JOIN:".length(), result.length()-1).split(",", 3);
			
			for (int i=0; i< messageParts.length; i++) {
				System.out.println("messageParts[" + i + "] = " + messageParts[i]);
			}
			
			String messageEles[] = messageParts[0].split(messageParts[1]);
			
			StringBuffer sb = new StringBuffer();
			System.out.println("Size of JOIN array: " + messageEles.length);
			for (int i=0; i < messageEles.length; i++) {
				if (i != 0) sb.append(messageParts[2]);
				sb.append(messageEles[i]);
			}
			
			toParse = toParse.replace(result, sb.toString());
		}
		Bot.instance.dbg.writeln(MessageParser.class, "toParse = " + toParse);
								
		// YTURL
		Matcher pattern_yturl_replace_matches = PATTERN_MAP.get("yturl").matcher(info.message);
		Matcher pattern_yturl_token_replace_matches = PATTERN_MAP.get("yturltoken").matcher(toParse);
		while(pattern_yturl_token_replace_matches.find()) {
			String result = pattern_yturl_token_replace_matches.group();
			String urlResult = "";
			
			if(pattern_yturl_replace_matches.find()) {
				urlResult = kdk.api.youtube.API.baseURL + pattern_yturl_replace_matches.group("VidID");
			} else {
				urlResult = "";
			}

			toParse = toParse.replace(result, urlResult);
		}
		Bot.instance.dbg.writeln(MessageParser.class, "toParse = " + toParse);
		
		// URL
		Matcher pattern_url_replace_matches = PATTERN_MAP.get("url").matcher(info.message);
		Matcher pattern_url_token_replace_matches = PATTERN_MAP.get("urltoken").matcher(toParse);
		while(pattern_url_token_replace_matches.find()) {
			String result = pattern_url_token_replace_matches.group();
			String urlResult = "";
			
			System.out.println(result);
			System.out.println(urlResult);
			
			if(pattern_url_replace_matches.find()) {
				urlResult = pattern_url_replace_matches.group();
			} else {
				urlResult = "";
			}
			
			toParse = toParse.replace(result, urlResult);
		}
		Bot.instance.dbg.writeln(MessageParser.class, "toParse = " + toParse);
		
		// Pagetitle
		matches = PATTERN_MAP.get("pagetitle").matcher(toParse);
		while(matches.find()) {
			String result = matches.group();
			// messagePiece will contain the URL to look-up
			String messagePiece = result.substring("%PAGETITLE:".length(), result.length()-1);

			toParse = toParse.replace(result, kdk.webinterface.Page.getWebpageTitle(messagePiece));
		}
		Bot.instance.dbg.writeln(MessageParser.class, "toParse = " + toParse);
		
		// Math
		matches = PATTERN_MAP.get("math").matcher(toParse);
		while(matches.find()) {
			String result = matches.group();
			
			// messagePiece will contain the math equation to evaluate
			String messagePiece = result.substring("%MATH:".length(), result.length()-1);
			try {
				toParse = toParse.replace(result, Double.toString(eval(sanitizeMath(messagePiece))));
			} catch(RuntimeException e) {
				toParse = toParse.replace(result, "Invalid Expression");
			}
			
		}
		Bot.instance.dbg.writeln(MessageParser.class, "toParse = " + toParse);
		
		// Advanced replacement (specifying max value)
		matches = PATTERN_MAP.get("rndMax").matcher(toParse);
		
		while(matches.find()) {
			String result = matches.group();
			
			String argValues = result.substring("%RND:".length(), result.length()-1);
			
			int maxValue = Integer.parseInt(argValues);

			toParse = toParse.replace(result, Integer.toString(rnd.nextInt(maxValue)));
		}
		Bot.instance.dbg.writeln(MessageParser.class, "toParse = " + toParse);
		
		// Advanced replacement (specifying min and max values)
		matches = PATTERN_MAP.get("rndMinMax").matcher(toParse);
		while(matches.find()) {
			String result = matches.group();

			String argValues = result.substring("%RND:".length(), result.length()-1);
			String[] argParts = argValues.split(",");
			
			int minValue = Integer.parseInt(argParts[0]);
			int maxValue = Integer.parseInt(argParts[1]);

			toParse = toParse.replace(result, Integer.toString(rnd.nextInt(maxValue - minValue + 1) + minValue));
		}
		Bot.instance.dbg.writeln(MessageParser.class, "toParse = " + toParse);
		
		// Warframe Market API Implementation
		matches = PATTERN_MAP.get("wfm").matcher(toParse);
		
		while(matches.find()) {
			String result = matches.group();
			
			String argValue = result.substring("%WFM:".length(), result.length()-1);
			
			JsonObject wfmRes = kdk.api.warframe.API.Market.getSellStatsDynamic(argValue);
			if(wfmRes == null) {
				toParse = toParse.replace(result, "Couldn't find listings for '" + argValue + "'");
			} else {
				System.out.println(wfmRes.toString());
				// Pretty-print the information
				String out = wfmRes.get("name").toString().replaceAll("\"", "") + " [" + wfmRes.get("status").toString().replaceAll("\"", "") + "] - Low: " + wfmRes.get("min") + ", High: " + wfmRes.get("max") + ", Med: " + wfmRes.get("median") + ", Avg: " + wfmRes.get("avg") + ", Amount: " + wfmRes.get("cnt") + ", # of Sellers: " + wfmRes.get("ppl");
				toParse = toParse.replace(result, out);
			}
			
		}
		
		// Warframe Scaling API Implementation
		matches = PATTERN_MAP.get("wfs").matcher(toParse);
		
		while(matches.find()) {
			String result = matches.group();
			
			String[] argsVals = result.substring("%WFS:".length(), result.length()-1).split(",");
					
			if(argsVals.length < 4) {
				toParse = toParse.replace(result, ""); // Not enough Args
			} else {
				String scaleType = argsVals[0];
				int baseStat = Integer.parseInt(argsVals[1]);
				int baseLevel = Integer.parseInt(argsVals[2]);
				int curLevel = Integer.parseInt(argsVals[3]);
				
				System.out.println("scaleType: " + scaleType + ", baseStat: " + baseStat + ", baseLevel: " + baseLevel + ", curLevel: " + curLevel);
				
				String mathResult = "DID NOT COMPUTE";
				switch(scaleType) {
					case "health":
					case "hp":
						mathResult = kdk.api.warframe.API.Scaling.scaleHealth(baseStat, baseLevel, curLevel);
						break;
					case "armor":
						mathResult = kdk.api.warframe.API.Scaling.scaleArmor(baseStat, baseLevel, curLevel);
						break;
					case "shield":
					case "shields":
						mathResult = kdk.api.warframe.API.Scaling.scaleShield(baseStat, baseLevel, curLevel);
						break;
					case "damage":
					case "dmg":
						mathResult = kdk.api.warframe.API.Scaling.scaleDamage(baseStat, baseLevel, curLevel);
						break;
					case "affinity":
					case "xp":
						mathResult = kdk.api.warframe.API.Scaling.scaleAffinity(baseStat, baseLevel, curLevel);
						break;
				}
				
				System.out.println(mathResult);
				
				toParse = toParse.replace(result, mathResult);
			}
		}
		
		// Warframe News (Events)
		matches = PATTERN_MAP.get("wfn").matcher(toParse);
		
		while(matches.find()) {
			String result = matches.group();
			
			// TODO: Implement Warframe News (Events) replacement
			toParse = toParse.replace(result, kdk.api.warframe.API.Warframe.getAllEventsReadable());
		}
				
		// Warframe News (Events) - First Only
		matches = PATTERN_MAP.get("wfnf").matcher(toParse);
		
		while(matches.find()) {
			String result = matches.group();
			
			// TODO: Implement Warframe News (Events) - First Only replacement
			toParse = toParse.replace(result, kdk.api.warframe.API.Warframe.getFirstEvent());
		}
				
		// Warframe Alerts
		matches = PATTERN_MAP.get("wfa").matcher(toParse);
		
		while(matches.find()) {
			String result = matches.group();
			
			// TODO: Implement Warframe Alerts replacement
			toParse = toParse.replace(result, kdk.api.warframe.API.Warframe.getAllAlertsReadable());
		}
		
		// Warframe Sorties
		matches = PATTERN_MAP.get("wfso").matcher(toParse);
		
		while(matches.find()) {
			String result = matches.group();
			
			// TODO: Implement Warframe Sorties replacement
			toParse = toParse.replace(result, kdk.api.warframe.API.Warframe.getAllSortiesReadable());
		}
		
		// Warframe Syndicate
		matches = PATTERN_MAP.get("wfsy").matcher(toParse);
		
		while(matches.find()) {
			String result = matches.group();
			
			// TODO: Implement Warframe Syndicate replacement
			toParse = toParse.replace(result, "");
		}
		
		// Warframe Active Missions (Void Fissures)
		matches = PATTERN_MAP.get("wfv").matcher(toParse);
		
		while(matches.find()) {
			String result = matches.group();
			
			// TODO: Implement Warframe Active Missions (Void Fissures) replacement
			toParse = toParse.replace(result, kdk.api.warframe.API.Warframe.getAllFissuresReadable());
		}
				
		// Warframe Flash Sales (Market Sales)
		matches = PATTERN_MAP.get("wfss").matcher(toParse);
		
		while(matches.find()) {
			String result = matches.group();
			
			// TODO: Implement Warframe Flash Sales (Market Sales) replacement
			toParse = toParse.replace(result, "");
		}
		
		// Warframe Invasions
		matches = PATTERN_MAP.get("wfi").matcher(toParse);
		
		while(matches.find()) {
			String result = matches.group();
			
			// TODO: Implement Warframe Invasions replacement
			toParse = toParse.replace(result, "");
		}
		
		// Warframe Badlands (Dark Sectors)
		matches = PATTERN_MAP.get("wfbad").matcher(toParse);
		
		while(matches.find()) {
			String result = matches.group();
			
			// TODO: Implement Warframe Badlands replacement
			toParse = toParse.replace(result, "");
		}
		
		// Warframe Baro
		matches = PATTERN_MAP.get("wfb").matcher(toParse);
		
		while(matches.find()) {
			String result = matches.group();
			
			//toParse = toParse.replace(result, kdkbot.api.warframe.API.Warframe.getBaroItemsReadable());
		}
		
		// Warframe Darvo
		matches = PATTERN_MAP.get("wfd").matcher(toParse);
		
		while(matches.find()) {
			String result = matches.group();
			
			toParse = toParse.replace(result, kdk.api.warframe.API.Warframe.getDailyDeal());
		}
		
		
		// Warframe Darvo - Readable
		matches = PATTERN_MAP.get("wfdr").matcher(toParse);
		
		while(matches.find()) {
			String result = matches.group();
			
			toParse = toParse.replace(result, kdk.api.warframe.API.Warframe.getDailyDealReadable());
		}
		
		
		// GAME - Gets Game of a given username
		matches = PATTERN_MAP.get("game").matcher(toParse);
		
		while(matches.find()) {
			String result = matches.group();
			
			String user = result.substring("%GAME:".length(), result.length() -1);
			
			String game = kdk.api.twitch.APIv5.getChannelGameId(Bot.instance.getClientID(), kdk.api.twitch.APIv5.getUserID(Bot.instance.getClientID(), user));
			
			toParse = toParse.replace(result, game);
		}
		
		// TITLE - Gets title of a given username
		Matcher pattern_title_matches = PATTERN_MAP.get("title").matcher(toParse);
		
		while(pattern_title_matches.find()) {
			String result = pattern_title_matches.group();

			String user = result.substring("%TITLE:".length(), result.length() -1);
			
			String status = kdk.api.twitch.APIv5.getChannelStatusId(Bot.instance.getClientID(), kdk.api.twitch.APIv5.getUserID(Bot.instance.getClientID(), user));
			
			toParse = toParse.replace(result, status);
			
		}
		
		// We've gone through all of the percent-args, we can clear the remaining ones now
		Iterator<Entry<String, Pattern>> patternItr = PATTERN_MAP.entrySet().iterator();
		while (patternItr.hasNext()) {
			Map.Entry<String, Pattern> pair = patternItr.next();
			if(pair.getKey() != "url" && pair.getKey() != "yturl") {
				Pattern obj = (Pattern) pair.getValue();
				toParse = toParse.replaceAll(obj.toString(), "");
				
			}
		}
		Bot.instance.dbg.writeln(MessageParser.class, "toParse = " + toParse);

		return toParse;
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
	
	private static String sanitizeMath(String message) {
		return message.replaceAll("[^0-9.*+\\-%/()^]", "");
	}
	
	/**
	 * Evaluates a string as a mathematical expression and returns it as a double. Supports basic math as well as 
	 * 
	 * Original found on http://stackoverflow.com/a/26227947
	 * @param str The expression to evaluate
	 * @return The result, as a double, of the result from the given expression
	 */
	public static double eval(final String str) {
	    return new Object() {
	        int pos = -1, ch;

	        void nextChar() {
	            ch = (++pos < str.length()) ? str.charAt(pos) : -1;
	        }

	        boolean eat(int charToEat) {
	            while (ch == ' ') nextChar();
	            if (ch == charToEat) {
	                nextChar();
	                return true;
	            }
	            return false;
	        }

	        double parse() {
	            nextChar();
	            double x = parseExpression();
	            if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
	            return x;
	        }

	        // Grammar:
	        // expression = term | expression `+` term | expression `-` term
	        // term = factor | term `*` factor | term `/` factor
	        // factor = `+` factor | `-` factor | `(` expression `)`
	        //        | number | functionName factor | factor `^` factor

	        double parseExpression() {
	            double x = parseTerm();
	            for (;;) {
	                if      (eat('+')) x += parseTerm(); // addition
	                else if (eat('-')) x -= parseTerm(); // subtraction
	                else return x;
	            }
	        }

	        double parseTerm() {
	            double x = parseFactor();
	            for (;;) {
	                if      (eat('*')) x *= parseFactor(); // multiplication
	                else if (eat('/')) x /= parseFactor(); // division
	                else return x;
	            }
	        }

	        double parseFactor() {
	            if (eat('+')) return parseFactor(); // unary plus
	            if (eat('-')) return -parseFactor(); // unary minus

	            double x;
	            int startPos = this.pos;
	            if (eat('(')) { // parentheses
	                x = parseExpression();
	                eat(')');
	            } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
	                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
	                x = Double.parseDouble(str.substring(startPos, this.pos));
	            } else if (ch >= 'a' && ch <= 'z') { // functions
	                while (ch >= 'a' && ch <= 'z') nextChar();
	                String func = str.substring(startPos, this.pos);
	                x = parseFactor();
	                if (func.equals("sqrt")) x = Math.sqrt(x);
	                else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
	                else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
	                else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
	                else throw new RuntimeException("Unknown function: " + func);
	            } else {
	                throw new RuntimeException("Unexpected: " + (char)ch);
	            }

	            if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

	            return x;
	        }
	    }.parse();
	}
}
