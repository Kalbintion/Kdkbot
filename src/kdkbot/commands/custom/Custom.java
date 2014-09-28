package kdkbot.commands.custom;

import java.util.Random;

import kdkbot.Kdkbot;
import kdkbot.commands.*;

public class Custom implements Command {
	private Kdkbot instance;
	private String trigger;
	private String message;
	
	@Override
	public void init(String trigger, Kdkbot instance) {
		this.trigger = trigger;
		this.instance = instance;
	}

	@Override
	public void executeCommand(String[] args) {
		
	}

	@Override
	public void setTrigger(String trigger) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTrigger() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Parses custom command parameters into their actual value
	 * @return
	 */
	public String parseParameters(String channel, String sender, String login, String hostname, String message) {
		String out = message.replace("(_SENDER_)", sender);
		out = out.replace("(_CHAN_)", channel);
		out = out.replace("(_HOST_)", hostname);
		out = out.replace("(_RAND_)", Integer.toString(new Random().nextInt()));
		
		return out;
	}
}
