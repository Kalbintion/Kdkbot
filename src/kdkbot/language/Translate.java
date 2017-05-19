package kdkbot.language;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import kdkbot.filemanager.Config;

public class Translate {
	private static Path basePath = FileSystems.getDefault().getPath("./cfg/lang/");
	private static Config langConfig;
	private static String lastLanguage;
	
	public static String getTranslate(String key, String language) {
		if(!language.equalsIgnoreCase(lastLanguage)) {
			langConfig = new Config(basePath + language);
			lastLanguage = language;
		}
		return langConfig.getSetting(key);
	}
	
	public static void setTranslate(String key, String language, String text) {
		
	}
}
