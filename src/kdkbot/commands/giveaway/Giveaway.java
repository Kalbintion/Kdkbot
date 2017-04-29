package kdkbot.commands.giveaway;

import java.util.ArrayList;
import java.util.Random;

import kdkbot.Kdkbot;
import kdkbot.MessageInfo;
import kdkbot.commands.Command;

public class Giveaway extends Command {
	private String channel;
	private String triggerWord = null;
	private boolean started = false;
	private ArrayList<String> entries = new ArrayList<String>();
	private ArrayList<String> pastPicked = new ArrayList<String>();
	
	public Giveaway(String channel) {
		this.setTrigger("giveaway");
		this.setAvailability(true);
		this.setPermissionLevel(3);
		this.channel = channel;
	}
	
	public void executeCommand(MessageInfo info) {
		String[] args = info.getSegments();
		String subCmd = "";

		if(args.length == 1) { subCmd = "random"; }	else { subCmd = args[1].toLowerCase(); }
		
		switch(subCmd) {
			case "start":
				// giveaway start <triggerword>
				if(args.length < 3) {
					Kdkbot.instance.sendChanMessage(channel, "Couldn't start giveaway! No keyword provided.");
				} else {
					this.triggerWord = args[2];
					start();
					Kdkbot.instance.sendChanMessage(channel, "Giveaway started! Send a message with '" + triggerWord + "' to enter.");
				}
				break;
			case "stop":
				// giveaway stop
				if(hasStarted()) {
					Kdkbot.instance.sendChanMessage(channel, "Giveaway stopped!");
					stop();
				} else {
					Kdkbot.instance.sendChanMessage(channel, "Giveaway hasn't started.");
				}
				break;
			case "count":
				if(hasStarted()) {
					Kdkbot.instance.sendChanMessage(channel, "Giveaway currently has " + numberOfEntries() + " people entered!");
				} else {
					Kdkbot.instance.sendChanMessage(channel, "Giveaway hasn't started.");
				}
				break;
			case "pick":
				// giveaway pick [n]
				if(hasStarted()) {
					if(args.length < 3) {
						Kdkbot.instance.sendChanMessage(channel, "Giveaway winner: " + pick());
					} else {
						// PickN
						try {
							int toPick = Integer.parseInt(args[2]);
							Kdkbot.instance.sendChanMessage(channel, "Giveaway winners: " + pickN(toPick));
						} catch (NumberFormatException e) {
							Kdkbot.instance.sendChanMessage(channel, "Couldn't pick winners. Invalid number: " + args[2]);
						}
					}
				} else {
					Kdkbot.instance.sendChanMessage(channel, "Giveaway hasn't started.");
				}
				break;
			case "pickr":
				// giveaway pickr [n]
				if(hasStarted()) {
					if(args.length < 3) {
						Kdkbot.instance.sendChanMessage(channel, "Giveaway winner: " + pick(true));
					} else {
						// PickN(true)
						try {
							int toPick = Integer.parseInt(args[2]);
							Kdkbot.instance.sendChanMessage(channel, "Giveaway winners: " + pickN(toPick, true));
						} catch (NumberFormatException e) {
							Kdkbot.instance.sendChanMessage(channel,  "Couldn't pick winners. Invalid number: " + args[2]);
						}
					}
				} else {
					Kdkbot.instance.sendChanMessage(channel, "Giveaway hasn't started.");
				}
			case "reset":
				if(hasStarted()) {
					Kdkbot.instance.sendChanMessage(channel, "Reset live giveaway entrants. People will need to re-enter giveaway.");
					entries.clear();
				} else {
					Kdkbot.instance.sendChanMessage(channel, "Reset giveaway.");
					triggerWord = null;
					entries.clear();
				}
				break;
			case "add":
				if(info.senderLevel >= 5) {
					if(hasStarted()) {
						Kdkbot.instance.sendChanMessage(channel, "Manually added user: " + args[2] + " to the entrants list.");
						addEntry(args[2]);
					} else {
						Kdkbot.instance.sendChanMessage(channel, "Giveaway hasn't started.");
					}
				}
				break;
			case "remove":
				if(info.senderLevel >= 5) {
					if(hasStarted()) {
						Kdkbot.instance.sendChanMessage(channel,  "Manually removed user: " + args[2] + " from the entrants list.");
					} else {
						Kdkbot.instance.sendChanMessage(channel, "Giveaway hasn't started.");
					}
				}
				break;
			case "pause":
				if(hasStarted()) {
					Kdkbot.instance.sendChanMessage(channel, "Pausing the giveaway! Entries will no longer be allowed until resumed.");;
					pause();
				} else {
					Kdkbot.instance.sendChanMessage(channel, "Giveaway hasn't started.");
				}
		}
	}
	
