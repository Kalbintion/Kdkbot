package kdkbot.channel;

import java.util.ArrayList;

import kdkbot.*;
import kdkbot.commands.*;
import kdkbot.economy.Economy;
import kdkbot.filemanager.Config;

public class Channel {
	private static Kdkbot instance;
	public Commands commands;
	private String channel;
	private Economy economy;
	private boolean repeatMessages;
	public ArrayList<Forwarder> forwarders;
	public String baseConfigLocation;
	public Config channelConfig;
	public String commandPrefix;
	
	public Channel() {
		this(null, null);
	}
	
	/**
	 * Constructs a new channel instance
	 * @param instance the instance of the bot to be assigned for this channel
	 * @param channel the channel name in which to join and reign control over
	 */
	public Channel(Kdkbot instance, String channel) {
		try {
			this.instance = instance;
			this.channel = channel;
			this.commands = new Commands(instance, channel);
			this.commands.loadSenderRanks();
			this.economy = new Economy(instance, channel);
			this.baseConfigLocation = "./cfg/" + channel + "/";
		
			this.channelConfig = new Config(this.baseConfigLocation + "channel.cfg");
			
			this.joinChannel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void joinChannel() {
		this.instance.joinChannel(this.channel);
	}
	
	public void leaveChannel() {
		this.instance.partChannel(this.channel);
	}
	
	public String getChannel() {
		return this.channel;
	}
	
	public void leaveChannel(String reason) {
		this.instance.partChannel(this.channel, reason);
	}
	
	public void kickUser(String nick) {
		this.instance.kick(this.channel, nick);
	}
	
	public void kickUser(String nick, String reason) {
		this.instance.kick(this.channel, nick, reason);
	}

	public String getCommandPrefix() {
		return this.commandPrefix;
	}
	
	public void setCommandPrefix(String commandPrefix) {
		this.commandPrefix = commandPrefix;
	}
}
