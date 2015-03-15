package kdkbot;

import java.util.ArrayList;

public class MessageInfo {
	public String channel;
	public String sender;
	public String message;
	public String login;
	public String hostname;
	public int senderLevel;
	
	public MessageInfo(String channel, String sender, String message, String login, String hostname, int senderLevel) {
		this.channel = channel;
		this.sender = sender;
		this.message = message;
		this.login = login;
		this.hostname = hostname;
		this.senderLevel = senderLevel;
	}
	
	public String[] getSegments() {
		return this.message.split(" ");
	}
	
	public String[] getSegments(int limit) {
		return this.message.split(" ", limit);
	}
}