package kdkbot.api.twitch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import kdkbot.Kdkbot;

public class APIv5 {
	private static String URL_BASE = "https://api.twitch.tv/kraken/";
	private static String URL_CHANNELS = URL_BASE + "channels/";
	private static String URL_CHANNEL_EDITORS = "/editors";
	private static String URL_STREAMS = URL_BASE + "streams/";
	private static String URL_USERS = URL_BASE + "users/";
	
	private static String URL_BASE_TMI = "https://tmi.twitch.tv/";
	private static String URL_TMI_HOSTS = URL_BASE_TMI + "hosts?include_logins=1&host=";
	
	private static String HEADER_ACCEPT = "application/vnd.twitchtv.v5+json";
	private static String HEADER_ACCEPT_NAME = "Accept";
	private static String HEADER_AUTH_NAME = "Authorization";
	private static String HEADER_CLIENT_ID_NAME = "Client-ID";
	
	
	public static String getChannelObjectId(String clientID, String channelID) {
		return getResponseId(clientID, URL_CHANNELS + channelID, "GET");
	}
	
	/**
	 * Gets the JSON object returned by twitch for a particular channel, note that an OAuth token is required.
	 * @param token The OAuth token used to authenticate
	 * @param channel The channel to retrieve the object for
	 * @return A string representation of the JSON object returned by the twitch servers
	 */
	public static String getChannelObject(String token, String channelID) {
		return getResponse(token, URL_CHANNELS + channelID, "GET");
	}
	
	
	public static String getChannelGameId(String clientID, String channelID) {
		String res = getChannelObjectId(clientID, channelID);
		JsonParser parser = new JsonParser();
		try {
			JsonObject jObj = parser.parse(res).getAsJsonObject();
			return jObj.get("game").toString();
		} catch(NullPointerException e) {
			return "";
		}
	}
	
