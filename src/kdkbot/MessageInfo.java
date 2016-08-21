package kdkbot;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageInfo {
	public String channel;
	public String sender;
	public String message;
	public String login;
	public String hostname;
	public int senderLevel;
	public long timestamp;
	
	/**
	 * Creates a new MessageInfo class that is used to store a particular messages information.
	 * @param channel The channel this message originates from
	 * @param sender The sender of this message
	 * @param message The message itself
	 * @param login The login of the sender of this message
	 * @param hostname The hostname of the sender of this message
	 * @param senderLevel The sender's permission level
	 */
	public MessageInfo(String channel, String sender, String message, String login, String hostname, int senderLevel) {
		this.channel = channel;
		this.sender = sender;
		this.message = message;
		this.login = login;
		this.hostname = hostname;
		this.senderLevel = senderLevel;
		this.timestamp = System.currentTimeMillis();
	}
	
	/**
	 * Returns a string array containing the message split by spaces for individual arguments
	 * @return The argument array
	 */
	public String[] getSegments() {
		return this.message.split(" ");
	}
	
	/**
	 * Returns a string array containing the message split by spaces for individual arguments, up to limit
	 * @param limit The amount of arguments to split this into
	 * @return The argument array
	 */
	public String[] getSegments(int limit) {
		return this.message.split(" ", limit);
	}
	
	/**
	 * Parses the provided message class variable for URIs, and returns all of them found.
	 * @return An ArrayList<String> containing all of the found URIs within the message variable
	 */
	public ArrayList<String> getURLsFromMessage() {
		Pattern url = Pattern.compile("(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?");
		Matcher url_matcher = url.matcher(message);
		ArrayList<String> urls = new ArrayList<String>();
		
		while(url_matcher.find()) {
			urls.add(url_matcher.group());
		}
		
		return urls;
	}
	
	/**
	 * Parses the provided message class variable for URIs, and returns only the first one.
	 * @return A String containing the first URI that was found in the message.
	 */
	public String getURLFromMessage() {
		return getURLsFromMessage().get(0);
	}
}