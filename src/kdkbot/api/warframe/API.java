package kdkbot.api.warframe;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import kdkbot.commands.MessageParser;
import kdkbot.filemanager.Config;

public final class API {
	public static class Scaling {
		private static String FORMULA_HEALTH = "base*(1+(curLevel-baseLevel)^2*0.015)";
		private static String FORMULA_SHIELD = "base*(1+(curLevel-baseLevel)^2*0.0075)";
		private static String FORMULA_ARMOR = "base*(1+(curLevel-baseLevel)^1.75*0.005)";
		private static String FORMULA_DAMAGE = "base*(1+(curLevel-baseLevel)^1.55*0.015)";
		private static String FORMULA_AFFINITY = "base*(1+curLevel^0.5*0.1425)";
		
		/**
		 * Scales up the health value of an entity found in Warframe based on the formula:
		 * 	base*(1+(curLevel-baseLevel)^2*0.015)
		 * @param base The base stat
		 * @param baseLevel The base level
		 * @param curLevel The current level
		 * @return A string representing a double with the calculated value
		 */
		public static String scaleHealth(int base, int baseLevel, int curLevel) {
			return String.valueOf(MessageParser.eval(prepFormula(FORMULA_HEALTH, base, baseLevel, curLevel)));
		}
		
		/**
		 * Scales up the shield value of an entity found in Warframe based on the formula:
		 * 	base*(1+(curLevel-baseLevel)^2*0.0075)
		 * @param base The base stat
		 * @param baseLevel The base level
		 * @param curLevel The current level
		 * @return A string representing a double with the calculated value
		 */
		public static String scaleShield(int base, int baseLevel, int curLevel) {
			return String.valueOf(MessageParser.eval(prepFormula(FORMULA_SHIELD, base, baseLevel, curLevel)));
		}
		
		/**
		 * Scales up the armor value of an entity found in Warframe based on the formula:
		 * 	base*(1+(curLevel-baseLevel)^1.75*0.005)
		 * @param base The base stat
		 * @param baseLevel The base level
		 * @param curLevel The current level
		 * @return A string representing a double with the calculated value
		 */
		public static String scaleArmor(int base, int baseLevel, int curLevel) {
			return String.valueOf(MessageParser.eval(prepFormula(FORMULA_ARMOR, base, baseLevel, curLevel)));
		}
		
		/**
		 * Scales up the damage value of an entity found in Warframe based on the formula:
		 * 	base*(1+(curLevel-baseLevel)^1.55*0.015)
		 * @param base The base stat
		 * @param baseLevel The base level
		 * @param curLevel The current level
		 * @return A string representing a double with the calculated value
		 */
		public static String scaleDamage(int base, int baseLevel, int curLevel) {
			return String.valueOf(MessageParser.eval(prepFormula(FORMULA_DAMAGE, base, baseLevel, curLevel)));			
		}
		
		/**
		 * Scales up the damage value of an entity found in Warframe based on the formula:
		 * 	floor(base*(1+curLevel^0.5*0.1425))
		 * @param base The base stat
		 * @param baseLevel The base level
		 * @param curLevel The current level
		 * @return A string representing a double with the calculated value
		 */
		public static String scaleAffinity(int base, int baseLevel, int curLevel) {
			return String.valueOf(Math.floor(MessageParser.eval(prepFormula(FORMULA_AFFINITY, base, baseLevel, curLevel))));			
		}
		
		/**
		 * Preps one of the scaling formulas for use with Warframe values.
		 * @param formula The formula to modify with the values
		 * @param base The base stat
		 * @param baseLevel The base level
		 * @param curLevel The current level
		 * @return A string with the modification to the provided formula and values, to be parsed by an evaluator
		 */
		private static String prepFormula(String formula, int base, int baseLevel, int curLevel) {
			formula = formula.replaceAll("baseLevel", String.valueOf(baseLevel));
			formula = formula.replaceAll("base", String.valueOf(base));
			formula = formula.replaceAll("curLevel", String.valueOf(curLevel));
			return formula;
		}
	}
	
	public static class Wiki {
		private static String cfgBase = "/cfg/api/wf/";
		public static Config cfg = new Config(cfgBase + "info.cfg");
		public static String baseURL = "http://warframe.wikia.com/wiki/";
		
		private static String[] healthTypes = {"Cloned Flesh", "Machinery", "Flesh", "Robotic", "Infested", "Infested Flesh", "Fossilized", "Infested Sinew", "Flesh", "Object"};
		private static String[] armorTypes = {"Ferrite Armor", "Alloy Armor"};
		private static enum BOX_TYPES {
			TABLE, ASIDE;
		}
		
		private static EnemyStats cachedES;
		
		public static EnemyStats getEnemyStats(String enemyName) {
			if(cachedES != null) { return cachedES; } // Use cached result if it exists
			
			Element ret = getPageInfobox(enemyName);
			EnemyStats es = parseEnemyInfobox(ret);
			
			cachedES = es; // Cache result for repeated use, lowers response time and look-up costs
			
			return es;
		}
		
