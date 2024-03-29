package kdk.api.warframe;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

import kdk.cmds.MessageParser;
import kdk.filemanager.Config;

public final class API {
	/**
	 * Provides an interface to the (semi-)official warframe API
	 * NOTE: Uses file in ./cfg/lang/enWFUN.lang for purposes of 
	 * 		 internal unique names found on warframes API to human readable names
	 * @author KDK
	 *
	 */
	public static class Warframe {
		private static String BASE_URL = "http://content.warframe.com/dynamic/worldState.php";
		
		/**
		 * Returns the latest news items
		 * @return String array containing all news items and their links
		 */
		public static ArrayList<String> getAllEvents() {
			JsonObject allData = getData();
			JsonArray eventData = allData.get("Events").getAsJsonArray();
			
			JsonArray outData = new JsonArray();
			
			for(JsonElement jEvent : eventData) {
				JsonObject objOut = new JsonObject();
				JsonObject objEvent = jEvent.getAsJsonObject();
				JsonArray tjArr = objEvent.get("Messages").getAsJsonArray();
				
				// Search for 'en' code Messages label containing news item label in English
				for(JsonElement tjEle  : tjArr) {
					JsonObject tjoEle = tjEle.getAsJsonObject();
					if(tjoEle.get("LanguageCode").toString().replaceAll("\"", "").equalsIgnoreCase("en")) {
						objOut.add("Message", tjoEle.get("Message"));
					}
				}

				// Add news item link
				objOut.add("Link", objOut.get("Prop"));
				
				// Add news item date
				objOut.add("Date", objEvent.get("Date").getAsJsonObject().get("$date").getAsJsonObject().get("$numberLong"));
				
				if(objOut.has("Message")) {
					outData.add(objOut);
				}
			}
			
			ArrayList<String> out = new ArrayList<String>();
			
			for(JsonElement jOutData : outData) {
				out.add(jOutData.toString());
			}
			
			return out;
		}
		
		public static String getAllEventsReadable() {
			ArrayList<String> allEvents = getAllEvents();
			// Message, Link, Date
			JsonParser parser = new JsonParser();
			String out = "";
			
			
			Iterator<String> iter = allEvents.iterator();
			while(iter.hasNext()) {
				String nxt = iter.next();
				
				JsonObject jObj = parser.parse(nxt).getAsJsonObject();
				
				out += kdk.cmds.stats.Stats.unixToTimestamp(Long.parseLong(jObj.get("Date").toString().replaceAll("\"", "")) / 1000, "dd/MM") + ": ";
				out += jObj.get("Message").toString().replaceAll("\"", "") + " | ";
			
			}
			
			if(out.endsWith(" | ")) { out = out.substring(0, out.length() - " | ".length()); }
			
			return out;
		}
		
		/**
		 * Returns only the first news item
		 * @return String containing the latest news item and link to it
		 */
		public static String getFirstEvent() {
			ArrayList<String> allEvents = getAllEvents();
			String nxt = allEvents.get(0);
			JsonParser parser = new JsonParser();
			JsonObject jObj = parser.parse(nxt).getAsJsonObject();
			
			String out = "";
			out += kdk.cmds.stats.Stats.unixToTimestamp(Long.parseLong(jObj.get("Date").toString().replaceAll("\"", "")) / 1000, "dd/MM") + ": " + jObj.get("Message").toString().replaceAll("\"", "");
			
			return out;
		}
		
		public static ArrayList<String> getAllAlerts() {
			JsonObject allData = getData();
			JsonArray eventData = allData.get("Alerts").getAsJsonArray();
			
			JsonArray outData = new JsonArray();
			
			for(JsonElement jEvent : eventData) {
				JsonObject objOut = new JsonObject();
				JsonObject objEvent = jEvent.getAsJsonObject();

				objOut.add("Type", objEvent.get("MissionInfo").getAsJsonObject().get("missionType"));
				objOut.add("Faction", objEvent.get("MissionInfo").getAsJsonObject().get("faction"));
				objOut.add("Location", objEvent.get("MissionInfo").getAsJsonObject().get("location"));
				objOut.add("LevelMin", objEvent.get("MissionInfo").getAsJsonObject().get("minEnemyLevel"));
				objOut.add("LevelMax", objEvent.get("MissionInfo").getAsJsonObject().get("maxEnemyLevel"));
				objOut.add("CreditReward", objEvent.get("MissionInfo").getAsJsonObject().get("missionReward").getAsJsonObject().get("credits"));
				objOut.add("ItemRewards", objEvent.get("MissionInfo").getAsJsonObject().get("missionReward").getAsJsonObject().get("items"));
				
				outData.add(objOut);
			}
			
			ArrayList<String> out = new ArrayList<String>();
			
			for(JsonElement jOutData : outData) {
				out.add(jOutData.toString());
			}
			
			return out;
		}
		
