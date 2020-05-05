package edu.wpi.cs3733.d20.teamL.util.search;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.entities.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
            additionLong = (node.getLongName() + " - (" + node.getBuilding() +" "+ node.getFloorAsString() + ")");
            suggestions.add(additionLong);
//            additionShort = (node.getShortName() + " - (" + node.getBuilding() +" "+ node.getFloorAsString() + ")");
//            suggestions.add(additionShort);

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
        }
        Collections.sort(suggestions);
    }

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
        for (Node node : nodeCache) {
            if (query.equals(node.getShortName()) || query.equals(node.getLongName())) return node;
        }
        return null;
    }
}
