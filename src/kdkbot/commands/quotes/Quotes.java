package kdkbot.commands.quotes;

import kdkbot.Kdkbot;
import kdkbot.commands.*;
import java.util.ArrayList;

public class Quotes implements Command {
	private String trigger = "quote";
	private Kdkbot instance;
	private boolean isAvailable;
	private CommandPermissionLevel cpl;
	
	public ArrayList<String> quotes = new ArrayList<String>();
	
	@Override
	public void executeCommand(String channel, String sender, String login, String hostname, String message, String[] additionalParams) {
		String[] args = message.split(" ");
		
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

	public int addQuote(String quote) {
		quotes.add(quote);
		return quotes.size();
	}
	
	public boolean removeQuote(int index) {
		try {
			quotes.remove(index);
		} catch(IndexOutOfBoundsException e) {
			return false;
		}
		return true;
	}
	
	public String getQuote(int index) {
		return quotes.get(index);
	}
	
	public void loadQuotes() {
		
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
}
