package kdkbot.api.warframe;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class holding data found in every node (sector) in Warframe
 * @author KDK
 *
 */
public class NodeData {
	private String node_id = "";
	private String readable_name = "";
	private String mission_type = "";
	private String faction_type = "";
	private String tileset_name = "";
	private int min_level = 0;
	private int max_level = 0;
	private ArrayList<String> node_previous;
	private ArrayList<String> node_next;
	private String planet_name = "";
	private String sector_name = "";
	
	public NodeData() {
		
	}
		
	public NodeData(String id, String readable) {
		this(id, readable, "", "", 0, 0, "");
	}
	
	public NodeData(String id, String readable, String missionType) {
		this(id, readable, missionType, "", 0, 0, "");
	}
	
	public NodeData(String id, String readable, String missionType, String faction) {
		this(id, readable, missionType, faction, 0, 0, "");
	}
	
	public NodeData(String id, String readable, String missionType, String faction, int minLevel, int maxLevel, String tileset) {
		this(id, readable, missionType, faction, 0, 0, tileset, "", "");
	}
	
	public NodeData(String id, String readable, String missionType, String faction, int minLevel, int maxLevel, String tileset, String prevNode, String nextNodes) {
		node_id = id;
		readable_name = readable;
		mission_type = missionType;
		faction_type = faction;
		tileset_name = tileset;
		min_level = minLevel;
		max_level = maxLevel;
		node_previous = new ArrayList<String>(Arrays.asList(prevNode.split(",")));
		node_next = new ArrayList<String>(Arrays.asList(nextNodes.split(",")));
		planet_name = readable_name.split(" \\(")[1].replaceAll("\\)", "");
		sector_name = readable_name.split(" \\(")[0];
	}
	
	public int getMinLevel() {
		return min_level;
	}
	
	public String getMinLevelStr() {
		return String.valueOf(min_level);
	}
	
	public int getMaxLevel() {
		return max_level;
	}
	
	public String getMaxLevelStr() {
		return String.valueOf(max_level);
	}
	
	public String getFaction() {
		return faction_type;
	}
	
	public String getMissionType() {
		return mission_type;
	}
	
	public String getNodeName() {
		return readable_name;
	}
	
	public String getNodeID() {
		return node_id;
	}
	
	public String getTileset() {
		return tileset_name;
	}
	
	public ArrayList<String> previousNode() {
		return node_previous;
	}
	
	public ArrayList<String> nextNode() {
		return node_next;
	}
	
	public String getSectorName() {
		return planet_name;
	}
	
	public String getPlanetName() {
		return sector_name;
	}
}
