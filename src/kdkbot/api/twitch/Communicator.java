package kdkbot.api.twitch;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonObject;

public abstract class Communicator {
	public JsonObject getResponse(String url, String method, HashMap<String, String> headers, String body) {
		URL parsedURL = null;
		try {
			parsedURL = new URL(url);
		} catch(MalformedURLException e) {
			System.out.println(e.toString());
		}
		return getResponse(parsedURL, method, headers, body);
	}
	
	public JsonObject getResponse(URL url, String method, HashMap<String, String> headers, String body) {
		HttpURLConnection conn = createConnection(url.toString(), method, headers);
		
		DataOutputStream dos;
		try {
			dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(body);
			dos.flush();
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public String getResponseValue(JsonObject response, String name) {
		
		
		return "";
	}
	
	public HttpURLConnection createConnection(String url, String method, HashMap<String, String> headers) {
		try {
			URL parsedURL = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) parsedURL.openConnection();
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
}
