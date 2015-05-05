package kdkbot.filemanager;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
	private Path filePath;
	
	/**
	 * Creates a new logger for the default file location.
	 */
	public Log() {
		String curDate = new SimpleDateFormat("yyyy_MM_dd").format(new Date());
		filePath = FileSystems.getDefault().getPath("./logs/" + curDate + ".log");
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
		this.filePath = Paths.get(filePath);
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
	 * Updates filePath object if time has changed from initial creation
	 */
	public void updateFileFromTime() {
		String curDate = new SimpleDateFormat("yyyy_MM_dd").format(new Date());
		this.filePath.getFileName().toString();
		if(!curDate.equalsIgnoreCase(curDate)) {
			this.filePath = Paths.get(this.filePath.getParent().toAbsolutePath().toString() + curDate + ".log");
		}
	}
	
	/**
	 * Logs a given line to this loggers file. Appends to end of file.
	 * @param line The line to log to this instances file.
	 */
	public void logln(String line) {
		// Open File
		BufferedWriter out;
		try {
			out = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(this.filePath.toAbsolutePath().toString(), true),
						StandardCharsets.UTF_8));
			out.write(line + "\r\n");
    		out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Logs a given set of text to the file. Does not automatically add a new line character
	 * @param text The text to log to this instances file.
	 */
	public void log(String text) {
		BufferedWriter out;
		try {
			out = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(this.filePath.toAbsolutePath().toString(), true),
						StandardCharsets.UTF_8));
			out.write(text);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
