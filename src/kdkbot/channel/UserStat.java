package kdkbot.channel;

public class UserStat {
	public String userName;
	public long since;
	public long timeSpent;
	public long messageCount;
	public long lastJoin;
	public long lastLeave;
	
	public UserStat() {
		
	}
	
	public UserStat(String userName, long since, long timeSpent, long messageCount, long lastJoin, long lastLeave) {
		this.userName = userName;
		this.since = since;
		this.timeSpent = timeSpent;
		this.messageCount = messageCount;
		this.lastJoin = lastJoin;
		this.lastLeave = lastLeave;
	}
	
	/**
	 * Creates a string representation of this object in the format of:
	 * userame:since:time spent:message count:last join:last leave
	 */
	@Override
	public String toString() {
		return (this.userName + ":" + this.since + ":" + this.timeSpent + ":" + this.messageCount + ":" + this.lastJoin + ":" + this.lastLeave);
	}
}