		public static String getAllAlertsReadable() {
			ArrayList<String> allEvents = getAllAlerts();
			// Message, Link, Date
			JsonParser parser = new JsonParser();
			String out = "";
			
			
			Iterator<String> iter = allEvents.iterator();
			while(iter.hasNext()) {
				String nxt = iter.next();
				
				JsonObject jObj = parser.parse(nxt).getAsJsonObject();
				
				out += InternalTranslator.getSolNodeName(jObj.get("Location").toString().replaceAll("\"", "")) + " - ";
				out += InternalTranslator.convertTagMissionType(jObj.get("Type").toString().replaceAll("\"", "")) + " (";
				out += InternalTranslator.convertTagFactionType(jObj.get("Faction").toString().replaceAll("\"", "")) + ") ";
				out += jObj.get("LevelMin").toString().replaceAll("\"", "") + "-";
				out += jObj.get("LevelMax").toString().replaceAll("\"", "") + " | ";
			
			}
			
			if(out.endsWith(" | ")) { out = out.substring(0, out.length() - " | ".length()); }
			
			return out;
		}
		
		public static ArrayList<String> getAllSorties() {
			JsonObject allData = getData();
			JsonArray eventData = allData.get("Sorties").getAsJsonArray();
			
			JsonArray outData = new JsonArray();
			
			for(JsonElement jEvent : eventData) {
				JsonObject objOut = new JsonObject();
				JsonObject objEvent = jEvent.getAsJsonObject();

				int i = 1;
				JsonArray tjArr = objEvent.get("Variants").getAsJsonArray();
				for(JsonElement tjaEle : tjArr) {
					JsonObject tjaObj = tjaEle.getAsJsonObject();
					objOut.add("Mission" + i, tjaObj.get("missionType"));
					objOut.add("MissionMod" + i, tjaObj.get("modifierType"));
					objOut.add("Location" + i, tjaObj.get("node"));
					objOut.add("Tileset" + i, tjaObj.get("tileset"));					
					i++;
				}
				
				objOut.add("Boss", objEvent.get("Boss"));
				
				outData.add(objOut);
			}
			
			ArrayList<String> out = new ArrayList<String>();
			
			for(JsonElement jOutData : outData) {
				out.add(jOutData.toString());
			}
			
			return out;
		}
		
		public static String getAllSortiesReadable() {
			ArrayList<String> allEvents = getAllSorties();
			// Boss, MissionX, MissionModX, LocationX, TilesetX
			JsonParser parser = new JsonParser();
			String out = "";
			
			
			Iterator<String> iter = allEvents.iterator();
			while(iter.hasNext()) {
				String nxt = iter.next();
				
				JsonObject jObj = parser.parse(nxt).getAsJsonObject();
				
				out += InternalTranslator.convertTagSortieBoss(jObj.get("Boss").toString().replaceAll("\"", "")) + " | ";
				
				for(int i = 1; i <= 3; i++) {
					out += InternalTranslator.getSolNodeName(jObj.get("Location" + i).toString().replaceAll("\"", "")) + " - ";
					out += InternalTranslator.convertTagMissionType(jObj.get("Mission" + i).toString().replaceAll("\"", "")) + " (";
					out += InternalTranslator.convertTagModifierType(jObj.get("MissionMod" + i).toString().replaceAll("\"", "")) + ") | ";
				}
			}
			
			if(out.endsWith(" | ")) { out = out.substring(0, out.length() - " | ".length()); }
			
			return out;
		}
		
