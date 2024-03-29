package kdk.discord;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vdurmont.emoji.EmojiManager;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.MessageHistory;

public class Events {
	@EventSubscriber
	public void onReadyEvent(ReadyEvent e) {
		
	}
	
	@SuppressWarnings("deprecation")
	@EventSubscriber
	public void onMessageReceivedEvent(MessageReceivedEvent e) {
		// Proof of Concept Test
		// [Kal's Coffee@world-build-public] Kalbintion: Some Message
		if((e.getGuild().getName().equalsIgnoreCase("Kal's Coffee") &&
		   e.getChannel().getName().equalsIgnoreCase("warframe")) || (e.getGuild().getName().equalsIgnoreCase("Mythikal Network") && e.getChannel().getName().equalsIgnoreCase("warframe-chat"))) {
			if(e.getMessage().getContent().startsWith("!baro")) {
				IChannel chanWF = e.getChannel();
				if(kdk.api.warframe.API.Warframe.isBaroHere()) {
					JsonParser parser = new JsonParser();
					ArrayList<String> baroManifest = kdk.api.warframe.API.Warframe.getBaroItemsReadable();
					String out = "BARO VISIT - " + kdk.api.warframe.API.Warframe.getBaroLocation() + "\r\n";
					
					if(baroManifest == null) { System.out.println("Baro Manifest Failed"); return; }
					System.out.println("Baro Manifest Size: " + baroManifest.size());
					
					for(String item : baroManifest) {
						JsonObject itm = parser.parse(item).getAsJsonObject();
						out += kdk.api.warframe.API.Warframe.guessItemName(itm.get("Item").toString().replaceAll("\"", ""));
						String ducats = itm.get("Ducats").toString().replaceAll("\"", "");
						out += " (";
						if(!ducats.equalsIgnoreCase("0")) {
							out += ducats + " <:du:421781169424564224>";
						}
						String credits = itm.get("Credits").toString().replaceAll("\"","");
						if(!ducats.equalsIgnoreCase("0") && !credits.equalsIgnoreCase("0")) {
							out += ", " + credits + " <:cr:421781138793562123>";
						} else if (!credits.equalsIgnoreCase("0")) {
							out += credits + " <:cr:421781138793562123>";
						}
						out += ")\r\n";
					}
					
					System.out.println("[" + out.length() + "] " + out);
					
					
					if(out.length() > 2000) { 
						// We need to split message
						String[] lines = out.split("\r\n");
						String nOut = "";
						for(String line : lines) {
							if(nOut.length() + line.length() + "\r\n".length() > 2000) {
								// We need to send out message first
								chanWF.sendMessage(nOut);
								nOut = "";
							}
							nOut += line + "\r\n";
						}
						chanWF.sendMessage(nOut);
					} else {
						chanWF.sendMessage(out);
					}
				} else {
					chanWF.sendMessage("Baro is not yet here!");
				}
			}
		} else if(e.getGuild().getName().equalsIgnoreCase("Kal's Coffee") &&
		   e.getChannel().getName().equalsIgnoreCase("wbp-jury")) {
			IChannel chanWBP = e.getGuild().getChannelByID(439191691195580417l);
			// IChannel chanWBPJ = e.getGuild().getChannelByID(434089649846222848l);
			IChannel chanWBPJ = e.getGuild().getChannelByID(437573865246294019l);
			if(e.getMessage().getContent().startsWith("|make ")) {
				String[] parts = e.getMessage().getContent().split(" ");
				Date start = new Date(2018 - 1900, Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
				Date end = new Date(2018 - 1900, Integer.parseInt(parts[3]) - 1, Integer.parseInt(parts[4]));
				Instant iStart = start.toInstant();
				Instant iEnd = end.toInstant();
				
				System.out.println(start.toString() + ":" + end.toString());
				System.out.println(iStart.toString() + ":" + iEnd.toString());

				long week = 60 * 60 * 24 * 7;
				Instant cur = Instant.now().minusSeconds(week); Instant lwk = Instant.now().minusSeconds(week * 2);
				MessageHistory msges = chanWBP.getMessageHistoryIn(iStart, iEnd);
				MessageHistory allmsges = chanWBP.getFullMessageHistory();
				System.out.println("Full History size: " + allmsges.size());
				
				System.out.println("History size: " + msges.size());
				
				Iterator<IMessage> miter = msges.iterator();
				int cnt = 0; String out = "";
				while(miter.hasNext()) {
					IMessage mnxt = miter.next();
					if(mnxt.getAuthor().getName().equalsIgnoreCase("Kalbintion") || mnxt.getAuthor().getName().equalsIgnoreCase("Kjata119")) { } else {
						cnt++;
						out += ":" + intToName(cnt) + ": " + mnxt.getContent() + "\r\n";
					}
				}
				
				if(out == "") { System.out.println("Empty message."); return; }
				IMessage handle = chanWBPJ.sendMessage(out);
				for(int i = 1; i <= cnt; i++) {
					handle.addReaction(EmojiManager.getForAlias(intToName(i)));
				}
			} else if(e.getMessage().getContent().startsWith("|vote")) {
				String msg = e.getMessage().getContent();
				String[] lines = msg.split("\n");
				String out = "";
				int cnt = 0;
				for(String line : lines) {
					if(cnt == 0) { cnt++; continue; }
					out += ":" + intToName(cnt) + ": " + line + "\r\n";
					cnt++;
				}
				
				IMessage handle = chanWBPJ.sendMessage(out);
				for(int i = 1; i < cnt; i++) {
					try {
						handle.addReaction(EmojiManager.getForAlias(intToName(i)));
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
		
		System.out.println("[" + e.getGuild().getName() + "@" + e.getChannel().getName() + " - " + e.getChannel().getLongID() + "] " + e.getAuthor().getName() + ": " + e.getMessage().getContent());
	}
	
	private String intToName(int num) {
		switch(num) {
			case 0: return "zero";
			case 1: return "one";
			case 2: return "two";
			case 3: return "three";
			case 4: return "four";
			case 5: return "five";
			case 6: return "six";
			case 7: return "seven";
			case 8: return "eight";
			case 9: return "nine";
			default: return String.valueOf(num);
		}
	}
}
