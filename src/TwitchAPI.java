import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.*;

public class TwitchAPI {
	public static final String URL_TWITCH = "https://api.twitch.tv/kraken";
	private static final String URL_CHANNEL = "https://api.twitch.tv/kraken/channels/%CHAN%";
	private static final String URL_STREAMS = "https://api.twitch.tv/karekn/streams/%CHAN%";
	
	private HashMap<String, String> headers = new HashMap<String, String>();
	private String clientID;
	private String OAuth;
	private String channel;
	
	public TwitchAPI() {
		this("", "");
	}
	
	public TwitchAPI(String clientID, String OAuth) {
		this.clientID = clientID;
		this.OAuth = OAuth;
		
		this.headers.put("Accept", "application/vnd.twitchtv.v2+json");
		this.headers.put("Authorization", "OAuth " + OAuth);
		if(!clientID.isEmpty())
			this.headers.put("Client-ID", clientID);
	}
	
	public String getAccessToken(String code) {
		String post_info = "client_id=" + this.clientID;
		post_info += "&client_secret=";
		post_info += "&grant_type=authorization_code";
		post_info += "&redirect_uri=http://localhost";
		post_info += "&code=" + code;
		JsonObject response = sendRawData(URL_TWITCH + "/oauth2/token", "POST", post_info);
		return response.get("access_token").toString();
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
	
	public HttpURLConnection createConnection(String urlString, String method) {
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod(method);
			Iterator<Entry<String, String>> headerIter = headers.entrySet().iterator();
			while(headerIter.hasNext()) {
				Map.Entry<String, String> pairs = headerIter.next();
				conn.setRequestProperty(pairs.getKey().toString(), pairs.getValue().toString());
			}

			return conn;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public JsonObject getRawData(String urlString, String method) {
		try {
			HttpURLConnection conn = createConnection(urlString, method);
			
			BufferedReader connReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder connBuilder = new StringBuilder();
			String line;
			
			while((line = connReader.readLine()) != null) {
				connBuilder.append(line + "\n");
			}
			
			connReader.close();
			
			JsonParser parser = new JsonParser();
			JsonObject res = (JsonObject) parser.parse(connBuilder.toString());
			
			return res;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public JsonObject sendRawData(String urlString, String method, String data) {
		try {
			HttpURLConnection conn = createConnection(urlString, method);
			
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(data);
			dos.flush();
			dos.close();
			
			BufferedReader connReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder connBuilder = new StringBuilder();
			String line;
			
			while((line = connReader.readLine()) != null) {
				connBuilder.append(line + "\n");
			}
			
			conn.setRequestProperty("Content-Type", "application/json");
			conn.addRequestProperty("Content-Length", "" + Integer.toString(data.length()));
			
			connReader.close();
			
			JsonParser parser = new JsonParser();
			JsonObject res = (JsonObject) parser.parse(connBuilder.toString());
			
			return res;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Retrieves a channels Json Object
	 * @param channel The channel to look-up
	 * @return Json Object containing a particular channels information
	 */
	public JsonObject getChannelInfo(String channel) {
		try {
			URL url = new URL(URL_CHANNEL.replace("%CHAN%", validateChannel(channel)));
			return getRawData(url.toString(), "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Sets a specific channels info according to the Twitch API
	 * @param channel The channel to send an update for
	 * @param info The JsonObject containing the new info
	 * @return a Json response object in regards to the sent request
	 */
	public JsonObject setChannelInfo(String channel, JsonObject info) {
		try {
			URL url = new URL(URL_CHANNEL.replace("%CHAN%", validateChannel(channel)));
			
			JsonParser parser = new JsonParser();
			JsonObject res = (JsonObject) parser.parse(sendRawData(url.toString(), "PUT", info.toString()).toString());
			
			return res;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns a given channel property from a channel
	 * @param channel The channel to look up for.
	 * @param property The name of the property to get result for
	 * @return The resulting string of a given channels property
	 */
	public JsonElement getChannelProperty(String channel, String property) {
		JsonObject info = getChannelInfo(channel);
		if(info.has(property)) {
			return getChannelInfo(channel).get(property);
		} else {
			return null;
		}
	}
	
	/**
	 * Sends an API call to update a channel property
	 * @param channel The channel to update
	 * @param property The name of the property to change
	 * @param value The new value for the given property
	 * @return A JsonObject, in string form, or an error message, depending on success
	 */
	public String setChannelProperty(String channel, String property, String value) {
		JsonObject chanInfo = new JsonObject();
		JsonObject chanData = new JsonObject();
		chanData.addProperty(property, value);
		chanInfo.add("channel", chanData);
		JsonObject res = setChannelInfo(channel, chanInfo);
		
		if(res==null)
			return "Error getting response object.";
		return res.toString();
	}
	
	public HashMap<String, String> getHeaders() {
		return this.headers;
	}
	
	public String validateChannel(String channel) {
		return channel.replace("#", "");
	}
	
	/**
	 * Checks to see if a given channel is currently streaming
	 * @param channel The channel to look-up
	 * @return True if the channel is streaming, false otherwise
	 */
	public boolean isChannelStreaming(String channel) {
		JsonObject res = getRawData(URL_STREAMS.replace("%CHAN%", channel), "GET");
		if(res.has("stream")) {
			if(res.get("stream") != null) {
				return true;
			}
		}
		return false;
	}
}