		public static ArrayList<String> getAllFissures() {
			JsonObject allData = getData();
			JsonArray eventData = allData.get("ActiveMissions").getAsJsonArray();
			
			JsonArray outData = new JsonArray();
			
			for(JsonElement jEvent : eventData) {
				JsonObject objOut = new JsonObject();
				JsonObject objEvent = jEvent.getAsJsonObject();

				objOut.add("Location", objEvent.get("Node"));
				objOut.add("Modifier", objEvent.get("Modifier"));

				long timeStart = Long.parseLong(objEvent.get("Activation").getAsJsonObject().get("$date").getAsJsonObject().get("$numberLong").getAsString().replaceAll("\"", ""));
				long timeEnd = Long.parseLong(objEvent.get("Expiry").getAsJsonObject().get("$date").getAsJsonObject().get("$numberLong").getAsString().replaceAll("\"", ""));
				long worldTime = getWorldTime();
				
				objOut.add("TimeStart", objEvent.get("Activation").getAsJsonObject().get("$date"));
				objOut.add("TimeEnd", objEvent.get("Expiry").getAsJsonObject().get("$date"));
				
				objOut.addProperty("TimeDuration", Math.floor((timeEnd - timeStart) / 1000));
				objOut.addProperty("TimeLeft", Math.floor((timeEnd / 1000) - worldTime));
				objOut.addProperty("TimeLeftRead", formatTime((long) Math.floor((timeEnd / 1000) - worldTime)));
				objOut.addProperty("TimeWorld", worldTime);
				
				outData.add(objOut);
			}
			
			ArrayList<String> out = new ArrayList<String>();
			
			for(JsonElement jOutData : outData) {
				out.add(jOutData.toString());
			}
			
			return out;
		}
		
		public static String getAllFissuresReadable() {
			ArrayList<String> allEvents = getAllFissures();
			// Location, Modifier
			JsonParser parser = new JsonParser();
			String out = "";
			
			
			Iterator<String> iter = allEvents.iterator();
			while(iter.hasNext()) {
				String nxt = iter.next();
				System.out.println("[DBG] [WF] [API] [WF] " + nxt);
				
				JsonObject jObj = parser.parse(nxt).getAsJsonObject();
				
				String solNode = jObj.get("Location").toString().replaceAll("\"", "");
				String nodeMod = InternalTranslator.convertVoidTierType(jObj.get("Modifier").toString().replaceAll("\"", ""));
				NodeData solNodeData = InternalTranslator.getSolNodeData(solNode);
				if(solNodeData == null) { solNodeData = new NodeData(solNode, solNode + " (" + solNode + ")", "Unknown"); }
				
				out += solNodeData.getNodeName() + " " + solNodeData.getMissionType() + " - " +	nodeMod + " - " + jObj.get("TimeLeftRead").toString().replace("\"", "") +" | ";
				
			}
			
			if(out.endsWith(" | ")) { out = out.substring(0, out.length() - " | ".length()); }
			
			return out;
		}
		
		public static String[] getAllInvasions() {
			JsonObject allData = getData();
			
			return null;
		}
		
		public static String getAllInvasionsReadable() {
			return null;
		}
		
		public static ArrayList<String> getBaroItems() {
			JsonObject allData = getData();
			JsonArray eventData = allData.get("VoidTraders").getAsJsonArray();
			
			JsonArray outData = new JsonArray();
			JsonArray manifestData = eventData.get(0).getAsJsonObject().get("Manifest").getAsJsonArray();
			
			for(JsonElement mItem : manifestData) {
				JsonObject outObj = new JsonObject();
				JsonObject mData = mItem.getAsJsonObject();
				
				outObj.addProperty("Item", mData.get("ItemType").toString());
				outObj.addProperty("Credits", mData.get("RegularPrice").toString());
				outObj.addProperty("Ducats", mData.get("PrimePrice").toString());
				
				outData.add(outObj);
			}
			
			ArrayList<String> out = new ArrayList<String>();
			
			for(JsonElement jOutData : outData) {
				out.add(jOutData.toString());
			}
			
			return out;
		}
		
