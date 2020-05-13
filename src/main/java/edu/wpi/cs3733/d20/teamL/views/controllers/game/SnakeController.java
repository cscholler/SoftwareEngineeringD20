package edu.wpi.cs3733.d20.teamL.views.controllers.game;

import edu.wpi.cs3733.d20.teamL.App;
import edu.wpi.cs3733.d20.teamL.services.db.IDatabaseCache;
import edu.wpi.cs3733.d20.teamL.util.FXMLLoaderFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

@Slf4j
public class SnakeController {
	public enum Direction {
		UP, DOWN, LEFT, RIGHT
	}

	private Stage stage;
	private Direction direction = Direction.RIGHT;
	private static final int WIDTH = (int) App.SCREEN_WIDTH;
	private static final int HEIGHT = (int) App.SCREEN_HEIGHT;
	private static final int BLOCK_SIZE = 64;
	private static final Color hallNodeColor = Color.rgb(13, 46, 87);
	private boolean running = false;
	private final Timeline timeline = new Timeline();
	private final ArrayList<edu.wpi.cs3733.d20.teamL.entities.Node> nodes = new ArrayList<>();
	private final FXMLLoaderFactory loaderFactory = new FXMLLoaderFactory();
	private final IDatabaseCache cache = FXMLLoaderFactory.injector.getInstance(IDatabaseCache.class);
	private ObservableList<Node> snake;
	private int lossCount = 0;

	public void setup(Stage stage) {
		App.isScreenSaverActive = true;
		for (edu.wpi.cs3733.d20.teamL.entities.Node node : cache.getNodeCache()) {
			if (node.getBuilding().equals("Faulkner") && node.getFloorAsString().equals("2")) {
				nodes.add(node);
			}
		}
		Scene scene = new Scene(createPane());
		FXMLLoaderFactory.applyTimeouts(scene);
		scene.setOnKeyPressed(event -> {
			switch(event.getCode()) {
				case UP:
				case W: {
					if (direction != Direction.DOWN) {
						direction = Direction.UP;
					}
				}
				break;
				case DOWN:
				case S: {
					if (direction != Direction.UP) {
						direction = Direction.DOWN;
					}
				}
				break;
				case LEFT:
				case A: {
					if (direction != Direction.RIGHT) {
						direction = Direction.LEFT;
					}
				}
				break;
				case RIGHT:
				case D: {
					if (direction != Direction.LEFT) {
						direction = Direction.RIGHT;
					}
				}
				break;
				case ESCAPE: {
					returnToMapViewer();
				}
			}
		});

		stage.setScene(scene);
		stage.show();
		startGame();
	}

	public Scene getScene() {
		return new Scene(createPane());
	}

	private Pane createPane() {
		Pane root = new Pane();
		ImageView background = new ImageView(new Image("/edu/wpi/cs3733/d20/teamL/assets/maps/snake_map.png", App.SCREEN_WIDTH, 0, true, false, true));
		background.setStyle("-fx-translate-y: -100");
		Group snakeBody = new Group();
		snake = snakeBody.getChildren();
		Circle food = new Circle((BLOCK_SIZE / 2) * 1.5);
		int nodeIndex = new Random().nextInt(nodes.size());
		edu.wpi.cs3733.d20.teamL.entities.Node node = nodes.get(nodeIndex);
		Point2D nodePos = node.getPosition();
		food.setTranslateX(nodePos.getX());
		food.setTranslateY(nodePos.getY());
		if (node.getType().equals("HALL")) {
			food.setFill(hallNodeColor);
		} else {
			food.setFill(new ImagePattern(new Image("/edu/wpi/cs3733/d20/teamL/assets/nodes_filled/" + node.getType() + "_filled.png")));
		}

		KeyFrame frame = new KeyFrame(Duration.seconds(0.1), event -> {
			if (!running) {
				return;
			}
			boolean allowRemoval = snake.size() > 1;
			Node tail = allowRemoval ? snake.remove(snake.size() - 1) : snake.get(0);
			double tailX = tail.getTranslateX();
			double tailY = tail.getTranslateY();
			switch (direction) {
				case UP: {
					tail.setTranslateX(snake.get(0).getTranslateX());
					tail.setTranslateY(snake.get(0).getTranslateY() - BLOCK_SIZE);
				}
				break;
				case DOWN: {
					tail.setTranslateX(snake.get(0).getTranslateX());
					tail.setTranslateY(snake.get(0).getTranslateY() + BLOCK_SIZE);
				}
				break;
				case LEFT: {
					tail.setTranslateX(snake.get(0).getTranslateX() - BLOCK_SIZE);
					tail.setTranslateY(snake.get(0).getTranslateY());
				}
				break;
				case RIGHT: {
					tail.setTranslateX(snake.get(0).getTranslateX() + BLOCK_SIZE);
					tail.setTranslateY(snake.get(0).getTranslateY());
				}
			}
			if (allowRemoval) {
				snake.add(0, tail);
			}
			for (Node bodyPart : snake) {
				if (bodyPart != tail && tail.getTranslateX() == bodyPart.getTranslateX() && tail.getTranslateY() == bodyPart.getTranslateY()) {
					restartGame();
					break;
				}
			}
			if (tail.getTranslateX() < 0 || tail.getTranslateX() >= WIDTH || tail.getTranslateY() < 0 || tail.getTranslateY() >= HEIGHT) {
				restartGame();
			}
			if (Math.abs(tail.getTranslateX() - food.getTranslateX()) <= BLOCK_SIZE && Math.abs(tail.getTranslateY() - food.getTranslateY()) <= BLOCK_SIZE) {
				int newNodeIndex = new Random().nextInt(nodes.size());
				edu.wpi.cs3733.d20.teamL.entities.Node newNode = nodes.get(newNodeIndex);
				Point2D newNodePos = newNode.getPosition();
				food.setTranslateX(newNodePos.getX());
				food.setTranslateY(newNodePos.getY());
				if (newNode.getType().equals("HALL")) {
					food.setFill(hallNodeColor);
				} else {
					food.setFill(new ImagePattern(new Image("/edu/wpi/cs3733/d20/teamL/assets/nodes_filled/" + newNode.getType() + "_filled.png")));
				}
				Rectangle rect = new Rectangle(BLOCK_SIZE, BLOCK_SIZE);
				rect.setTranslateX(tailX);
				rect.setTranslateY(tailY);
				rect.setFill(Color.SPRINGGREEN);
				snake.add(rect);
			}
		});
		timeline.getKeyFrames().add(frame);
		timeline.setCycleCount(Timeline.INDEFINITE);
		root.getChildren().addAll(background, food, snakeBody);
		return root;
	}

	private void restartGame() {
		stopGame();
		lossCount++;
		if (lossCount == 3) {
			returnToMapViewer();
		}
		startGame();
	}

	private void stopGame() {
		running = false;
		timeline.stop();
		snake.clear();
	}

	private void returnToMapViewer() {
		try {
			Parent root = loaderFactory.getFXMLLoader("map_viewer/MapViewer").load();
			loaderFactory.setupScene(new Scene(root));
		} catch (IOException ex) {
			log.error("Encountered IOException", ex);
		}
	}

	private void startGame() {
		direction = Direction.RIGHT;
		Rectangle head = new Rectangle(BLOCK_SIZE, BLOCK_SIZE);
		head.setFill(Color.SPRINGGREEN);
		snake.add(head);
		timeline.play();
		running = true;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
}
