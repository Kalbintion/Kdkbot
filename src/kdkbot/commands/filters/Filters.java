package kdkbot.commands.filters;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import kdkbot.filemanager.Config;

public class Filters {
	public final int FILTER_USER_NONE = 0;
	public final int FILTER_USER_PURGE = 1;
	public final int FILTER_USER_TIMEOUT = 2;
	public final int FILTER_USER_BAN = 3;
	public final int FILTER_USER_MSG = 4;
	
	private ArrayList<Filter> filters;
	private Config cfgFilters;
	
	public Filters(String channel) {
		try {
			this.filters = new ArrayList<Filter>();
			cfgFilters = new Config("./cfg/" + channel + "/filters.cfg");
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
			while(sIter.hasNext()) {
				String sNxt = sIter.next();
				String[] values = sNxt.split("=", 3);
				Filter f = new Filter(values[0], values[1], values[2]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public ArrayList<Filter> getFilters() {
		return this.filters;
	}
	
	public void executeCommand(String channel, String sender, String login, String hostname, String message, ArrayList<String> additionalParams) {
		String[] args = message.split(" ");
		
		switch(args[1]) {
			case "new":

				break;
			case "view":

				break;
			case "edit":

				break;
			case "save":

				break;
			case "reload":

				break;
		}
	}
}