		public static ArrayList<String> getBaroItemsReadable() {
			ArrayList<String> manifestData = getBaroItems();
			ArrayList<String> outData = new ArrayList<String>();
			JsonParser parser = new JsonParser();
			
			for(String item : manifestData) {
				JsonObject obj = parser.parse(item).getAsJsonObject();
				obj.addProperty("Item", InternalTranslator.getReadableName(obj.get("Item").toString().replaceAll("\\\\", "")));
				item = obj.toString();
				outData.add(item);
			}
			
			return outData;
		}
		
		/**
		 * Returns an estimated name for an item based on its internal name.
		 * @param internal_name The internal string to check.
		 * @return The guessed name from the internal name string
		 */
		public static String guessItemName(String internal_name) {
			System.out.println("Guessing: " + internal_name);
			if(internal_name.contains("/")) {
				String[] parts = internal_name.split("/");
				String ret = parts[parts.length-1]; // Grab last element of internal name
				ret = ret.replaceAll("([A-Z])", " $1"); // Camel Case Spacing
				return ret.trim(); // Return Trimmed
			} else {
				return internal_name;
			}
		}
		
		/**
		 * Returns Baro Ki'Teer's location
		 * @return
		 */
		public static String getBaroLocation() {
			JsonObject allData = getData();
			JsonArray bData = allData.get("VoidTraders").getAsJsonArray();
			
			String node = bData.get(0).getAsJsonObject().get("Node").toString().replaceAll("\"", "").replaceAll("HUB", "");
			
			return node;
		}
		
		/**
		 * Returns if Baro Ki'Teer is at a relay or not
		 * @return
		 */
		public static boolean isBaroHere() {
			JsonObject allData = getData();
			JsonArray bData = allData.get("VoidTraders").getAsJsonArray();
			JsonObject bd = bData.get(0).getAsJsonObject();
			if(bd.has("Manifest")) { return true; } else { return false; }
		}
		
		/**
		 * Returns Darvo's Daily Deal
		 * @return A string containing the JSON data
		 */
		public static String getDailyDeal() {
			JsonObject data = getDailyDealHelper();
			
			return data.toString();
		}
		
		/**
		 * Returns Darvo's Daily Deal in a human readable format
		 * @return A human readable string containing the daily deal from Darvo
		 */
		public static String getDailyDealReadable() {
			JsonObject data = getDailyDealHelper();
			return InternalTranslator.getReadableName(data.get("name").toString()) + " " + data.get("curPrice") + " [" + data.get("oriPrice") + "] " + data.get("curAmount") + "/" + data.get("oriAmount");
		}
		
		/**
		 * Retrieves Darvo's Daily Deal information for use in other functions
		 * @return A JSON Object contianing the information about Darvo's Daily Deal
		 */
		private static JsonObject getDailyDealHelper() {
			JsonObject allData = getData();
			JsonObject data = allData.get("DailyDeals").getAsJsonArray().get(0).getAsJsonObject();
			
			JsonObject retData = new JsonObject();
			retData.add("name", data.get("StoreItem"));
			retData.add("curPrice", data.get("Discount"));
			retData.add("oriPrice", data.get("OriginalPrice"));
			retData.add("curAmount", data.get("AmountSold"));
			retData.add("oriAmount", data.get("AmountTotal"));
			
			return retData;
		}
		
		public static String getWorldSeed() {
			JsonObject allData = getData();
			
			return allData.get("data").toString();
		}
		
		public static long getWorldTime() {
			JsonObject allData = getData();
			
			return Long.parseLong(allData.get("Time").toString().replaceAll("\"", ""));
		}
		
		public static JsonObject getData() {
			JsonParser parser = new JsonParser();
			JsonObject jobj = null;
			
			try {
				Connection conn = Jsoup.connect(BASE_URL);
				conn.timeout(10000);
				Document doc;
				doc = conn.ignoreContentType(true).get();
				
				jobj = parser.parse(doc.body().html()).getAsJsonObject();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}

			return jobj;
		}

		public static String formatTime(long seconds) {
			int sec = 0, min = 0, hr = 0;
			
			while(seconds > 0) {
				seconds--;
				sec++;
				if(sec >= 60) { sec = 0; min++; }
				if(min >= 60) { min = 0; hr++; }
			}
			
			String out = "";
			if( hr > 0) { out += hr + "h "; }
			if(min > 0) { out += min + "m "; }
			if(sec > 0) { out += sec + "s "; }
			return out;
		}
	}
	
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
	 * API URL FORMAT: https://api.warframe.market/v1/items/%ITEM_NAME%/orders
	 * 
	 * NOTE: The site API is *not* officially public and may change at any time with
	 *       no backwards compatibility. Possible breakings may happen on site updates
	 * 
	 * @author KDK
	 */
	public static class Market {

