package kdkbot.channel;

import java.util.ArrayList;

import kdkbot.commands.filters.Filter;

/**
 * This class is reponsible for handling the message forwarding system between Twitch channels
 * @author KDK
 *
 */
public class Forwarder {
	// The channel this forwarder is going to send messages to
	private String toChannel;
	// The filtering for messages not to forward
	private ArrayList<Filter> messageFilters;
	// The prefix format of the message (Default: %user%)
	private String prefixFormat;
	
	/*
	 * Initializes an empty forwarder that goes to no specific channel
	 */
	public Forwarder() {
		this("");
	}
	
	/*
	 * Initializes a new forwarder that goes to a specific channel
	 */
	public Forwarder(String channel) {
		this(channel, "%user%:");
	}
	
	/*
	 * Initializes a new forwarder that goes to a specific channel w/ a specific message prefix
	 */
	public Forwarder(String channel, String prefixFormat) {
		this.toChannel = channel;
		this.prefixFormat = prefixFormat;
	}
	
	/*
	 * Formats the message using the information provided
	 */
	public String formatMessage(String channel, String sender, String login, String hostname, String message) {
		return parseMessage(prefixFormat, channel, sender, login, hostname, message) + message;
	}
	
	/**
	 * Returns a parsed message using one of the Percent-Args.
	 * @param toFormat The message to parse
	 * @param channel The channel name to be used in place of %CHAN%
	 * @param sender The sender name to be used in place of %USER%
	 * @param login The login name to be used in place of %LOGIN%
	 * @param hostname The hostname to be used in place of %HOST%
	 * @param message The message to be used in place of %MSG%
	 * @return The finalized, parsed, message
	 */
	public String parseMessage(String toFormat, String channel, String sender, String login, String hostname, String message) {
		return toFormat.replace("%USER%", sender).replace("%CHAN%", channel).replace("%LOGIN%", login).replace("%HOST%", hostname).replace("%MSG%", message);
	}
}
