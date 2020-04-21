package edu.wpi.cs3733.d20.teamL.services.search;

import edu.wpi.cs3733.d20.teamL.entities.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SearchFields {
	private ArrayList<Node> nodeCache;
	private ArrayList<String> suggestions;

	public enum Field { nodeID, longName, shortName, building }

	private List<Field> fields = new ArrayList<>(Arrays.asList(Field.longName, Field.shortName));

	public SearchFields(ArrayList<Node> nodeCache) {
		this.nodeCache = nodeCache;
	}

    public List<Field> getFields() {
        return fields;
    }

    /**
     * populates search arrayList to be searched by navigation bar.
     */
    public void populateSearchFields(){
        // TODO: Don't let non-visible nodes show-up
        if (suggestions == null) suggestions = new ArrayList<>();
        for(Node node: nodeCache) {
            for (Field field : fields) {
                switch (field) {
                    case nodeID:
                        suggestions.add(node.getID());
                        break;
                    case building:
                        suggestions.add(node.getBuilding());
                        break;
                    case longName:
                        suggestions.add(node.getLongName());
                        break;
                    case shortName:
                        suggestions.add(node.getShortName());
                        break;
                    default:
                        break;
                }
            }
        }
        Collections.sort(suggestions);
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
     * @return  If keyword is found it'll return that node, otherwise null if your node isn't found.
     */
    public Node getNode(String query) {
        for (Node node : nodeCache) {
            if (query.equals(node.getShortName()) || query.equals(node.getLongName())) return node;
        }
        return null;
    }
}
