package kdk.filemanager;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Settings {
	private Path _file = null;
	private HashMap<String, String> contents = new HashMap<String, String>();
	private boolean contentsModified = false;
	private String data = "";
	public boolean loaded = false;
	
	public Settings(File file) {
		this(file.getPath());
	}
	
	public Settings(Path path) {
		_file = path;
	}
	
	public Settings(String path) {
		this(FileSystems.getDefault().getPath(path));
	}
	
	public boolean load() {
		if(loaded) {
			data = "";
			contents.clear();
			loaded = false;
		}
		
		try {
			data = String.valueOf(Files.readAllBytes(_file));
			String[] lines = data.split("\n");
			for(String line : lines) {
				String[] pieces = line.split("=", 2);
				if(pieces.length == 1) {
					contents.put(pieces[0], null);
				} else {
					contents.put(pieces[0], pieces[1]);
				}
			}
		} catch (IOException e) {
			return false;
		}
		
		loaded = true;
		return true;
	}
	
	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean save() {
		if(contentsModified) {
			data = "";
			Iterator<?> it = contents.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry<String, String> pair = (Map.Entry<String, String>) it.next();
		        data += pair.getKey() + "=" + pair.getValue() + "\r\n";
		    }
		    contentsModified = false;
		}

		return write();
	}
	
	public boolean write() {
		if(contentsModified) { return false; }
		
		try {
			Files.write(_file, data.getBytes());
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public String getSetting(String key) {
		return contents.get(key);
	}
	
	public String setSetting(String key, String value) {
		contentsModified = true;
		return contents.put(key, value);
	}
	
	public String removeSetting(String key) {
		return contents.remove(key);
	}

}
