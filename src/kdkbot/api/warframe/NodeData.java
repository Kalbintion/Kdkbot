package kdkbot.api.warframe;

/**
 * Class holding data found in every node (sector) in Warframe
 * @author KDK
 *
 */
public class NodeData {
	private String node_id;
	private String readable_name;
	private String mission_type;
	private String faction_type;
	private String tileset_name;
	private int min_level;
	private int max_level;
	
	public NodeData(String id, String readable) {
		this(id, readable, "", "", 0, 0, "");
	}
	
	public NodeData(String id, String readable, String missionType, String faction) {
		this(id, readable, missionType, faction, 0, 0, "");
	}
	
	public NodeData(String id, String readable, String missionType, String faction, int minLevel, int maxLevel, String tileset) {
		node_id = id;
		readable_name = readable;
		mission_type = missionType;
		faction_type = faction;
		tileset_name = tileset;
		min_level = minLevel;
		max_level = maxLevel;
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
}
