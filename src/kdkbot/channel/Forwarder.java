package kdkbot.channel;

import java.util.ArrayList;

import kdkbot.commands.filters.Filter;

public class Forwarder {
	private String toChannel;
	private ArrayList<Filter> messageFilters;
	private String prefixFormat;
	
	public Forwarder() {
		this("");
	}
	
	public Forwarder(String channel) {
		this(channel, "%user%:");
	}
	
	public Forwarder(String channel, String prefixFormat) {
		this.toChannel = channel;
		this.prefixFormat = prefixFormat;
	}
	
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
