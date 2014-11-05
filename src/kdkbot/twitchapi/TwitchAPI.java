package kdkbot.twitchapi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

public class TwitchAPI {
	private static final String URL_TWITCH = "https://api.twitch.tv/kraken";
	private static final String URL_CHANNEL = "https://api.twitch.tv/kraken/channels/%CHAN%/";
	
	private HashMap<String, String> headers;
	private String clientID;
	private String OAuth;
	private String channel;
	
	public TwitchAPI() {
		this("", "");
	}
	
	public TwitchAPI(String clientID, String OAuth) {
		this.clientID = clientID;
		
		this.headers.put("Accept", "application/vnd.twitchtv.v2+json");
		if(!clientID.isEmpty())
			this.headers.put("Client-ID", clientID);
	}
	
	public void setOAuth(String OAuth) {
		this.OAuth = OAuth;
	}
	
	public void addHeader(String name, String value) {
		this.headers.put(name, value);
	}
	
	public void removeHeader(String name) {
		this.headers.remove(name);
	}
	
	public void updateGame(String channel, String game) {
		try {
			URL url = new URL(URL_CHANNEL.replace("%CHAN%", channel));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateTitle(String channel, String title) {
		try {
			URL url = new URL(URL_CHANNEL.replace("%CHAN%", channel));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getGame(String channel) {
		try {
			URL url = new URL(URL_CHANNEL.replace("%CHAN%", channel));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			Iterator headerIter = headers.entrySet().iterator();
			while(headerIter.hasNext()) {
				Map.Entry pairs = (Map.Entry) headerIter.next();
				conn.addRequestProperty(pairs.getKey().toString(), pairs.getValue().toString());
			}
			BufferedReader connReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder connBuilder = new StringBuilder();
			String line;
			
			while((line = connReader.readLine()) != null) {
				connBuilder.append(line + "\n");
			}
			
			connReader.close();
			
			return connBuilder.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	public String getTitle(String channel) {
		return "";
	}
}
