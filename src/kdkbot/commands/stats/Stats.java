package kdkbot.commands.stats;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import kdkbot.Kdkbot;
import kdkbot.MessageInfo;
import kdkbot.channel.UserStat;

public class Stats {
	private Kdkbot instance;
	private String channel;
	
	public Stats(Kdkbot instance, String channel) {
		this.instance = instance;
		this.channel = channel;
	}
	
	public void executeCommand(MessageInfo info) {
		String[] args = info.getSegments();
		String subCmd = args[1];

		switch(subCmd) {
			case "time":
				UserStat userstat = instance.getChannel(channel).stats.userStats.get(info.sender);
				if(userstat == null) {
					instance.sendMessage(channel, "Could not retreive user stats for " + info.sender);
				} else {
					if(userstat.firstJoin == 0) {
						instance.sendMessage(channel, info.sender + ": You have yet to spend enough time here to have been tracked!");
					} else {
						instance.sendMessage(channel, info.sender + ": You have spent " + getDurationTime(userstat) + " since " + getFirstJoinDate(userstat));
					}
				}
				break;
		}
	}
	

	public String getFirstJoinDate(UserStat user) {
		return unixToTimestamp(user.firstJoin, "d/M/y");
	}
	
	public String getMessageCount(UserStat user) {
		return String.valueOf(user.messageCount);
	}
	
	public String getLastJoinDate(UserStat user) {
		return unixToTimestamp(user.lastJoin, "d/M/y");
	}
	
	public String getDurationTime(UserStat user) {
		long diffSeconds = user.timeSpent / 1000 % 60;
		long diffMinutes = user.timeSpent / (60 * 1000) % 60;
		long diffHours = user.timeSpent / (60 * 60 * 1000) % 24;
		long diffDays = user.timeSpent / (24 * 60 * 60 * 1000);

		return diffDays + " days, " + diffHours + " hours, " + diffMinutes + " minutes and " + diffSeconds + " seconds";
	}
		
	/**
	 * Converts a given unix timestamp to a readable format, defaulted to the GMT-6 timezone
	 * @param value the unix timestamp
	 * @param format The format of the timestamp
	 * @return the formatted date, per /format/ param
	 */
	public String unixToTimestamp(long value, String format) {
		return unixToTimestamp(value, format, TimeZone.getTimeZone("GMT-6"));
	}
	
	/**
	 * Converts a given unix timestamp to a readable format
	 * @param value the unix timestamp
	 * @param format The format of the timestamp
	 * @param zone The timezone to format the date for
	 * @return the formatted date, per /format/ param
	 */
	public String unixToTimestamp(long value, String format, TimeZone zone) {
		Date date = new Date(value);
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setTimeZone(zone);
		return sdf.format(date);
	}
}
