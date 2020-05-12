package edu.wpi.cs3733.d20.teamL.views.controllers.game;

import edu.wpi.cs3733.d20.teamL.App;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

//TODO: Call when screensaver timeout hits. Add ability to return to map viewer. Change colors and add background image."
public class SnakeController {

	public SnakeController(Stage stage) {
		initialize(stage);
	}

	public enum Direction {
		UP, DOWN, LEFT, RIGHT
	}

	private static final int WIDTH = (int) App.SCREEN_WIDTH;
	private static final int HEIGHT = (int) App.SCREEN_HEIGHT;
	private static final int BLOCK_SIZE = 64;

	private Direction direction = Direction.RIGHT;

	private boolean moved = false;
	private boolean running = false;

	private Timeline timeline = new Timeline();

	private ObservableList<Node> snake;

	public void initialize(Stage stage) {
		Scene scene = new Scene(createPane());
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
			}
			moved = false;
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

		Group snakeBody = new Group();
		snake = snakeBody.getChildren();
		Rectangle food = new Rectangle(BLOCK_SIZE, BLOCK_SIZE);
		food.setFill(Color.BLUE);
		food.setTranslateX((int) (Math.random() * (WIDTH - BLOCK_SIZE)) / BLOCK_SIZE * BLOCK_SIZE);
		food.setTranslateY((int) (Math.random() * (HEIGHT - BLOCK_SIZE)) / BLOCK_SIZE * BLOCK_SIZE);

		KeyFrame frame = new KeyFrame(Duration.seconds(0.2), event -> {
			if (!running) {
				return;
			}
			boolean toRemove = snake.size() > 1;

			Node tail = toRemove ? snake.remove(snake.size() - 1) : snake.get(0);

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
			moved = true;

			if (toRemove) {
				snake.add(0, tail);
			}

			for (Node node : snake) {
				if (node != tail && tail.getTranslateX() == node.getTranslateX() && tail.getTranslateY() == node.getTranslateY()) {
					restartGame();
					break;
				}
			}

			if (tail.getTranslateX() < 0 || tail.getTranslateX() >= WIDTH || tail.getTranslateY() < 0 || tail.getTranslateY() >= HEIGHT) {
				restartGame();
			}

			if (tail.getTranslateX() == food.getTranslateX() && tail.getTranslateY() == food.getTranslateY()) {
				food.setTranslateX((int) (Math.random() * (WIDTH - BLOCK_SIZE)) / BLOCK_SIZE * BLOCK_SIZE);
				food.setTranslateY((int) (Math.random() * (HEIGHT - BLOCK_SIZE)) / BLOCK_SIZE * BLOCK_SIZE);

				Rectangle rect = new Rectangle(BLOCK_SIZE, BLOCK_SIZE);
				rect.setTranslateX(tailX);
				rect.setTranslateY(tailY);

				snake.add(rect);
			}
		});

		timeline.getKeyFrames().add(frame);
		timeline.setCycleCount(Timeline.INDEFINITE);

		root.getChildren().addAll(food, snakeBody);

		return root;
	}

	private void restartGame() {
		stopGame();
		startGame();
	}

	private void stopGame() {
		running = false;
		timeline.stop();
		snake.clear();
	}

	private void startGame() {
		direction = Direction.RIGHT;
		Rectangle head = new Rectangle(BLOCK_SIZE, BLOCK_SIZE);
		snake.add(head);
		timeline.play();
		running = true;
	}
}
