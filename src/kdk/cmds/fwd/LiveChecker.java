package kdk.cmds.fwd;

import java.util.TimerTask;

import kdk.Bot;
import kdk.MessageInfo;
import kdk.channel.Channel;

public class LiveChecker extends TimerTask {

	@Override
	public void run() {
		for(Channel c : Bot.inst.getAllChannels()) {
			String oChanID = c.getUserID();
			String curActive = c.getActiveForwarders();
			if(curActive != null) {
				String[] portions = curActive.split(", ");
				if(!kdk.api.twitch.APIv5.isStreamerLive(Bot.inst.getClientID(), oChanID)) {
					// This channel is not live, shut down all forwarders
					c.commands.forwards.executeCommand(new MessageInfo(c.channel, Bot.inst.getNick(), "FWD DEL *", "", "", Integer.MAX_VALUE));
				} else {
					// Is everyone else live though?
					for(String sc : portions) {
						// Target User ID
						String tChanID = kdk.api.twitch.APIv5.getUserID(Bot.inst.getClientID(), sc);
						if(!kdk.api.twitch.APIv5.isStreamerLive(Bot.inst.getClientID(), tChanID)) {
							// We need to shut down active forwarders from this channel
							c.commands.forwards.executeCommand(new MessageInfo(c.channel, Bot.inst.getNick(), "FWD DEL " + sc, "", "", Integer.MAX_VALUE));
						}
					}
				}
			}
		}
	}

}
