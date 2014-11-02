package kdkbot.twitchapi;

import java.util.HashMap;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

public class TwitchAPI {
	private static final String URL_TWITCH = "https://api.twitch.tv/kraken";
	private static final String URL_TEAMS = URL_TWITCH + "/teams";
	private static final String URL_CHANNEL = URL_TWITCH + "/channel";
	private static final String URL_USER = URL_TWITCH + "/user";
	private static final String URL_INGEST = URL_TWITCH + "/ingests";
	private static final String URL_STREAMS = URL_TWITCH + "/streams";
	private static final String URL_SEARCH = URL_TWITCH + "/search";
		
	private HashMap<String, String> headers;
	private String clientID;
	private String channel;
	
	public class RequestType {
		public String GET_BLOCKS = "/users/:login/blocks";
		public String PUT_BLOCKS = "/users/:user/blocks/:target";
		public String DEL_BLOCKS = "/users/:user/blocks/:target";
		public String CHANNELS = "/channels/";
		public String TEAMS = "/teams";
	}
	
	public TwitchAPI() {
		this("");
	}
	
	public TwitchAPI(String clientID) {
		this.clientID = clientID;
		
		this.headers.put("Accept", "application/vnd.twitchtv.v2+json");
		if(!clientID.isEmpty())
			this.headers.put("Client-ID", clientID);
	}
	
	public JSONObject sendRequest(RequestType type, String channel) {
		this.channel = channel;
		return sendRequest(type);
	}
	
	public JSONObject sendRequest(RequestType type) {
		URLConnection conn;
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}

		
		return new JSONObject();
	}
	
	public void addHeader(String name, String value) {
		this.headers.put(name, value);
	}
	
	public void removeHeader(String name) {
		this.headers.remove(name);
	}
}
