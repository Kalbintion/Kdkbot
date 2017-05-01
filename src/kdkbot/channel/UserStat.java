package kdkbot.channel;

public class UserStat {
	public String userName;
	public long firstJoin;
	public long lastJoin;
	public long lastLeave;
	public long messageCount;
	public long timeSpent;
	public long characterCount;
	public long bitsCount;
	public long bitsDate;

	public UserStat(String userName) {
		this(userName, 0, 0, 0, 0, 0, 0, 0, 0);
	}
	
	public UserStat(String userName, long firstJoin, long timeSpent, long messageCount, long lastJoin, long lastLeave, long characterCount, long bitsCount, long bitsDate) {
		this.userName = userName;
		this.firstJoin = firstJoin;
		this.timeSpent = timeSpent;
		this.messageCount = messageCount;
		this.lastJoin = lastJoin;
		this.lastLeave = lastLeave;
		this.characterCount = characterCount;
		this.bitsCount = bitsCount;
		this.bitsDate = bitsDate;
	}
	
	/**
	 * Creates a string representation of this object in the format of:
	 * username:since:time spent:message count:last join:last leave
	 */
	@Override
	public String toString() {
		return (this.userName + ":" + this.firstJoin + ":" + this.timeSpent + ":" + this.messageCount + ":" + this.lastJoin + ":" + this.lastLeave + ":" + this.characterCount + ":" + this.bitsCount + ":" + this.bitsDate);
	}
}
