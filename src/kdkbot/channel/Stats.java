package kdkbot.channel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import kdkbot.MessageInfo;
import kdkbot.filemanager.Config;

public class Stats {
	public Config statsConfig;
	public HashMap<String, UserStat> userStats;
	public String channel;
	
	public Stats(String channel) {
		statsConfig = new Config("./cfg/" + channel + "/stats.cfg");
	}
	
	public void loadStats() {
		try {
			List<String> lines = statsConfig.getConfigContents();
			Iterator<String> iter = lines.iterator();
			while(iter.hasNext()) {
				
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