		// The types of "items" on the api system, these are derived from various look-ups on the site
		private static HashMap<String, String> renamedItems = new HashMap<String, String>();
		
		static {
			renamedItems.put("twin vipers wraith", "wraith twin vipers");
			renamedItems.put("scimitar avionics blueprint", "scimitar avionics");
			renamedItems.put("scimitar fuselage blueprint",  "scimitar fuselage");
			renamedItems.put("scimitar engines blueprint", "scimitar engines");
			renamedItems.put("primed pistol ammo mutation", "primed pistol mutation");
			renamedItems.put("primed rifle ammo mutation", "primed rifle mutation");
			renamedItems.put("primed shotgun ammo mutation", "primed shotgun mutation");
		}
		
		// Base url to the API, found via site function "make_request()"
		private static String baseURL = "https://api.warframe.market/v1/items";
		private static String postURL = "orders";
		
		/**
		 * Gets the average price of everyone marked as online for a particular item.
		 * @param lookupName The item name to look-up
		 * @return A string representing a float of the average price (in platinum) of the item
		 */
		public static String getAveragePrice(String lookupName) {
			return getSellStatsDynamic(lookupName).get("avg").toString();
		}
		
		/**
		 * Gets all selling stats about a particular thing
		 * @param lookupName The item name to look-up
		 * @return a JsonObject containing fields for the min value, max value, avg value, the number of ppl selling, the number of them being sold and the name of the item in its proper format
		 */
		public static JsonObject getSellStatsDynamic(String lookupName) {
			lookupName = itemRenamed(lookupName).toLowerCase();
			JsonObject allPpl = getResults(lookupName);
			if (allPpl == null) { return null; }
			
			JsonArray onlinePpl = getOnly(allPpl, "ingame", "sell");
			JsonArray offlinePpl = getOnly(allPpl, "offline", "sell");
			JsonArray sitePpl = getOnly(allPpl, "online", "sell");
			JsonArray toIterate = onlinePpl;
			
			if(onlinePpl.size() <= 0) {
				toIterate = sitePpl;
			}
			
			if(sitePpl.size() <= 0) {
				toIterate = offlinePpl;
			}
			
			JsonObject values = calculateStats(toIterate);

			values.addProperty("name", camelCaseStr(lookupName));
			values.addProperty("status", "Online");
			if(onlinePpl.size() <= 0) { values.addProperty("status", "Site"); }
			if(sitePpl.size() <= 0) { values.addProperty("status", "Offline"); }
			
			return values;
		}
		
		public static JsonObject getBuyStatsDynamic(String lookupName) {
			lookupName = itemRenamed(lookupName).toLowerCase();
			JsonObject allPpl = getResults(lookupName);
			if (allPpl == null) { return null; }
			
			JsonArray onlinePpl = getOnly(allPpl, "ingame", "buy");
			JsonArray offlinePpl = getOnly(allPpl, "offline", "buy");
			JsonArray sitePpl = getOnly(allPpl, "online", "buy");
			JsonArray toIterate = onlinePpl;
			
			if(onlinePpl.size() <= 0) {
				toIterate = sitePpl;
			}
			
			if(sitePpl.size() <= 0) {
				toIterate = offlinePpl;
			}
			
			JsonObject values = calculateStats(toIterate);

			values.addProperty("name", camelCaseStr(lookupName));
			values.addProperty("status", "Online");
			if(onlinePpl.size() <= 0) { values.addProperty("status", "Site"); }
			if(sitePpl.size() <= 0) { values.addProperty("status", "Offline"); }
			
			return values;
		}
		
