package kdkbot.commands.filters;

import java.util.EnumSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

import kdkbot.Kdkbot;
import kdkbot.MessageInfo;
import kdkbot.filemanager.Config;

/**
 * Filters will check messages based on a given RegEx and enact upon them. 
 * This can be anything from doing nothing, purging chat, timeout, ban, and
 * message response. Filters may be permitted to be bypassed by use of the
 * 'permit' command which allows a user to ignore filters that are not set
 * to override permit status. This allows for filters to exist that are always
 * active and be enabled regardless of permission.
 * @author KDK
 *
 */
public class Filters {
	// Enum list of filter types
	public enum TYPE {
		NONE, PURGE, TIMEOUT, BAN, MSG
	}
	
	// Enum list of java.util.regex.Pattern;
	public enum FLAG {
		CANON_EQ(Pattern.CANON_EQ),
		CASE_INSENSITIVE(Pattern.CASE_INSENSITIVE),
		COMMENTS(Pattern.COMMENTS),
		DOTALL(Pattern.DOTALL),
		LITERAL(Pattern.LITERAL),
		MULTILINE(Pattern.MULTILINE),
		UNICODE_CASE(Pattern.UNICODE_CASE),
		UNICODE_CHARACTER_CLASS(Pattern.UNICODE_CHARACTER_CLASS),
		UNIX_LINES(Pattern.UNIX_LINES);

		@SuppressWarnings("unused")
		private int value;
		
		private FLAG(int value) {
			this.value = value;
		}
	}
	
	private ArrayList<Filter> filters = new ArrayList<Filter>();
	private Config cfgFilters;
	private String channel;
	
	public Filters(String channel) {
		try {
			this.filters = new ArrayList<Filter>();
			cfgFilters = new Config("./cfg/" + channel + "/filters.cfg");
			this.channel = channel;
			loadFilters();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Filter format:
	 * Human Name||Action||Action Info||Ignores Permit||Regex
	 */
	public void saveFilters() {
		ArrayList<String> sFilters = new ArrayList<String>();
		Iterator<Filter> fIter = filters.iterator();
		while(fIter.hasNext()) {
			Filter fNxt = fIter.next();
			sFilters.add(fNxt.toString());
		}
		cfgFilters.saveSettings(sFilters);
	}
	
	/**
	 * Filter format:
	 * Human Name||Action||Action Info||Ignores Permit||Regex
	 */
	public void loadFilters() {
		try {
			List<String> sFilters = cfgFilters.getConfigContents();
			Iterator<String> sIter = sFilters.iterator();
			filters.clear();
			while(sIter.hasNext()) {
				String sNxt = sIter.next();
				String[] values = sNxt.split("\\|\\|", 5);
				// String toFind, int action, String actionInfo, String humanName, boolean ignoresPermit
				Filter f = new Filter(values[4], Integer.parseInt(values[1]), values[2], values[0], Boolean.parseBoolean(values[3]));
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
		String[] args = info.getSegments();
		
		switch(args[1]) {
			case "new":
					// |filter new <type> <name> <find_regex>
					String[] parts = info.getSegments(5);
					String newAdditionalInfo = "";
					switch(parts[2]) {
						case "none":
						case "0":
							filters.add(new Filter(parts[3], TYPE.NONE.ordinal()));
							break;
						case "purge":
						case "1":
							filters.add(new Filter(parts[3], TYPE.PURGE.ordinal()));
							break;
						case "timeout":
						case "2":
							filters.add(new Filter(parts[3], TYPE.TIMEOUT.ordinal()));
							break;
						case "ban":
						case "3":
							filters.add(new Filter(parts[3], TYPE.BAN.ordinal()));
							break;
						case "message":
						case "4":
							filters.add(new Filter(parts[3], TYPE.MSG.ordinal()));
							newAdditionalInfo = " Use command 'filter edit <name> info <msg> to add response message.";
							break;
					}
					Kdkbot.instance.sendMessage(this.channel, "Added a new filter named " + parts[3] + "." + newAdditionalInfo);
					saveFilters();
				break;
			case "remove":
				// |filter remove <index>
				parts = info.getSegments(3);
				this.filters.remove(Integer.parseInt(parts[2]) - 1);
				Kdkbot.instance.sendMessage(this.channel, "Removed filter #" + parts[2]);
				saveFilters();
				break;
			case "view":
					// |filter view <number> <value>
				parts = info.getSegments(4);
				switch(parts[3]) {
					case "type":
						Kdkbot.instance.sendMessage(this.channel, "Filter #" + parts[2] + "'s " + parts[3] + " has value of " + filters.get(Integer.parseInt(parts[2]) - 1).action);
						break;
					case "regex":
						Kdkbot.instance.sendMessage(this.channel, "Filter #" + parts[2] + "'s " + parts[3] + " has value of " + filters.get(Integer.parseInt(parts[2]) - 1).toFind.toString());
						break;
					case "info":
						Kdkbot.instance.sendMessage(this.channel, "Filter #" + parts[2] + "'s " + parts[3] + " has value of " + filters.get(Integer.parseInt(parts[2]) - 1).actionInfo);
						break;
				}
				break;
			case "edit":
					// |filter edit <number> <value> <new_value>
				parts = info.getSegments(5);
				Filter toModify = getFilterOnName(parts[2]);
				switch(parts[3]) {
					case "type":
						toModify.action = Integer.parseInt(parts[4]);
						break;
					case "regex":
						toModify.toFind = Pattern.compile(parts[4]);
						break;
					case "info":
						toModify.actionInfo = parts[4];
						break;
					case "name":
						toModify.humanName = parts[4];
						break;
					case "flag":
						// Toggles flag values based on <new_value>
				}
				Kdkbot.instance.sendMessage(this.channel, "Changed filter " + parts[2] + "'s " + parts[3] + " value to: " + parts[4]);
				saveFilters();
				break;
			case "save":
				break;
			case "reload":
					// |filter reload
				break;
			case "size":
				Kdkbot.instance.sendMessage(this.channel, "There are " + filters.size() + " filter(s) in this channel.");
				break;
		}
	}
	
	public Filter getFilterOnName(String name) {
		try {
			int filterNumber = Integer.parseInt(name);
			return filters.get(filterNumber);
		} catch(NumberFormatException e) {
			// We have a proper name
			Iterator<Filter> iter = filters.iterator();
			while(iter.hasNext()) {
				Filter next = iter.next();
				if(next.humanName.equalsIgnoreCase("name")) {
					return next;
				}
			}
		}
		return null;
	}
}
