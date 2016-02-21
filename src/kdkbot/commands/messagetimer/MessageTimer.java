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
	private Kdkbot instance;
	
	public MessageTimer(Kdkbot instance, String channel) {
		this(instance, channel, "", "");
	}
	
	public MessageTimer(Kdkbot instance, String channel, String id, String message) {
		this(instance, channel, id, message, null);
	}
	
	public MessageTimer(Kdkbot instance, String channel, String id, String message, Timer timer) {
		this.instance = instance;
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
		instance.sendMessage(channel, MessageParser.parseMessage(message, info));
	}
	
	public String parseMessage() {
		return "";
	}
}
