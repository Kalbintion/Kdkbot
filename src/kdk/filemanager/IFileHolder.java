package kdk.filemanager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public abstract class IFileHolder {
	public Path _file = null;
	public String contents = "";
	public boolean loaded = false;
	
	public boolean write() {
		if(loaded) {
			try {
				if(Files.write(_file, contents.getBytes()) != null) {
					return true;
				}
			} catch (IOException e) {
				return false;
			}
		}
		return false;
	}
	
	public boolean write(Byte[] bytes) {
		return false;
	}
	
	public boolean write(String data) {
		try {
			if(Files.write(_file, data.getBytes()) != null) { return true; } else { return false; }
		} catch (IOException e) {
			return false;
		}
	}
	
	public static boolean writeOnce(Path file, String contents) {
		return false;
	}
	
	public static boolean writeOnce(Path file, HashMap<String, String> contents) {
		return false;
	}

	public HashMap<String, String> readOnce(Path file) {
		return null;
	}
}
