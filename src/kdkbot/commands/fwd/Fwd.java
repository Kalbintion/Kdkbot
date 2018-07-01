package kdkbot.commands.fwd;

import kdkbot.Kdkbot;
import kdkbot.MessageInfo;
import kdkbot.channel.Channel;
import kdkbot.channel.Forwarder;
import kdkbot.commands.Command;

public class Fwd extends Command {
	public Fwd() {
		this.setTrigger("ama");
		this.setPermissionLevel(1);
		this.setAvailability(true);
	}
	
	public void executeCommand(MessageInfo info) {
		String[] args = info.message.split(" ");
		Channel chan = info.getChannel();
		Channel targetChan;

		switch(args[1]) {
			case "req":
				// !fwd req <name>
				if(args.length >= 3) {
					String toChan = args[2].toLowerCase();
					if(!toChan.startsWith("#")) {
						toChan = "#" + toChan;
					}
					
					if(Kdkbot.instance.isInChannel(toChan)) {
						targetChan = Kdkbot.instance.getChannel(toChan);
						targetChan.sendMessage(info.channel + " has requested forwarding permissions. Type '" + targetChan.cfgChan.getSetting("commandPrefix") + "afwd " + info.channel.replaceAll("#", "") + "' to authorize or '" + targetChan.cfgChan.getSetting("commandPrefix") + "dfwd " + info.channel.replaceAll("#", "") + "' to deny.");
						chan.sendMessage("Sent forward request, awaiting reply.");
						
						chan.forwarders.add(new Forwarder(toChan, true));
						targetChan.forwarders.add(new Forwarder(info.channel));
					} else {
						chan.sendMessage(info.sender + ": This bot is not in that channel. Have them join my channel and type !join");
					}
				} else {
					chan.sendMessage(info.sender + ": You did not provide a channel name.");
				}
				break;
			case "allow":
				// !fwd allow <name>
				if(args.length >= 3) {
					String toAuthorize = args[2].toLowerCase();
					if(!toAuthorize.startsWith("#")) { toAuthorize = "#" + toAuthorize; }
					
					if(chan.isAwaitingForwarderResponse(toAuthorize)) {
						if(chan.isFwdRequestor(toAuthorize)) {
							chan.sendMessage("You cannot accept this forward. Waiting reply from " + toAuthorize + ".");
						} else {
							targetChan = Kdkbot.instance.getChannel(toAuthorize);
							targetChan.sendMessage("Forwarding authorization request accepted from " + chan.channel + ".");
							chan.sendMessage("Forwarding authorization request accepted from " + toAuthorize + ".");
							targetChan.authorizeForwarder(info.channel);
							chan.authorizeForwarder(toAuthorize);
						}
					} else {
						chan.sendMessage("Channel " + toAuthorize + " was not awaiting response.");
					}
				} else {
					chan.sendMessage("You did not specify a channel to accept the forward request from.");
				}
				break;
			case "deny":
				// !fwd deny <name>
				if(args.length >= 3) {
					String toDeny = args[2].toLowerCase();
					if(!toDeny.startsWith("#")) { toDeny = "#" + toDeny; }
					
					if(chan.isAwaitingForwarderResponse(toDeny)) {
						targetChan = Kdkbot.instance.getChannel(toDeny);
						targetChan.sendMessage("Forwarding authorization request denied from " + chan.channel + ".");
						chan.sendMessage("Forwarding authorization request denied from " + toDeny + ".");
						targetChan.denyForwarder(info.channel);
						chan.denyForwarder(toDeny);
					} else {
						chan.sendMessage("Channel " + toDeny + " was not awaiting response.");
					}
					
				} else {
					chan.sendMessage("You did not specify a channel to deny the forward request from.");
				}
				break;
			case "del":
				// !fwd del <name>
				if(args.length >= 3) {
					String toStop = args[2].toLowerCase();
					if(!toStop.startsWith("#")) { toStop = "#" + toStop; }
					
					if(chan.hasActiveForwarder(toStop)) {
						targetChan = Kdkbot.instance.getChannel(toStop);
						targetChan.sendMessage("Stopping message forwarding from " + chan.channel + ".");
						chan.sendMessage("Stopping message forwarding from " + toStop + ".");
						
						targetChan.removeForwarder(info.channel);
						chan.removeForwarder(toStop);
					} else {
						chan.sendMessage("Channel " + toStop + " was not actively being forwarded to.");
					}
				} else {
					chan.sendMessage("You did not specify a channel to stop the forwarder from.");
				}
				break;
			default:
				// !fwd <name> <name>
				break;
		}
	}
}