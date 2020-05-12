package edu.wpi.cs3733.d20.teamL.views.controllers.map;

import java.io.IOException;
import java.util.*;

import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.jfoenix.controls.*;
import com.squareup.okhttp.*;
import edu.wpi.cs3733.d20.teamL.App;
import edu.wpi.cs3733.d20.teamL.services.IHTTPClientService;
import edu.wpi.cs3733.d20.teamL.services.accessability.ITextToSpeechService;
import edu.wpi.cs3733.d20.teamL.entities.*;
import edu.wpi.cs3733.d20.teamL.services.messaging.IMessengerService;
import edu.wpi.cs3733.d20.teamL.services.pathfinding.IPathfinderService;
import edu.wpi.cs3733.d20.teamL.util.AsyncTaskManager;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import edu.wpi.cs3733.d20.teamL.views.controllers.screening.QuestionnaireController;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import edu.wpi.cs3733.d20.teamL.util.TimerManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import edu.wpi.cs3733.d20.teamL.util.search.SearchFields;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.google.inject.Inject;

import javafx.util.Duration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.entities.Path;
import edu.wpi.cs3733.d20.teamL.views.components.EdgeGUI;
import edu.wpi.cs3733.d20.teamL.views.components.MapPane;
import edu.wpi.cs3733.d20.teamL.views.components.NodeGUI;
import org.json.JSONObject;

import static java.awt.Color.BLACK;

@Slf4j
public class MapViewerController {
    private final FXMLLoaderFactory loaderFactory = new FXMLLoaderFactory();
    private final TimerManager timerManager = new TimerManager();
    private SearchFields searchFields;
    private JFXAutoCompletePopup<String> autoCompletePopup;
    private final ObservableList<String> directions = FXCollections.observableArrayList();
    private Path path = new Path();
    @FXML
    private JFXToggleButton handicapToggle;
    @FXML
    private MapPane map;
    @FXML
    private JFXTextField startingPoint, destination;
    @FXML
    private JFXButton btnNavigate, floorUp, floorDown, btnScreening, btnTextMe, btnQR, btnRobot, btnTextToSpeechStart, btnTextToSpeechDestination, btnFeedback, btnLegend, btnAbout;
    @FXML
    private VBox sideBox, floorSelector, directionButtonsVBox, textDirectionsVBox;
    @FXML
    private HBox getDirectionsHBox;
    @FXML
    private JFXListView dirList = new JFXListView();
    @FXML
    StackPane stackPane, keyStackPane, screeningPane;
    @FXML
    private JFXListView listF1, listF2, listF3, listF4, listF5;
    @FXML
    private JFXComboBox<String> buildingChooser;
    @FXML
    private Accordion accordion = new Accordion();
    @FXML
    private Label timeLabel, dateLabel, currentTempLabel, etaLabel, btnMute;
    @FXML
    private ImageView currentWeatherIcon;
    @FXML
    private TitledPane departments, amenities, labs, services, conferenceRooms;

    @Inject
    private IDatabaseCache cache;
    @Inject
    private IPathfinderService pathfinderService;
    @Inject
    private IMessengerService messenger;
    @Inject
    private ITextToSpeechService textToSpeech;
    @Inject
    private IHTTPClientService httpClient;


    private final Image IMAGE_LEFT = new Image("/edu/wpi/cs3733/d20/teamL/assets/Directions/left.png", 15, 15, true, false, true);
    private final Image IMAGE_RIGHT = new Image("/edu/wpi/cs3733/d20/teamL/assets/Directions/right.jpg", 15, 15, true, false, true);
    private final Image IMAGE_SHLEFT = new Image("/edu/wpi/cs3733/d20/teamL/assets/Directions/sharp left.jpg", 15, 15, true, false, true);
    private final Image IMAGE_SHRIGHT = new Image("/edu/wpi/cs3733/d20/teamL/assets/Directions/sharp right.jpg", 15, 15, true, false, true);
    private final Image IMAGE_SLLEFT = new Image("/edu/wpi/cs3733/d20/teamL/assets/Directions/slightLeft.jpg", 15, 15, true, false, true);
    private final Image IMAGE_SLRIGHT = new Image("/edu/wpi/cs3733/d20/teamL/assets/Directions/slightRight.jpg", 15, 15, true, false, true);
    private final Image IMAGE_STRAIGHT = new Image("/edu/wpi/cs3733/d20/teamL/assets/Directions/straight.png", 15, 15, true, false, true);
    private final Image IMAGE_ELEV = new Image("/edu/wpi/cs3733/d20/teamL/assets/Directions/elevator.jpg", 15, 15, true, false, true);
    private final Image IMAGE_STAIR = new Image("/edu/wpi/cs3733/d20/teamL/assets/Directions/stair.png", 15, 15, true, false, true);
    private final Image IMAGE_DEST = new Image("/edu/wpi/cs3733/d20/teamL/assets/Directions/destFlag.png", 15, 15, true, false, true);
    private final Image IMAGE_FTOM = new Image("/edu/wpi/cs3733/d20/teamL/assets/maps/FaulkToMain.PNG");
    private final Image IMAGE_MTOF = new Image("/edu/wpi/cs3733/d20/teamL/assets/maps/MainToFaulk.PNG");

