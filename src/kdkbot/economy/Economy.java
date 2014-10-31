package kdkbot.economy;

import java.util.HashMap;

import kdkbot.Kdkbot;
import kdkbot.filemanager.Config;

public class Economy {
	private Kdkbot instance;
	private String channel;	
	private String currencyName;
	private String currencySymbol;
	private Config cfg;	
	private HashMap<String, Double> amounts;
	
	public Economy(Kdkbot instance, String channel) {
		try {
			// Basics
			this.instance = instance;
			this.channel = channel;
			
			// Initialize config file
			this.cfg = new Config("./economy/" + channel + ".cfg");
			this.currencyName = cfg.getSetting("name");
			this.currencySymbol = cfg.getSetting("symbol");
			
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
}
