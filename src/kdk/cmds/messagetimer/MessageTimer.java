package kdk.cmds.messagetimer;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import kdk.Bot;
import kdk.MessageInfo;
import kdk.channel.Channel;
import kdk.cmds.MessageParser;
import kdk.dataman.DBFetcher;

public class MessageTimer extends TimerTask {
	public String timerID;
	private String channel;
	public String message;
	public String flags;
	public Flags flagsVals;
	public long delay;
	private Timer timer;
	
	/**
	 * Creates a new MessageTimer instance with a provided channel, id, messsage and delay time
	 * @param channel The channel this timer belongs to, used for identifying which channel to send the message to
	 * @param id The id for this timer, used for the purposes of editing and removing
	 * @param message The message to send to the channel
	 * @param delay The delay, in seconds, for the timertask
	 * @param flags The flags to use for this message timer
	 */
	public MessageTimer(String channel, String id, String message, long delay, String flags) {
		this.channel = channel;
		this.timerID = id;
		this.message = message;
		this.delay = delay;
		this.flags = flags;
		this.flagsVals = parseFlags();
		if(timer == null) {
			timer = new Timer(id, true);
		}
		timer.schedule(this, delay * 1000, delay * 1000);
	}
	
	/**
	 * Creates a new MessageTimer instance with a provided channel, id, messsage and delay time
	 * @param channel The channel this timer belongs to, used for identifying which channel to send the message to
	 * @param id The id for this timer, used for the purposes of editing and removing
	 * @param message The message to send to the channel
	 * @param delay The delay, in seconds, for the timertask
	 */
	public MessageTimer(String channel, String id, String message, long delay) {
		this(channel, id, message, delay, "REQUIRES_LIVE+");
	}
	
	/**
	 * Stops this message timer
	 */
	public void stop() {
		this.timer.cancel();
	}
	
	/**
	 * Handles sending the message to the channel
	 */
	@Override
	public void run() {
		Bot.instance.dbg.writeln(this, "Running scheduled task id " + this.timerID + " for channel " + this.channel);
		
		MessageInfo info = new MessageInfo(channel, "", message, "", "", 0);
		Bot.instance.dbg.writeln(this, "Flags: " + this.flags);

		if (this.flagsVals.REQUIRES_MSG_COUNT && Integer.parseInt(this.flagsVals.REQUIRES_MSG_COUNT_AMT) > this.flagsVals.MSGES_SINCE_LAST_TRIGGER) {
			this.flagsVals.MSGES_SINCE_LAST_TRIGGER = 0; // Reset msges count, even if the timer didnt get to run
			Bot.instance.dbg.writeln(this, "Timer failed. Not enough messages. Channel: " + this.channel + ", id: " + this.timerID);
			return; // Not enough messages have been sent for this to trigger again
		}
		
		if (this.flagsVals.REQUIRES_LIVE) {
			if(!kdk.api.twitch.APIv5.isStreamerLive(Bot.instance.getClientID(), Bot.instance.getChannel(channel).getUserID())) {
				Bot.instance.dbg.writeln(this, "Timer failed. Streamer isn't live. Channel: " + this.channel + ", id: " + this.timerID);
				return; // Streamer isn't live, we arnt going to send message
			}
		}
		
		if (this.flagsVals.REQUIRES_GAME) {
			Channel chan = Bot.instance.getChannel(this.channel);
			String retrievedGame = kdk.api.twitch.APIv5.getChannelGame(DBFetcher.getTwitchOAuth(kdk.Bot.dbm), chan.getUserID()).replaceAll("\"", "");
			if(!this.flagsVals.REQUIRES_GAME_NAME.equalsIgnoreCase(retrievedGame)) {
				Bot.instance.dbg.writeln(this, "Timer failed. Game did not match. Match: " + retrievedGame + ", To: " + this.flagsVals.REQUIRES_GAME_NAME + ", Channel: " + this.channel + ", id: " + this.timerID);
				return;
			}
		}

		Bot.instance.dbg.writeln(this, "Triggered timer " + this.timerID + ", all conditions met");
		this.flagsVals.MSGES_SINCE_LAST_TRIGGER = 0; // Reset msges count, even if we're not using it
		Bot.instance.sendMessage(channel, MessageParser.parseMessage(message, info));
		
		if(flagsVals.RANDOM_MODIFIER) {
			Random rng = new Random();
			System.out.println("ID: " + timerID + ", RNG_MAX: " + flagsVals.RANDOM_MODIFIER_MAX);
			int rni = rng.nextInt(Integer.parseInt(flagsVals.RANDOM_MODIFIER_MAX));
			
			timer.schedule(this, (delay + rni) * 1000, delay * 1000);
		}
	}
	