    private final Image EXIT_filled = new Image("/edu/wpi/cs3733/d20/teamL/assets/nodes_filled/EXIT_filled.png", 45, 45, true, true, true);
    private final Image REST_filled = new Image("/edu/wpi/cs3733/d20/teamL/assets/nodes_filled/REST_filled.png", 45, 45, true, false, true);
    private final Image INFO_filled = new Image("/edu/wpi/cs3733/d20/teamL/assets/nodes_filled/INFO_filled.png", 45, 45, true, false, true);
    private final Image ELEV_filled = new Image("/edu/wpi/cs3733/d20/teamL/assets/nodes_filled/ELEV_filled.png", 45, 45, true, false, true);
    private final Image STAI_filled = new Image("/edu/wpi/cs3733/d20/teamL/assets/nodes_filled/STAI_filled.png", 45, 45, true, false, true);
    private final Image RETL_filled = new Image("/edu/wpi/cs3733/d20/teamL/assets/nodes_filled/RETL_filled.png", 45, 45, true, false, true);

    private ArrayList<String> deptNodes = new ArrayList<>();
    private ArrayList<String> labNodes = new ArrayList<>();
    private ArrayList<String> serviceNodes = new ArrayList<>();
    private ArrayList<String> retailNodes = new ArrayList<>();
    private ArrayList<String> confNodes = new ArrayList<>();
    private ArrayList<String> newDeptNodes = new ArrayList<>();
    private ArrayList<String> newLabNodes = new ArrayList<>();
    private ArrayList<String> newServiceNodes = new ArrayList<>();
    private ArrayList<String> newRetailNodes = new ArrayList<>();
    private ArrayList<String> newConfNodes = new ArrayList<>();

    private QuestionnaireController qc;
    private String currentLang = "en";


    public static final String MAIN = "Main";

