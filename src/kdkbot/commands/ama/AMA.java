package kdkbot.commands.ama;

import kdkbot.Kdkbot;
import kdkbot.commands.*;
import kdkbot.filemanager.Config;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AMA implements Command {
	private String trigger = "ama";
	private Kdkbot instance;
	private boolean isAvailable;
	private String channel;
	private CommandPermissionLevel cpl = new CommandPermissionLevel();
	private Config cfg;
	private ArrayList<String> questions;
	private int curIndex;
	
	public AMA(Kdkbot instance, String channel) {
		this.cpl.setLevel(1);
		this.isAvailable = true;
		this.instance = instance;
		this.channel = channel;
		this.curIndex = 0;
		this.questions = new ArrayList<String>();
		try {
			this.cfg = new Config("./cfg/ama/" + channel + ".cfg", false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void executeCommand(String channel, String sender, String login, String hostname, String message, String[] additionalParams) {
		String[] args = message.split(" ");
		
		// System.out.println("[DBG] [questions] [EXEC] Args[1] is " + args[1]);
		
		switch(args[1]) {
			case "get":
				try {
					// System.out.println("[DBG] [questions] [EXEC] Args[2] is " + args[2]);
					String question = questions.get(Integer.parseInt(args[1]));
					if(question != null) {
						instance.sendMessage(channel, "Question #" + args[1] + ": " + question);
					} else {
						instance.sendMessage(channel, "Question #" + args[1] + " does not exist.");
					}
					
				} catch(NumberFormatException e) {
					this.instance.sendMessage(channel, "That is not a number, therefore I cannot find the quote.");
				} catch(IndexOutOfBoundsException e) {
					this.instance.sendMessage(channel, "The requested quote cannot be found.");
				}
				break;
			case "add":
				questions.add(message.substring("ama add ".length()));
				instance.sendMessage(channel, "Question added.");
				saveQuestions();
				break;
			case "remove":
				questions.remove(args[1]);
				instance.sendMessage(channel, "Question #" + args[2] + " removed.");
				break;
			case "save":
				this.saveQuestions();
				instance.sendMessage(channel, "Manually saved question list for this channel.");
				break;
			case "next":
				if(questions.size() > 0) {
					String question = this.questions.get(curIndex++);
					instance.sendMessage(channel, "Question: " + question);
				} else {
					instance.sendMessage(channel, "No more questions left!");
				}
				break;
			case "index":
				if(args.length < 3) {
					instance.sendMessage(channel, "Current index set to " + this.curIndex);
				} else {
					this.curIndex = Integer.parseInt(args[2]);
					instance.sendMessage(channel, "Set current question index to " + args[2]);
				}
				break;
			case "repeat":
				if(questions.size() > 0) {
					String question = this.questions.get(--curIndex);
					curIndex++;
					instance.sendMessage(channel, "Question: " + question);
				} else {
					instance.sendMessage(channel, "No more questions left!");
				}
				break;
		}
	}

	@Override
	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}

	@Override
	public String getTrigger() {
		return this.trigger;
	}

	@Override
	public void init(String trigger, Kdkbot instance) {
		this.trigger = trigger;
		this.instance = instance;
	}
	
	public boolean removeQuestion(int index) {
		try {
			questions.remove(Integer.toString(index));
		} catch(IndexOutOfBoundsException e) {
			return false;
		}
		return true;
	}
	
	public String getQuestion(int index) {
		return questions.get(index);
	}
	
	public void loadQuestions() {
		try {
			List<String> lines = cfg.getConfigContents();
			Iterator<String> lineItero = lines.iterator();
			while(lineItero.hasNext()) {
				String line = lineItero.next();

				questions.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	public void saveQuestions() {
		try {
			cfg.saveSettings(this.questions);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
