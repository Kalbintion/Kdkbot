package kdkbot.commands;

public class CommandParser {
	public CommandParser() {
		
	}
	
	public String parseMessage(String msgToParse) {
		return parseMessage(msgToParse, "");
	}

	public String parseMessage(String msgToParse, String msg) {
		return parseMessage(msgToParse, msg, "");
	}
	
	public String parseMessage(String msgToParse, String msg, String sender) {
		return parseMessage(msgToParse, msg, sender, "");
	}
	
	public String parseMessage(String msgToParse, String msg, String sender, String login) {
		return parseMessage(msgToParse, msg, sender, login, "");
	}
	
	public String parseMessage(String msgToParse, String msg, String sender, String login, String hostname) {
		return "";
	}
}
