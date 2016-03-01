package kdkbot.filemanager;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import kdkbot.Kdkbot;

public class Log {
	private Path filePath;
	
	/**
	 * Creates a new logger for the default file location (based on settings.cfg value of "logChatLocation")
	 */
	public Log() {
		this(Paths.get(Kdkbot.instance.botCfg.getSetting("logChatLocation")));
	}
	
	/**
	 * Creates a new logger with a given location from a Path object
	 * @param filePath The target path location
	 */
	public Log(Path filePath) {
		this.filePath = filePath;
	}
	
	/**
	 * Creates a new logger with a given location from a String object
	 * @param filePath The target path location
	 */
	public Log(String filePath) {
		this(Paths.get(filePath));
	}
	
	/**
	 * Get this loggers file path, in a Path object
	 * @return The PATH object to the file this object points to
	 */
	public Path getFilePath() {
		return this.filePath;
	}
	
	/**
	 * Get this loggers file path, in a string format.
	 * @return The URI to the file this object points to
	 */
	public String getFileString() {
		return this.filePath.toString();
	}
	
	
	/**
	 * Logs a given line to this loggers file. Appends to end of file.
	 * @param line The line to log to this instances file.
	 */
	public void logln(String line) {
		this.log(line + "\r\n");
	}
	
	/**
	 * Logs a given set of text to the file. Does not automatically add a new line character
	 * @param text The text to log to this instances file.
	 */
	public void log(String text) {
		Path finalizedPath = Paths.get(this.filePath.toAbsolutePath().toString() + "\\" + getChannel(text) + "\\");
		verifyExists(finalizedPath);
		
		BufferedWriter out;
		try {
			out = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(finalizedPath.toAbsolutePath().toString() + "\\" + getCurrentDate() + ".log", true),
						StandardCharsets.UTF_8));
			out.write(text);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getChannel(String text) {
		String parts[] = text.split(" ");
		for(int i = 0; i < parts.length; i++) {
			if(parts[i].startsWith("#")) {
				// We can be sure this is the channel, since itll be the first we run into due to the format of the messages
				// unless its "###"
				if(parts[i].equalsIgnoreCase("###")) {
					return "internal";
				} else {
					return parts[i].substring(1).replace("\r", "").replace("\n", "");
				}
			}
		}
		
		return "internal";	// If we cannot for some reason find a channel name, the message may be a general message and we'll return it under "internal"
	}
	
	private String getCurrentDate() {
		return new SimpleDateFormat("yyyy_MM_dd").format(new Date());
	}
	
	/**
	 * Verifies the existence of the file at a given path, if it does not exist, it will
	 * automatically create it.
	 */
	public void verifyExists(Path file) {
		if(!Files.exists(file)) {
			try {
				Files.createDirectories(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
