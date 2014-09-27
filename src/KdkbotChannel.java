
/**
 * @deprecated Replaced by kdkbot.channel.Channel;
 * @author KDK
 *
 */
public class KdkbotChannel {
	private java.util.Date uptime;
	private java.util.Date curtime;
	private java.util.Date downtime;
    private static KdkbotCommands KDKCMD;
    private static Kdkbot KDKBOT;
    public String channel;
    private boolean repeatchat;
    private KdkbotConfig KDKCFG;
    
    public KdkbotChannel(Kdkbot instance, String channel) throws Exception {
    	this.KDKBOT = instance;
    	this.channel = channel;
    	KDKBOT.joinChannel(channel);
    	KDKCFG = new KdkbotConfig("cfg/channels/" + channel + ".cfg");
    	KDKCMD = new KdkbotCommands("cfg/channels/" + channel + ".cmd");
    }
    
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
    	if(KDKCMD.isCommand(message)) {
    		KDKBOT.sendMessage(channel, "The previous message was recognized as a command");
    	}
    	
    	if(repeatchat) { KDKBOT.sendMessage(channel, message); }
    	
    	if(message.equalsIgnoreCase("!repeatchat")) {
    		this.repeatchat = !this.repeatchat;
    		KDKBOT.sendMessage(channel, "Message repeat for " + channel + " is now " + this.repeatchat);
    	} else if(message.equalsIgnoreCase("!version")) {
    		KDKBOT.sendMessage(channel, "I am version " + KDKBOT.getLocalVersion());
    	}
    }
}
