package kdkbot.commands.messagetimer;

import kdkbot.Kdkbot;
import kdkbot.MessageInfo;
import kdkbot.commands.MessageParser;

import java.util.Timer;
import java.util.TimerTask;

public class MessageTimer extends TimerTask {
	private String timerID;
	private String channel;
	private String message;
	private Timer timer;
	
	public MessageTimer(String channel) {
		this(channel, "", "");
	}
	
	public MessageTimer(String channel, String id, String message) {
		this(channel, id, message, null);
	}
	
	public MessageTimer(String channel, String id, String message, Timer timer) {
		this.timerID = id;
		this.message = message;
		this.timer = timer;
		if(timer == null) {
			timer = new Timer();
		}
	}
	
	public void stop() {
		this.timer.cancel();
	}
	
	@Override
	public void run() {
		MessageInfo info = new MessageInfo(channel, "", message, "", "", 0);
		Kdkbot.instance.sendMessage(channel, MessageParser.parseMessage(message, info));
	}
	
	public String parseMessage() {
		return "";
	}
}
