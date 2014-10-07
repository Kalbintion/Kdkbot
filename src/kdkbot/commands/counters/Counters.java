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
	
	public void loadCommands() {
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
				System.out.println("[DBG] [COUNT] [LOAD] args[0]: " + Integer.parseInt(args[0]));
				System.out.println("[DBG] [COUNT] [LOAD] args[1]: " + Boolean.parseBoolean(args[1]));
				for(int i = 0; i < args.length; i++) {
					System.out.println("[DBG] [COUNT] [LOAD] args[" + i + "] is " + args[i]);
				}
				counters.add(new Counter());
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addCommand(String trigger, String message, String level) {	
		counters.add(new Counter());
	}
	
	public void saveCommands() {
		try {
			Iterator<Counter> countersIter = this.counters.iterator();
			List<String> toSave = new ArrayList<String>();
			
			while(countersIter.hasNext()) {
				Counter curCounter = countersIter.next();
				toSave.add("");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void executeCommand(String channel, String sender, String login, String hostname, String message, String[] additionalParams) {

	}
}
