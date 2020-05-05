package edu.wpi.cs3733.d20.teamL.views.controllers.map;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamL.entities.*;
import edu.wpi.cs3733.d20.teamL.services.messaging.IMessengerService;
import edu.wpi.cs3733.d20.teamL.services.pathfinding.IPathfinderService;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import edu.wpi.cs3733.d20.teamL.util.search.SearchFields;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.google.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.entities.Path;
import edu.wpi.cs3733.d20.teamL.views.components.EdgeGUI;
import edu.wpi.cs3733.d20.teamL.views.components.MapPane;
import edu.wpi.cs3733.d20.teamL.views.components.NodeGUI;
import org.apache.xmlgraphics.image.codec.png.PNGEncodeParam;

@Slf4j
public class MapViewerController {
    @FXML
    private MapPane map;

    @FXML
    private JFXTextField startingPoint, destination;

    @FXML
    private JFXButton btnNavigate, floorUp, floorDown;

    @FXML
    private ScrollPane scroll;

    @FXML
    private VBox instructions;
    @FXML
    private JFXNodesList textDirNode;
    @FXML
    private VBox floorSelector;
    @FXML
    private JFXListView dirList;
    @FXML
    private JFXButton btnTextMe, btnQR;

    @FXML
    StackPane stackPane;

    @FXML
    private JFXListView listF1, listF2, listF3, listF4, listF5;

    @FXML
    private JFXComboBox<String> buildingChooser;

    @FXML
    private Label timeLabel;

    @Inject
    private IDatabaseCache cache;
    @Inject
    private IPathfinderService pathfinderService;
    @Inject
    private IMessengerService messenger;


    private SearchFields sf;
    private JFXAutoCompletePopup<String> autoCompletePopup;
    private FXMLLoaderFactory loaderHelper = new FXMLLoaderFactory();
    private final Timer timer = new Timer();
    private Path path = new Path();
    private final ObservableList<String> direct = FXCollections.observableArrayList();

    private final Image IMAGE_LEFT = new Image("/edu/wpi/cs3733/d20/teamL/assets/Directions/left.png");
    private final Image IMAGE_RIGHT = new Image("/edu/wpi/cs3733/d20/teamL/assets/Directions/right.jpg");
    private final Image IMAGE_SHLEFT = new Image("/edu/wpi/cs3733/d20/teamL/assets/Directions/sharp left.jpg");
    private final Image IMAGE_SHRIGHT = new Image("/edu/wpi/cs3733/d20/teamL/assets/Directions/sharp right.jpg");
    private final Image IMAGE_SLLEFT = new Image("/edu/wpi/cs3733/d20/teamL/assets/Directions/slightLeft.jpg");
    private final Image IMAGE_SLRIGHT = new Image("/edu/wpi/cs3733/d20/teamL/assets/Directions/slightRight.jpg");
    private final Image IMAGE_ELEV = new Image("/edu/wpi/cs3733/d20/teamL/assets/Directions/elevator.jpg");
    private final Image IMAGE_STAIR = new Image("/edu/wpi/cs3733/d20/teamL/assets/Directions/stair.png");
    private final Image IMAGE_DEST = new Image("/edu/wpi/cs3733/d20/teamL/assets/Directions/destFlag.png");
    private final Image IMAGE_FTOM = new Image("/edu/wpi/cs3733/d20/teamL/assets/maps/FaulkToMain.PNG");
    private final Image IMAGE_MTOF = new Image("/edu/wpi/cs3733/d20/teamL/assets/maps/MainToFaulk.PNG");

