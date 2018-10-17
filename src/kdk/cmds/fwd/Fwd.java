package kdk.cmds.fwd;

import kdk.Bot;
import kdk.MessageInfo;
import kdk.channel.Channel;
import kdk.channel.Forwarder;
import kdk.cmds.Command;

public class Fwd extends Command {
	public Fwd() {
		this.setTrigger("fwd");
		this.setPermissionLevel(5);
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
					
					if(Bot.inst.isInChannel(toChan)) {
						targetChan = Bot.inst.getChannel(toChan);
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
							targetChan = Bot.inst.getChannel(toAuthorize);
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
						targetChan = Bot.inst.getChannel(toDeny);
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
					if(toStop.equalsIgnoreCase("*")) {
						// Clearing all forwards
						String fwds = chan.getActiveForwarders();
						if(fwds == null) { chan.sendMessage("There were no active forwarders to stop!"); } else {
						System.out.println(fwds);
						String[] fwd = fwds.split(", ");
						System.out.println("# Fwds: " + fwd.length);
						for(String fwdc : fwd) {
							System.out.println("fwd: " + fwdc);
							if(!fwdc.startsWith("#")) { fwdc = "#" + fwdc; }
							System.out.println("fwd-p: " + fwdc);
							targetChan = Bot.inst.getChannel(fwdc);
							System.out.println("tChan: " + targetChan);
							targetChan.sendMessage("Stopping message forwarding from " + chan.channel + ".");
							targetChan.removeForwarder(info.channel);
							chan.removeForwarder(fwdc);
						}
						chan.sendMessage("Stopped all [" + fwd.length + "] active forwarders.");
						}
					} else {
						// Clearing one forward
						if(!toStop.startsWith("#")) { toStop = "#" + toStop; }
						
						if(chan.hasActiveForwarder(toStop)) {
							targetChan = Bot.inst.getChannel(toStop);
							targetChan.sendMessage("Stopping message forwarding from " + chan.channel + ".");
							chan.sendMessage("Stopping message forwarding from " + toStop + ".");
							
							targetChan.removeForwarder(info.channel);
							chan.removeForwarder(toStop);
						} else {
							chan.sendMessage("Channel " + toStop + " was not actively being forwarded to.");
						}
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
