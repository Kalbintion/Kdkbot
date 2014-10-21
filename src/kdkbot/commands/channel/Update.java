package kdkbot.commands.channel;

import kdkbot.Kdkbot;
import kdkbot.commands.*;

import java.net.*;
import java.util.ArrayList;

public class Update extends Command {
	// Standard Command variables
	private Kdkbot instance;
	private String trigger;
	private CommandPermissionLevel cpl = new CommandPermissionLevel();
	private boolean isAvailable;
	
	// Other command variables
	private URL url;
	private HttpURLConnection hurl;
	
	public void executeCommand(String channel, String sender, String login, String hostname, String message, ArrayList<String> additionalParams) {
		String[] args = message.split(" ");
		
		// additionalParams[0] should be oauth
		if(args[2].startsWith("set")) {
			try {
				url = new URL("api.twitch.tv/kraken/channels/" + channel);
				hurl.disconnect();
				hurl.setRequestMethod("PUT");
				hurl.setRequestProperty("Accept", "application/vnd.twitchtv.v2+json");
				hurl.setRequestProperty("Authorization", "OAuth " + additionalParams.get(0));
			} catch(ProtocolException e) {
				e.printStackTrace();				
			} catch(SecurityException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

}
