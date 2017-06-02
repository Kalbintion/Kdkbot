package kdkbot.api.youtube;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public final class API {
	public static String baseURL = "https://www.youtube.com/watch?v=";
	
	public static String getVideoTitle(String url) {
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			Element ele = doc.select("title").get(0);
			return ele.text();
		} catch (IOException e) {
			return "Not a valid video link.";
		}
	}
	
	public static String getVideoTitleFromID(String id) {
		return getVideoTitle(baseURL + id);
	}
	
	public static String getNumberOfVideoViews(String id) {
		Document doc;
		try {
			doc = Jsoup.connect(baseURL + id).get();
			Element ele = doc.select("#watch7-views-info").get(0).child(0);
			return ele.html();
		} catch(IOException e) {
			return "Not a valid video link.";
		}
	}
}
