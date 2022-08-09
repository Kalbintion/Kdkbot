package kdk.cmds.fwd;

import java.util.Iterator;

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
		Channel targetChan = null;
		Bot.inst.dbg.writeln("Processing message info. " + info.toString());
		switch(args[1].toLowerCase()) {
			case "req":
				// !fwd req <name> ...
				if(args.length == 3) {
					// !fwd req <name>
					String toChan = args[2].toLowerCase();
					if(!toChan.startsWith("#")) {
						toChan = "#" + toChan;
					}
					
					if(Bot.inst.isInChannel(toChan)) {
						targetChan = Bot.inst.getChannel(toChan);
						
						if(!targetChan.hasActiveForwarder(toChan) && !chan.hasActiveForwarder(toChan)) {
							Bot.inst.sendChanMessageTrans(targetChan.channel, "fwd.request.receive", info.channel, targetChan.cfgChan.getSetting("commandPrefix"), info.channel.replaceAll("#",  ""));
							Bot.inst.sendChanMessageTrans(chan.channel, "fwd.request.sent", toChan);
							
							chan.forwarders.add(new Forwarder(toChan, true));
							targetChan.forwarders.add(new Forwarder(info.channel));
						} else {
							Bot.inst.sendChanMessageTrans(chan.channel, "fwd.request.pending", toChan);
						}
					} else {
						Bot.inst.sendChanMessageTrans(info.channel, "fwd.error.botNotIn", info.sender, toChan.replaceAll("#", ""));
					}
				} else if(args.length > 3) {
					// !fwd req <name> <name> ...
					String sent = "";
					String failed = "";
					
					for(int i = 2; i < args.length; i++) {
						String toChan = args[i].toLowerCase();
						if(!toChan.startsWith("#")) {
							toChan = "#" + toChan;
						}
						
						if(Bot.inst.isInChannel(toChan)) {
							targetChan = Bot.inst.getChannel(toChan);
							Bot.inst.sendChanMessageTrans(targetChan.channel, "fwd.request.receive", info.channel, targetChan.cfgChan.getSetting("commandPrefix"), info.channel.replaceAll("#",  ""));
							
							chan.forwarders.add(new Forwarder(toChan, true));
							targetChan.forwarders.add(new Forwarder(info.channel));
							
							sent += toChan + ", ";
						} else {
							failed += toChan + ", ";
						}
					}
					
					if(failed.length() > 0) { failed = failed.substring(0,  failed.length()-2); } // Trim off excess ', '
					if(sent.length() > 0) { sent = sent.substring(0, sent.length()-2); } // Trim off excess ', '
					
					if(failed.length() > 0) {
						// At least one failure
						Bot.inst.sendChanMessageTrans(chan.channel, "fwd.request.fail.multi", sent, failed);
					} else {
						// No failure to include
						Bot.inst.sendChanMessageTrans(chan.channel, "fwd.request.send.multi", sent);
					}
				} else {
					Bot.inst.sendChanMessageTrans(info.channel, "fwd.request.fail", info.sender);
				}
				break;
			case "allow":
				// !fwd allow <name>
				if(args.length >= 3) {
					String toAuthorize = args[2].toLowerCase();
					if(toAuthorize.equalsIgnoreCase("*")) {
						// Accepting All
						int numAccept = 0;
						
						Iterator<Forwarder> iter = chan.forwarders.iterator();
						while (iter.hasNext()) {
							Forwarder nxt = iter.next();
							if(chan.isAwaitingForwarderResponse(nxt.getChannel()) && !chan.isFwdRequestor(nxt.getChannel())) {
								targetChan = Bot.inst.getChannel(toAuthorize);
								targetChan.authorizeForwarder(info.channel);
								Bot.inst.sendChanMessageTrans(targetChan.channel, "fwd.request.accept", chan.channel);
								nxt.authorize();
								numAccept++;
							}
						}
						
						if(numAccept > 0) {
							Bot.inst.sendChanMessageTrans(info.channel, "fwd.accept.all", numAccept);
						} else {
							Bot.inst.sendChanMessageTrans(info.channel, "fwd.accept.fail");
						}
					} else {
						// Accepting One
						if(!toAuthorize.startsWith("#")) { toAuthorize = "#" + toAuthorize; }
						
						if(chan.isAwaitingForwarderResponse(toAuthorize)) {
							if(chan.isFwdRequestor(toAuthorize)) {
								Bot.inst.sendChanMessageTrans(info.channel, "fwd.request.accept.fail", toAuthorize);
							} else {
								targetChan = Bot.inst.getChannel(toAuthorize);
								Bot.inst.sendChanMessageTrans(targetChan.channel, "fwd.request.accept", chan.channel);
								Bot.inst.sendChanMessageTrans(info.channel, "fwd.request.accept", toAuthorize);
								targetChan.authorizeForwarder(info.channel);
								chan.authorizeForwarder(toAuthorize);
							}
						} else {
							Bot.inst.sendChanMessageTrans(chan.channel, "fwd.error.notAwaiting", toAuthorize);
						}
					}
				} else {
					Bot.inst.sendChanMessageTrans(chan.channel, "fwd.error.noChannel");
				}
				break;
			case "deny":
				// !fwd deny <name>
				if(args.length >= 3) {
					String toDeny = args[2].toLowerCase();
					
					if(toDeny.equalsIgnoreCase("*")) {
						// Denying All
						// Accepting All
						int numDeny = 0;
						
						Iterator<Forwarder> iter = chan.forwarders.iterator();
						while (iter.hasNext()) {
							Forwarder nxt = iter.next();
							if(chan.isAwaitingForwarderResponse(nxt.getChannel())) {
								targetChan = Bot.inst.getChannel(toDeny);
								targetChan.denyForwarder(info.channel);
								chan.denyForwarder(toDeny);
								Bot.inst.sendChanMessageTrans(targetChan.channel, "fwd.request.deny", chan.channel);
							}
						}
						
						if(numDeny > 0) {
							Bot.inst.sendChanMessageTrans(info.channel, "fwd.deny.all", numDeny);
						} else {
							Bot.inst.sendChanMessageTrans(info.channel, "fwd.deny.fail");
						}
					} else {
						// Denying one
						if(!toDeny.startsWith("#")) { toDeny = "#" + toDeny; }
						
						if(chan.isAwaitingForwarderResponse(toDeny)) {
							targetChan = Bot.inst.getChannel(toDeny);
							Bot.inst.sendChanMessageTrans(targetChan.channel, "fwd.request.deny", chan.channel);
							Bot.inst.sendChanMessageTrans(chan.channel, "fwd.request.deny", toDeny);
							targetChan.denyForwarder(info.channel);
							chan.denyForwarder(toDeny);
						} else {
							Bot.inst.sendChanMessageTrans(chan.channel, "fwd.error.notAwaiting", toDeny);
						}
					}
				} else {
					Bot.inst.sendChanMessageTrans(chan.channel, "fwd.error.noChannel");
				}
				break;
			case "del":
				// !fwd del <name>
				if(args.length >= 3) {
					String toStop = args[2].toLowerCase();
					if(toStop.equalsIgnoreCase("*")) {
						// Clearing all forwards
						String fwds = chan.getActiveForwarders();
						if(fwds == null) { Bot.inst.sendChanMessageTrans(chan.channel, "fwd.stop.none"); } else {
							String[] fwd = fwds.split(", ");
							for(String fwdc : fwd) {
								if(!fwdc.startsWith("#")) { fwdc = "#" + fwdc; }
								targetChan = Bot.inst.getChannel(fwdc);
								Bot.inst.sendChanMessageTrans(targetChan.channel, "fwd.stop", chan.channel);
								targetChan.removeForwarder(info.channel);
								chan.removeForwarder(fwdc);
							}
							Bot.inst.sendChanMessageTrans(chan.channel, "fwd.stop.all", fwd.length);
						}
					} else {
						// Clearing one forward
						if(!toStop.startsWith("#")) { toStop = "#" + toStop; }
						
						if(chan.hasActiveForwarder(toStop)) {
							targetChan = Bot.inst.getChannel(toStop);
							Bot.inst.sendChanMessageTrans(targetChan.channel, "fwd.stop", chan.channel);
							Bot.inst.sendChanMessageTrans(chan.channel, "fwd.stop", toStop);
							
							targetChan.removeForwarder(info.channel);
							chan.removeForwarder(toStop);
						} else {
							Bot.inst.sendChanMessageTrans(chan.channel, "fwd.error.notActive");
						}
					}
				} else {
					Bot.inst.sendChanMessageTrans(chan.channel, "fwd.error.noChannel.stop");
				}
				break;
			default:
				// !fwd <name> <name>
				break;
		}
	}
}
