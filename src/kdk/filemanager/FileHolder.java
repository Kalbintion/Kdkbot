package kdk.filemanager;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileHolder {
	public Path _file;
	public String loadedContents;
	public boolean loaded = false;
	
	public FileHolder(String file) {
		this(FileSystems.getDefault().getPath(file));
	}
	
	public FileHolder(Path file) {
		_file = file;
	}
	
	public static String readOnce(Path file) {
		return null;
	}
	
	public static boolean writeOnce(Path file, String contents) {
		return false;
	}
	
	/**
	 * Reads and returns the contents of the file in this instance.
	 * @return A string containing the contents of the file, null if reading failed.
	 */
	public String read() {
		try {
			if(loaded) { loadedContents = null; loaded = false; }
			loadedContents = new String(Files.readAllBytes(_file));
			loaded = true;
			return loadedContents;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Reads the contents of the file in this instance..
	 * @return True if the load was sucessful, false otherwise
	 */
	public boolean load() {
		String res = read();
		if(res == null) { return false; } else { return true;}
	}
	
	public boolean write() {
		try {
			Files.write(_file, loadedContents.getBytes());
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public void setContent(String contents) {
		loadedContents = contents;
	}
}
