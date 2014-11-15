package kdkbot.commands.filters;

import java.util.regex.Pattern;

public class Filter {
	public Pattern toFind;
	public int action;
	
	/**
	 * Creates a new filter with a given String object
	 * @param toFind The string REGEX pattern to use.
	 */
	public Filter(String toFind) {
		this.toFind.compile(toFind);
	}
	
	/**
	 * Creates a new filter with a given REGEX Pattern object
	 * @param toFind The pattern regex object to use.
	 */
	public Filter(Pattern toFind) {
		this.toFind = toFind;
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
}
