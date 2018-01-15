package kdkbot.api.warframe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import kdkbot.Kdkbot;
import kdkbot.api.warframe.API.Market;
import kdkbot.filemanager.Config;
import kdkbot.language.Translate;

public class InternalTranslator {
	public static ArrayList<NodeData> solNodes = new ArrayList<NodeData>();
	public static HashMap<String, String> solNodesMap = new HashMap<String, String>();
	public static HashMap<String, String> itmNodes = new HashMap<String, String>();
	
	static {
		reloadNodeData();
	}
	
	/**
	 * Reloads the node data information
	 */
	public static void reloadNodeData() {
		String last_line = "";
		
		try {
			Config cfg = new Config("./cfg/api/warframe/solNodes.kdk");
			List<String> data = cfg.getConfigContents();
			Iterator<String> iter = data.iterator();
			
			if(solNodes.size() > 0) { solNodes.clear(); }
			
			while(iter.hasNext()) {
				String nxt = iter.next();
				last_line = nxt;
				String[] parts = nxt.split("\\|");
				
				//         0                1                   2               3             4             5               6                7                8
				// String id, String readable, String missionType, String faction, int minLevel, int maxLevel, String tileset, String nodePrev, String nodeNext
				
				String nodeID = parts[0];
				String nodeName = parts[1];
				String nodeMission = "";
				String nodeFaction = "";
				int nodeMinLvl = 0;
				int nodeMaxLvl = 0;
				String nodeTileset = "";
				String nodeNext = "";
				String nodePrev = "";
				if(parts.length >= 3) { nodeMission = parts[2];	}
				if(parts.length >= 4) { nodeFaction = parts[3]; }
				if(parts.length >= 5) { nodeMinLvl = Integer.parseInt(parts[4]); }
				if(parts.length >= 6) { nodeMaxLvl = Integer.parseInt(parts[5]); }
				if(parts.length >= 7) { nodeTileset = parts[6]; }
				if(parts.length >= 8) { nodePrev = parts[7]; }
				if(parts.length >= 9) { nodeNext = parts[8]; }
				
				solNodes.add(new NodeData(nodeID, nodeName, nodeMission, nodeFaction, nodeMinLvl, nodeMaxLvl, nodeTileset, nodePrev, nodeNext));
			}
		} catch (Exception e) {
			Kdkbot.instance.dbg.writeln("Error parsing line in ./cfg/api/warframe/solNodes.kdk");
			Kdkbot.instance.dbg.writeln("Line: " + last_line);
			Kdkbot.instance.dbg.writeln(e.getMessage());
			Kdkbot.instance.dbg.writeln(e.getStackTrace().toString());
		}
		
		if(solNodesMap.size() > 0) { solNodesMap.clear(); }
		
		// Load all data into solNodesMap for faster SolNode ID => Name lookups (Costs extra RAM)
		Iterator<NodeData> iter = solNodes.iterator();
		while(iter.hasNext()) {
			NodeData nxt = iter.next();
			solNodesMap.put(nxt.getNodeID(), nxt.getNodeName());
		}
	}
	
	/**
	 * Retrieves the readable name of a node provided with its unique ID
	 * @param solTag A Unique ID to lookup. In the form of SolNode# or SettlementNode#
	 * @return A String containing the readable name of the node, null if it couldnt be found
	 */
	public static String getSolNodeName(String solTag) {
		String data = solNodesMap.get(solTag);
		if(data == null) { return solTag; }
		else { return data; }
	}
	
	/**
	 * Retrieves a NodeData instance of a node provided with its unique ID
	 * @param solTag A Unique ID to lookup. In the form of SolNode# or SettlementNode#
	 * @return A NodeData containing the information regarding the node, null if it couldnt be found
	 */
	public static NodeData getSolNodeData(String solTag) {
		Iterator<NodeData> iter = solNodes.iterator();
		while(iter.hasNext()) {
			NodeData nxt = iter.next();
			if(nxt.getNodeID().equalsIgnoreCase(solTag)) { return nxt; }
		}
		
		return null;
	}
	
	
	/**
	 * Retrieves the human-readable name from a unique name
	 * @param uniqueName The unique name to get the real name for
	 * @return The real name, if found, of the item
	 */
	public static String getReadableName(String uniqueName) {
		uniqueName = uniqueName.replaceAll("\"", "");
		String ret = Translate.getTranslate(uniqueName, "enWFUN");
		
		// Set translation of this object to itself for future correction
		if(ret.equalsIgnoreCase("null")) {
			Translate.setTranslate(uniqueName, "enWFUN", uniqueName);
			ret = uniqueName;
		}
		
		return ret;
	}
	
	/**
	 * Converts an internal sortie boss tag (form of: SORTIE_BOSS_...) to actual boss name
	 * @param sortieBossTag The tag to convert
	 * @return A String containing the converted boss tag
	 */
	public static String convertTagSortieBoss(String sortieBossTag) {
		sortieBossTag = sortieBossTag.replace("SORTIE_BOSS_", "").toLowerCase();
		sortieBossTag = sortieBossTag.substring(0,1).toUpperCase() + sortieBossTag.substring(1);
		return sortieBossTag;
	}
	
	/**
	 * Converts an internal mission type tag (form of: MT_...) to actual mission type name
	 * @param missionTypeTag The tag to convert
	 * @return A String containing the converted mission type tag
	 */
	public static String convertTagMissionType(String missionTypeTag) {
		switch(missionTypeTag) {
			case "MT_INTEL":
				return "Spy";
			case "MT_EVACUATION":
				return "Defection";
			case "MT_TERRITORY":
				return "Interception";
			default:
				missionTypeTag = missionTypeTag.replace("MT_", "").toLowerCase().replaceAll("_", " ");
				missionTypeTag = Market.camelCaseStr(missionTypeTag);
				return missionTypeTag;
		}
	}
	
	/**
	 * Converts an internal sortie modifier tag to the human readable name. Format: SORTIE_MODIFIER_...
	 * @param modifierTypeTag The tag to convert
	 * @return A String containing the converted sortie modifier tag
	 */
	public static String convertTagModifierType(String modifierTypeTag) {
		modifierTypeTag = modifierTypeTag.replace("SORTIE_MODIFIER_", "").toLowerCase().replaceAll("_", " ");
		modifierTypeTag = Market.camelCaseStr(modifierTypeTag);
		return modifierTypeTag;
	}
	
	/**
	 * Converts an internal faction tag to a human readable name. Format: FC_...
	 * @param factionTypeTag The tag to convert
	 * @return A String containing the converted faction tag
	 */
	public static String convertTagFactionType(String factionTypeTag) {
		factionTypeTag = factionTypeTag.replace("FC_", "").toLowerCase().replaceAll("_", " ");
		factionTypeTag = Market.camelCaseStr(factionTypeTag);
		return factionTypeTag;
	}
	
	/**
	 * Converts an internal void tier type to a human readable name. Format: VoidT#
	 * @param voidTypeTag The tag to convert
	 * @return A String containing the converted void tier tag, returns voidTypeTag if not valid
	 */
	public static String convertVoidTierType(String voidTypeTag) {
		switch(voidTypeTag) {
			case "VoidT1":
				return "Lith";
			case "VoidT2":
				return "Meso";
			case "VoidT3":
				return "Neo";
			case "VoidT4":
				return "Axi";
			default:
				return voidTypeTag;
		}
	}
}