    @FXML
    private void initialize() throws IOException {
        timerManager.startTimer(() -> timerManager.updateTime(timeLabel), 0, 1000);
        timerManager.startTimer(() -> timerManager.updateDate(dateLabel), 0, 1000);
        //ToDO: uncomment this when its time to get weather
        //timerManager.startTimer(() -> timerManager.updateWeather(currentTempLabel, currentWeatherIcon), 0,1800000);
        if (App.doUpdateCacheOnLoad) {
            cache.cacheAllFromDB();
            App.doUpdateCacheOnLoad = false;
        }
        startingPoint.setLabelFloat(false);
        map.setEditable(false);
        map.setHighLightColor(Color.SPRINGGREEN);
        btnNavigate.setDisableVisualFocus(true);

        // Stops stackPanes from stoping you clicking on whats underneath
        stackPane.setPickOnBounds(false);
        keyStackPane.setPickOnBounds(false);
        screeningPane.setPickOnBounds(false);

        dirList.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent -> goToSelected()));
        // Import all the nodes from the cache and set the current building to Faulkner
        String startB = "Faulkner";
        Building faulkner = cache.getBuilding("Faulkner");
        Building main = cache.getBuilding(MAIN);

        if (!faulkner.getNodes().isEmpty()) map.setBuilding(faulkner);
        if (!main.getNodes().isEmpty()) map.getBuildings().add(main);
        buildingChooser.getItems().addAll("Faulkner", MAIN);
        buildingChooser.getSelectionModel().select(startB);

        //Set Current Language


        // Add floor buttons
        generateFloorButtons();

        setFloor(2);

        map.setZoomLevel(0.25 * App.UI_SCALE);
        map.init();

        // Populate autocomplete
        searchFields = new SearchFields(cache.getNodeCache());
        searchFields.getFields().addAll(Arrays.asList(SearchFields.Field.shortName, SearchFields.Field.longName));
        searchFields.populateSearchFields();
        autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(searchFields.getSuggestions());

        Collection<Node> allNodes = map.getBuilding().getNodes();

        for (Node node : allNodes) {
            if (node.getType().equals("DEPT")) {
                deptNodes.add(node.getLongName() + " - (" + node.getBuilding() + " " + node.getFloor() + ")");
            } else if (node.getType().equals("LABS")) {
                labNodes.add(node.getLongName() + " - (" + node.getBuilding() + " " + node.getFloor() + ")");
            } else if ((node.getType().equals("SERV") || node.getType().equals("INFO"))) {
                serviceNodes.add(node.getLongName() + " - (" + node.getBuilding() + " " + node.getFloor() + ")");
            } else if (node.getType().equals("RETL")) {
                retailNodes.add(node.getLongName() + " - (" + node.getBuilding() + " " + node.getFloor() + ")");
            } else if (node.getType().equals("CONF")) {
                confNodes.add(node.getLongName() + " - (" + node.getBuilding() + " " + node.getFloor() + ")");
            }
        }

        listF1 = new JFXListView();
        listF2 = new JFXListView();
        listF3 = new JFXListView();
        listF4 = new JFXListView();
        listF5 = new JFXListView();

        JFXListView[] listOfListViews = new JFXListView[]{listF1, listF2, listF3, listF4, listF5};
        for (JFXListView list : listOfListViews) {
            list.addEventHandler(MouseEvent.MOUSE_CLICKED, (mouseEvent -> {
                if (currentLang.equals("en")) {
                    destination.setText((String) list.getSelectionModel().getSelectedItem());
                } else {
                if (listF1.equals(list)) {
                    destination.setText(deptNodes.get(newDeptNodes.indexOf(listF1.getSelectionModel().getSelectedItem())));
                } else if (listF2.equals(list)) {
                    destination.setText(labNodes.get(newLabNodes.indexOf(listF2.getSelectionModel().getSelectedItem())));
                } else if (listF3.equals(list)) {
                    destination.setText(serviceNodes.get(newServiceNodes.indexOf(listF3.getSelectionModel().getSelectedItem())));
                } else if (listF4.equals(list)) {
                    destination.setText(retailNodes.get(newRetailNodes.indexOf(listF4.getSelectionModel().getSelectedItem())));
                } else if (listF5.equals(list)) {
                    destination.setText(confNodes.get(newConfNodes.indexOf(listF5.getSelectionModel().getSelectedItem())));
                }
                  }
                navigate();
            }));
            list.setStyle("-fx-font-size: 16");
        }

        listF1.getItems().addAll(deptNodes);
        listF2.getItems().addAll(labNodes);
        listF3.getItems().addAll(serviceNodes);
        listF4.getItems().addAll(retailNodes);
        listF5.getItems().addAll(confNodes);

        departments = new TitledPane("Departments", listF1);
        departments.setStyle("-fx-font-size: 16;" + "-fx-body-color: #7DA7D9;");
        labs = new TitledPane("Labs", listF2);
        labs.setStyle("-fx-font-size: 16;" + "-fx-body-color: #8881BD;");
        services = new TitledPane("Services/Information", listF3);
        services.setStyle("-fx-font-size: 16;" + "-fx-body-color: #F5989D;");
        amenities = new TitledPane("Amenities", listF4);
        amenities.setStyle("-fx-font-size: 16;" + "-fx-body-color: #79BD92;");
        conferenceRooms = new TitledPane("Conference Rooms", listF5);
        conferenceRooms.setStyle("-fx-font-size: 16;" + "-fx-body-color: #AD87AD;");

        accordion.getPanes().addAll(departments, labs, services, amenities, conferenceRooms);
        accordion.setStyle("-fx-effect: dropshadow(three-pass-box, derive(BLACK, -20%), 10, 0, 2, 2)");
        // Create directions buttons
        directionButtonsVBox = new VBox();
        directionButtonsVBox.setAlignment(Pos.CENTER);
        directionButtonsVBox.setSpacing(10);

        btnTextMe = new JFXButton();
        btnTextMe.setText("Text me directions");
        btnTextMe.getStyleClass().add("save-button-jfx");
        btnTextMe.setStyle("-fx-pref-width: 200;" + "-fx-max-width: 200;" + "-fx-background-color: #00043B;" + "-fx-background-radius: 50;");
        btnTextMe.setOnAction(actionEvent -> handleText());

        btnQR = new JFXButton();
        btnQR.setText("Scan directions");
        btnQR.getStyleClass().add("save-button-jfx");
        btnQR.setStyle("-jfx-button-type: RAISED;" + "-fx-pref-width: 200;" + "-fx-max-width: 200;" + "-fx-background-color: #00043B;" + "-fx-background-radius:  50;");
        btnQR.setOnAction(actionEvent -> genQR());

        btnRobot = new JFXButton();
        btnRobot.setText("Escort me there");
        btnRobot.getStyleClass().add("save-button-jfx");
        btnRobot.setStyle("-jfx-button-type: RAISED;" + "-fx-pref-width: 200;" + "-fx-max-width: 200;" + "-fx-background-color: #00043B;" + "-fx-background-radius:  50;");
        btnRobot.setOnAction(actionEvent -> launchRobot());

        directionButtonsVBox.getChildren().addAll(btnTextMe, btnQR, btnRobot);
        showDefaultOptions();

        // Fill text directions box
        textDirectionsVBox = new VBox();
        textDirectionsVBox.setStyle("-fx-effect: dropshadow(three-pass-box, derive(BLACK, -20%), 10, 0, 2, 2)");
        HBox dirListHeader = new HBox();
        dirListHeader.setStyle("-fx-alignment: center;" + "-fx-background-color: #00043B;");

        etaLabel = new Label("Directions");
        etaLabel.setStyle("-fx-text-fill: white;" + "-fx-font-weight: bold;" + "-fx-font-size: 16;");

        JFXButton speakAllButton = new JFXButton("", new ImageView(new Image("/edu/wpi/cs3733/d20/teamL/assets/home_page/speakerIcon.png", 0, 15, true, false, true)));
        speakAllButton.setPadding(Insets.EMPTY);
        speakAllButton.setStyle("-fx-background-color: transparent;" + "-fx-content-display: graphic-only;");
        speakAllButton.setOnAction(e -> speakAllDirections());

        btnMute = new Label("un-muted");
        btnMute.setStyle("-fx-text-fill: white;" + "-fx-font-size: 16;" + "-fx-background-color: transparent;" + "-fx-max-height: 15;");
        btnMute.setPadding(Insets.EMPTY);
        btnMute.setOnMouseClicked(e -> toggleAudio());

        HBox muteHBox = new HBox();
        muteHBox.setStyle("-fx-alignment: center-left;" + "-fx-background-color: #00043B;");

        dirListHeader.getChildren().addAll(etaLabel, speakAllButton);
        muteHBox.getChildren().addAll(btnMute);
        muteHBox.setAlignment(Pos.CENTER_LEFT);
        muteHBox.setMaxHeight(50);
        textDirectionsVBox.getChildren().addAll(dirListHeader, dirList, muteHBox);

        // Create Screening Button
        btnScreening.setText("Think you have COVID-19?");
        btnScreening.setStyle("-fx-font-weight: bold");
        btnScreening.setMinWidth(300);
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
        map.setZoomLevel(.25 * App.UI_SCALE);
        if (!path.getPathNodes().isEmpty()) highLightPath();
    }

    @FXML
    private void startingPointAutocomplete() {
        searchFields.applyAutocomplete(startingPoint, autoCompletePopup);
    }

    @FXML
    private void destinationAutocomplete() {
        searchFields.applyAutocomplete(destination, autoCompletePopup);
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

        Node startNode = searchFields.getNode(start);
        Node destNode = searchFields.getNode(end);

        if (!(startNode.getBuilding().equals(map.getBuilding().getName()))) {
            map.setBuilding(startNode.getBuilding());
            buildingChooser.getSelectionModel().select(startNode.getBuilding());
        }

        // Set the building and floor to the start node so it doesn't break
        map.setBuilding(startNode.getBuilding());
        buildingChooser.getSelectionModel().select(startNode.getBuilding());
        setFloor(startNode.getFloor());

        if (startNode != null && destNode != null) {
            String directions = highlightSourceToDestination(startNode, destNode);

            messenger.setDirections(directions);

            btnTextMe.setDisable(false);
            btnTextMe.setVisible(true);
            btnQR.setDisable(false);
            btnQR.setVisible(true);
            btnRobot.setDisable(false);
            btnRobot.setVisible(true);
//            textDirNode.setDisable(false);
//            textDirNode.setVisible(true);
        }
        showPathFindingOptions();

    }

    /**
     * Shows the key popup
     */
    @FXML
    private void showLegend() throws IOException {
        JFXDialogLayout legendContent = new JFXDialogLayout();
        Label title = new Label("Map Legend");
        title.setStyle("-fx-font-size: 30;" + "-fx-text-fill: #0d2e57;" + "-fx-font-weight: bold");
        legendContent.setHeading(title);

        HBox contentHBox = new HBox();
        VBox colorKey = new VBox();
        colorKey.setMinWidth(300);
        colorKey.setSpacing(5);
        VBox iconKey = new VBox();
        iconKey.setMinWidth(160);
        iconKey.setSpacing(5);

        String[] colors = new String[]{" #7DA7D9", " #FCB963", " #FFF77D", " #79BD92", " #8881BD", " #F69679", " #6DCFF6", " #AD87AD", " #BDDEA2", " #F5989D", " #7DA7D9"};
        String[] colorText = new String[]{"Departments/Clinics/Waiting Area", "Stairwell", "Restrooms", "Food/Shops/Payphone/etc.", "Labs/Imaging/Testing Areas",
                "Exits/Entrances", "Info Desk/Security/Lost and Found", "Conference Rooms", "Elevators", "Interpreters/Spiritual/Library/etc", "Departments/Clinics/Waiting Area"};
        Image[] icons = new Image[]{EXIT_filled, REST_filled, INFO_filled, ELEV_filled, STAI_filled, RETL_filled};
        String[] iconText = new String[]{"Exit/Entrance", "Restrooms", "Information Desk", "Elevator", "Stairs", "Retail Locations"};

        for (int i = 0; i < colors.length; i++) {
            JFXButton colorSwatch = new JFXButton();
            String fxColor = "-fx-background-color: " + colors[i] + ";";
            colorSwatch.setStyle("-fx-min-height: 30;" + "-fx-min-width: 30;" + "-fx-border-radius: 0;" + fxColor);
            VBox swatchText;

            if (currentLang.equals("en")) {
                swatchText = new VBox(new Label(colorText[i]));
            } else {
                swatchText = new VBox(new Label(httpClient.translate("en", currentLang, colorText[i])));
            }
            swatchText.setAlignment(Pos.CENTER);

            HBox colorRow = new HBox();
            colorRow.setSpacing(5);
            colorRow.getChildren().setAll(colorSwatch, swatchText);

            colorKey.getChildren().add(colorRow);
        }

        for (int i = 0; i < icons.length; i++) {
            ImageView icon = new ImageView(icons[i]);

            VBox displayText;
            if (currentLang.equals("en")) {
                displayText = new VBox(new Label(iconText[i]));
            } else {
                displayText = new VBox(new Label(httpClient.translate("en", currentLang, iconText[i])));
            }

            displayText.setAlignment(Pos.CENTER);


            HBox iconRow = new HBox();
            iconRow.setSpacing(5);
            iconRow.getChildren().setAll(icon, displayText);

            iconKey.getChildren().add(iconRow);
        }

        contentHBox.getChildren().addAll(colorKey, iconKey);
        legendContent.setBody(contentHBox);

        JFXDialog legend = new JFXDialog(keyStackPane, legendContent, JFXDialog.DialogTransition.TOP);


        JFXButton btnClose = new JFXButton("X");
        btnClose.setStyle("-fx-font-weight: bolder");
        btnClose.setOnAction(e -> legend.close());
        legendContent.setActions(btnClose);

        legend.show();
    }

    /**
     * Shows the future weather
     */
    @FXML
    private void openNextHoursWeather() {

    }

    private void clearPath() {
        map.getSelector().clear();

        if (!path.getPathNodes().isEmpty()) {
            NodeGUI start = map.getNodeGUI(path.getPathNodes().get(0));
            NodeGUI end = map.getNodeGUI(path.getPathNodes().get(path.getPathNodes().size() - 1));

            map.resetNodeVisibility(start);
            map.resetNodeVisibility(end);
        }
        path.getPathNodes().clear();
    }

    private String highlightSourceToDestination(Node source, Node destination) {
        clearPath();

        path = pathfinderService.pathfind(map.getAllNodes(), source, destination);
        highLightPath();

        path.generateTextMessage();
        ArrayList<String> message = path.getMessage();
        //TODO: make this a separate thing
        etaLabel.setText(message.get(0));
        message.remove(0);

        StringBuilder builder = new StringBuilder();

        dirList.getItems().clear();
        dirList.setStyle("-fx-font-size: 15");
        dirList.setCellFactory(param -> {
            return new ListCell<String>() {
                private ImageView imageView = new ImageView();


                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                        setText(null);
                        // other stuff to do...
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
                        } else {
                            imageView.setImage(IMAGE_STRAIGHT);
                        }
                        setText(item);
                        setGraphic(imageView);


                        setMinWidth(getWidth());
                        setMaxWidth(getWidth());
                        setPrefWidth(getWidth());

                        // allow wrapping
                        setWrapText(true);

                        if (currentLang != "en") {
                            try {
                                setText(httpClient.translate("en", currentLang, item));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            setText(item);
                        }

                    }
                }
            };
        });
        directions.clear();
        directions.addAll(message);
        dirList.getItems().addAll(directions);

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

            // Please help me untangle my spaghetti
            if (edgeGUI != null) {
                edgeGUI.getHighlightGUI().getStrokeDashArray().setAll(5d, 20d, 20d, 5d);
                Line highlight = map.getEdgeGUI(currentNode.getEdge(nextNode)).getHighlightGUI();

                final double maxOffset = highlight.getStrokeDashArray().stream().reduce(0d, (a, b) -> a + b);
                Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(highlight.strokeDashOffsetProperty(), maxOffset, Interpolator.LINEAR)),
                        new KeyFrame(Duration.seconds(2), new KeyValue(highlight.strokeDashOffsetProperty(), 0, Interpolator.LINEAR)));

                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play();
            }

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
    private void toggleHandicap() {
        pathfinderService.setHandicapped(handicapToggle.isSelected());
    }

    @FXML
    public void handleText() {
        try {
            Parent root = loaderFactory.getFXMLLoader("map_viewer/SendDirectionsPage").load();
            loaderFactory.setupPopup(new Stage(), new Scene(root));
        } catch (IOException e) {
            log.error("Encountered IOException", e);
        }
    }

    @FXML
    public void genQR() {
        try {
            Parent root = loaderFactory.getFXMLLoader("map_viewer/QRCode").load();
            loaderFactory.setupPopup(new Stage(), new Scene(root));
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
        map.getSelector().clear();
        showDefaultOptions();
        clearPath();
    }

    /**
     * Clears the text in destination textfield
     */
    @FXML
    private void clearDest() {
        destination.clear();
        map.getSelector().clear();
        showDefaultOptions();
        clearPath();
    }

    /**
     * login pops up when login button is clicked
     */
    @FXML
    private void loginBtnClicked() {
        try {
            Parent root = loaderFactory.getFXMLLoader("staff/LoginPage").load();
            loaderFactory.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }
    }

    /**
     * Displays the About page of the application
     */
    @FXML
    public void handleAbout() {
        AsyncTaskManager.newTask(() -> {
            Platform.runLater(() -> {
                try {
                    translateMapViewer("es");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
//        JFXDialogLayout content = new JFXDialogLayout();
//        content.setHeading(new Text("About"));
//        content.setBody(new Text("WPI Computer Science Department\n" +
//                "CS3733-D20 Software Engineering\n" +
//                "Prof. Wilson Wong\n" +
//                "Team Coach: Chris Myers\n" +
//                "Lead Software Engineer: Conrad Tulig\n" +
//                "Assistant Lead Software Engineer: Luke Bodwell\n" +
//                "Assistant Lead Software Engineer: Caleb Farwell\n" +
//                "Project Manager: Joshua Hoy\n" +
//                "Scrum Master: Colin Scholler\n" +
//                "Product Owner: Tori Buyck\n" +
//                "Algorithms Specialist: Cameron Jacobson\n" +
//                "UI Engineer: Winnie Ly\n" +
//                "Documentation Analyst: Zaiyang Zhong\n\n" +
//                "Thank you Brigham and Women's Hospital \nand Andrew Shinn for your time and input."));
//        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.BOTTOM);
//        JFXButton btnDone = new JFXButton("Done");
//        btnDone.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                dialog.close();
//            }
//        });
//        content.setActions(btnDone);
//        dialog.show();
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
    public void openScreening() throws IOException {

        qc = new QuestionnaireController(cache.getQuestions());

        JFXDialogLayout layout = new JFXDialogLayout();
        layout.getStylesheets().add("/edu/wpi/cs3733/d20/teamL/css/GlobalStyleSheet.css");
        Label headingLabel = new Label("Coronavirus Screening Test");
        headingLabel.getStyleClass().add("service-request-header-label-fx");
        headingLabel.setStyle("-fx-font-size: 30;");
        layout.setHeading(headingLabel);
        layout.setBody(qc.nextClicked());

        JFXDialog screeningDialog = new JFXDialog(screeningPane, layout, JFXDialog.DialogTransition.TOP);
        screeningDialog.getStylesheets().add("/edu/wpi/cs3733/d20/teamL/css/GlobalStyleSheet.css");
        screeningDialog.show();

        JFXButton btnClose = new JFXButton("Quit");
        btnClose.getStyleClass().add("cancel-button-jfx");
        btnClose.setStyle("-fx-pref-width: 75;" + "-fx-pref-height: 50");
        btnClose.setOnAction(e -> screeningDialog.close());

        JFXButton btnNext = new JFXButton("Next");
        btnNext.getStyleClass().add("save-button-jfx");
        btnNext.setStyle("-fx-pref-width: 75;" + "-fx-pref-height: 50");
        btnNext.setOnAction(e -> {
            if (!qc.getTestFinished()) {
                //System.out.println("first statement");
                qc.calculateScore();
                layout.setHeading(qc.nextClicked());
            } else if (qc.getTestFinished() && !qc.getDone()) {
                //System.out.println("second statement");
                qc.calculateScore();
                btnNext.setText("Close");
                layout.setHeading(qc.nextClicked());
                btnClose.setVisible(false);
                btnClose.setDisable(true);
            } else if (qc.getDone()) {
                screeningDialog.close();
            }
        });
        layout.setActions(btnClose, btnNext);
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
        AsyncTaskManager.newTask(() -> {
            try {
                textToSpeech.playSpeech( textToSpeech.convertTextToSpeech(currentLang.equals("en") ? dirList.getSelectionModel().getSelectedItem().toString(): httpClient.translate("en", currentLang, dirList.getSelectionModel().getSelectedItem().toString() ), currentLang.equals("en") ? "en-US" : currentLang, SsmlVoiceGender.MALE));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        int index = dirList.getSelectionModel().getSelectedIndex();
        ArrayList<Node> subpath = path.getSubpaths().get(index);
        if (dirList.getSelectionModel().getSelectedItem().toString().contains("Navigate")) {
            if (subpath.get(0).getBuilding().equals("Faulkner")) {
                map.setBuilding(new Building("Google"));
                map.setMapImage(IMAGE_FTOM);
            } else if (subpath.get(0).getBuilding().equals(MAIN)) {
                map.setBuilding(new Building("Google"));
                map.setMapImage(IMAGE_MTOF);
            }

            generateFloorButtons();
            map.setZoomLevel(.25 * App.UI_SCALE);
        } else {
            if (!(subpath.get(0).getBuilding().equals(map.getBuilding().getName()))) {
                map.setBuilding(subpath.get(0).getBuilding());
                generateFloorButtons();
                map.setZoomLevel(.25 * App.UI_SCALE);
                goToSelected();
            }
            if (!(subpath.get(0).getFloorAsString().equals(map.getFloor())))
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

            if (diffX > diffY) scale = Math.min(400 / diffX, 2);
            else scale = Math.min(400 / diffY, 2);

            totalX = totalX / subpath.size();
            totalY = totalY / subpath.size();

            map.setZoomLevelToPosition(scale, new Point2D(totalX, totalY));
            highLightPath();
        }
    }

    private void launchRobot() {
        System.out.println("Wheres the robot Conrad?!?");
    }

    private void showDefaultOptions() {
        if (sideBox.getChildren().contains(textDirectionsVBox)) sideBox.getChildren().remove(textDirectionsVBox);
        if (sideBox.getChildren().contains(directionButtonsVBox)) sideBox.getChildren().remove(directionButtonsVBox);

        if (!sideBox.getChildren().contains(getDirectionsHBox)) sideBox.getChildren().add(getDirectionsHBox);
        if (!sideBox.getChildren().contains(accordion)) sideBox.getChildren().add(accordion);
    }

    private void showPathFindingOptions() {
        if (sideBox.getChildren().contains(accordion)) sideBox.getChildren().remove(accordion);
        if (sideBox.getChildren().contains(getDirectionsHBox)) sideBox.getChildren().remove(getDirectionsHBox);

        if (!sideBox.getChildren().contains(textDirectionsVBox)) sideBox.getChildren().add(textDirectionsVBox);
        if (!sideBox.getChildren().contains(directionButtonsVBox)) sideBox.getChildren().add(directionButtonsVBox);
    }

    public void textToSpeachStartIcon(ActionEvent actionEvent) {
    }

    public void textToSpeachDestinationIcon(ActionEvent actionEvent) {
    }

    @FXML
    private void handleFeedback() {
        try {
            Parent root = loaderFactory.getFXMLLoader("map_viewer/Feedback").load();
            loaderFactory.setupPopup(new Stage(), new Scene(root));
        } catch (IOException ex) {
            log.error("Encountered IOException", ex);
        }

    }

    @FXML
    private void handleHandicap() {

    }

    @FXML
    private void handleRobotDirections() {

    }

    private void speakAllDirections() {
        System.out.println("Ok time to talk now");
    }

    private void toggleAudio() {
        System.out.println("Toggle audio");
        if (btnMute.getText().equals("un-muted")) btnMute.setText("muted");
        else if (btnMute.getText().equals("muted")) btnMute.setText("un-muted");
    }


    public void translateMapViewer(String language) throws IOException {
        btnNavigate.setText(httpClient.translate("en", language, "Get Directions"));
        btnScreening.setText(httpClient.translate(currentLang, language, btnScreening.getText()));
        btnAbout.setText(httpClient.translate(currentLang, language, btnAbout.getText()));
        btnTextMe.setText(httpClient.translate(currentLang, language, btnTextMe.getText()));
        btnRobot.setText(httpClient.translate(currentLang, language, btnRobot.getText()));
        btnQR.setText(httpClient.translate(currentLang, language, btnQR.getText()));
        btnLegend.setText(httpClient.translate("en", language, "Legend"));
        btnFeedback.setText(httpClient.translate(currentLang, language, btnFeedback.getText()));
        departments.setText(httpClient.translate("en", language, "Directions"));
        labs.setText(httpClient.translate("en", language, "Labs"));
        services.setText(httpClient.translate("en", language, "Services/Information"));
        amenities.setText(httpClient.translate("en", language, "Amenities"));
        conferenceRooms.setText(httpClient.translate("en", language, "Conference Rooms"));


        for (String node : deptNodes) {
            newDeptNodes.add(httpClient.translate(currentLang, language, node));
        }

        listF1.getItems().removeAll(deptNodes);
        listF1.getItems().addAll(newDeptNodes);

        for (String node : labNodes) {
            newLabNodes.add(httpClient.translate(currentLang, language, node));
        }

        listF2.getItems().removeAll(labNodes);
        listF2.getItems().addAll(newLabNodes);

        for (String node : serviceNodes) {
            newServiceNodes.add(httpClient.translate(currentLang, language, node));
        }

        listF3.getItems().removeAll(serviceNodes);
        listF3.getItems().addAll(newServiceNodes);

        for (String node : retailNodes) {
            newRetailNodes.add(httpClient.translate(currentLang, language, node));
        }

        listF4.getItems().removeAll(retailNodes);
        listF4.getItems().addAll(newRetailNodes);

        for (String node : confNodes) {
            newConfNodes.add(httpClient.translate(currentLang, language, node));
        }

        listF5.getItems().removeAll(confNodes);
        listF5.getItems().addAll(newConfNodes);

        dirList.setCellFactory(param -> {
            return new ListCell<String>() {
                private ImageView imageView = new ImageView();
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                        setText(null);
                        // other stuff to do...
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
                        } else {
                            imageView.setImage(IMAGE_STRAIGHT);
                        }
                        setText(item);
                        setGraphic(imageView);
                        setMinWidth(getWidth());
                        setMaxWidth(getWidth());
                        setPrefWidth(getWidth());
                        // allow wrapping
                        setWrapText(true);
                        try {
                            setText(httpClient.translate("en", language, item));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
        });


        currentLang = language;
        httpClient.setCurrLang(currentLang);
    }
}
