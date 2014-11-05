package kdkbot.economy;

import java.util.HashMap;

import kdkbot.Kdkbot;
import kdkbot.filemanager.Config;

/**
 * Economy is an extension of a Channel that permits for a currency to exist within a particular channel
 * @author KDK
 *
 */
public class Economy {
	private Kdkbot instance;
	private String channel;	
	private String currencyName;
	private String currencySymbol;
	private int currenyLocation;
	private Config cfg;
	private Config users;
	private HashMap<String, Double> amounts;
	
	public enum CurrencyLocation {
		PREFIX, SUFFIX;
	}
	
	public Economy(Kdkbot instance, String channel) {
		try {
			// Basics
			this.instance = instance;
			this.channel = channel;
			
			// Initialize config file
			this.cfg = new Config("./economy/" + channel + ".cfg");
			this.currencyName = cfg.getSetting("name");
			this.currencySymbol = cfg.getSetting("symbol");
			
			// Initialize users config file
			this.users = new Config("./economy/" + channel + "_users.cfg");
			
			// Get amounts
			this.amounts = new HashMap<String, Double>();
			this.amounts.put("letter", Double.parseDouble(cfg.getSetting("amountPerLetter")));
			this.amounts.put("word", Double.parseDouble(cfg.getSetting("amountPerWord")));
			this.amounts.put("line", Double.parseDouble(cfg.getSetting("amountPerLine")));
			this.amounts.put("join", Double.parseDouble(cfg.getSetting("amountPerJoin")));
			this.amounts.put("link", Double.parseDouble(cfg.getSetting("amountPerLink")));
			this.amounts.put("minute", Double.parseDouble(cfg.getSetting("amountPerMinute")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setAmount(String type, double value) {
		this.amounts.put(type, value);
	}
	
	public double getAmount(String type) {
		return this.amounts.get(type);
	}
	
	public void handleMessage(String sender, String message) {
		setUserAmount(sender, getUserAmount(sender) + (message.length() * this.amounts.get("letter") + (message.split(" ").length * this.amounts.get("word") + this.amounts.get("line"))));
	}
	
	public void handleJoin(String sender) {
		setUserAmount(sender, getUserAmount(sender) + this.amounts.get("join"));
	}
	
	public double getUserAmount(String sender) {
		return Double.parseDouble(users.getSetting(sender));
	}
	
	public void setUserAmount(String sender, double amount) {
		users.setSetting(sender, Double.toString(amount));
	}
}
