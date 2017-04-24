package kdkbot.api.twitch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class API {
	private static String URL_BASE = "https://api.twitch.tv/kraken/";
	private static String URL_CHANNELS = URL_BASE + "channels/";
	private static String URL_CHANNEL = URL_BASE + "channel/";
	private static String HEADER_ACCEPT = "application/vnd.twitchtv.v3+json";
	private static String HEADER_ACCEPT_NAME = "Accept";
	private static String HEADER_AUTH_NAME = "Authorization";
	
	public static String getChannelObject(String token, String channel) {
		return getResponse(token, URL_CHANNELS + channel.replace("#",  ""), "GET");
	}
	
	public static String getChannelGame(String token, String channel) {
		String res = getChannelObject(token, channel);
		JsonParser parser = new JsonParser();
		try {
			JsonObject jObj = parser.parse(res).getAsJsonObject();
			return jObj.get("game").toString();
		} catch(NullPointerException e) {
			return "";
		}
	}
	
	public static String getChannelStatus(String token, String channel) {
		String res = getChannelObject(token, channel);
		JsonParser parser = new JsonParser();
		try {
			JsonObject jObj = parser.parse(res).getAsJsonObject();
			return jObj.get("status").toString();
		} catch(NullPointerException e) {
			return "";
		}
	}
	
	public static boolean setChannelObject(String token, String channel, String data) {
		return setResponse(token, URL_CHANNELS + channel.replace("#",  ""), "PUT", data);
	}
	
	public static boolean setChannelGame(String token, String channel, String newGame) {
		return setChannelObject(token, channel, "channel[game]=" + newGame.replace(" ", "+"));
	}
	
	public static boolean setChannelStatus(String token, String channel, String newTitle) {
		return setChannelObject(token, channel, "channel[status]=" + newTitle.replace(" ", "+"));
	}
	
	private static String getResponse(String token, String sURL, String requestMethod) {
		HttpURLConnection hc;
		 try {
	        URL address = new URL(sURL);
	       
			hc = (HttpURLConnection) address.openConnection();
	        hc.setDoOutput(true);
	        hc.setDoInput(true);
	        hc.setUseCaches(false);
            hc.setRequestProperty(HEADER_ACCEPT_NAME, HEADER_ACCEPT);
            hc.setRequestProperty(HEADER_AUTH_NAME, "OAuth " + token);
            hc.setRequestMethod(requestMethod);
            
            hc.connect();
            
            try {
                BufferedReader r = new BufferedReader(new InputStreamReader(hc.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            } catch(IOException e) {
            	return null;
            }
        } catch (IOException e) {
			return null;
		}
	}

	private static boolean setResponse(String token, String sURL, String requestMethod, String data) {
		HttpURLConnection hc;
		try {
			URL address = new URL(sURL + "/");
			
			hc = (HttpURLConnection) address.openConnection();
			hc.setDoOutput(true);
			hc.setDoInput(true);
	        hc.setUseCaches(false);
			hc.setRequestProperty(HEADER_ACCEPT_NAME, HEADER_ACCEPT);
			hc.setRequestProperty(HEADER_AUTH_NAME, "OAuth " + token);
			hc.setRequestProperty("Content-Length", String.valueOf(data.length()));
			hc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
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
}
