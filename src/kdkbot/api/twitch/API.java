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
import java.util.TimeZone;

import kdkbot.Kdkbot;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class API {
	private static String URL_BASE = "https://api.twitch.tv/kraken/";
	private static String URL_CHANNELS = URL_BASE + "channels/";
	private static String URL_CHANNEL = URL_BASE + "channel/";
	private static String URL_CHANNEL_EDITORS = "/editors";
	private static String URL_STREAMS = URL_BASE + "streams/";
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
	
	public static String getStreamObject(String token, String channel) {
		return getResponse(token, URL_STREAMS + channel.replace("#", ""), "GET");
	}
	
	public static String getStreamUptime(String token, String channel) {
		String res = getStreamObject(token, channel);
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
			
			String out = dDay + "D" + dHour + "H" + dMin + "M" + dSec + "S";
			return out;
		} catch(ParseException | NullPointerException | IllegalStateException e) {
			return null;
		}
	}
	
	public static String getStreamViewers(String token, String channel) {
		String res = getStreamObject(token, channel);
		JsonParser parser = new JsonParser();
		try {
			JsonObject jObj = parser.parse(res).getAsJsonObject();
			return jObj.get("stream").getAsJsonObject().get("viewers").toString();
		} catch(NullPointerException | IllegalStateException e) {
			return null;
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
	
	public static boolean isEditorOf(String token, String channel) {
		String res = getResponse(token, URL_CHANNELS + channel.replace("#", "") + URL_CHANNEL_EDITORS, "GET");
		return res.contains("\"" + Kdkbot.instance.getName() + "\"");
	}
	
	public static String getResponse(String token, String sURL, String requestMethod) {
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

	public static boolean setResponse(String token, String sURL, String requestMethod, String data) {
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
