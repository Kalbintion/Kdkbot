package kdkbot.commands.ama;

import kdkbot.Kdkbot;
import kdkbot.MessageInfo;
import kdkbot.commands.*;
import kdkbot.filemanager.Config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AMA extends Command {
	private String channel;
	private Config cfg;
	private ArrayList<String> questions;
	private int curIndex;
	
	public AMA(String channel) {
		this.setTrigger("ama");
		this.setPermissionLevel(1);
		this.setAvailability(true);
		this.channel = channel;
		this.curIndex = 0;
		this.questions = new ArrayList<String>();
		try {
			this.cfg = new Config("./cfg/" + channel + "/ama.cfg");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void executeCommand(MessageInfo info) {
		String[] args = info.message.split(" ");
				
		switch(args[1]) {
			case "get":
				try {
					String question = questions.get(Integer.parseInt(args[2]));
					if(question != null) {
						Kdkbot.instance.sendMessage(channel, "Question #" + args[2] + ": " + question);
					} else {
						Kdkbot.instance.sendMessage(channel, "Question #" + args[2] + " does not exist.");
					}
				} catch(NumberFormatException e) {
					Kdkbot.instance.sendMessage(channel, "That is not a number, therefore I cannot find the quote.");
				} catch(IndexOutOfBoundsException e) {
					Kdkbot.instance.sendMessage(channel, "The requested quote cannot be found.");
				}
				break;
			case "add":
				questions.add(info.message.substring("ama add ".length()));
				Kdkbot.instance.sendMessage(channel, "Question added.");
				saveQuestions();
				break;
			case "remove":
				questions.remove(args[1]);
				Kdkbot.instance.sendMessage(channel, "Question #" + args[2] + " removed.");
				break;
			case "save":
				this.saveQuestions();
				Kdkbot.instance.sendMessage(channel, "Manually saved question list for this channel.");
				break;
			case "next":
				if(questions.size() > 0) {
					String question = this.questions.get(curIndex++);
					Kdkbot.instance.sendMessage(channel, "Question: " + question);
				} else {
					Kdkbot.instance.sendMessage(channel, "No more questions left!");
				}
				break;
			case "index":
				if(args.length < 3) {
					Kdkbot.instance.sendMessage(channel, "Current index set to " + this.curIndex);
				} else {
					this.curIndex = Integer.parseInt(args[2]);
					Kdkbot.instance.sendMessage(channel, "Set current question index to " + args[2]);
				}
				break;
			case "repeat":
				if(questions.size() > 0) {
					String question = this.questions.get(--curIndex);
					curIndex++;
					Kdkbot.instance.sendMessage(channel, "Question: " + question);
				} else {
					Kdkbot.instance.sendMessage(channel, "No more questions left!");
				}
				break;
		}
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
	
	public void saveQuestions() {
		try {
			cfg.saveSettings(this.questions);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
