package kdkbot.commands.filters;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.regex.Pattern;

import kdkbot.Kdkbot;
import kdkbot.MessageInfo;
import kdkbot.filemanager.Config;

public class Filters {
	public final int FILTER_USER_NONE = 0;
	public final int FILTER_USER_PURGE = 1;
	public final int FILTER_USER_TIMEOUT = 2;
	public final int FILTER_USER_BAN = 3;
	public final int FILTER_USER_MSG = 4;
	
	private ArrayList<Filter> filters = new ArrayList<Filter>();
	private Config cfgFilters;
	private Kdkbot instance;
	private String channel;
	
	public Filters(Kdkbot instance, String channel) {
		try {
			this.filters = new ArrayList<Filter>();
			cfgFilters = new Config("./cfg/" + channel + "/filters.cfg");
			this.channel = channel;
			loadFilters();
			this.instance = instance;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveFilters() {
		ArrayList<String> sFilters = new ArrayList<String>();
		Iterator<Filter> fIter = filters.iterator();
		while(fIter.hasNext()) {
			Filter fNxt = fIter.next();
			sFilters.add(fNxt.toString());
		}
		cfgFilters.saveSettings(sFilters);
	}
	
	public void loadFilters() {
		try {
			List<String> sFilters = cfgFilters.getConfigContents();
			Iterator<String> sIter = sFilters.iterator();
			filters.clear();
			while(sIter.hasNext()) {
				String sNxt = sIter.next();
				String[] values = sNxt.split("\\|\\|", 3);
				Filter f = new Filter(values[0], values[1], values[2]);
				filters.add(f);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public ArrayList<Filter> getFilters() {
		return this.filters;
	}
	
	public void executeCommand(MessageInfo info) {
		String[] args = info.message.split(" ");
		
		switch(args[1]) {
			case "new":
					// |filter new <type> <find_regex>
					String[] parts = info.message.split(" ", 4);
					String newAdditionalInfo = "";
					switch(parts[2]) {
						case "none":
						case "0":
							filters.add(new Filter(parts[3], FILTER_USER_NONE));
							break;
						case "purge":
						case "1":
							filters.add(new Filter(parts[3], FILTER_USER_PURGE));
							break;
						case "timeout":
						case "2":
							filters.add(new Filter(parts[3], FILTER_USER_TIMEOUT));
							break;
						case "ban":
						case "3":
							filters.add(new Filter(parts[3], FILTER_USER_BAN));
							break;
						case "message":
						case "4":
							filters.add(new Filter(parts[3], FILTER_USER_MSG));
							newAdditionalInfo = " Use command 'filter edit <id> info <new info> to add response message.";
							break;
					}
					instance.sendMessage(this.channel, "Added a new filter with id #" + filters.size() + "." + newAdditionalInfo);
					saveFilters();
				break;
			case "remove":
				// |filter remove <index>
				parts = info.message.split(" ", 3);
				this.filters.remove(Integer.parseInt(parts[2]) - 1);
				instance.sendMessage(this.channel, "Removed filter #" + parts[2]);
				saveFilters();
				break;
			case "view":
					// |filter view <number> <value>
				parts = info.message.split(" ", 4);
				switch(parts[3]) {
					case "type":
						instance.sendMessage(this.channel, "Filter #" + parts[2] + "'s " + parts[3] + " has value of " + filters.get(Integer.parseInt(parts[2]) - 1).action);
						break;
					case "regex":
						instance.sendMessage(this.channel, "Filter #" + parts[2] + "'s " + parts[3] + " has value of " + filters.get(Integer.parseInt(parts[2]) - 1).toFind.toString());
						break;
					case "info":
						instance.sendMessage(this.channel, "Filter #" + parts[2] + "'s " + parts[3] + " has value of " + filters.get(Integer.parseInt(parts[2]) - 1).actionInfo);
						break;
				}
				break;
			case "edit":
					// |filter edit <number> <value> <new_value>
				parts = info.message.split(" ", 5);
				switch(parts[3]) {
					case "type":
						filters.get(Integer.parseInt(parts[2]) - 1).action = Integer.parseInt(parts[4]);
						break;
					case "regex":
						filters.get(Integer.parseInt(parts[2]) - 1).toFind = Pattern.compile(parts[4]);
						break;
					case "info":
						filters.get(Integer.parseInt(parts[2]) - 1).actionInfo = parts[4];
						break;
				}
				instance.sendMessage(this.channel, "Changed filter #" + parts[2] + "'s " + parts[3] + " value to: " + parts[4]);
				saveFilters();
				break;
			case "save":
					// |filter save
				break;
			case "reload":
					// |filter reload
				break;
			case "size":
				instance.sendMessage(this.channel, "There are " + filters.size() + " filter(s) in this channel.");
				break;
		}
	}
}
