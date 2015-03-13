package kdkbot.commands.messagetimer;

import kdkbot.Kdkbot;
import kdkbot.channel.Channel;
import kdkbot.commands.Command;
import kdkbot.commands.strings.StringCommand;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageTimer extends TimerTask {
	private String timerID;
	private String channel;
	private String message;
	private Timer timer;
	private Kdkbot instance;
	
	public MessageTimer(Kdkbot instance, String channel) {
		this(instance, channel, "", "");
	}
	
	public MessageTimer(Kdkbot instance, String channel, String id, String message) {
		this(instance, channel, id, message, null);
	}
	
	public MessageTimer(Kdkbot instance, String channel, String id, String message, Timer timer) {
		this.instance = instance;
		this.timerID = id;
		this.message = message;
		this.timer = timer;
	}
	
	public void close() {
		this.timer.cancel();
	}
	
	@Override
	public void run() {
		instance.sendMessage(channel, parseMessage());
	}
	
	public String parseMessage() {
		String unparsedString = this.message;
		
		// Get channel instance
		Channel chan = instance.getChannel(channel);
		
		if(chan == null) {
			return "Error: Could not find channel " + channel;
		}
		
		final Pattern STRING_COMMAND_PATTERN = Pattern.compile("%CMD:.*?%");
		Matcher string_command_matcher = STRING_COMMAND_PATTERN.matcher(this.message);
		while(string_command_matcher.find()) {
			String result = string_command_matcher.group();
			String strCmdToGet = result.substring("%CMD:".length(), result.length() - 1);
			Iterator<StringCommand> strCmdIter = chan.commands.commandStrings.commands.iterator();
			StringCommand strCmd;
			while(strCmdIter.hasNext()) {
				strCmd = strCmdIter.next();
				if(strCmd.getTrigger().equalsIgnoreCase(strCmdToGet)) {
					// unparsedString = unparsedString.replace(result, strCmd.parseMessage(strCmd.getMessage(), this.channel, "", "", "", "", null));
				}
			}
		}
		
		// unparsedString is now parsed.
		return unparsedString;
	}
}
