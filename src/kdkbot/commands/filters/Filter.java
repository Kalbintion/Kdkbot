package kdkbot.commands.filters;

import java.util.regex.Pattern;

public class Filter {
	public Pattern toFind;
	public int action;
	public String actionInfo;
	public String humanName;
	
	/**
	 * Creates a new filter with a given String object
	 * @param toFind The string REGEX pattern to use.
	 */
	public Filter(String toFind) {
		this(Pattern.compile(toFind));
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
		this.toFind = toFind;
		this.action = action;
		this.actionInfo = actionInfo;
	}
	
	/**
	 * Creates a new filter with a given REGEX String object with a defined action and action message
	 * @param toFind The regex string to use.
	 * @param action The integer value to associate (See Filters.FILTER_*)
	 * @param actionInfo The message to send in response, if action = 4
	 */
	public Filter(String toFind, String action, String actionInfo) {
		this(Pattern.compile(toFind), Integer.parseInt(action), actionInfo);
	}
	
	/**
	 * Creates a new filter with a given regex string to use and associated action.
	 * @param toFind The regex string to use.
	 * @param action The integer value to associate (See Filters.FILTER_*)
	 */
	public Filter(String toFind, int action) {
		this(Pattern.compile(toFind), action, "");
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
	 */
	@Override
	public String toString() {
		return toFind.toString() + "||" + action + "||" + actionInfo;
	}
}