	/**
	 * Compiles a string used for storing this message timer. Values are separated by a pipe (|) character.
	 * @return The compiled string separated by pipe (|) characters
	 */
	@Override
	public String toString() {
		return this.timerID + "|" + this.delay + "|" + this.flags + "|" + this.message;
	}
	
	public void incrementMessageAmount(int amount) {
		this.flagsVals.MSGES_SINCE_LAST_TRIGGER += amount;
	}
	
	public void incrementMessageAmount() {
		incrementMessageAmount(1);
	}
	
	/**
	 * Parses the flags value to set this message timers requirements
	 * @return A Flags class object representing the flags to be required
	 */
	private Flags parseFlags() {
		Flags flags = new Flags();
		String[] flag = this.flags.split("\\+");
		for(String flg : flag) {
			String[] parts = flg.split("=");
			if(parts.length <= 2 && parts.length >= 1) {
				switch(parts[0]) {
					case "REQUIRES_LIVE":
						flags.REQUIRES_LIVE = true;
						break;
					case "REQUIRES_MSG_COUNT":
						flags.REQUIRES_MSG_COUNT = true;
						flags.REQUIRES_MSG_COUNT_AMT = parts[1];
						break;
					case "RANDOM_MODIFIER":
						flags.RANDOM_MODIFIER = true;
						flags.RANDOM_MODIFIER_MAX = parts[1];
						break;
					case "REQUIRES_GAME":
						flags.REQUIRES_GAME = true;
						flags.REQUIRES_GAME_NAME = parts[1];
						break;
					case "REQUIRES_IN_TITLE":
						flags.REQUIRES_IN_TITLE = true;
						flags.REQUIRES_IN_TITLE_TEXT = parts[1];
						break;
				}
			} // Else we have an invalid flag setting
		}
		
		return flags;
	}
	
	class Flags {
		public boolean REQUIRES_LIVE = false;
		public boolean REQUIRES_MSG_COUNT = false;
		public String REQUIRES_MSG_COUNT_AMT = "0";
		public int MSGES_SINCE_LAST_TRIGGER = 0;
		public boolean RANDOM_MODIFIER = false;
		public String RANDOM_MODIFIER_MAX = "0";
		public boolean REQUIRES_GAME = false;
		public String REQUIRES_GAME_NAME = "";
		public boolean REQUIRES_IN_TITLE = false;
		public String REQUIRES_IN_TITLE_TEXT = "";
		
		@Override
		public String toString() {
			String out = "";
			if(REQUIRES_LIVE) {
				out += "REQUIRES_LIVE+";
			}
			
			if(REQUIRES_MSG_COUNT) {
				out += "REQUIRES_MSG_COUNT=" + REQUIRES_MSG_COUNT_AMT + "+";
			}
			
			if(RANDOM_MODIFIER) {
				out += "RANDOM_MODIFIER=" + RANDOM_MODIFIER_MAX + "+";
			}
			
			if(REQUIRES_GAME) {
				out += "REQUIRES_GAME=" + REQUIRES_GAME_NAME + "+";
			}
			
			if(REQUIRES_IN_TITLE) {
				out+= "REQUIRES_IN_TITLE=" + REQUIRES_IN_TITLE_TEXT + "+";
			}
			
			return out;
		}
		
		public Flags() {}
	}
}