    @FXML
    private void initialize() {
        timer.scheduleAtFixedRate(timerWrapper(this::updateTime), 0, 1000);
        cache.cacheAllFromDB();

        map.setEditable(false);
        map.setHighLightColor(Color.GOLD);
        btnNavigate.setDisableVisualFocus(true);

        // Import all the nodes from the cache and set the current building to Faulkner
        String startB = "Faulkner";
        Building faulkner = cache.getBuilding("Faulkner");
        Building btm = cache.getBuilding("BTM");

        if (!faulkner.getNodes().isEmpty()) map.setBuilding(faulkner);
        if (!btm.getNodes().isEmpty()) map.getBuildings().add(btm);
        buildingChooser.getItems().addAll("Faulkner", "BTM");
        buildingChooser.getSelectionModel().select(startB);

        // Add floor buttons
        generateFloorButtons();

        setFloor(2);

        map.setZoomLevel(0.65);
        map.init();

        // Populate autocomplete
        sf = new SearchFields(cache.getNodeCache());
        sf.getFields().addAll(Arrays.asList(SearchFields.Field.shortName, SearchFields.Field.longName));
        sf.populateSearchFields();
        autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(sf.getSuggestions());

        // TODO: Change node dropdowns to be generated
        Collection<Node> allNodes = map.getBuilding().getNodes();
        Collection<String> deptNodes = new ArrayList<>();
        Collection<String> labNodes = new ArrayList<>();
        Collection<String> serviceNodes = new ArrayList<>();
        Collection<String> retailNodes = new ArrayList<>();
        Collection<String> confNodes = new ArrayList<>();

        for (Node node : allNodes) {
            if (node.getType().equals("DEPT")) {
                deptNodes.add(node.getLongName());
            } else if (node.getType().equals("LABS")) {
                labNodes.add(node.getLongName());
            } else if ((node.getType().equals("SERV") || node.getType().equals("INFO"))) {
                serviceNodes.add(node.getLongName());
            } else if (node.getType().equals("RETL")) {
                retailNodes.add(node.getLongName());
            } else if (node.getType().equals("CONF")) {
                confNodes.add(node.getLongName());
            }
        }

        listF1.getItems().addAll(deptNodes);
        listF2.getItems().addAll(labNodes);
        listF3.getItems().addAll(serviceNodes);
        listF4.getItems().addAll(retailNodes);
        listF5.getItems().addAll(confNodes);
    }

    private void generateFloorButtons() {
        map.generateFloorButtons(floorSelector, this::handleFloor);
    }

    @FXML
    private void switchBuilding() {
        String selected = buildingChooser.getSelectionModel().getSelectedItem();

        Building newBuilding = cache.getBuilding(selected);
        map.setBuilding(newBuilding);

        int prevFloor = map.getFloor();
        generateFloorButtons();
        setFloor(Math.max(map.getBuilding().getMinFloor(), Math.min(prevFloor, map.getBuilding().getMaxFloor())));

        if (!path.getPathNodes().isEmpty()) highLightPath();
    }

    @FXML
    private void startingPointAutocomplete() {
        sf.applyAutocomplete(startingPoint, autoCompletePopup);
    }

    @FXML
    private void destinationAutocomplete() {
        sf.applyAutocomplete(destination, autoCompletePopup);
    }

    public void setStartingPoint(String startingPoint) {
        this.startingPoint.setText(startingPoint);
    }

    public void setDestination(String destination) {
        this.destination.setText(destination);
    }

    /**
     * Shows everything required for a navigations, includes:
     * highlighting the path
     * showing text directions
     * showing 'text me directions' button
     */
    @FXML
    public void navigate() {
        String start = startingPoint.getText();
        String end = destination.getText();
        String buildingS;
        String buildingE;
        String floorS = start.substring(start.length()-2,start.length()-1);
        String floorE = end.substring(end.length()-2,end.length()-1);


        if(start.contains("(Faulkner"))
        {
            start= start.substring(0, start.length()-15);
            buildingS = "Faulkner";
        }else if(start.contains("(BTM")){
            start= start.substring(0, start.length()-10);
            buildingS = "BTM";
        }
        if(end.contains("(Faulkner"))
        {
            end= end.substring(0, end.length()-15);
            buildingE = "Faulkner";
        }else if(end.contains("(BTM")){
            end= end.substring(0, end.length()-10);
            buildingE = "BTM";
        }

        Node startNode = sf.getNode(start);
        Node destNode = sf.getNode(end);


        if (!(startNode.getBuilding().equals(map.getBuilding().getName()))) {
            map.setBuilding(startNode.getBuilding());
            buildingChooser.getSelectionModel().select(startNode.getBuilding());
        }
        setFloor(startNode.getFloor());


        if (startNode != null && destNode != null) {
            String directions = highlightSourceToDestination(startNode, destNode);

            messenger.setDirections(directions);

            btnTextMe.setDisable(false);
            btnTextMe.setVisible(true);
            btnQR.setDisable(false);
            btnQR.setVisible(true);
            textDirNode.setDisable(false);
            textDirNode.setVisible(true);
        }
    }