		private static JsonObject calculateStats(JsonArray toParse) {
			JsonObject values = new JsonObject();
			
			int runningTotal = 0;
			int lowestPrice = -1;
			int highestPrice = 0;
			int numberOfPeople = 0;
			int numberOfItems = 0;
			
			Iterator<JsonElement> iter = toParse.iterator();
			
			while(iter.hasNext()) {
				JsonElement nxt = iter.next();
				JsonObject jObj = nxt.getAsJsonObject();
				int price = (int) Math.floor(Float.parseFloat(jObj.get("platinum").toString())); //Integer.parseInt(jObj.get("platinum").toString());
				int count = (int) Math.floor(Float.parseFloat(jObj.get("quantity").toString())); //Integer.parseInt(jObj.get("quantity").toString());
				
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
		
			values.addProperty("min", lowestPrice);
			values.addProperty("max", highestPrice);
			values.addProperty("ppl", numberOfPeople);
			values.addProperty("cnt", numberOfItems);
			int midPoint = toParse.size() / 2;
			if(numberOfPeople > 0) {
				if(toParse.size() % 2 == 0) {
					// then we are even
					int midPoint1 = (int) Math.floor(Float.parseFloat(toParse.get(midPoint).getAsJsonObject().get("platinum").toString())); //Integer.parseInt(toParse.get(midPoint).getAsJsonObject().get("platinum").toString());
					int midPoint2 = (int) Math.floor(Float.parseFloat(toParse.get(midPoint + 1).getAsJsonObject().get("platinum").toString())); //Integer.parseInt(toParse.get(midPoint + 1).getAsJsonObject().get("platinum").toString());
					values.addProperty("median", (midPoint1 + midPoint2) / 2);
				} else {
					// we are odd
					values.addProperty("median", (int) Math.floor(Float.parseFloat(toParse.get(midPoint).getAsJsonObject().get("platinum").toString())));
				}
			} else {
				values.addProperty("median", 0);
			}
			
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
		@SuppressWarnings("finally")
		private static JsonObject getResults(String lookupName) {
			JsonParser parser = new JsonParser();
			JsonObject jobj = null;
			
			try {
				URL url = new URL(baseURL + "/" + itemRenamed(lookupName).replaceAll(" ", "_").replaceAll("-", "_") + "/" + postURL);
				HttpURLConnection request = (HttpURLConnection) url.openConnection();
				request.connect();

				jobj = parser.parse(new InputStreamReader((InputStream) request.getContent())).getAsJsonObject();
				
				return jobj;
			} catch (IOException e) {
				jobj = new JsonObject();
				jobj.addProperty("Error", "Could not properly retrieve information. IOException.");
			} finally {
				return jobj;
			}
		}

		private static JsonObject cloneJson(JsonObject toClone) {
			JsonParser parser = new JsonParser();
			return parser.parse(toClone.toString()).getAsJsonObject();
		}
		
		private static JsonArray getOnly(JsonObject toSearch, String filterStatus, String filterBuySell) {
			JsonObject toSearchC = cloneJson(toSearch);
			JsonArray jArr;
			
			try {
				jArr = toSearchC.getAsJsonObject("payload").getAsJsonArray("orders");
			} catch(NullPointerException e) {
				return null;
			}
				
			Iterator<JsonElement> iter = jArr.iterator();
			
			while(iter.hasNext()) {
				JsonElement nxt = iter.next();
				JsonObject tjObj = nxt.getAsJsonObject();
				JsonObject tjUser = tjObj.getAsJsonObject("user");
				String tStatus = tjUser.get("status").toString().replaceAll("\"", "");
				String tOrderType = tjObj.get("order_type").toString().replaceAll("\"", "");
				
				if(tStatus.equalsIgnoreCase(filterStatus) && tOrderType.equalsIgnoreCase(filterBuySell)) {
					// Do Nothing
				} else {
					iter.remove();
				}
			}
			
			return jArr;
		}
				
		/**
		 * 
		 * @param lookupName
		 * @return
		 */
		public static String itemRenamed(String lookupName) {
			lookupName = lookupName.toLowerCase();
			
			Iterator<Map.Entry<String, String>> iter = renamedItems.entrySet().iterator();
			while(iter.hasNext()) {
				Map.Entry<String, String> nxt = iter.next();
				lookupName = lookupName.replace(nxt.getKey(), nxt.getValue());
			}
			return lookupName;
		}
		
		/**
		 * Formats a string for Camel Case Styling, for display purposes
		 * @param toFormat The string to format
		 * @return A properly formatted string to be used for display purposes
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
