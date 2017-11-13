package kdkbot.commands.quotes;

import kdkbot.Kdkbot;
import kdkbot.MessageInfo;
import kdkbot.commands.*;
import kdkbot.filemanager.Config;
import kdkbot.language.Translate;

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
						Kdkbot.instance.sendChanMessage(channel, String.format(Translate.getTranslate("quotes.get", info.getChannel().getLang()), args[2], quote));
					} else {
						Kdkbot.instance.sendChanMessage(channel, String.format(Translate.getTranslate("quotes.get.fail", info.getChannel().getLang()), args[2]));
					}
					
				} catch(NumberFormatException e) {
					Kdkbot.instance.sendChanMessage(channel, String.format(Translate.getTranslate("quotes.exNFE", info.getChannel().getLang()), info.sender));
				} catch(IndexOutOfBoundsException e) {
					Kdkbot.instance.sendChanMessage(channel, String.format(Translate.getTranslate("quotes.exIOOB", info.getChannel().getLang()), info.sender));
				}
				break;
			case "add":
				quotes.put(Integer.toString(++lastIndex), info.message.substring("quote add ".length()));
				Kdkbot.instance.sendChanMessage(channel, String.format(Translate.getTranslate("quotes.add", info.getChannel().getLang()), lastIndex));
				saveQuotes();
				break;
			case "remove":
				quotes.remove(args[2]);
				Kdkbot.instance.sendChanMessage(channel, String.format(Translate.getTranslate("quotes.del", info.getChannel().getLang()), args[2]));
				break;
			case "save":
				this.saveQuotes();
				Kdkbot.instance.sendChanMessage(channel, Translate.getTranslate("quotes.save", info.getChannel().getLang()));
				break;
			case "reload":
				this.quotes = new HashMap<String, String>();
				this.loadQuotes();
				Kdkbot.instance.sendChanMessage(channel, Translate.getTranslate("quotes.reload", info.getChannel().getLang()));
			case "count":
			case "amount":
			case "total":
			case "size":
				Kdkbot.instance.sendChanMessage(channel, String.format(Translate.getTranslate("quotes.size", info.getChannel().getLang()), quotes.size()));
				break;
			case "list":
				Kdkbot.instance.sendChanMessage(channel, String.format(Translate.getTranslate("quotes.list", info.getChannel().getLang()), info.channel.replace("#", "")));
				break;
			case "random":
				Random rnd = new Random();
				int quoteNum = 0;
				do {
					quoteNum = rnd.nextInt(this.quotes.size() + 1);
				} while(quotes.get(Integer.toString(quoteNum)) == null);
				
				Kdkbot.instance.sendChanMessage(channel, String.format(Translate.getTranslate("quotes.random", info.getChannel().getLang()), quoteNum, quotes.get(Integer.toString(quoteNum))));
				
				break;
			default:
				try {
					int quoteIndex = Integer.parseInt(args[1]);
					
					String quote = quotes.get(String.valueOf(quoteIndex));
					if(quote != null) {
						Kdkbot.instance.sendChanMessage(channel, String.format(Translate.getTranslate("quotes.get", info.getChannel().getLang()), args[1], quote));
					} else {
						Kdkbot.instance.sendChanMessage(channel, String.format(Translate.getTranslate("quotes.get.fail", info.getChannel().getLang()), args[1]));
					}
				} catch(NumberFormatException e) {
					
				}
				break;
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
			if(quotes.size() > 0) {
				// Re-clear quotes list before loading again
				quotes.clear();
			}
			
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
