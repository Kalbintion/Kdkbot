package kdkbot.channel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kdkbot.MessageInfo;
import kdkbot.filemanager.Config;

public class Stats {
	public Config statsConfig;
	public HashMap<String, UserStat> userStats = new HashMap<String, UserStat>();
	public String channel;
	
	public Stats(Channel channel) {
		this(channel.channel);
	}
	
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
					// Load in userstat info
					UserStat userstat = new UserStat(lineValues[0], Long.parseLong(lineValues[1]), Long.parseLong(lineValues[2]), Long.parseLong(lineValues[3]), Long.parseLong(lineValues[4]), Long.parseLong(lineValues[5]));
					
					// Add it to the array
					userStats.put(lineValues[0], userstat);
					
					// Reset userstat var
					userstat = null;
				} catch(Exception e) {
					e.printStackTrace();
				}
				
			}
		} catch (Exception e) {
			System.out.println("Failed to load user stats for channel: " + channel);
		}
		
	}
	
	public void saveStats() {
		try {
			List<String> userstatList = null;
			Iterator<Entry<String, UserStat>> it = userStats.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<String, UserStat> pairs = it.next();
				userstatList.add(pairs.getValue().toString());
			}
			statsConfig.saveSettings(userstatList);
		} catch (Exception e) {
			System.out.println("Failed to save user stats for channel: " + channel);
		}
	}
	
	public void incrementMessageCount(MessageInfo info) {
		UserStat userstat = userStats.get(info.sender);
		userstat.messageCount += 1;
	}
}