    /**
     * Shows the key popup
     */
    @FXML
    private void showLegend() {
        try {
            Parent root = loaderHelper.getFXMLLoader("map_viewer/LegendPopup").load();
            loaderHelper.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Couldn't load LegendPopup.fxml", ex);
        }
    }

    private String highlightSourceToDestination(Node source, Node destination) {
        map.getSelector().clear();

        if (!path.getPathNodes().isEmpty()) {
            NodeGUI start = map.getNodeGUI(path.getPathNodes().get(0));
            NodeGUI end = map.getNodeGUI(path.getPathNodes().get(path.getPathNodes().size() - 1));

            map.resetNodeVisibility(start);
            map.resetNodeVisibility(end);
        }

        path = pathfinderService.pathfind(map.getAllNodes(), source, destination);
        highLightPath();

        path.generateTextMessage();
        ArrayList<String> message = path.getMessage();
        StringBuilder builder = new StringBuilder();

        dirList.getItems().clear();
        dirList.setCellFactory(param -> {
            return new ListCell<String>() {
                private ImageView imageView = new ImageView();

                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                        setText(null);
                        // other stuff to do...
                        imageView.setFitWidth(10);
                        imageView.setFitHeight(10);
                    } else {

                        if (item.contains("right")) {
                            if (item.contains("slight")) {
                                imageView.setImage(IMAGE_SLRIGHT);
                            } else if (item.contains("sharp")) {
                                imageView.setImage(IMAGE_SHRIGHT);
                            } else {
                                imageView.setImage(IMAGE_RIGHT);
                            }
                        } else if (item.contains("left")) {
                            if (item.contains("slight")) {
                                imageView.setImage(IMAGE_SLLEFT);
                            } else if (item.contains("sharp")) {
                                imageView.setImage(IMAGE_SHLEFT);
                            } else {
                                imageView.setImage(IMAGE_LEFT);
                            }
                        } else if (item.contains("elevator")) {
                            imageView.setImage(IMAGE_ELEV);
                        } else if (item.contains("stair")) {
                            imageView.setImage(IMAGE_STAIR);
                        } else if (item.contains("destination")) {
                            imageView.setImage(IMAGE_DEST);
                        }
                        setText(item);
                        setGraphic(imageView);


                        setMinWidth(getWidth());
                        setMaxWidth(getWidth());
                        setPrefWidth(getWidth());

                        // allow wrapping
                        setWrapText(true);

                        setText(item);


                    }
                }
            };
        });
        direct.clear();
        direct.addAll(message);
        dirList.getItems().addAll(direct);

        for (String direction : message) {
            builder.append(direction + "\n\n");
        }

        return builder.toString();
    }

    private void highLightPath() {
        Iterator<Node> nodeIterator = path.iterator();

        // Loop through each node in the path and select it as well as the edge pointing to the next node
        Node currentNode = nodeIterator.next();
        Node nextNode;

        while (nodeIterator.hasNext()) {
            nextNode = nodeIterator.next();
            EdgeGUI edgeGUI = map.getEdgeGUI(currentNode.getEdge(nextNode));

            if (edgeGUI != null) map.getSelector().add(edgeGUI);

            currentNode = nextNode;
        }

        NodeGUI start = map.getNodeGUI(path.getPathNodes().get(0));
        NodeGUI end = map.getNodeGUI(path.getPathNodes().get(path.getPathNodes().size() - 1));

        if (start != null) {
            start.setVisible(true);
            start.getCircle().setFill(new ImagePattern(new Image("/edu/wpi/cs3733/d20/teamL/assets/nodes_filled/START_filled.png")));
        }
        if (end != null) {
            end.setVisible(true);
            end.getCircle().setFill(new ImagePattern(new Image("/edu/wpi/cs3733/d20/teamL/assets/nodes_filled/END_filled.png")));
        }
    }

    public MapPane getMap() {
        return map;
    }

    @FXML
    public void handleText() {
        try {
            Parent root = loaderHelper.getFXMLLoader("map_viewer/SendDirectionsPage").load();
            loaderHelper.setupPopup(new Stage(), new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }

    @FXML
    public void genQR() {
        try {
            Parent root = loaderHelper.getFXMLLoader("map_viewer/QRCode").load();
            loaderHelper.setupPopup(new Stage(), new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }

    @FXML
    public void handleFloor(ActionEvent event) {
        JFXButton sourceButton = (JFXButton) event.getSource();

        if (event.getSource() == floorUp && map.getFloor() < 5) {
            setFloor(map.getFloor() + 1);
        } else if (event.getSource() == floorDown) {
            setFloor(map.getFloor() - 1);
        } else {
            setFloor(Node.floorStringToInt(sourceButton.getText()));
        }

        if (!path.getPathNodes().isEmpty()) highLightPath();
    }

    @FXML
    private void zoomIn() {
        map.setZoomLevelToPosition(map.getZoomLevel() * 1.2, new Point2D(map.getBody().getWidth() / 2, map.getBody().getHeight() / 2));
    }

    @FXML
    private void zoomOut() {
        map.setZoomLevelToPosition(map.getZoomLevel() * 0.8, new Point2D(map.getBody().getWidth() / 2, map.getBody().getHeight() / 2));
    }

    public void setFloor(int newFloor) {
        map.setFloor(Math.max(map.getBuilding().getMinFloor(), Math.min(newFloor, map.getBuilding().getMaxFloor())));

        for (javafx.scene.Node node : floorSelector.getChildren()) {
            JFXButton floorButton = (JFXButton) node;
            if (!floorButton.getText().equals(Node.floorIntToString(map.getFloor()))) {
                if (floorButton.getStyleClass().contains("selected-floor")) {
                    floorButton.getStyleClass().clear();
                    floorButton.getStyleClass().add("button");
                    floorButton.getStyleClass().add("floor-buttons");
                }
            } else {
                if (!floorButton.getStyleClass().contains("selected-floor"))
                    floorButton.getStyleClass().add("selected-floor");
            }
        }
    }

    /**
     * Clears the text in source textfield
     */
    @FXML
    private void clearSource() {
        startingPoint.clear();
    }

    /**
     * Clears the text in destination textfield
     */
    @FXML
    private void clearDest() {
        destination.clear();
    }

    /**
     * login pops up when login button is clicked
     */
    @FXML
    private void loginBtnClicked() {
        try {
            Parent root = loaderHelper.getFXMLLoader("staff/LoginPage").load();
            loaderHelper.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    /**
     * Displays the About page of the application
     */
    @FXML
    public void handleAbout() {

        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("About"));
        content.setBody(new Text("WPI Computer Science Department\n" +
                "CS3733-D20 Software Engineering\n" +
                "Prof. Wilson Wong\n" +
                "Team Coach: Chris Myers\n" +
                "Lead Software Engineer: Conrad Tulig\n" +
                "Assistant Lead Software Engineer: Luke Bodwell\n" +
                "Assistant Lead Software Engineer: Caleb Farwell\n" +
                "Project Manager: Joshua Hoy\n" +
                "Scrum Master: Colin Scholler\n" +
                "Product Owner: Tori Buyck\n" +
                "Algorithms Specialist: Cameron Jacobson\n" +
                "UI Engineer: Winnie Ly\n" +
                "Documentation Analyst: Zaiyang Zhong\n\n" +
                "Thank you Brigham and Women's Hospital and Andrew Shinn for your time and input."));
        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        JFXButton btnDone = new JFXButton("Done");
        btnDone.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.close();
            }
        });
        content.setActions(btnDone);
        dialog.show();
    }

    private TimerTask timerWrapper(Runnable r) {
        return new TimerTask() {
            @Override
            public void run() {
                r.run();
            }
        };
    }

    private void updateTime() {
        ;
        Platform.runLater(() -> timeLabel.setText(new SimpleDateFormat("E, MMM d | h:mm aa").format(new Date())));
    }

    /**
     * Changes starting location with destination and vice-versa.
     */
    @FXML
    public void handleLocationChange() {

        String startLoc = startingPoint.getText();
        String destLoc = destination.getText();

        startingPoint.setText(destLoc);
        destination.setText(startLoc);
    }

    @FXML
    public void navigateFloor1() {
        destination.setText((String) listF1.getSelectionModel().getSelectedItem());
        navigate();
    }

    @FXML
    public void navigateFloor2() {
        destination.setText((String) listF2.getSelectionModel().getSelectedItem());
        navigate();
    }

    @FXML
    public void navigateFloor3() {
        destination.setText((String) listF3.getSelectionModel().getSelectedItem());
        navigate();
    }

    @FXML
    public void navigateFloor4() {
        destination.setText((String) listF4.getSelectionModel().getSelectedItem());
        navigate();
    }

    @FXML
    public void navigateFloor5() {
        destination.setText((String) listF5.getSelectionModel().getSelectedItem());
        navigate();
    }

    @FXML
    private void goToSelected() {
        int index = dirList.getSelectionModel().getSelectedIndex();

        ArrayList<Node> subpath = path.getSubpaths().get(index);

        if (dirList.getSelectionModel().getSelectedItem().toString().contains("Navigate")) {
            if (subpath.get(0).getBuilding().equals("Faulkner")) {
                map.setBuilding(new Building("Google"));
                map.setMapImage(IMAGE_FTOM);
            } else if (subpath.get(0).getBuilding().equals("BTM")) {
                map.setBuilding(new Building("Google"));
                map.setMapImage(IMAGE_MTOF);
            }

            generateFloorButtons();
            map.setZoomLevel(1.1);
        } else {
            if (!(subpath.get(0).getBuilding().equals(map.getBuilding().getName()))) {
                map.setBuilding(subpath.get(0).getBuilding());
                generateFloorButtons();
            }
            setFloor(subpath.get(0).getFloor());

            double totalX = 0;
            double totalY = 0;
            double minX = 200000;
            double maxX = 0;
            double minY = 200000;
            double maxY = 0;
            for (Node node : subpath) {
                double xPos = node.getPosition().getX();
                double yPos = node.getPosition().getY();
                double xPosGui = map.getNodeGUI(node).getLayoutX();
                double yPosGui = map.getNodeGUI(node).getLayoutY();

                totalX += xPosGui;
                totalY += yPosGui;

                if (xPos > maxX) maxX = xPos;
                if (xPos < minX) minX = xPos;
                if (yPos > maxY) maxY = yPos;
                if (yPos < minY) minY = yPos;
            }

            double diffX = maxX - minX;
            double diffY = maxY - minY;
            double scale;

            if (diffX > diffY) scale = Math.min(400 / diffX, 5);
            else scale = Math.min(400 / diffY, 5);

            totalX = totalX / subpath.size();
            totalY = totalY / subpath.size();

            map.setZoomLevelToPosition(scale, new Point2D(totalX, totalY));
            highLightPath();
        }
    }
}
