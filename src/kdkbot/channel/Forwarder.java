package kdkbot.channel;

import java.util.ArrayList;

import kdkbot.commands.filters.Filter;

public class Forwarder {
	private String toChannel;
	private ArrayList<Filter> messageFilters;
	
	public Forwarder(String channel) {
		this.toChannel = channel;
	}
}
