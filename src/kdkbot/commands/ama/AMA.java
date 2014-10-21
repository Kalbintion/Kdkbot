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

public class AMA extends Command {
	private String channel;
	private Config cfg;
	private ArrayList<String> questions;
	private int curIndex;
	
	public AMA(Kdkbot instance, String channel) {
		this.setBotInstance(instance);
		this.setTrigger("ama");
		this.setPermissionLevel(1);
		this.setAvailability(true);
		this.channel = channel;
		this.curIndex = 0;
		this.questions = new ArrayList<String>();
		try {
			this.cfg = new Config("./cfg/ama/" + channel + ".cfg", false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void executeCommand(String channel, String sender, String login, String hostname, String message, ArrayList<String> additionalParams) {
		String[] args = message.split(" ");
		
		// System.out.println("[DBG] [questions] [EXEC] Args[1] is " + args[1]);
		
		switch(args[1]) {
			case "get":
				try {
					// System.out.println("[DBG] [questions] [EXEC] Args[2] is " + args[2]);
					String question = questions.get(Integer.parseInt(args[2]));
					if(question != null) {
						this.getBotInstance().sendMessage(channel, "Question #" + args[2] + ": " + question);
					} else {
						this.getBotInstance().sendMessage(channel, "Question #" + args[2] + " does not exist.");
					}
				} catch(NumberFormatException e) {
					this.getBotInstance().sendMessage(channel, "That is not a number, therefore I cannot find the quote.");
				} catch(IndexOutOfBoundsException e) {
					this.getBotInstance().sendMessage(channel, "The requested quote cannot be found.");
				}
				break;
			case "add":
				questions.add(message.substring("ama add ".length()));
				this.getBotInstance().sendMessage(channel, "Question added.");
				saveQuestions();
				break;
			case "remove":
				questions.remove(args[1]);
				this.getBotInstance().sendMessage(channel, "Question #" + args[2] + " removed.");
				break;
			case "save":
				this.saveQuestions();
				this.getBotInstance().sendMessage(channel, "Manually saved question list for this channel.");
				break;
			case "next":
				if(questions.size() > 0) {
					String question = this.questions.get(curIndex++);
					this.getBotInstance().sendMessage(channel, "Question: " + question);
				} else {
					this.getBotInstance().sendMessage(channel, "No more questions left!");
				}
				break;
			case "index":
				if(args.length < 3) {
					this.getBotInstance().sendMessage(channel, "Current index set to " + this.curIndex);
				} else {
					this.curIndex = Integer.parseInt(args[2]);
					this.getBotInstance().sendMessage(channel, "Set current question index to " + args[2]);
				}
				break;
			case "repeat":
				if(questions.size() > 0) {
					String question = this.questions.get(--curIndex);
					curIndex++;
					this.getBotInstance().sendMessage(channel, "Question: " + question);
				} else {
					this.getBotInstance().sendMessage(channel, "No more questions left!");
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
