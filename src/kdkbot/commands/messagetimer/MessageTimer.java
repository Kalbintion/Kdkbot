package kdkbot.commands.messagetimer;

import kdkbot.Kdkbot;
import kdkbot.MessageInfo;
import kdkbot.commands.MessageParser;

import java.util.Timer;
import java.util.TimerTask;

public class MessageTimer extends TimerTask {
	public String timerID;
	private String channel;
	private String message;
	public long delay;
	private Timer timer;
	
	/**
	 * Creates a new MessageTimer instance with a provided channel, id, messsage and delay time
	 * @param channel The channel this timer belongs to, used for identifying which channel to send the message to
	 * @param id The id for this timer, used for the purposes of editing and removing
	 * @param message The message to send to the channel
	 * @param delay The delay, in seconds, for the timertask
	 */
	public MessageTimer(String channel, String id, String message, long delay) {
		this.channel = channel;
		this.timerID = id;
		this.message = message;
		if(timer == null) {
			timer = new Timer(id, true);
		}
		timer.schedule(this, delay * 1000, delay * 1000);
	}
	
	public void stop() {
		this.timer.cancel();
	}
	
	@Override
	public void run() {
		MessageInfo info = new MessageInfo(channel, "", message, "", "", 0);
		Kdkbot.instance.sendMessage(channel, MessageParser.parseMessage(message, info));
	}
	
	public String toString() {
		return this.timerID + "|" + this.delay + "|" + this.message;
	}
}
