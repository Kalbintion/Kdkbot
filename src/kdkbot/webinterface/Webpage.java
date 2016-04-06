package kdkbot.webinterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

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
}
