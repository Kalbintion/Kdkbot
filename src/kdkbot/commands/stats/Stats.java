package kdkbot.commands.stats;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import kdkbot.Kdkbot;
import kdkbot.MessageInfo;
import kdkbot.channel.UserStat;

public class Stats {
	private String channel;
	
	public Stats(String channel) {
		this.channel = channel;
	}
	
	/**
	 * Handles parsing of command
	 * @param info The Message Info holding the data about the message to parse.
	 */
	public void executeCommand(MessageInfo info) {
		String[] args = info.getSegments();
		String subCmd = args[1];
		UserStat userstat = Kdkbot.instance.getChannel(channel).stats.userStats.get(info.sender);
		if(userstat == null) {
			failedToFind(info);
			return;
		}
		
		switch(subCmd) {
			case "time":
				if(userstat.firstJoin == 0) {
					Kdkbot.instance.sendChanMessageTrans(channel, "stats.time.fail", info.sender);
				} else {
					Kdkbot.instance.sendChanMessageTrans(channel, "stats.time", info.sender, getDurationTime(userstat), getFirstJoinDate(userstat));
				}
				break;
			case "msges":
				Kdkbot.instance.sendChanMessageTrans(channel, "stats.msges", info.sender, getMessageCount(userstat), getCharacterCount(userstat));		
				break;
			case "char":
				Kdkbot.instance.sendChanMessageTrans(channel, "stats.char",info.sender, getCharacterCount(userstat));
				break;
			case "all":
				Kdkbot.instance.sendChanMessageTrans(channel, "stats.all", info.sender, getDurationTime(userstat), getFirstJoinDate(userstat), getMessageCount(userstat), getCharacterCount(userstat));
				break;
			case "bits":
				if(userstat.bitsDate == 0) {
					Kdkbot.instance.sendChanMessageTrans(channel, "stats.bits.fail", info.sender);
				} else {
					Kdkbot.instance.sendChanMessageTrans(channel, "stats.bits", info.sender, getBitCount(userstat), getFirstBitDate(userstat));
				}
				break;
		}
	}
	
	public UserStat getUserStat(String user) {
		return Kdkbot.instance.getChannel(channel).stats.userStats.get(user);
	}
	
	public void failedToFind(MessageInfo info) {
		Kdkbot.instance.sendChanMessageTrans(channel, "stats.failed", info.sender);
	}

	public String getFirstJoinDate(UserStat user) {
		return unixToTimestamp(user.firstJoin, "d/M/y");
	}
	
	public String getFirstBitDate(UserStat user) {
		return unixToTimestamp(user.bitsDate, "d/M/y");
	}
	
	public String getMessageCount(UserStat user) {
		return String.valueOf(user.messageCount);
	}
	
	public long getMessageCountValue(UserStat user) {
		return user.messageCount;
	}
	
	public String getCharacterCount(UserStat user) {
		return String.valueOf(user.characterCount);
	}
	
	public long getCharacterCountValue(UserStat user) {
		return user.characterCount;
	}
	
	public long getBitCount(UserStat user) {
		return user.bitsCount;
	}
	
	public String getLastJoinDate(UserStat user) {
		return unixToTimestamp(user.lastJoin, "d/M/y");
	}
	
	public String getFirstJoinDate(String user) {
		UserStat stat = getUserStat(user);
		if(stat == null) { return null; }
		return unixToTimestamp(stat.firstJoin, "d/M/y");
	}
	
	public String getLastJoinDate(String user) {
		UserStat stat = getUserStat(user);
		if(stat == null) { return null; }
		return unixToTimestamp(stat.lastJoin, "d/M/y");
	}
	
	public String getLastLeaveDate(String user) {
		UserStat stat = getUserStat(user);
		if(stat == null) { return null; }
		return unixToTimestamp(stat.lastLeave, "d/M/y");
	}
	
	public String getDurationTime(UserStat user) {
		byte diffSeconds = (byte) (user.timeSpent / 1000 % 60);
		byte diffMinutes = (byte) (user.timeSpent / (60 * 1000) % 60);
		byte diffHours = (byte) (user.timeSpent / (60 * 60 * 1000) % 24);
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
