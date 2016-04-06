package kdkbot.urbanapi;

import java.io.IOException;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public final class UrbanAPI {	
	private static String baseURL = "http://www.urbandictionary.com/define.php?term=";
	public static String getTopDefinition(String lookup) {
		try {
			Document doc = Jsoup.connect(baseURL + lookup).get();
			Elements eles = doc.select(".def-panel");
			Element topDef = eles.get(0); // We know the 0th one is always the top definition
			Element meaning = topDef.select(".meaning").get(0);
			String def = meaning.text();
			return def;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
