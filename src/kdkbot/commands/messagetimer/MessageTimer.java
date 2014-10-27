package kdkbot.commands.messagetimer;

import kdkbot.commands.Command;
import java.util.Timer;
import java.util.TimerTask;

public class MessageTimer extends TimerTask {
	private String timerID;
	private String message;
	private java.util.Timer timer;
	
	public MessageTimer(String id, String message) {
		this.timerID = id;
		this.message = message;
		this.timer = new Timer(id, true);
	}
	
	public MessageTimer(String id, String message, Timer timer) {
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
