package kdkbot.webinterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Webpage {
	public static String getWebpageContents(String url) {
		try {
			return getWebpageContents(new URL(url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	public static String getWebpageContents(URL url) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(url.openStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		StringBuilder contents = new StringBuilder();
		
		String nextLine;
		try {
			if(in != null) {
				while((nextLine = in.readLine()) != null) {
					contents.append(nextLine);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return contents.toString();
	}
	
	public static String getWebpageTitle(String url) {
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			Element ele = doc.select("title").get(0);
			return ele.text();
		} catch (IOException e) {
			return "";
		}
	}
	
	public static String putWebpageContents(String url, Map<String, String> data) {
		Connection.Response response;
		try {
			response = Jsoup.connect(url).data(data).method(Connection.Method.PUT).execute();
			return response.toString();
		} catch (IOException e) {
			return "";
		}
	}
	
	public static String putWebpageContents(String url, Map<String, String> data, Map<String, String> headers) {
		try {
			Connection.Response response;
			Connection con = Jsoup.connect(url).data(data).method(Connection.Method.PUT);
			for(Map.Entry<String, String> entry : headers.entrySet()) {
				con.header(entry.getKey(), entry.getValue());
			}
			response = con.execute();
			return response.toString();
		} catch (IOException e) {
			return "";
		}
	}
}
