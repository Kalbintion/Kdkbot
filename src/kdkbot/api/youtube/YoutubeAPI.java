package kdkbot.api.youtube;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public final class YoutubeAPI {
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
}
