package kdkbot.commands.counters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kdkbot.Kdkbot;
import kdkbot.MessageInfo;
import kdkbot.filemanager.Config;

public class Counters {
	public ArrayList<Counter> counters;
	private String channel;
	private Kdkbot instance;
	private Config config;
	
	public Counters(Kdkbot instance, String channel) {
		try {
			this.instance = instance;
			this.channel = channel;
			this.config = new Config("./cfg/" + channel + "/counters.cfg");
			this.counters = new ArrayList<Counter>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadCounters() {
		try {
			instance.dbg.writeln(this, "Starting load process...");
			List<String> strings = config.getConfigContents();
			instance.dbg.writeln(this, "Loaded contents. Size: " + strings.size());
			Iterator<String> string = strings.iterator();
			while(string.hasNext()) {
				String str = string.next();
				instance.dbg.writeln(this, "Parsing next string: " + str);
				String[] args = str.split("\\|");
				instance.dbg.writeln(this, "Size of args: " + args.length);
				for(int i = 0; i < args.length; i++) {
					instance.dbg.writeln(this, "args[" + i + "] is " + args[i]);
				}
				counters.add(new Counter(args[0], Integer.parseInt(args[1])));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addCounter(String name, int value) {	
		counters.add(new Counter(name, value));
	}
	
	public void removeCounter(String name) {
		Iterator<Counter> cntrIter = counters.iterator();
		while(cntrIter.hasNext()) {
			Counter cntr = cntrIter.next();
			if(cntr.name.equalsIgnoreCase(name)) {
				counters.remove(cntr);
				break;
			}
		}
	}
	
	public void saveCounters() {
		try {
			Iterator<Counter> countersIter = this.counters.iterator();
			List<String> toSave = new ArrayList<String>();
			
			while(countersIter.hasNext()) {
				Counter curCounter = countersIter.next();
				toSave.add(curCounter.name + "|" + curCounter.value);
			}
			config.saveSettings(toSave);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void executeCommand(MessageInfo info) {
		String[] args = info.message.split(" ");
		
		Iterator<Counter> cntrIter = this.counters.iterator();
		Counter cntr;
		
		switch(args[1]) {
			case "new":
				if(info.senderLevel >= 2) {
					if(args.length >= 3) {
						this.addCounter(args[2], Integer.parseInt(args[3]));
						instance.sendMessage(channel, "Added new counter called " + args[2] + " with value of " + args[3]);
					} else {
						this.addCounter(args[2], 0);
						instance.sendMessage(channel, "Added new counter called " + args[2] + " with value of 0");
					}
				}
				break;
			case "delete":
			case "remove":
				if(info.senderLevel >= 2)
					this.removeCounter(args[2]);
				break;
			case "+":
			case "add":
				while(cntrIter.hasNext()) {
					cntr = cntrIter.next();
					if(cntr.name.equalsIgnoreCase(args[2])) {
						if(args.length > 3) {
							cntr.addValue(Integer.parseInt(args[3]));
							instance.sendMessage(channel, "Incremented " + args[2] + " by " + args[3] + ". Value is now " + cntr.value);
						} else {
							cntr.addValue(1);
							instance.sendMessage(channel, "Incremented " + args[2] + " by 1. Value is now " + cntr.value);
						}
					}
				}
				break;
			case "-":
			case "sub":
				while(cntrIter.hasNext()) {
					cntr = cntrIter.next();
					if(cntr.name.equalsIgnoreCase(args[2])) {
						cntr.subtractValue(Integer.parseInt(args[3]));
						instance.sendMessage(channel, "Decremented " + args[2] + " by " + args[3] + ". Value is now " + cntr.value);
					}
				}
				break;
			case "*":
			case "mult":
				while(cntrIter.hasNext()) {
					cntr = cntrIter.next();
					if(cntr.name.equalsIgnoreCase(args[2])) {
						cntr.multiplyValue(Integer.parseInt(args[3]));
						instance.sendMessage(channel, "Multiplied " + args[2] + " by " + args[3] + ". Value is now " + cntr.value);
					}
				}
				break;
			case "/":
			case "divide":
				while(cntrIter.hasNext()) {
					cntr = cntrIter.next();
					if(cntr.name.equalsIgnoreCase(args[2])) {
						cntr.divideValue(Integer.parseInt(args[3]));
						instance.sendMessage(channel, "Divided " + args[2] + " by " + args[3] + ". Value is now " + cntr.value);
					}
				}
				break;
			case "=":
			case "get":
				while(cntrIter.hasNext()) {
					cntr = cntrIter.next();
					if(cntr.name.equalsIgnoreCase(args[2])) {
						instance.sendMessage(channel, "Counter " + cntr.name + " is set to " + cntr.value);
					}
				}
				break;
			case "list":
				String out = "";
				while(cntrIter.hasNext()) {
					cntr = cntrIter.next();
					out += cntr.name +"=" + cntr.value + " ";
				}
				instance.sendMessage(channel, out);
				break;
			
		}
		this.saveCounters();
	}
}
