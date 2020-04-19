package edu.wpi.cs3733.d20.teamL.services.navSearch;

import java.util.ArrayList;

public class SearchFields {
	private ArrayList<ArrayList<String>> nodeCache;
	private ArrayList<String> suggestions;

	public SearchFields(ArrayList<ArrayList<String>> nodeCache) {
		this.nodeCache = nodeCache;
	}

    /**
     * populates search arrayList to be searched by navigation bar
     */
    public void populateSearchFields(){
        // TODO: Don't let non-visible nodes show-up
        if (suggestions == null) suggestions = new ArrayList<>();
        for(ArrayList<String> nodes: nodeCache) {
            suggestions.add(nodes.get(6)); //long name
            suggestions.add(nodes.get(7)); //short name
        }
    }

    /**
     *
     * @return ArrayList<String> That can be displayed and searched with the search bar
     */
    public ArrayList<String> getSuggestions() {
        return suggestions;
    }

    /**
     * Provides PrimaryKey to locate database row with query keyword
     * @param query The keyword you're searching by
     * @return  If keyword is found it'll return that nodes PK, otherwise null if your node isn't found
     */
    public String getNodePK(String query) {
        for (ArrayList<String> node : nodeCache) {
            if (query.equals(node.get(6)) || query.equals(node.get(7))) return node.get(0);
        }
        return null;
    }
}