	/**
	 * Gets the game name returned by twitch for a particular channel, note that an OAuth token is required.
	 * @param token The OAuth token used to authenticate
	 * @param channel The channel to retrieve the game for
	 * @return An encapsulated string containing the title of the game
	 */
	public static String getChannelGame(String token, String channelID) {
		String res = getChannelObject(token, channelID);
		JsonParser parser = new JsonParser();
		try {
			JsonObject jObj = parser.parse(res).getAsJsonObject();
			return jObj.get("game").toString();
		} catch(NullPointerException e) {
			return "";
		}
	}
	
	
	public static String getChannelStatusId(String clientID, String channelID) {
		String res = getChannelObjectId(clientID, channelID);
		JsonParser parser = new JsonParser();
		try {
			JsonObject jObj = parser.parse(res).getAsJsonObject();
			return jObj.get("status").toString();
		} catch(NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * Gets the title, or status, of a stream returned by twitch for a particular channel, note that an OAuth token is required.
	 * @param token The OAuth token used to authenticate
	 * @param channel The channel to retrieve the status for
	 * @return An encapsulated string containing the status of the stream
	 */
	public static String getChannelStatus(String token, String channelID) {
		String res = getChannelObject(token, channelID);
		JsonParser parser = new JsonParser();
		try {
			JsonObject jObj = parser.parse(res).getAsJsonObject();
			return jObj.get("status").toString();
		} catch(NullPointerException e) {
			return null;
		}
	}
	
	public static String getHostTarget(String clientId, String channelID) {
		String res = getResponseId(clientId, URL_TMI_HOSTS + channelID, "GET");
		JsonParser parser = new JsonParser();
		try {
			JsonObject jObj = parser.parse(res).getAsJsonObject();
			JsonArray jHosts = jObj.get("hosts").getAsJsonArray();
			JsonObject firstHost = jHosts.get(0).getAsJsonObject();
			return firstHost.get("target_display_name").toString();
		} catch(NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * Gets the stream object returned by twitch for a particular channel, note that a Client ID is required.
	 * @param clientID The client id used to authenticate with the twitch servers.
	 * @param channel The channel to retrieve the object for
	 * @return A string representation of the JSON object returned by the twitch servers
	 */
	public static String getStreamObject(String clientID, String channelID) {
		return getResponseId(clientID, URL_STREAMS + channelID, "GET");
	}
	
	/**
	 * Gets the stream uptime returned by twitch for a particular channel, note that a Client ID is required.
	 * @param clientID The client id used to authenticate with the twitch servers
	 * @param channel The channel to retrieve the object for
	 * @return The amount of time a stream has been live, in the format of nDnHnMnS where n is a number, or null in the event it was unable to find the information
	 */
	public static String getStreamUptime(String clientID, String channelID) {
		String res = getStreamObject(clientID, channelID);
		JsonParser parser = new JsonParser();
		try {
			JsonObject jObj = parser.parse(res).getAsJsonObject();
			// 2017-04-26T15:22:37Z
			DateFormat dFormat = new SimpleDateFormat("'\"'yyyy-MM-dd'T'HH:mm:ss'Z\"'");
			dFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			
			DateFormat dFormat2 = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy");
			dFormat2.setTimeZone(TimeZone.getTimeZone("CST"));
			
			JsonObject stream = jObj.get("stream").getAsJsonObject();
			String created_at = stream.get("created_at").toString();

			Date dStart = dFormat.parse(created_at);
			Date dNow = dFormat2.parse(new Date().toString());
			dNow.toString();

			long dDiff = dNow.getTime() - dStart.getTime();

			long dSec = dDiff / 1000 % 60;
			long dMin = dDiff / 1000 / 60 % 60;
			long dHour = dDiff / 1000 / 60 / 60 % 60;
			long dDay = dDiff / 1000 / 60 / 60 / 24 % 24;
			
			String out = "";
			if(dDay > 0) { out = dDay + "D "; }
			if(dHour > 0 || dDay > 0) { out = dHour + "H "; }
			if(dMin > 0 || dHour > 0 || dDay > 0) { out += dMin + "M "; }
			if(dSec > 0 || dMin > 0 || dHour > 0 || dDay > 0) { out += dSec + "S"; }
			
			return out;
		} catch(ParseException | NullPointerException | IllegalStateException e) {
			return null;
		}
	}
	
	/**
	 * Gets the amount of viewers of a particular channel, provided by the twitch servers.
	 * @param clientID The client id used to authenticate with the twitch servers
	 * @param channel The channel to retrieve the amount of viewers for
	 * @return A string representation of the number of viewers retrieved, or null in the event it was unable to find the information
	 */
	public static String getStreamViewers(String clientID, String channelID) {
		String res = getStreamObject(clientID, channelID);
		JsonParser parser = new JsonParser();
		try {
			JsonObject jObj = parser.parse(res).getAsJsonObject();
			return jObj.get("stream").getAsJsonObject().get("viewers").toString();
		} catch(NullPointerException | IllegalStateException e) {
			return null;
		}
	}

	public static boolean setChannelObject(String token, String channelID, String data) {
		return setResponse(token, URL_CHANNELS + channelID, "PUT", data);
	}
	
	public static boolean setChannelGame(String token, String channelID, String newGame) {
		return setChannelObject(token, channelID, "channel[game]=" + newGame.replace(" ", "+").replace("&", "%26"));
	}
	
	public static boolean setChannelStatus(String token, String channelID, String newTitle) {
		return setChannelObject(token, channelID, "channel[status]=" + newTitle.replace(" ", "+").replace("&", "%26"));
	}
	
	public static boolean isEditorOf(String token, String channelID) {
		String res = getResponse(token, URL_CHANNELS + channelID + URL_CHANNEL_EDITORS, "GET");
		return res.contains("\"" + Kdkbot.instance.getName() + "\"");
	}
	
	public static boolean isStreamerLive(String clientID, String channelID) {
		String res = getStreamObject(clientID, channelID);
		JsonParser parser = new JsonParser();
		JsonObject jObj = parser.parse(res).getAsJsonObject();
		if(jObj.get("stream") == null || jObj.get("stream").toString().toLowerCase().contains("null")) { return false; } else { return true; }
	}
	
	public static String getResponse(String token, String sURL, String requestMethod) {
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(HEADER_ACCEPT_NAME, HEADER_ACCEPT);
		headers.put(HEADER_AUTH_NAME, "OAuth " + token);
		
		return getResponse(headers, sURL, requestMethod);
	}
	
	public static String getResponseId(String clientID, String sURL, String requestMethod) {
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(HEADER_ACCEPT_NAME, HEADER_ACCEPT);
		headers.put(HEADER_CLIENT_ID_NAME, clientID);
		
		return getResponse(headers, sURL, requestMethod);
	}

	public static boolean setResponse(String token, String sURL, String requestMethod, String data) {
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(HEADER_ACCEPT_NAME, HEADER_ACCEPT);
		headers.put(HEADER_AUTH_NAME, "OAuth " + token);
		headers.put("Content-Length", String.valueOf(data.length()));
		headers.put("Content-Type",  "application/x-www-form-urlencoded");
		
		return setResponse(headers, sURL, requestMethod, data);
	}
	
	public static String getResponse(HashMap<String, String> headers, String sURL, String requestMethod) {
		HttpURLConnection hc;
		try {
			URL addr = new URL(sURL);
			hc = (HttpURLConnection) addr.openConnection();
			
	        hc.setDoOutput(true);
	        hc.setDoInput(true);
	        hc.setUseCaches(false);
	        
    		Iterator<Map.Entry<String, String>> it = headers.entrySet().iterator();
    		while(it.hasNext()) {
    			Map.Entry<String, String> pair = it.next();
    			hc.setRequestProperty(pair.getKey(), pair.getValue());
    		}
            hc.setRequestMethod(requestMethod);
            hc.connect();
            
            BufferedReader r = new BufferedReader(new InputStreamReader(hc.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
		} catch (IOException e) {
			return null;
		}
	}
	
	public static boolean setResponse(HashMap<String, String> headers, String sURL, String requestMethod, String data) {
		HttpURLConnection hc;
		try {
			URL address = new URL(sURL);
			
			hc = (HttpURLConnection) address.openConnection();
			hc.setDoOutput(true);
			hc.setDoInput(true);
	        hc.setUseCaches(false);
	        
	        Iterator<Map.Entry<String, String>> it = headers.entrySet().iterator();
    		while(it.hasNext()) {
    			Map.Entry<String, String> pair = it.next();
    			hc.setRequestProperty(pair.getKey(), pair.getValue());
    		}
			hc.setRequestMethod(requestMethod);
			
			OutputStreamWriter out = new OutputStreamWriter(hc.getOutputStream());
			out.write(data);
			out.close();
			
			hc.connect();

			if(hc.getResponseCode() == 400) {
				return false;
			} else {
				return true;
			}
		} catch(IOException e) {
			return false;
		}
	}
	
	/**
	 * Gets a users id based on their login name
	 * @param clientID The client id to use for authentication
	 * @param username The username to look up their ID for
	 * @return The ID, in string form, of the resulted look-up. If user wasn't found, returns null.
	 */
	public static String getUserID(String clientID, String username) {
		try {
			String res = getResponseId(clientID, URL_USERS + "?login=" + username, "GET");
			JsonParser parser = new JsonParser();
			JsonObject jObj = parser.parse(res).getAsJsonObject();
			JsonArray jUsers = jObj.get("users").getAsJsonArray();
			JsonObject jFirst = jUsers.get(0).getAsJsonObject();
			
			return jFirst.get("_id").toString().replace("\"", "");
		} catch(NullPointerException e) {
			return null;
		}
	}
}
