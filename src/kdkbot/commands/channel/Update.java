package kdkbot.commands.channel;

import kdkbot.Kdkbot;
import kdkbot.commands.*;
import java.net.*;

public class Update implements Command {
	// Standard Command variables
	private String trigger;
	private CommandPermissionLevel cpl;
	private boolean isAvailable;
	
	// Other command variables
	private URL url;
	private HttpURLConnection hurl;
	private Kdkbot instance;

	@Override
	public void init(String trigger, Kdkbot instance) {
		this.trigger = trigger;
		this.instance = instance;
	}
	
	@Override
	public void executeCommand(String channel, String sender, String login, String hostname, String message, String[] additionalParams) {
		String[] args = message.split(" ");
		
		// additionalParams[0] should be oauth
		if(args[2].startsWith("set")) {
			try {
				url = new URL("api.twitch.tv/kraken/channels/" + channel);
				hurl.disconnect();
				hurl.setRequestMethod("PUT");
				hurl.setRequestProperty("Accept", "application/vnd.twitchtv.v2+json");
				hurl.setRequestProperty("Authorization", "OAuth " + additionalParams[0]);
			} catch(ProtocolException e) {
				e.printStackTrace();				
			} catch(SecurityException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}

	@Override
	public String getTrigger() {
		return this.trigger;
	}

	@Override
	public boolean isAvailable() {
		return this.isAvailable;
	}

	@Override
	public void setAvailability(boolean available) {
		this.isAvailable = available;
	}

	@Override
	public int getPermissionLevel() {
		return this.cpl.getLevel();
	}

	@Override
	public void setPermissionLevel(int level) {
		this.cpl.setLevel(level);
	}
}
