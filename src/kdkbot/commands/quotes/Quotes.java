package kdkbot.commands.quotes;

import kdkbot.Kdkbot;
import kdkbot.commands.*;
import java.util.ArrayList;

public class Quotes implements Command {
	private String trigger = "quote";
	private Kdkbot instance;
	
	public ArrayList<String> quotes = new ArrayList<String>();
	
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

	@Override
	public void executeCommand(String[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTrigger(String trigger) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTrigger() {
		// TODO Auto-generated method stub
		return null;
	}
}
