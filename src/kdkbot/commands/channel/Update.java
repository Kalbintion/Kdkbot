package kdkbot.commands.channel;

import kdkbot.Kdkbot;
import kdkbot.commands.*;
import java.net.*;

public class Update implements Command {
	private String trigger;
	private CommandPermissionLevel cpl;
	private URL url;
	private HttpURLConnection hurl;
	private Kdkbot instance;

	@Override
	public void init(String trigger, Kdkbot instance) {
		this.trigger = trigger;
		this.instance = instance;
	}
	
	@Override
	public void executeCommand(String[] args) {
		// args[0] should be oauth
		// args[1] should be channel
		if(args[2].startsWith("set")) {
			try {
				url = new URL("api.twitch.tv/kraken/channels/" + args[1]);
				hurl.disconnect();
				hurl.setRequestMethod("PUT");
				hurl.setRequestProperty("Accept", "application/vnd.twitchtv.v2+json");
				hurl.setRequestProperty("Authorization", "OAuth " + args[0]);
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
}
