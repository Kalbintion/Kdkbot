package kdk.language;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import kdk.Bot;
import kdk.filemanager.Config;

/**
 * Handles methods for translating text found without the software into other languages
 * @author Kalbintion Dier Kier
 *
 */
public class Translate {
	private static Path basePath = FileSystems.getDefault().getPath("./cfg/lang/");
	private static Config langConfig;
	private static String lastLanguage;
	
	/**
	 * Retrieves a keys text with a given language name
	 * @param key The key to look-up
	 * @param language The language code (ie: enUS) to look for
	 * @return The string found with the provided key in the language requested
	 */
	public static String getTranslate(String key, String language) {
		Bot.instance.dbg.writeln("key: " + key + " - lang: " + language);
		
		if(!language.equalsIgnoreCase(lastLanguage)) {
			langConfig = new Config(basePath + "\\" + language + ".lang");
			langConfig.loadConfigContents();
			lastLanguage = language;
		}
		
		Bot.instance.dbg.writeln("cfgPath: " + basePath + "\\" + language + ".lang");
		return langConfig.getSetting(key);
	}
	
	/**
	 * Sets a keys text with a given language name
	 * @param key The key to look-up
	 * @param language The language code (ie: enUS) to look for
	 * @param text The text to set the key to
	 */
	public static void setTranslate(String key, String language, String text) {
		Bot.instance.dbg.writeln("key: " + key + " - lang: " + language);
		
		if(!language.equalsIgnoreCase(lastLanguage)) {
			langConfig = new Config(basePath + "\\" + language + ".lang");
			langConfig.loadConfigContents();
			lastLanguage = language;
		}
		
		langConfig.setSetting(key, text);
		langConfig.saveSettings();
	}
	
	/**
	 * Resets the last language code, permitting other methods to see the cache should be reloaded
	 */
	public static void resetLanguageCache() {
		lastLanguage = "";
	}
}
