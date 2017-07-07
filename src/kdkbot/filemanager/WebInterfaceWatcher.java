package kdkbot.filemanager;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static java.nio.file.StandardWatchEventKinds.*;

public class WebInterfaceWatcher {
	private WatchService watcher;
	private WatchKey key;
	private Path path;
	private String fileName;
	
	public WebInterfaceWatcher(Path path, String fileName) {
		try {
			this.fileName = fileName;
			this.path = path;
			watcher = FileSystems.getDefault().newWatchService();
			key = path.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		} catch(IOException e) {
			
		}
	}
	
	public WebInterfaceWatcher(String path, String fileName) {
		this(Paths.get(path), fileName);
	}
	
	public void watch() {
		while(true) {
			WatchKey key;
			try {
				key = watcher.take();
			} catch(InterruptedException ex) {
				return;
			}
			
			for(WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind kind = event.kind();
				
				WatchEvent<Path> ev = (WatchEvent<Path>) event;
				Path fileName = ev.context();
				
				System.out.println(kind.name() + ": " + fileName);
				
				if (kind == OVERFLOW || !fileName.toString().equalsIgnoreCase(this.fileName)) {
					continue;
				} else {
					System.out.println("FOR " + kind + ": " + fileName);
					parseFileEvents();
				}
			}
			
			boolean valid = key.reset();
			if(!valid) {
				break;
			}
		}
	}
	
	private void parseFileEvents() {
		// Read the file contents
			String contents;
			try {
				contents = new String(Files.readAllBytes(Paths.get(this.path.toAbsolutePath() + "\\" + this.fileName)));
				JsonParser parser = new JsonParser();
				JsonObject jObj = parser.parse(contents).getAsJsonObject();
				
				String channel = jObj.get("channel").toString();
				String type = jObj.get("updateType").toString();
				String targetFile = jObj.get("targetFile").toString();
				String targetContents = jObj.get("targetContents").toString();
				
			} catch (IOException e) {
				
			}
	}
}
