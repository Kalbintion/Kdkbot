package kdkbot.commands.quotes;

import kdkbot.Kdkbot;
import kdkbot.commands.*;
import kdkbot.filemanager.Config;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Quotes implements Command {
	private String trigger = "quote";
	private Kdkbot instance;
	private boolean isAvailable;
	private String channel;
	private CommandPermissionLevel cpl = new CommandPermissionLevel();
	private Config cfg;
	private int lastIndex;
	
	public HashMap<String, String> quotes = new HashMap<String, String>();
	
	public Quotes(Kdkbot instance, String channel) {
		this.cpl.setLevel(1);
		this.isAvailable = true;
		this.instance = instance;
		this.channel = channel;
		try {
			this.cfg = new Config("./cfg/quotes/" + channel + ".cfg", false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void executeCommand(String channel, String sender, String login, String hostname, String message, ArrayList<String> additionalParams) {
		String[] args = message.split(" ");
		
		// System.out.println("[DBG] [QUOTES] [EXEC] Args[1] is " + args[1]);
		
		switch(args[1]) {
			case "get":
				try {
					// System.out.println("[DBG] [QUOTES] [EXEC] Args[2] is " + args[2]);
					String quote = quotes.get(args[2]);
					if(quote != null) {
						instance.sendMessage(channel, "Quote #" + args[2] + ": " + quote);
					} else {
						instance.sendMessage(channel, "Quote #" + args[2] + " does not exist.");
					}
					
				} catch(NumberFormatException e) {
					this.instance.sendMessage(channel, "That is not a number, therefore I cannot find the quote.");
				} catch(IndexOutOfBoundsException e) {
					this.instance.sendMessage(channel, "The requested quote cannot be found.");
				}
				break;
			case "add":
				quotes.put(Integer.toString(++lastIndex), message.substring("quote add ".length()));
				instance.sendMessage(channel, "Quote #" + lastIndex + " added.");
				saveQuotes();
				break;
			case "remove":
				quotes.remove(args[2]);
				instance.sendMessage(channel, "Quote #" + args[2] + " removed.");
				break;
			case "save":
				this.saveQuotes();
				instance.sendMessage(channel, "Manually saved quote list for this channel.");
				break;
		}
	}

	@Override
	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}

	@Override
	public String getTrigger() {
		return this.trigger;
	}

	@Override
	public void init(String trigger, Kdkbot instance) {
		this.trigger = trigger;
		this.instance = instance;
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
				
				// System.out.println("[DBG] [QUOTE] [LOAD] line: " + line);
				// System.out.println("[DBG] [QUOTE] [LOAD] linePiece length: " + linePieces.length);
				
				quotes.put(linePieces[0], linePieces[1]);
				
				if(lastIndex < Integer.parseInt(linePieces[0])) {
					// System.out.println("[DBG] [QUOTE] [LOAD] Setting lastIndex to " + linePieces[0] + " from " + lastIndex);
					lastIndex = Integer.parseInt(linePieces[0]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean isAvailable() {
		return this.isAvailable;
	}

	@Override
	public void setAvailability(boolean available) {
		this.isAvailable = available;
	}

	@Override
	public int getPermissionLevel() {
		return this.cpl.getLevel();
	}

	@Override
	public void setPermissionLevel(int level) {
		this.cpl.setLevel(level);
	}
	
	public void saveQuotes() {
		try {
			List<String> toSave = new ArrayList<String>();
			
			Iterator hashMapIter = quotes.entrySet().iterator();
			
			while(hashMapIter.hasNext()) {
				Map.Entry pairs = (Map.Entry)hashMapIter.next();
				toSave.add(pairs.getKey() + ": " + pairs.getValue());
			}
			cfg.saveSettings(toSave);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
