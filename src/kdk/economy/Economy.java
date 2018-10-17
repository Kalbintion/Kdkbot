package kdk.economy;

import java.util.HashMap;

import kdk.Bot;
import kdk.MessageInfo;
import kdk.channel.Channel;
import kdk.filemanager.Config;

/**
 * Economy is an extension of a Channel that permits for a currency to exist within a particular channel
 * @author KDK
 *
 */
public class Economy {
	private Channel channel;
	private String currencyName;
	private String currencySymbol;
	private boolean currencyLocation;
	private Config cfg;
	private Config users;
	private Config costs_cmd;
	private Config costs_title;
	private HashMap<String, Double> amounts;
	
	public enum CurrencyLocation {
		PREFIX, SUFFIX;
	}
	
	public Economy(Channel chan) {
		try {
			// Basics
			this.channel = chan;
			
			load();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Economy(String channel) {
		this(Bot.inst.getChannel(channel));
	}
	
	public void load() {
		try {
			// Initialize config file
			this.cfg = new Config("./cfg/" + channel.channel + "/economy.cfg");
			this.cfg.loadConfigContents();
			this.currencyName = cfg.getSetting("name", "Moneys");
			this.currencySymbol = cfg.getSetting("symbol", "$");
			this.currencyLocation = Boolean.parseBoolean(cfg.getSetting("symbol-in-back", "false"));
			
			// Initialize users config file
			this.users = new Config("./cfg/" + channel.channel + "/economy_users.cfg");
			this.users.loadConfigContents();
			
			// Initialize command costs config file
			this.costs_cmd = new Config("./cfg/" + channel.channel + "/economy_costs_cmd.cfg");
			
			// Initialize title costs config file
			this.costs_title = new Config("./cfg/" + channel.channel + "/economy_costs_title.cfg");
			
			// Get amounts
			this.amounts = new HashMap<String, Double>();
			this.amounts.put("letter", Double.parseDouble(cfg.getSetting("amountPerLetter", "0")));
			this.amounts.put("word", Double.parseDouble(cfg.getSetting("amountPerWord", "0")));
			this.amounts.put("line", Double.parseDouble(cfg.getSetting("amountPerLine", "0")));
			this.amounts.put("join", Double.parseDouble(cfg.getSetting("amountPerJoin", "0")));
			this.amounts.put("leave", Double.parseDouble(cfg.getSetting("amountPerLeave", "0")));
			this.amounts.put("link", Double.parseDouble(cfg.getSetting("amountPerLink", "0")));
			this.amounts.put("minute", Double.parseDouble(cfg.getSetting("amountPerMinute", "0")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the amount of money that a particular type offers
	 * @param type The monetary type to look-up.
	 * 	Valid types are:
	 * 		letter, word, line, join, leave, link, minute
	 * @param value The amount to set the value for the type
	 */
	public void setAmount(String type, double value) {
		this.amounts.put(type, value);
	}
	
	/**
	 * Sets the amount of money that a particular type offers
	 * @param type The monetary type to look-up.
	 * 	Valid types are:
	 * 		letter, word, line, join, leave, link, minute
	 * @param value The amount to set the value for the type
	 */
	public void setAmount(String type, String value) {
		setAmount(type, Double.parseDouble(value));
	}
	
	/**
	 * Retrieves the amount of money that a particular type offers
	 * @param type The monetary type to look-up.
	 * 	Valid types are:
	 * 		letter, word, line, join, leave, link, minute
	 * @return The amount, as a double, of the value for the look-up
	 */
	public double getAmount(String type) {
		return this.amounts.get(type);
	}
	
	/**
	 * Spends an amount of a users money
	 * @param user The name of the user to take amount away from
	 * @param amount The amount to take away
	 */
	public void spendMoney(String user, String amount) {
		spendMoney(user, Integer.parseInt(amount));
	}
	
	/**
	 * Spends an amount of a users money
	 * @param user The name of the user to take amount away from
	 * @param amount The amount to take away
	 */
	public void spendMoney(String user, double amount) {
		setUserAmount(user, getUserAmount(user) - amount);
	}
	
	/**
	 * Checks if a user has a certain amount of money
	 * @param user The name of the user to check funds of
	 * @param amount The minimum amount the user needs to succeed
	 * @return True if the user has at least that amount, false if they do not
	 */
	public boolean hasAtLeast(String user, String amount) {
		return hasAtLeast(user, Double.parseDouble(amount));
	}
	
	/**
	 * Checks if a user has a certain amount of money
	 * @param user The name of the user to check funds of
	 * @param amount The minimum amount the user needs to succeed
	 * @return True if the user has at least that amount, false if they do not
	 */
	public boolean hasAtLeast(String user, double amount) {
		if(getUserAmount(user) >= amount) {
			return true;
		}
		return false;
	}
	
	/**
	 * Handles the monetary changes for a message
	 * @param sender The name of the sender
	 * @param message The message from the sender
	 */
	public void handleMessage(String sender, String message) {
		setUserAmount(sender, getUserAmount(sender) + (message.length() * this.amounts.get("letter") + (message.split(" ").length * this.amounts.get("word") + this.amounts.get("line"))));
	}
	
	/**
	 * Handles the monetary changes for a join
	 * @param sender The name of the sender
	 */
	public void handleJoin(String sender) {
		setUserAmount(sender, getUserAmount(sender) + this.amounts.get("join"));
	}
	
	/**
	 * Handles the monetary changes for a leave (parts)
	 * @param sender The name of the sender
	 */
	public void handleLeave(String sender) {
		setUserAmount(sender, getUserAmount(sender) + this.amounts.get("leave"));
	}
	
	/**
	 * Handles the monetary changes for time spent in a channel
	 * @param sender The name of the sender
	 */
	public void handleTime(String sender, long time) {
		setUserAmount(sender, getUserAmount(sender) + (this.amounts.get("amountPerMinute") * time));
	}
	
	/**
	 * Retrieves the amount of money a user has
	 * @param sender The name of the user to retrieve
	 * @return The amount, as a double, that the user currently has.
	 */
	public double getUserAmount(String sender) {
		return Double.parseDouble(users.getSetting(sender, "0"));
	}
	
	/**
	 * Sets the amount of money a user has
	 * @param sender The name of the user to set
	 * @param amount The amount to set that user to
	 */
	public void setUserAmount(String sender, double amount) {
		users.setSetting(sender, Double.toString(amount));
	}
	
	/**
	 * Sets the amount of money a user has
	 * @param sender The name of the user to set
	 * @param amount The amount to set that user to
	 */
	public void setUserAmount(String sender, String amount) {
		setUserAmount(sender, Double.parseDouble(amount));
	}
	
	/**
	 * Determines if a particular command has a price in the channel
	 * @param command The command to look-up
	 * @return True if the command does have a price (non-zero), false otherwise
	 */
	public boolean commandHasPrice(String command) {
		String cmdCostStr = costs_cmd.getSetting(command);
		if(cmdCostStr == null) { return false; }
		
		double cmdCost = Double.parseDouble(costs_cmd.getSetting(command));
		return (cmdCost != 0) ? true : false;
	}
	
	/**
	 * Returns the amount a particular command costs, if it has one.
	 * @param command The command to look-up
	 * @return The amount, as a double, the command costs, 0 otherwise.
	 */
	public double getCommandPrice(String command) {
		if(commandHasPrice(command)) {
			return Double.parseDouble(costs_cmd.getSetting(command));
		} else {
			return 0d;
		}
	}
	
	/**
	 * Returns a formatted string for the amount of money based on the channels settings
	 * @param amount The amount used to format
	 * @return The formatted string containing some sort of monetary symbol and the amount
	 */
	public String compileCurrency(double amount) {
		return compileCurrency(Double.toString(amount));
	}
	
	/**
	 * Returns a formatted string for the amount of money based on the channels settings
	 * @param amount The amount used to format
	 * @return The formatted string containing some sort of monetary symbol and the amount
	 */
	public String compileCurrency(String amount) {
		if(!this.currencyLocation) {
			return this.currencySymbol + amount;
		} else {
			return amount + this.currencySymbol;
		}
	}
	
	/**
	 * Handles the command information for the economy system
	 * @param info
	 * @return
	 */
	public String handleMessage(MessageInfo info) {
		Bot.inst.dbg.writeln(this, "Attempting to parse last message for channel " + info.channel);
		
		// Enforce senders name to be lowercased - prevents case sensitive issues later on
		info.sender = info.sender.toLowerCase();
		
		// Start command checking
		String args[] = info.getSegments();
		
		if(args.length <= 1) { return ""; }
		switch(args[1]) {
			case "money":
				return "You have " + compileCurrency(getUserAmount(info.sender)) + " " + this.currencyName;
			case "perLetter":
				if(args.length >= 2) {
					setAmount("letter", args[2]);
					return "Changed per letter value to " + args[2];
				} else {
					return "You earn " + getAmount("letter") + " per letter.";
				}
			case "perWord":
				if(args.length >= 2) {
					setAmount("word", args[2]);
					return "Changed per word value to " + args[2];
				} else {
					return "You earn " + getAmount("word") + " per word.";
				}
			case "perLine":
				if(args.length >= 2) {
					setAmount("word", args[2]);
					return "Changed per line value to " + args[2];
				} else {
					return "You earn " + getAmount("line") + " per line.";
				}
			case "perJoin":
				if(args.length >= 2) {
					setAmount("word", args[2]);
					return "Changed per join value to " + args[2];
				} else {
					return "You earn " + getAmount("join") + " per join.";
				}
			case "perLink":
				if(args.length >= 2) {
					setAmount("word", args[2]);
					return "Changed per link value to " + args[2];
				} else {
					return "You earn " + getAmount("link") + " per link.";
				}
			case "perMinute":
				if(args.length >= 2) {
					setAmount("word", args[2]);
					return "Changed per minute value to " + args[2];
				} else {
					return "You earn " + getAmount("minute") + " per minute.";
				}
			case "command":
				// economy command <command> <value>
				if(args.length >= 4) {
					String command = args[2];
					String value = args[3];
					costs_cmd.setSetting(command, value);
					return "Set command " + command + " to cost " + value + ".";
				} else {
					return "Not enough arguments. Please supply the command and new value.";
				}
			case "title":
				// economy title <title> <value>
				if(args.length >= 4) {
					String title = args[2];
					String value = args[3];
					costs_title.setSetting(title, value);
				} else {
					return "Not enough arguments. Please supply the title and new value.";
				}
		}
		
		return "";
	}
}
