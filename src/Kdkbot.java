
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;

import org.jibble.pircbot.*;

public class Kdkbot extends PircBot {
	private String version = "0.1.0.17";
	
    public static Kdkbot KDKBOT;
    public boolean repeat_chat = false;
    private ArrayList<KdkbotChannel> KDKCHAN = new ArrayList<KdkbotChannel>();
    
    
    public Kdkbot() throws Exception {
    	KDKBOT = this;
    	KDKBOT.setVerbose(true);
        KDKBOT.setName("Kdkbot");
        KDKBOT.connect("irc.twitch.tv", 6667, "oauth:e0ez3wup3dyr3l5w7t2kv9zp6oe3re2");
        
        // Initialize folder hierarchy
        Path path = FileSystems.getDefault().getPath("cfg");
        if(!Files.exists(path)) { Files.createDirectory(path); }
        path = FileSystems.getDefault().getPath("cfg/channels");
        if(!Files.exists(path)) { Files.createDirectory(path); }
        
        // Join the channels
        String[] channels = {"#kalbintion", "#taitfox"};
        for (int i = 0; i < channels.length; i++) {
        	KDKCHAN.add(new KdkbotChannel(KDKBOT, channels[i]));
        }
    }
    
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
    	// Iterate through channel list and forward command handling to the individual channel
    	Iterator<KdkbotChannel> CHANITER = KDKCHAN.iterator();
    	KdkbotChannel cur;
    	while(CHANITER.hasNext()) {
    		cur = CHANITER.next();
    		System.out.println("DBG: Testing channel message against: " + cur.channel);
    		if(cur.channel.equals(channel)) {
    			System.out.println("DBG: Channel matched.");
    			cur.onMessage(channel,  sender,  login,  hostname,  message);
    			break;
    		}
    	}
	}
    
    public String getLocalVersion() {
    	return this.version;
    }
}