package edu.wpi.cs3733.d20.teamL.services.navSearch;

import edu.wpi.cs3733.d20.teamL.util.io.CSVReader;
import edu.wpi.cs3733.d20.teamL.util.io.DBCache;

import java.util.ArrayList;

public class SearchFields {
    private static SearchFields searchFields;
    DBCache dbCache = new DBCache();
    private SearchFields(){}

    private ArrayList<String> suggestions;

    private static class SingletonHelper{
        private static final SearchFields searchFields = new SearchFields();
    }

    public static SearchFields getSearchFields(){
        return SingletonHelper.searchFields;
    }

    /**
     * populates search arrayList to be searched by navigation bar
     */
    public void populateSearchFields(){
        // TODO: Don't let non-visible nodes show-up

        if (suggestions == null) suggestions = new ArrayList<>();
        for(ArrayList<String> nodes: dbCache.getNodeCache()) {
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
        for (ArrayList<String> node : dbCache.getNodeCache() ){
            if (query.equals(node.get(6)) || query.equals(node.get(7))) return node.get(0);
        }
        return null;
    }
}
