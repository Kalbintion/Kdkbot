package kdkbot.channel;

public class UserStat {
	public long since;
	public long timeSpent;
	public long messageCount;
	public long lastJoin;
	public long lastLeave;
	
	public UserStat() {
		
	}
	
	public UserStat(long since, long timeSpent, long messageCount, long lastJoin, long lastLeave) {
		this.since = since;
		this.timeSpent = timeSpent;
		this.messageCount = messageCount;
		this.lastJoin = lastJoin;
		this.lastLeave = lastLeave;
	}
	
	@Override
	public String toString() {
		return (this.since + ":" + this.timeSpent + ":" + this.messageCount + ":" + this.lastJoin + ":" + this.lastLeave);
	}
}
