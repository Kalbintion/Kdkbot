package kdkbot.commands.custom;

import java.util.Random;

import kdkbot.Kdkbot;
import kdkbot.commands.*;

public class Custom implements Command {
	private boolean isAvailable;
	private CommandPermissionLevel cpl;
	private Kdkbot instance;
	private String trigger;
	private String message;
	
	@Override
	public void init(String trigger, Kdkbot instance) {
		this.trigger = trigger;
		this.instance = instance;
	}

	@Override
	public void executeCommand(String channel, String sender, String login, String hostname, String message, String[] additionalParams) {
		// args[0] is channel, args[1] is sender
		// args[2] is login, args[3] is hostname
		// args[4] is message
		instance.sendMessage(channel, parseParameters(channel, sender, login, hostname, message));
	}

	@Override
	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}

	@Override
	public String getTrigger() {
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

	@Override
	public boolean isAvailable() {
		return this.isAvailable;
	}

	@Override
	public void setAvailability(boolean available) {
		this.isAvailable = available;
	}

	@Override
	public int getPermissionLevel() {
		return this.cpl.getLevel();
	}

	@Override
	public void setPermissionLevel(int level) {
		this.cpl.setLevel(level);
	}
}
