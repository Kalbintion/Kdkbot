package kdkbot.commands.filters;

import java.util.regex.Pattern;

/**
 * An individual Filter for the Filters class, this is the main class
 * responsible for determining whether or not a filter is to trigger
 * on a message.
 * @author KDK
 *
 */
public class Filter {
	public Pattern toFind;
	public int action;
	public String actionInfo;
	public String humanName;
	public boolean ignoresPermit = false;
	public int flags;
	
	/**
	 * Creates a new filter with a given String object
	 * @param toFind The string REGEX pattern to use.
	 */
	public Filter(String toFind) {
		this(Pattern.compile(toFind, Pattern.CASE_INSENSITIVE));
	}
	
	/**
	 * Creates a new filter with a given REGEX Pattern object
	 * @param toFind The pattern regex object to use.
	 */
	public Filter(Pattern toFind) {
		this(toFind, 0, "");
	}
	
	/**
	 * Creates a new filter with a given REGEX Pattern object with a defined action and action message
	 * @param toFind The pattern regex object to use.
	 * @param action The integer value to associate (See Filters.FILTER_*)
	 * @param actionInfo The message to send in response, if action = 4
	 */
	public Filter(Pattern toFind, int action, String actionInfo) {
		this(toFind, action, actionInfo, "", false);
	}
	
	public Filter(String toFind, String action, String humanName) {
		this(Pattern.compile(toFind, Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE), Integer.parseInt(action), "", humanName, false);
	}
	
	public Filter(String toFind, int action, String humanName) {
		this(Pattern.compile(toFind, Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE), action, "", humanName, false);
	}
	
	/**
	 * Creates a new filter with a given regex string to use and associated action.
	 * @param toFind The regex string to use.
	 * @param action The integer value to associate (See Filters.FILTER_*)
	 */
	public Filter(String toFind, int action) {
		this(Pattern.compile(toFind, Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE), action, "");
	}
	
	/**
	 * Creates a new filter with a given regex pattern, action id, action info, human name, and indication if
	 * this filter can ignore the permit system.
	 * @param toFind the regex string to use.
	 * @param action the integer value to associate (See Filters.FILTER_*)
	 * @param actionInfo the text associated with the action
	 * @param humanName the human readable name associated with the filter
	 * @param ignoresPermit whether or not this filter ignores the permit system
	 */
	public Filter(String toFind, int action, String actionInfo, String humanName, boolean ignoresPermit) {
		this(Pattern.compile(toFind), action, actionInfo, humanName, ignoresPermit);
	}
	
	/**
	 * Creates a new filter with a given regex pattern, action id, action info, human name, and indication if
	 * this filter can ignore the permit system.
	 */
	public Filter(Pattern toFind, int action, String actionInfo, String humanName, boolean ignoresPermit) {
		this.toFind = toFind;
		this.action = action;
		this.actionInfo = actionInfo;
		this.humanName = humanName;
		this.ignoresPermit = ignoresPermit;
	}
	
	/**
	 * Determines if this filters regex pattern is equal to the message
	 * @param message The message to check this filter against
	 * @return True if it matches, false otherwise
	 */
	public boolean equals(String message) {
		return toFind.matcher(message).matches();
	}
	
	/**
	 * Determines if this filters regex pattern is contained in the message
	 * @param message The message to check this filter against
	 * @return True if there are any matches, false otherwise
	 */
	public boolean contains(String message) {
		return toFind.matcher(message).find();
	}
	
	/**
	 * Returns this Filter into a string formation, used for storing into a file.
	 * This is in the format of the RegEx, Action and ActionInfo separated by two pipe (|) characters.
	 * NOTE: This does impose the limit of not being able to be permitted to use two pipe characters
	 * in the filters regex as this will cause the information to segment incorrectly when read.
	 * 
	 * Human Name||Action||Action Info||Ignores Permit||Regex
	 */
	@Override
	public String toString() {
		return this.humanName + "||" + action + "||" + actionInfo + "||" + ignoresPermit +  "||" + toFind.toString();
	}
}
