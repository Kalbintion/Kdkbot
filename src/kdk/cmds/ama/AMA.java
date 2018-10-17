package kdk.cmds.ama;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kdk.Bot;
import kdk.MessageInfo;
import kdk.cmds.*;
import kdk.filemanager.Config;

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
						Bot.inst.sendChanMessageTrans(info.channel, "ama.gotQuestion", args[2], question);
					} else {
						Bot.inst.sendChanMessageTrans(info.channel, "ama.dneQuestion", args[2]);
					}
				} catch(NumberFormatException e) {
					Bot.inst.sendChanMessageTrans(info.channel, "ama.exNumberFormat");
				} catch(IndexOutOfBoundsException e) {
					Bot.inst.sendChanMessageTrans(info.channel, "ama.exIndexOutOfBounds");
				}
				break;
			case "add":
				questions.add(info.message.substring("ama add ".length()));
				Bot.inst.sendChanMessageTrans(channel, "ama.addedQuestion");
				saveQuestions();
				break;
			case "remove":
				questions.remove(args[1]);
				Bot.inst.sendChanMessageTrans(channel, "ama.delQuestion", args[2]);
				break;
			case "save":
				this.saveQuestions();
				Bot.inst.sendChanMessageTrans(channel, "ama.save");
				break;
			case "next":
				if(questions.size() > 0) {
					String question = this.questions.get(curIndex++);
					Bot.inst.sendChanMessageTrans(channel, "ama.next", question);
				} else {
					Bot.inst.sendChanMessageTrans(channel, "ama.nextFail");
				}
				break;
			case "index":
				if(args.length < 3) {
					Bot.inst.sendChanMessageTrans(channel, "ama.cur.index", this.curIndex);
				} else {
					this.curIndex = Integer.parseInt(args[2]);
					Bot.inst.sendChanMessageTrans(channel, "ama.set.index", args[2]);
				}
				break;
			case "repeat":
				if(questions.size() > 0) {
					String question = this.questions.get(--curIndex);
					curIndex++;
					Bot.inst.sendChanMessageTrans(channel, "ama.repeat", question);
				} else {
					Bot.inst.sendChanMessageTrans(channel, "ama.nextFail");
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
			if(questions.size() > 0) {
				// Clear existing questions on loading
				questions.clear();
			}
			
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
