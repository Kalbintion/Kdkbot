package kdkbot.channel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import kdkbot.MessageInfo;
import kdkbot.filemanager.Config;

public class Stats {
	public Config statsConfig;
	public HashMap<String, UserStat> userStats = new HashMap<String, UserStat>();
	public String channel;
	
	public Stats(String channel) {
		statsConfig = new Config("./cfg/" + channel + "/stats.cfg");
	}
	
	public void loadStats() {
		try {
			List<String> lines = statsConfig.getConfigContents();
			Iterator<String> iter = lines.iterator();
			while(iter.hasNext()) {
				String[] lineValues = iter.next().split(":");
				try {
					UserStat userstat = new UserStat(Long.parseLong(lineValues[0]), Long.parseLong(lineValues[1]), Long.parseLong(lineValues[2]), Long.parseLong(lineValues[3]), Long.parseLong(lineValues[4]));
				} catch(Exception e) {
					e.printStackTrace();
				}
				
			}
		} catch (Exception e) {
			System.out.println("Failed to load user stats for channel: " + channel);
		}
		
	}
	
	public void saveStats() {
		
	}
	
	public void incrementMessageCount(MessageInfo info) {
		UserStat userstat = userStats.get(info.sender);
		userstat.messageCount += 1;
	}
}
