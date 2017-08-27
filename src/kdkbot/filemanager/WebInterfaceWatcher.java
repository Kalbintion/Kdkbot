package kdkbot.filemanager;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import kdkbot.Kdkbot;
import kdkbot.channel.Channel;

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
				
				if (kind == OVERFLOW || !fileName.toString().equalsIgnoreCase(this.fileName)) {
					continue;
				} else {
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
			boolean actuallyUpdated = false;
			contents = new String(Files.readAllBytes(Paths.get(this.path.toAbsolutePath() + "\\" + this.fileName)));
			String[] lines = contents.split("\r\n");
			for(int i = 0; i < lines.length; i++) {
				String[] parts = lines[i].split("=", 2);
				
				if(parts.length < 2) { continue; }
				
				actuallyUpdated = true;
				
				String channel = parts[0];
				String type = parts[1];
				
				Channel chan = Kdkbot.instance.getChannel(channel);
				if(chan == null && !type.equalsIgnoreCase("join") && !type.equalsIgnoreCase("leave")) { System.out.println("Couldnt find channel object for " + channel); continue; }
				switch(type) {
					case "channel":
						chan.reload();
						break;
					case "leave":
						Kdkbot.instance.exitChannel(channel);
						break;
					case "join":
						Kdkbot.instance.enterChannel(channel);
						break;
					case "cmds_cust":
						chan.commands.commandStrings.loadCommands();
						break;
					case "perms":
						chan.loadSenderRanks();
						break;
				}
			}
			
			if(actuallyUpdated) { // Prevents infinite update loop
				// Clear file contents after having gone through update process
				Files.write(Paths.get(this.path.toAbsolutePath() + "\\" + this.fileName), "".getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