	public String getTriggerWord() {
		return triggerWord;
	}
	
	public boolean hasStarted() {
		return this.started;
	}
	
	/**
	 * Adds a username to the entries pool
	 * @param username The username to enter
	 */
	public void addEntry(String username) {
		entries.add(username);
	}
	
	/**
	 * Determines if a particular user exists in the pool.
	 * @param username The username to look up
	 * @return True if the entries contain username, false if not found
	 */
	public boolean hasEntry(String username) {
		return entries.contains(username);
	}
	
	/**
	 * Returns the number of entries found in the giveaway pool.
	 * @return The number of entries found in the giveaway pool.
	 */
	public int numberOfEntries() {
		return entries.size();
	}
	
	/**
	 * Picks a random person from the giveaway pool.
	 * @return The username of the person picked.
	 */
	public String pick() {
		Random rng = new Random();
		int userNum = 0;
		do {
			userNum = rng.nextInt(this.entries.size());
		} while(entries.get(userNum) == null);
		return entries.get(userNum);
	}
	
	/**
	 * Picks a random person from the giveaway pool.
	 * @param removePicked If true, it will remove user from being picked again. if false, acts as if calling pick();
	 * @return The username of the person picked.
	 */
	public String pick(boolean removePicked) {
		String user = "";
		if(removePicked) {
			do {
				user = pick();
			} while(pastPicked.contains(user));
			pastPicked.add(user);
		} else {
			user = pick();
		}
		return user;
	}
	
	/**
	 * Picks people from the giveaway pool until the number provided has been reached.
	 * @param number The number to pick. If 0 will return an empty string.
	 * @return The usernames, separated by a comma and a space, that are picked.
	 */
	public String pickN(int number) {
		if(number <= 0) { return ""; }
		
		ArrayList<String> winners = new ArrayList<String>();
		if(number > entries.size()) {
			number = entries.size();
		}
		
		do {
			String pickedUser = pick();
			if(!winners.contains(pickedUser)) {
				winners.add(pickedUser);
			}
		} while(winners.size() < number);
		
		StringBuilder sb = new StringBuilder();
		for (String winner : winners) {
			sb.append(winner);
			sb.append(", ");
		}
		
		return sb.toString().substring(0, sb.toString().length() - 2);
	}
	
	/**
	 * Picks people from the giveaway pool until the number provided has been reached.
	 * @param number The number to pick. If 0 will return an empty string.
	 * @param removePicked If true, it will remove user from being picked again. if false, acts as if calling pickN(int);
	 * @return The usernames, separated by a comma and a space, that are picked.
	 */
	public String pickN(int number, boolean removePicked) {
		if(number <= 0) { return ""; }
		
		ArrayList<String> winners = new ArrayList<String>();
		if(number > entries.size()) {
			number = entries.size();
		}
		
		do {
			String pickedUser = pick();
			if(!winners.contains(pickedUser) && !pastPicked.contains(pickedUser)) {
				winners.add(pickedUser);
				pastPicked.add(pickedUser);
			}
		} while(winners.size() < number || (entries.size() - (winners.size() + pastPicked.size()) <= 0));
		
		StringBuilder sb = new StringBuilder();
		for(String winner : winners) {
			sb.append(winner);
			sb.append(", ");
		}
		
		return sb.toString().substring(0, sb.toString().length() - 2);
	}
	
	public String stopAndPick() {
		if(hasStarted()) {
			String user = pick();
			stop();
			return user;
		} else {
			return null;
		}
	}
	
	public void pause() {
		this.started = false;
	}
	
	public void resume() {
		this.started = true;
	}
	
	public void stop() {
		this.started = false;
	}
	
	public void start() {
		this.started = true;
		this.entries.clear();
		this.pastPicked.clear();
	}
}
