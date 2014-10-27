package kdkbot.commands.messagetimer;

import kdkbot.Kdkbot;
import kdkbot.commands.Command;
import java.util.Timer;
import java.util.TimerTask;

public class MessageTimer extends TimerTask {
	private String timerID;
	private String message;
	private java.util.Timer timer;
	private Kdkbot instance;
	
	public MessageTimer(Kdkbot instance) {
		this(instance, "", "");
	}
	
	public MessageTimer(Kdkbot instance, String id, String message) {
		this(instance, id, message, null);
	}
	
	public MessageTimer(Kdkbot instance, String id, String message, Timer timer) {
		this.instance = instance;
		this.timerID = id;
		this.message = message;
		this.timer = timer;
	}
	
	public void terminate() {
		this.timer.cancel();
	}
	
	@Override
	public void run() {
		
	}
}
