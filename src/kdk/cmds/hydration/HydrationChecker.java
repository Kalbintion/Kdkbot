package kdk.cmds.hydration;

import java.util.TimerTask;

import kdk.Bot;
import kdk.channel.Channel;

public class HydrationChecker extends TimerTask {
	@Override
	public void run() {
		kdk.Bot.inst.dbg.writeln("[DBG] EXEC HydrationChecker");
		kdk.Bot.inst.dbg.writeln("[DBG] Total Channels: " + Bot.inst.getAllChannels().size());
		for(Channel c : Bot.inst.getAllChannels()) {
			kdk.Bot.inst.dbg.writeln("[DBG] Working on: " + c.channel);
			if(c.hydrationAlert) {
				kdk.Bot.inst.dbg.writeln("[DBG] " + c.channel + " has hydration enabled.");
				// Channel has Hydration Enabled
				long uptime = kdk.api.twitch.APIv5.getStreamUptimeRaw(Bot.inst.getClientID(), c.getUserID());
				kdk.Bot.inst.dbg.writeln("[DBG] " + c.channel + " raw uptime: " + uptime);
				kdk.Bot.inst.dbg.writeln("[DBG] " + c.channel + " last announce: " + c.hydrationLastAnnounce);
				
				long dSec = uptime / 1000 % 60;
				long dMin = uptime / 1000 / 60 % 60;
				long dHour = uptime / 1000 / 60 / 60 % 60;
				long dDay = uptime / 1000 / 60 / 60 / 24 % 24;
				
				String out = "";
				if(dDay > 0) { out = dDay + "D "; }
				if(dHour > 0 || dDay > 0) { out = dHour + "H "; }
				if(dMin > 0 || dHour > 0 || dDay > 0) { out += dMin + "M "; }
				
				if(uptime != -1) {
					if((uptime - c.hydrationLastAnnounce) >= c.hydrationFreq * 60 * 60 * 1000) {
						// We've past our notification period!
						long hydrationAmt = c.hydrationPerHour * c.hydrationFreq * dHour;
						if(c.hydrationMetric) {
							hydrationAmt = hydrationAmt * 30; // 1oz = 30mL
							c.sendTrans("hydration.alert.metric", out, hydrationAmt);
						} else {
							c.sendTrans("hydration.alert", out, hydrationAmt);
						}
					}
					c.hydrationLastAnnounce = uptime - dSec - (dMin * 60); // Set last announce time to most recent frequency
					kdk.Bot.inst.dbg.writeln("[DBG] " + c.channel + " last announce post: " + c.hydrationLastAnnounce);
				} else { // Not Live, don't say anything
					c.hydrationLastAnnounce = 0; // Reset last announcement time to 0 for next live time.
				}
			}
		}
	}
}
