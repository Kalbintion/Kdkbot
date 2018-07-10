package kdk.cmds.filters;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

import kdk.Bot;
import kdk.MessageInfo;
import kdk.filemanager.Config;
import kdk.language.Translate;

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
		NONE(0), PURGE(1), TIMEOUT(2), BAN(3), MSG(4);
		
		@SuppressWarnings("unused")
		private int value;
		
		private TYPE(int value) {
			this.value = value;
		}
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
			if(filters.size() > 0) { filters.clear(); } // Clear filters if needed
			
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
							filters.add(new Filter(parts[4], TYPE.NONE.ordinal(), parts[3]));
							break;
						case "purge":
						case "1":
							filters.add(new Filter(parts[4], TYPE.PURGE.ordinal(), parts[3]));
							break;
						case "timeout":
						case "2":
							filters.add(new Filter(parts[4], TYPE.TIMEOUT.ordinal(), parts[3]));
							break;
						case "ban":
						case "3":
							filters.add(new Filter(parts[4], TYPE.BAN.ordinal(), parts[3]));
							break;
						case "message":
						case "msg":
						case "4":
							filters.add(new Filter(parts[4], TYPE.MSG.ordinal(), parts[3]));
							newAdditionalInfo = Translate.getTranslate("filters.add.info", info.getChannel().getLang());
							break;
					}
					Bot.instance.sendChanMessageTrans(channel, "filters.add", parts[3], newAdditionalInfo);
					saveFilters();
				break;
			case "remove":
				// |filter remove (<name> | <index>)
				parts = info.getSegments(3);
				try {
					int idx = Integer.parseInt(parts[2]);
					
					this.filters.remove(idx);
					Bot.instance.sendChanMessageTrans(channel, "filters.remove", parts[2]);
				} catch(NumberFormatException e) {
					// We must be dealing with a human name
					Iterator<Filter> it = this.filters.iterator();
					while(it.hasNext()) {
						Filter nxt = it.next();
						if(nxt.humanName.equalsIgnoreCase(parts[2])) {
							it.remove();
							Bot.instance.sendChanMessageTrans(channel, "filters.remove", parts[2]);
							break;
						}
					}
				} catch(IndexOutOfBoundsException e) {
					// IOOB exception
					Bot.instance.sendChanMessageTrans(channel, "filters.remove.fail", parts[2]);
				}
				
				saveFilters();
				break;
			case "view":
					// |filter view (<name> | <index>) <value>
				parts = info.getSegments(4);
				Filter toView = getFilterOnName(parts[2]);
				
				switch(parts[3]) {
					case "type":
						Bot.instance.sendChanMessageTrans(channel, "filters.view.type", parts[2], parts[3], toView.action);
						break;
					case "regex":
						Bot.instance.sendChanMessageTrans(channel, "filters.view.regex", parts[2], parts[3], toView.toFind.toString());
						break;
					case "info":
						Bot.instance.sendChanMessageTrans(channel, "filters.view.info", parts[2], parts[3], toView.actionInfo);
						break;
				}
				break;
			case "edit":
					// |filter edit (<name> | <index>) <value> <new_value>
				parts = info.getSegments(5);
				
				Filter toModify = getFilterOnName(parts[2]);
				switch(parts[3]) {
					case "type":
						toModify.action = getTypeOnName(parts[4]);
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
				Bot.instance.sendChanMessageTrans(channel, "filters.mod", parts[2], parts[3], parts[4]);
				saveFilters();
				break;
			case "save":
				break;
			case "reload":
					// |filter reload
				break;
			case "size":
				Bot.instance.sendChanMessageTrans(channel, "filters.size", filters.size());
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
				if(next.humanName.equalsIgnoreCase(name)) {
					return next;
				}
			}
		}
		return null;
	}
	
	public int getTypeOnName(String name) {
		switch(name) {
			case "none":
			case "0":
				return 0;
			case "purge":
			case "1":
				return 1;
			case "timeout":
			case "2":
				return 2;
			case "ban":
			case "3":
				return 3;
			case "message":
			case "msg":
			case "4":
				return 4;
			default:
				return 0;
		}
	}
}
