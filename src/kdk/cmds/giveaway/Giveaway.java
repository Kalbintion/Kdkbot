package kdk.cmds.giveaway;

import java.util.ArrayList;
import java.util.Random;

import kdk.Bot;
import kdk.MessageInfo;
import kdk.cmds.Command;
import kdk.filemanager.Config;

public class Giveaway extends Command {
	private String channel;
	private String triggerWord = null;
	private boolean started = false;
	private ArrayList<String> entries = new ArrayList<String>();
	private ArrayList<String> pastPicked = new ArrayList<String>();
	@SuppressWarnings("unused")
	private Config entrants = new Config();
	
	public Giveaway(String channel) {
		this.setTrigger("giveaway");
		this.setAvailability(true);
		this.setPermissionLevel(3);
		this.channel = channel;
	}
	
	public void executeCommand(MessageInfo info) {
		String[] args = info.getSegments();
		String subCmd = "";

		if(args.length == 1) { subCmd = "status"; }	else { subCmd = args[1].toLowerCase(); }
		
		switch(subCmd) {
			case "start":
				// giveaway start <triggerword>
				if(args.length < 3) {
					Bot.inst.sendChanMessageTrans(channel, "giveaway.started.failed");
				} else {
					start(args[2]);
					Bot.inst.sendChanMessageTrans(channel, "giveaway.started", args[2]);
				}
				break;
			case "stop":
				// giveaway stop
				if(hasStarted()) {
					Bot.inst.sendChanMessageTrans(channel, "giveaway.stopped");
					stop();
				} else {
					Bot.inst.sendChanMessageTrans(channel, "giveaway.hasntStarted");
				}
				break;
			case "count":
				if(hasStarted()) {
					Bot.inst.sendChanMessageTrans(channel, "giveaway.entered.count", numberOfEntries());
				} else {
					Bot.inst.sendChanMessageTrans(channel, "giveaway.hasntStarted");
				}
				break;
			case "cancel":
				if(hasStarted()) {
					cancel();
				} else {
					Bot.inst.sendChanMessageTrans(channel, "giveaway.hasntStarted");
				}
				break;
			case "pick":
				// giveaway pick [n]
				if(hasStarted()) {
					if(args.length < 3) {
						Bot.inst.sendChanMessageTrans(channel, "giveaway.winner", pick());
					} else {
						// PickN
						try {
							int toPick = Integer.parseInt(args[2]);
							Bot.inst.sendChanMessageTrans(channel, "giveaway.winners", pickN(toPick));
						} catch (NumberFormatException e) {
							Bot.inst.sendChanMessageTrans(channel, "giveaway.badnumber", args[2]);
						}
					}
				} else {
					Bot.inst.sendChanMessageTrans(channel, "giveaway.hasntStarted");
				}
				break;
			case "pickr":
				// giveaway pickr [n]
				if(hasStarted()) {
					if(args.length < 3) {
						Bot.inst.sendChanMessageTrans(channel, "giveaway.winner", pick(true));
					} else {
						// PickN(true)
						try {
							int toPick = Integer.parseInt(args[2]);
							Bot.inst.sendChanMessageTrans(channel, "giveaway.winners", pickN(toPick, true));
						} catch (NumberFormatException e) {
							Bot.inst.sendChanMessageTrans(channel, "giveaway.badnumber", args[2]);
						}
					}
				} else {
					Bot.inst.sendChanMessageTrans(channel, "giveaway.hasntStarted");
				}
			case "reset":
				if(hasStarted()) {
					Bot.inst.sendChanMessageTrans(channel, "giveaway.reset.live");
					entries.clear();
				} else {
					Bot.inst.sendChanMessageTrans(channel, "giveaway.reset");
					triggerWord = null;
					entries.clear();
				}
				break;
			case "add":
				if(info.senderLevel >= 5) {
					if(hasStarted()) {
						Bot.inst.sendChanMessageTrans(channel, "giveaway.manual.add", args[2]);
						addEntry(args[2]);
					} else {
						Bot.inst.sendChanMessageTrans(channel, "giveaway.hasntStarted");
					}
				}
				break;
			case "remove":
				if(info.senderLevel >= 5) {
					if(hasStarted()) {
						Bot.inst.sendChanMessageTrans(channel, "giveaway.manual.del", args[2]);
						removeEntry(args[2]);
					} else {
						Bot.inst.sendChanMessageTrans(channel, "giveaway.hasntStarted");
					}
				}
				break;
			case "pause":
				if(hasStarted()) {
					Bot.inst.sendChanMessageTrans(channel, "giveaway.paused");;
					pause();
				} else {
					Bot.inst.sendChanMessageTrans(channel, "giveaway.hasntStarted");
				}
				break;
			case "resume":
				if(!hasStarted()) {
					Bot.inst.sendChanMessageTrans(channel, "giveaway.resume", this.triggerWord);
					resume();
				} else {
					Bot.inst.sendChanMessageTrans(channel, "giveaway.alreadyStarted");
				}
				break;
			case "status":
				if(hasStarted()) {
					Bot.inst.sendChanMessageTrans(channel, "giveaway.status", this.triggerWord, this.numberOfEntries());
				} else {
					Bot.inst.sendChanMessageTrans(channel, "giveaway.hasntStarted");
				}
				break;
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
	 * Removes a username from the entries pool
	 * @param username The username to remove
	 */
	public void removeEntry(String username) {
		entries.remove(username);
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
		return pickN(1, removePicked);
	}
	
	/**
	 * Picks people from the giveaway pool until the number provided has been reached.
	 * @param number The number to pick. If 0 will return an empty string.
	 * @return The usernames, separated by a comma and a space, that are picked.
	 */
	public String pickN(int number) {
		return pickN(number, false);
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
	
	/**
	 * Stops giveaway from receiving additional entries and returns a winner
	 * @return A string containing the name of the user who won the giveaway
	 */
	public String stopAndPick() {
		if(hasStarted()) {
			stop();
			return pick();
		} else {
			return null;
		}
	}
	
	/**
	 * Picks a winner and writes it to a temporary file ('./cfg/#channel/giveaway.temp')
	 */
	public void pickWrite() {
		pickWrite(false);
	}
	
	/**
	 * Picks a winner and writes it to a temporary file ('./cfg/#channel/giveaway.temp')
	 * @param removePicked Removes picked winner from being picked again if true
	 */
	public void pickWrite(boolean removePicked) {
		pickNWrite(1, removePicked);
	}
	
	/**
	 * Picks a winner and writes it to a temporary file ('./cfg/#channel/giveaway.temp')
	 */
	public void pickNWrite() {
		pickNWrite(1, false);
	}
	
	/**
	 * Picks N winner(s) and writes it to a temporary file ('./cfg/#channel/giveaway.temp')
	 * @param number The number of entrants to select
	 */
	public void pickNWrite(int number) {
		pickNWrite(number, false);
	}
	
	/**
	 * Picks N winner(s) and writes it to a temporary file ('./cfg/#channel/giveaway.temp')
	 * @param number The number of entrants to select
	 * @param removePicked Removes picked winner from being picked again if true
	 */
	public void pickNWrite(int number, boolean removePicked) {
		
	}
	
	/**
	 * Pauses the giveaway from receicing additional entries
	 */
	public void pause() {
		this.started = false;
	}
	
	/**
	 * Resumes a giveaway
	 */
	public void resume() {
		this.started = true;
	}
	
	/**
	 * Stops a giveaway from receiving additional entries, does not clear entrant or past list
	 */
	public void stop() {
		this.started = false;
	}
	
	/**
	 * Starts a giveaway with no keyword and clears previous entries and past winners from prior giveaways
	 */
	public void start() {
		start("");
	}
	
	/**
	 * Starts a giveaway with provided keyword and clears previous entries and past winners from prior giveaways
	 * @param keyword
	 */
	public void start(String keyword) {
		this.started = true;
		this.triggerWord = keyword;
		clear();
	}
	
	/**
	 * Clears entrants and past picked entrants from being in the giveaway
	 */
	public void clear() {
		this.entries.clear();
		this.pastPicked.clear();
	}
	
	/**
	 * Cancels a giveaway from receiving additional entrants and clears entrant and past picked lists
	 */
	public void cancel() {
		this.started = false;
		clear();
	}
}