		public static EnemyStats parseEnemyInfobox(Element infobox) {
			EnemyStats es = new EnemyStats();
			es.name = infobox.child(0).child(0).html();
			
			Elements sections = infobox.getElementsByTag("section");
			// sections[0] = General data
			if(sections.size() < 3) { return null; }
			
			// General Information
			es.faction = sections.get(0).child(1).child(1).child(1).html();
			es.planet = sections.get(0).child(2).child(1).child(0).html();
			es.mission = sections.get(0).child(3).child(1).html();
			es.weapons = sections.get(0).child(4).child(1).child(0).html();
			
			// Statistics
			es.health = sections.get(1).child(1).child(1).html();
			es.healthType = sections.get(1).child(1).child(0).html();
			es.shield = sections.get(1).child(3).child(1).html();
			es.shieldType = sections.get(1).child(3).child(0).html();
			
			return es;
		}
		
		public static Element getStatFromBox(Element toSearch, String toFind) {
			Tag tag = toSearch.tag();
			Element ele = null;
			
			if(tag.getName().equalsIgnoreCase("table")) {
				// We are dealing with the table
				Elements lefts = toSearch.getElementsByClass("left");
				Elements rights = toSearch.getElementsByClass("right");
				
				for(int i = 0; i < lefts.size(); i++) {
					if (lefts.get(i).html().toString().toLowerCase().contains(toFind.toLowerCase())) {
						ele = rights.get(i);
						break;
					}
				}
				
			} else if(tag.getName().equalsIgnoreCase("aside")) {
				// We are dealing with an aside
				Elements labels = toSearch.getElementsByClass("pi-data-label");
				Elements values = toSearch.getElementsByClass("pi-data-value");
				
				// Remove values from list that have no label attached to them
				Iterator<Element> iter = values.iterator();
				while(iter.hasNext()) {
					Element temp = iter.next();
					if(temp.parent().children().size() < 2) {
						iter.remove();
					}
				}
				
				// Our two maps should line up now
				for(int i = 0; i < labels.size(); i++) {
					if (labels.get(i).html().toString().toLowerCase().contains(toFind.toLowerCase()))  {
						ele = values.get(i);
						break;
					}
				}
			}
	
	
			return ele;
		}
		
		public static Element getPageInfobox(String enemyName) {
			try {
				Document doc = Jsoup.connect(baseURL + enemyName).get();
				Elements asides = doc.getElementsByTag("aside");
				if(asides.size() < 1) { return null; }
				
				return asides.get(0);	// We grab the first infobox's <aside> tag
	
			} catch(IOException e) {
				return null;
			}
		}
		
		public static boolean hasSavedEnemyData(String enemyName) {
			return new File(cfgBase + "/data_enemy/" + enemyName).isFile();
		}
		
		public static boolean hasSavedWeaponData(String weaponName) {
			return new File(cfgBase + "/data_weapon/" + weaponName).isFile();
		}
		
		public static EnemyStats loadEnemyData(String enemyName) {
			EnemyStats es = new EnemyStats();
			
			return es;
		}
		
		public static WeaponStats loadWeaponData(String weaponName) {
			WeaponStats ws = new WeaponStats();
			
			return ws;
		}
		
		// ENEMYSTATS METHODS
		
		public static String getEnemyHealth(String enemyName) {
			return getEnemyStats(enemyName).health;
		}
		
		public static String getEnemyHealthType(String enemyName) {
			return getEnemyStats(enemyName).healthType;
		}
		
		public static String getEnemyArmor(String enemyName) {
			return getEnemyStats(enemyName).armor;
		}
		
		public static String getEnemyArmorType(String enemyName) {
			return getEnemyStats(enemyName).armorType;
		}
		
		public static String getEnemyName(String enemyName) {
			return getEnemyStats(enemyName).name;
		}
		
		public static String getEnemyFaction(String enemyName) {
			return getEnemyStats(enemyName).faction;
		}
		
		public static String getEnemyPlanet(String enemyName) {
			return getEnemyStats(enemyName).planet;
		}
		
		public static String getEnemyMission(String enemyName) {
			return getEnemyStats(enemyName).mission;
		}
		
		public static String getEnemyWeapons(String enemyName) {
			return getEnemyStats(enemyName).weapons;
		}
		
		public static String getEnemyAbilities(String enemyName) {
			return getEnemyStats(enemyName).abilities;
		}
	}
	
	/**Warframe.Market API Portion
	 * API URL FORMAT: http://warframe.market/api/get_orders/%ITEM_TYPE%/%ITEM_NAME%
	 * 
	 * NOTE: This API is *not* public and may change at any time with no backwards compatibility.
	 * 
	 * @author KDK
	 *
	 */
	public static class Market {

		// The types of "items" on the api system, these are derived from various look-ups on the site
		private static String[] itemTypes = {"Set", "Blueprint", "Mod", "Void Trader", "Void Relic", "Scene", "Arcane Enhancements"};
		
