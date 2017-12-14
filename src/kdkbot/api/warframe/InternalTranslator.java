package kdkbot.api.warframe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import kdkbot.filemanager.Config;

public class InternalTranslator {
	public static ArrayList<NodeData> solNodes = new ArrayList<NodeData>();
	public static HashMap<String, String> solNodesMap = new HashMap<String, String>();
	public static HashMap<String, String> itmNodes = new HashMap<String, String>();
	
	static {
		reloadNodeData();
		
		// Load all data into solNodesMap for faster SolNode ID => Name lookups (Costs extra RAM)
		Iterator<NodeData> iter = solNodes.iterator();
		while(iter.hasNext()) {
			NodeData nxt = iter.next();
			solNodesMap.put(nxt.getNodeID(), nxt.getNodeName());
		}
	}
	
	public static void reloadNodeData() {
		try {
			Config cfg = new Config("./cfg/api/warframe/solNodes.kdk");
			List<String> data = cfg.getConfigContents();
			Iterator<String> iter = data.iterator();
			
			if(solNodes.size() > 0) { solNodes.clear(); }
			
			while(iter.hasNext()) {
				String nxt = iter.next();
				String[] parts = nxt.split("\\|");
				
				//         0                1                   2               3             4             5               6
				// String id, String readable, String missionType, String faction, int minLevel, int maxLevel, String tileset
				
				String nodeID = parts[0];
				String nodeName = parts[1];
				String nodeMission = "";
				String nodeFaction = "";
				int nodeMinLvl = 0;
				int nodeMaxLvl = 0;
				String nodeTileset = "";
				if(parts.length >= 3) { nodeMission = parts[2];	}
				if(parts.length >= 4) { nodeFaction = parts[3]; }
				if(parts.length >= 5) { nodeMinLvl = Integer.parseInt(parts[4]); }
				if(parts.length >= 6) { nodeMaxLvl = Integer.parseInt(parts[5]); }
				if(parts.length >= 7) { nodeTileset = parts[6]; }
				
				solNodes.add(new NodeData(nodeID, nodeName, nodeMission, nodeFaction, nodeMinLvl, nodeMaxLvl, nodeTileset));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getSolNodeName(String solTag) {
		String data = solNodesMap.get(solTag);
		if(data == null) { return solTag; }
		else { return data; }
	}
	
	public static NodeData getSolNodeData(String solTag) {
		Iterator<NodeData> iter = solNodes.iterator();
		while(iter.hasNext()) {
			NodeData nxt = iter.next();
			if(nxt.getNodeID().equalsIgnoreCase(solTag)) { return nxt; }
		}
		
		return null;
	}
}
