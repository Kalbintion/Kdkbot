package kdkbot.commands.quotes;

import kdkbot.Kdkbot;
import kdkbot.MessageInfo;
import kdkbot.commands.*;
import kdkbot.filemanager.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class Quotes extends Command {
	private String channel;
	private Config cfg;
	private int lastIndex;
	
	public HashMap<String, String> quotes = new HashMap<String, String>();
	
	public Quotes(String channel) {
		this.setTrigger("quote");
		this.setAvailability(true);
		this.setPermissionLevel(1);
		this.channel = channel;
		try {
			this.cfg = new Config("./cfg/" + channel + "/quotes.cfg");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void executeCommand(MessageInfo info) {
		String[] args = info.getSegments();
		String subCmd = "";
		
		if(args.length == 1) { subCmd = "random"; }
		else { subCmd = args[1].toLowerCase(); }

		
		switch(subCmd) {
			case "get":
				try {
					String quote = quotes.get(args[2]);
					if(quote != null) {
						Kdkbot.instance.sendChanMessage(channel, "Quote #" + args[2] + ": " + quote);
					} else {
						Kdkbot.instance.sendChanMessage(channel, "Quote #" + args[2] + " does not exist.");
					}
					
				} catch(NumberFormatException e) {
					this.getBotInstance().sendMessage(channel, info.sender + ": That is not a number, therefore I cannot find the quote.");
				} catch(IndexOutOfBoundsException e) {
					this.getBotInstance().sendMessage(channel, info.sender + ": The requested quote cannot be found.");
				}
				break;
			case "add":
				quotes.put(Integer.toString(++lastIndex), info.message.substring("quote add ".length()));
				Kdkbot.instance.sendChanMessage(channel, "Quote #" + lastIndex + " added.");
				saveQuotes();
				break;
			case "remove":
				quotes.remove(args[2]);
				Kdkbot.instance.sendChanMessage(channel, "Quote #" + args[2] + " removed.");
				break;
			case "save":
				this.saveQuotes();
				Kdkbot.instance.sendChanMessage(channel, "Manually saved quote list for this channel.");
				break;
			case "reload":
				this.quotes = new HashMap<String, String>();
				this.loadQuotes();
				Kdkbot.instance.sendChanMessage(channel, "Manually reloaded quote list for this channel.");
			case "count":
			case "amount":
			case "total":
			case "size":
				Kdkbot.instance.sendChanMessage(channel, "There are " + quotes.size() + " quotes.");
				break;
			case "list":
				Kdkbot.instance.sendChanMessage(channel, "You are see all available quotes by visiting http://tfk.zapto.org/kdkbot/?p=channels&c=" + info.channel.replace("#", "") + "&t=q");
				break;
			case "random":
				Random rnd = new Random();
				int quoteNum = 0;
				do {
					quoteNum = rnd.nextInt(this.quotes.size() + 1);
				} while(quotes.get(Integer.toString(quoteNum)) == null);
				
				Kdkbot.instance.sendChanMessage(channel, "Quote #" + quoteNum + ": " + quotes.get(Integer.toString(quoteNum)));
				
				break;
			default:
				try {
					int quoteIndex = Integer.parseInt(args[1]);
					
					String quote = quotes.get(quoteIndex);
					if(quote != null) {
						Kdkbot.instance.sendChanMessage(channel, "Quote #" + args[2] + ": " + quote);
					} else {
						Kdkbot.instance.sendChanMessage(channel, "Quote #" + args[2] + " does not exist.");
					}
				} catch(NumberFormatException|IndexOutOfBoundsException e) {
					
				}
		}
	}

	public boolean removeQuote(int index) {
		try {
			quotes.remove(Integer.toString(index));
		} catch(IndexOutOfBoundsException e) {
			return false;
		}
		return true;
	}
	
	public String getQuote(int index) {
		return quotes.get(index);
	}
	
	public void loadQuotes() {
		try {
			List<String> lines = cfg.getConfigContents();
			Iterator<String> lineItero = lines.iterator();
			while(lineItero.hasNext()) {
				String line = lineItero.next();
				String[] linePieces = line.split(": ", 2);
				
				quotes.put(linePieces[0], linePieces[1]);
				
				if(lastIndex < Integer.parseInt(linePieces[0])) {
					lastIndex = Integer.parseInt(linePieces[0]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveQuotes() {
		try {
			List<String> toSave = new ArrayList<String>();
			
			Iterator<Entry<String, String>> hashMapIter = quotes.entrySet().iterator();
			
			while(hashMapIter.hasNext()) {
				Map.Entry<String, String> pairs = hashMapIter.next();
				toSave.add(pairs.getKey() + ": " + pairs.getValue());
			}
			cfg.saveSettings(toSave);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
