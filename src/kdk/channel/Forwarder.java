package kdk.channel;

import java.util.ArrayList;

import kdk.MessageInfo;
import kdk.cmds.MessageParser;
import kdk.cmds.filters.Filter;

/**
 * This class is responsible for handling the message forwarding system between Twitch channels
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
	
	// Authorization variable, if this is false then this forwarder hasnt been validated yet
	private boolean authorized = false;
	private boolean isRequestor = false;
	
	/**
	 * Initializes an empty forwarder that goes to no specific channel
	 */
	public Forwarder() {
		this("");
	}
	
	/**
	 * Initializes an empty forwarder that goes to no specific channel
	 * @param isRequestor Is this channel the requestor?
	 */
	public Forwarder(boolean isRequestor) {
		this("", isRequestor);
	}
	
	/**
	 * Initializes a new forwarder that goes to a specific channel
	 */
	public Forwarder(String channel) {
		this(channel, "%USER%: ");
	}
	
	public Forwarder(String channel, boolean isRequestor) {
		this(channel, "%USER%: ", isRequestor);
	}
	
	/**
	 * Initializes a new forwarder that goes to a specific channel w/ a specific message prefix
	 */
	public Forwarder(String channel, String prefixFormat) {
		this(channel, prefixFormat, false);
	}
	
	public Forwarder(String channel, String prefixFormat, boolean isRequestor) {
		this.toChannel = channel;
		this.prefixFormat = prefixFormat;
		this.isRequestor = isRequestor;
	}
	
	/**
	 * Formats the message using the information provided
	 */
	public String formatMessage(MessageInfo info) {
		return parseMessage(prefixFormat, info) + info.message;
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
	public String parseMessage(String toFormat, MessageInfo info) {
		return MessageParser.parseMessage(toFormat, info);
	}
	
	/**
	 * Authorizes this forwarder to work
	 */
	public void authorize() {
		this.authorized = true;
	}
	
	/**
	 * Unauthorizes this forwarder to work
	 */
	public void unauthorize() {
		this.authorized = false;
	}
	
	/**
	 * Gets the forwarders authorization status
	 * @return True if the forwarder is authorized, false otherwise
	 */
	public boolean isAuthorized() {
		return this.authorized;
	}
	
	public boolean isRequestor() {
		return this.isRequestor;
	}
	
	public String getChannel() {
		return this.toChannel;
	}
}
