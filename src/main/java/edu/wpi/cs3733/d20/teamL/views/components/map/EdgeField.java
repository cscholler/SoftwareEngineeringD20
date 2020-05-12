package edu.wpi.cs3733.d20.teamL.views.components.map;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d20.teamL.entities.Building;
import edu.wpi.cs3733.d20.teamL.entities.Graph;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import edu.wpi.cs3733.d20.teamL.util.search.SearchFields;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class EdgeField extends HBox {

    @FXML
    JFXTextField text;
    @FXML
    JFXButton close;

    @Inject
    private IDatabaseCache dbCache;

    private FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();

    private SearchFields edgesSf;
    private JFXAutoCompletePopup<String> edgesAutoCompletePopup;

    public EdgeField(Graph graph) {
        FXMLLoader fxmlLoader = loaderHelper.getFXMLLoader("components/EdgeField");
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            log.error("Could not load EdgeField.fxml", exception);
        }

        text.setOnKeyTyped(event -> autocomplete());
        close.setOnAction(event -> delete());

        edgesSf = new SearchFields(new ArrayList<>(graph.getNodes()));
        edgesSf.getFields().add(SearchFields.Field.nodeID);
        edgesSf.populateSearchFields();
        edgesAutoCompletePopup = new JFXAutoCompletePopup<>();
        edgesAutoCompletePopup.getSuggestions().addAll(edgesSf.getSuggestions());
    }

    @FXML
    private void initialize() {

    }

    private void autocomplete() {
        edgesSf.applyAutocomplete(text, edgesAutoCompletePopup);
    }

    private void delete() {
        Pane parent = (Pane) getParent();

        parent.getChildren().remove(this);
    }

    public void setText(String text) {
        this.text.setText(text);
    }

    public String getText() {
        return text.getText();
    }
}
