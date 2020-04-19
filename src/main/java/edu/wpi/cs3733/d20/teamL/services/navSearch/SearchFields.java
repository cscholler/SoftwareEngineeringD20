package edu.wpi.cs3733.d20.teamL.services.navSearch;

import edu.wpi.cs3733.d20.teamL.entities.Node;

import java.util.ArrayList;

public class SearchFields {
	private ArrayList<Node> nodeCache;
	private ArrayList<String> suggestions;

	public SearchFields(ArrayList<Node> nodeCache) {
		this.nodeCache = nodeCache;
	}

    /**
     * populates search arrayList to be searched by navigation bar.
     */
    public void populateSearchFields(){
        // TODO: Don't let non-visible nodes show-up
        if (suggestions == null) suggestions = new ArrayList<>();
        for(Node node: nodeCache) {
            suggestions.add(node.getLongName());
            suggestions.add(node.getShortName());
        }
    }

    /**
     *
     * @return ArrayList<String> That can be displayed and searched with the search bar.
     */
    public ArrayList<String> getSuggestions() {
        return suggestions;
    }

    /**
     * Provides nodeID to locate database row with query keyword.
     *
     * @param query The keyword you're searching by.
     * @return  If keyword is found it'll return that nodeID, otherwise null if your node isn't found.
     */
    public String getNodeID(String query) {
        for (Node node : nodeCache) {
            if (query.equals(node.getShortName()) || query.equals(node.getLongName())) return node.getID();
        }
        return null;
    }
}
