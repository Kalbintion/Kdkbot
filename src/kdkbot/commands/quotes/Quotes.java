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

public class Quotes extends Command {
	private String channel;
	private CommandPermissionLevel cpl = new CommandPermissionLevel();
	private Config cfg;
	private int lastIndex;
	
	public HashMap<String, String> quotes = new HashMap<String, String>();
	
	public Quotes(Kdkbot instance, String channel) {
		this.setTrigger("quote");
		this.setAvailability(true);
		this.setBotInstance(instance);
		this.channel = channel;
		try {
			this.cfg = new Config("./cfg/quotes/" + channel + ".cfg", false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void executeCommand(String channel, String sender, String login, String hostname, String message, ArrayList<String> additionalParams) {
		String[] args = message.split(" ");
		
		// System.out.println("[DBG] [QUOTES] [EXEC] Args[1] is " + args[1]);
		
		switch(args[1]) {
			case "get":
				try {
					// System.out.println("[DBG] [QUOTES] [EXEC] Args[2] is " + args[2]);
					String quote = quotes.get(args[2]);
					if(quote != null) {
						this.getBotInstance().sendMessage(channel, "Quote #" + args[2] + ": " + quote);
					} else {
						this.getBotInstance().sendMessage(channel, "Quote #" + args[2] + " does not exist.");
					}
					
				} catch(NumberFormatException e) {
					this.getBotInstance().sendMessage(channel, "That is not a number, therefore I cannot find the quote.");
				} catch(IndexOutOfBoundsException e) {
					this.getBotInstance().sendMessage(channel, "The requested quote cannot be found.");
				}
				break;
			case "add":
				quotes.put(Integer.toString(++lastIndex), message.substring("quote add ".length()));
				this.getBotInstance().sendMessage(channel, "Quote #" + lastIndex + " added.");
				saveQuotes();
				break;
			case "remove":
				quotes.remove(args[2]);
				this.getBotInstance().sendMessage(channel, "Quote #" + args[2] + " removed.");
				break;
			case "save":
				this.saveQuotes();
				this.getBotInstance().sendMessage(channel, "Manually saved quote list for this channel.");
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
