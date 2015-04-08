package kdkbot;

import java.util.ArrayList;

public class MessageInfo {
	public String channel;
	public String sender;
	public String message;
	public String login;
	public String hostname;
	public int senderLevel;
	
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
}