		// Base url to the API, found via site function "make_request()"
		private static String baseURL = "http://warframe.market/api/get_orders";
		
		/**
		 * Gets the average price of everyone marked as online for a particular item.
		 * @param lookupName The item name to look-up
		 * @return A string representing a float of the average price (in platinum) of the item
		 */
		public static String getAveragePrice(String lookupName) {
			return getSellStats(lookupName).get("avg").toString();
		}

		/**
		 * Gets all selling stats about a particular thing
		 * @param lookupName The item name to look-up
		 * @return a JsonObject containing fields for the min value, max value, avg value, the number of ppl selling, the number of them being sold and the name of the item in its proper format
		 */
		public static JsonObject getSellStats(String lookupName) {
			// document.getElementById("search-item");
			// document.getElementsByTagName("button")[0].click();
			JsonObject allPpl = getResults(camelCaseStr(lookupName));
			if (allPpl == null) { return null; }
			
			JsonArray onlinePpl = getOnlyOnline(allPpl);
			
			int runningTotal = 0;
			int lowestPrice = -1;
			int highestPrice = 0;
			int numberOfPeople = 0;
			int numberOfItems = 0;
			Iterator<JsonElement> iter = onlinePpl.iterator();
			while(iter.hasNext()) {
				JsonElement nxt = iter.next();
				JsonObject jObj = nxt.getAsJsonObject();
				int price = Integer.parseInt(jObj.get("price").toString());
				int count = Integer.parseInt(jObj.get("count").toString());
				
				if(lowestPrice == -1 || price < lowestPrice) {
					lowestPrice = price;
				}
				
				if(highestPrice < price) {
					highestPrice = price;
				}

				runningTotal += price;
				numberOfItems += count;
				numberOfPeople++;
			}
			
			JsonObject values = new JsonObject();
			values.addProperty("min", lowestPrice);
			values.addProperty("max", highestPrice);
			values.addProperty("ppl", numberOfPeople);
			values.addProperty("cnt", numberOfItems);
			values.addProperty("name", camelCaseStr(lookupName));
			if(numberOfPeople == 0) {
				values.addProperty("avg", 0);
			} else {
				values.addProperty("avg", runningTotal / numberOfPeople);
			}
			
			return values;
		}
		
		/**
		 * Gets the results of an item from the Warframe.Market API system
		 * @param lookupName The name to lookup
		 * @return a JsonObject containing a parsed API response
		 */
		private static JsonObject getResults(String lookupName) {
			JsonParser parser = new JsonParser();
			JsonObject jobj = null;
			
			try {
				for (String itemType : itemTypes) {
					Connection conn = Jsoup.connect(baseURL + "/"  + itemType + "/" + lookupName);
					Document doc;
					doc = conn.ignoreContentType(true).get();
					
					jobj = parser.parse(doc.body().html()).getAsJsonObject();
					
					if(!jobj.get("code").toString().equalsIgnoreCase("200")) { // If we arnt getting a 200 (OK) response code, it isnt the right itemType
						jobj = null;
					} else {
						break; // We found the right itemType lets return it
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			
			return jobj;
		}
		
		/**
		 * Filters out offline and online-on-site people from an API call
		 * @param toSearch The JsonObject containing the API response to filter from
		 * @return A JsonArray containing only people who are online
		 */
		private static JsonArray getOnlyOnline(JsonObject toSearch) {
			
			JsonArray jArr = toSearch.getAsJsonObject("response").getAsJsonArray("sell");
			
			Iterator<JsonElement> iter = jArr.iterator();

			while(iter.hasNext()) {
				JsonElement nxt = iter.next();
				
				JsonObject tjObj = nxt.getAsJsonObject();
				if(tjObj.get("online_ingame").toString().equalsIgnoreCase("false")) {
					iter.remove();
				}
			}
			
			return jArr;
		}
		
		/**
		 * Formats a string for Camel Case Styling, to be used within the warframe.market API
		 * @param toFormat The string to format
		 * @return A properly formatted string to be used with the API
		 */
		public static String camelCaseStr(String toFormat) {
			toFormat = toFormat.toLowerCase();
			
			String[] parts = toFormat.split(" ");
			toFormat = "";
			for(String part : parts) {
				int idx = 0;
				if(part.startsWith("(")) { idx++; } // (Veiled) case
				if(part.contains("-")) { // "Medi-Pet Ray" etc case
					String[] hyphenParts = part.split("-");
					part = "";
					for(String hyphenPart : hyphenParts) {
						part += hyphenPart.substring(0, 1).toUpperCase() + hyphenPart.substring(1) + "-";
					}
					
					part = part.substring(0, part.length() - 1); // Trim off excess hyphen
				}
				
				toFormat += part.substring(0, idx + 1).toUpperCase() + part.substring(idx + 1) + " ";
			}
			
			return toFormat.trim();
		}
	}
	
	
}
