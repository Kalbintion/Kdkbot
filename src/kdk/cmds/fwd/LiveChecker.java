package kdk.cmds.fwd;

import java.util.TimerTask;

import kdk.Bot;
import kdk.MessageInfo;
import kdk.channel.Channel;

public class LiveChecker extends TimerTask {

	@Override
	public void run() {
		kdk.Bot.inst.dbg.writeln("[DBG] EXEC LiveChecker");
		kdk.Bot.inst.dbg.writeln("[DBG] Total Channels: " + Bot.inst.getAllChannels().size());
		for(Channel c : Bot.inst.getAllChannels()) {
			kdk.Bot.inst.dbg.writeln("[DBG] Working on: " + c.channel);
			String oChanID = c.getUserID();
			String curActive = c.getActiveForwarders();
			kdk.Bot.inst.dbg.writeln("[DBG] Active Forwarders: " + curActive);
			if(curActive != null) {
				String[] portions = curActive.split(", ");
				kdk.Bot.inst.dbg.writeln("[DBG] Checking if " + oChanID + " is live.");
				if(!kdk.api.twitch.APIv5.isStreamerLive(Bot.inst.getClientID(), oChanID)) {
					// This channel is not live, shut down all forwarders
					kdk.Bot.inst.dbg.writeln("[DBG] " + oChanID + " [" + c.channel + "] is NOT live.");
					c.commands.forwards.executeCommand(Bot.spoofMessage(c.channel, "FWD DEL *"));
				} else {
					kdk.Bot.inst.dbg.writeln("[DBG] " + oChanID + " [" + c.channel + "] is live.");
					// Is everyone else live though?

					kdk.Bot.inst.dbg.writeln("[DBG] Scanning through " + portions.length + " active forwarders.");
					if(portions.length > 0) {
						for(String sc : portions) {
							String tChanID = Bot.inst.getChannel(sc).getUserID();
							kdk.Bot.inst.dbg.writeln("[DBG] Checking if " + tChanID + " [" + sc + "] is live.");
							// Target User ID
							if(!kdk.api.twitch.APIv5.isStreamerLive(Bot.inst.getClientID(), tChanID)) {
								// We need to shut down active forwarders from this channel
								kdk.Bot.inst.dbg.writeln("[DBG] " + tChanID + " [" + sc + "] is NOT live.");
								c.commands.forwards.executeCommand(Bot.spoofMessage(c.channel, "FWD DEL " + sc));
							} else {
								kdk.Bot.inst.dbg.writeln("[DBG] " + tChanID + " [" + sc + "] is live.");
							}
						}
					}
				}
			}
		}
	}

}
