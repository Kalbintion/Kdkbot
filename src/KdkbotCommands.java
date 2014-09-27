import java.io.Reader;
import java.util.HashMap;
import org.jibble.pircbot.*;
import java.util.ArrayList;

import sun.security.ssl.Debug;

/**
 * @deprecated Replaced by kdkbot.commands.Commands;
 */
public class KdkbotCommands {
	// To be used in a <String CommandName, String CommandText> setup
	private HashMap<String, String> commands;
	private String commandKey = "!";
	private KdkbotConfig KDKCFG;
	
	public KdkbotCommands() {
		// Setup some basic commands.
		commands = new HashMap<String, String>();
		
		commands.put("chan quit", "KDK:FUNC_Chan_Quit");
		commands.put("chan join", "KDK:FUNC_Chan_Join");
		commands.put("counter new", "KDK:FUNC_Counter_New");
		commands.put("counter add", "KDK:FUNC_Counter_Add");
		commands.put("counter sub", "KDK:FUNC_Counter_Sub");
	}
	
	public KdkbotCommands(String filePath) throws Exception {
		KDKCFG = new KdkbotConfig(filePath);
		ArrayList<String> lines = KDKCFG.getFileLines();
		for(int i = 0, j = lines.size(); i < j; i++) {
			String[] tokens = getTokens(lines.get(i));
			commands.put(tokens[0], tokens[1]);
		}
	}
	
	public boolean isCommand(String message) {
		// Grab message tokens
		String[] tokens = getTokens(message);
		if(commands.containsKey(tokens[0])) { return true; }
		return false;
	}
	
	public String[] getTokens(String message) {
		String[] retVals = message.split(" ", 2);
		return retVals;
	}
}
