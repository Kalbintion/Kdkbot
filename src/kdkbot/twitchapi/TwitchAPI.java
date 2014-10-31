package kdkbot.twitchapi;

public class TwitchAPI {
	private static final String URL_TWITCH = "https://api.twitch.tv/kraken";
	private static final String URL_TEAMS = URL_TWITCH + "/teams";
	private static final String URL_CHANNEL = URL_TWITCH + "/channel";
	private static final String URL_USER = URL_TWITCH + "/user";
	private static final String URL_INGEST = URL_TWITCH + "/ingests";
	private static final String URL_STREAMS = URL_TWITCH + "/streams";
	private static final String URL_SEARCH = URL_TWITCH + "/search";
	
	private Headers headers;
	private String clientID;
	
	public TwitchAPI() {
		this("");
	}
	
	public TwitchAPI(String clientID) {
		this.clientID = clientID;
		
		headers.addHeader("Accept", "application/vnd.twitchtv.v2+json");
		if(!clientID.isEmpty())
			headers.addHeader("Client-ID", clientID);
	}
}
