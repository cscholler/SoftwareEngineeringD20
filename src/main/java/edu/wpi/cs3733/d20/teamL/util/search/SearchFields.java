package edu.wpi.cs3733.d20.teamL.util.search;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.entities.Node;
import edu.wpi.cs3733.d20.teamL.views.controllers.map.MapViewerController;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class SearchFields {
    private List<Node> nodeCache;
    private List<String> suggestions;

    public enum Field {nodeID, longName, shortName, building}

    private List<Field> fields = new ArrayList<>();

    public SearchFields(List<Node> nodeCache) {
        this.nodeCache = nodeCache;
    }

    /**
     * Returns a list of field enums that is used to determine what fields of Node will be autocompleted. (ex. autocomplete nodeID and longName)
     *
     * @return A list of Field enums
     */
    public List<Field> getFields() {
        return fields;
    }

    /**
     * populates search arrayList to be searched by navigation bar.
     */
    public void populateSearchFields() {
        // TODO: Don't let non-visible nodes show-up
        StringBuilder sb = new StringBuilder();
        String additionLong;
        String additionShort;
        if (suggestions == null) suggestions = new ArrayList<>();
        for (Node node : nodeCache) {
            if (!(node.getType().equals("HALL"))) {
                additionLong = (node.getLongName() + " - (" + node.getBuilding() + " " + node.getFloorAsString() + ")");
                suggestions.add(additionLong);
            }
        }
        Collections.sort(suggestions);
    }


//    /**
//     * populates search arrayList to be searched by navigation bar.
//     */
//    public void populateSearchFields() {
//        // TODO: Don't let non-visible nodes show-up
//        StringBuilder sb = new StringBuilder();
//        String additionLong;
//        String additionShort;
//        if (suggestions == null) suggestions = new ArrayList<>();
//        for (Node node : nodeCache) {
//            for (Field field : fields) {
//                switch (field) {
//                    case nodeID:
//                        suggestions.add(node.getID());
//                        break;
//                    case building:
//                        suggestions.add(node.getBuilding());
//                        break;
//                    case longName:
//                        suggestions.add(node.getLongName());
//                        break;
//                    case shortName:
//                        suggestions.add(node.getShortName());
//                        break;
//                    default:
//                        break;
//                }
//            }
//        }
//        Collections.sort(suggestions);
//    }


    public void populateWithExits() {
        // TODO: Don't let non-visible nodes show-up
        if (suggestions == null) suggestions = new ArrayList<>();
        for (Node node : nodeCache) {
            if(node.getType().equals("EXIT")){
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
        }
        Collections.sort(suggestions);
    }

    /**
     * Displays the given autocomplete popup underneath a given textfield using the data from this SearchFields instance.
     *
     * @param textField         The textfield to display the autocomplete popup underneath
     * @param autoCompletePopup The JFoenix AutoCompletePopup to display
     */
    public void applyAutocomplete(JFXTextField textField, JFXAutoCompletePopup<String> autoCompletePopup) {
        autoCompletePopup.setSelectionHandler(event -> textField.setText(event.getObject()));
        textField.textProperty().addListener(observable -> {
            autoCompletePopup.filter(string ->
                    string.toLowerCase().contains(textField.getText().toLowerCase()));
            if (autoCompletePopup.getFilteredSuggestions().isEmpty() ||
                    textField.getText().isEmpty()) {
                autoCompletePopup.hide();
            } else {
                autoCompletePopup.show(textField);
            }
        });
    }

    /**
     * Gives an arraylist of strings to display in the suggestions. This should be passed into the JFXAutoCompletePopup.
     *
     * @return ArrayList<String> That can be displayed and searched with the search bar.
     */
    public List<String> getSuggestions() {
        return suggestions;
    }

    /**
     * Provides nodeID to locate database row with query keyword.
     *
     * @param query The keyword you're searching by.
     * @return If keyword is found it'll return that node, otherwise null if your node isn't found.
     */
    public Node getNode(String query) {
        System.out.println("Here");

        String building = "";

        if (query.contains("(Faulkner")) {
            query = query.substring(0, query.length() - 15);
            building = "Faulkner";
        }else if(query.contains("(" + MapViewerController.MAIN)) {
            if(query.contains("L2") || query.contains("L1"))
                query = query.substring(0, query.length() - 12);
            else  query = query.substring(0, query.length() - 11);
            building = MapViewerController.MAIN;
        }
        log.info(building);
        if (building.isEmpty()) {
            for (Node node : nodeCache) {
                if (query.equals(node.getShortName()) || query.equals(node.getLongName())) return node;
            }
        } else {
            for (Node node : getNodesInBuilding(building)) {
                if (query.equals(node.getShortName()) || query.equals(node.getLongName())) return node;
            }
        }
        return null;
    }

    public Node getNode(String query, String building, String floor) {
        for (Node node : nodeCache) {
            if ((query.equals(node.getShortName()) || query.equals(node.getLongName())) && building.equals(node.getBuilding()) && floor.equals(node.getFloorAsString())) return node;
        }
        return null;
    }

    private List<Node> getNodesInBuilding(String building) {
        List<Node> newNodes = new ArrayList<>();
        for (Node node : nodeCache) {
            if (node.getBuilding().equals(building))
                newNodes.add(node);
        }

        return newNodes;
    }
}
