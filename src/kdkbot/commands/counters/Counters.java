package kdkbot.commands.counters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kdkbot.Kdkbot;
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
			this.config = new Config("./cfg/counter/" + channel + ".cfg", false);
			this.counters = new ArrayList<Counter>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadCounters() {
		try {
			System.out.println("[DBG] [COUNT] [LOAD] Starting load process...");
			List<String> strings = config.getConfigContents();
			System.out.println("[DBG] [COUNT] [LOAD] Loaded contents. Size: " + strings.size());
			Iterator<String> string = strings.iterator();
			while(string.hasNext()) {
				String str = string.next();
				System.out.println("[DBG] [COUNT] [LOAD] Parsing next string: " + str);
				String[] args = str.split("\\|");
				System.out.println("[DBG] [COUNT] [LOAD] Size of args: " + args.length);
				for(int i = 0; i < args.length; i++) {
					System.out.println("[DBG] [COUNT] [LOAD] args[" + i + "] is " + args[i]);
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
	
	public void executeCommand(String channel, String sender, String login, String hostname, String message, String[] additionalParams) {

	}
}
