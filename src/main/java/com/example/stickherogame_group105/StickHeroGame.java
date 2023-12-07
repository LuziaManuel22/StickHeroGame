package com.example.stickherogame_group105;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StickHeroGame extends Application {

    private GameCanvas gameCanvas;
    private ScoreLabel scoreLabel;
    private CherriesLabel cherriesLabel;
    private StickProgressBar stickProgressBar;
    private SaveButton saveButton;

    private StickHero stickHero;
    private List<Pillar> pillars;

    private AnimationTimer gameLoop;

    private int highestScore;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        createUI(primaryStage);
        animateBackgroundColorChange();
    }

    private void animateBackgroundColorChange() {
        // Animation logic for background color change (if needed)
    }

    private void createUI(Stage primaryStage) {
        gameCanvas = new GameCanvas(400, 250);
        scoreLabel = new ScoreLabel();
        cherriesLabel = new CherriesLabel();
        stickProgressBar = new StickProgressBar();
        saveButton = new SaveButton();

        setupButtons();
        setupLayout(primaryStage);
        setupKeyEvents();
        createGameLoop();  // Move this line to ensure gameLoop is initialized
        loadHighestScore(); // Load the highest score when the game starts
    }

    private void setupButtons() {
        saveButton.setOnAction(e -> saveProgress());
    }

    private void setupLayout(Stage primaryStage) {
        HBox topContainer = new HBox(10, scoreLabel, cherriesLabel, saveButton);
        topContainer.setAlignment(Pos.CENTER);

        HBox bottomContainer = new HBox(10, stickProgressBar);
        bottomContainer.setAlignment(Pos.CENTER);

        VBox uiContainer = new VBox(20, topContainer, bottomContainer);
        uiContainer.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setCenter(gameCanvas);
        root.setBottom(uiContainer);

        Scene scene = new Scene(root, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Stick Hero Game");
        primaryStage.show();

        startGame(); // Start the game when the UI is ready
    }

    private void setupKeyEvents() {
        gameCanvas.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                stickHero.flip();
            }
        });
    }

    private void createGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateGame();
                renderGame();
            }
        };
    }

    private void startGame() {
        resetGame();
        generatePillars();
        initializeStickHero();
        extendStick();
        gameLoop.start();
    }

    private void initializeStickHero() {
        stickHero = new StickHero();
        gameCanvas.getChildren().add(stickHero.getHero());
    }

    private void resetGame() {
        scoreLabel.reset();
        cherriesLabel.reset();
        pillars = new ArrayList<>();
        stickProgressBar.reset();
        if (stickHero != null) {
            gameCanvas.getChildren().remove(stickHero.getHero());
            stickHero.reset();
        }
    }

    private void updateGame() {
        checkCollision();
        // Add any other game logic updates here
    }

    private void renderGame() {
        drawBackground();
        for (Pillar pillar : pillars) {
            pillar.render(gameCanvas.getGraphicsContext());
        }
        if (stickHero != null) {
            stickHero.render(gameCanvas.getGraphicsContext());
        }
    }

    private void generatePillars() {
        double gapBetweenPillars = 150; // Adjust this value as needed

        for (int i = 0; i < 5; i++) { // Generate 5 pillars (you can change this number)
            double randomWidth = Math.random() * 100 + 50; // Adjust the range for pillar width
            Pillar pillar = new Pillar(randomWidth);
            pillars.add(pillar);

            double xPosition = i * gapBetweenPillars + gameCanvas.getWidth(); // Position each pillar with a gap
            double yPosition = gameCanvas.getHeight() - Pillar.PILLAR_HEIGHT; // Adjust the height as needed
            pillar.setPosition(xPosition, yPosition);
        }
    }

    private void checkCollision() {
        if (stickHero == null) return;

        double heroX = stickHero.getX() + stickHero.getWidth();
        double heroY = stickHero.getY();

        // Check if the hero has landed on a pillar
        for (Pillar pillar : pillars) {
            double pillarX = pillar.getX();
            double pillarWidth = pillar.getWidth();

            if (heroX >= pillarX && heroX <= pillarX + pillarWidth && heroY == gameCanvas.getHeight() - Pillar.PILLAR_HEIGHT) {
                retractStick(); // Successful landing on the platform
                return;
            }
        }

        // Check if the hero has fallen off the screen
        if (heroY > gameCanvas.getHeight()) {
            fallAnimation(); // If no successful landing, initiate fall animation
        }
    }

    private void fallAnimation() {
        // Animation logic for hero falling off the screen
        // You can add more details to the animation
        gameLoop.stop();
    }

    private void retractStick() {
        // Animation logic for retracting the stick
        // You can add more details to the animation
        gameLoop.stop();
    }

    private void extendStick() {
        double stickLength = 100; // Adjust this value as needed
        if (stickHero != null) {
            stickHero.extendStick(stickLength);

            // You may want to update the progress bar or any other UI elements here
            stickProgressBar.increaseProgress(0.2); // Assuming the progress bar supports increments

            // Check for collision after extending the stick
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(1),
                            new KeyValue(stickProgressBar.progressProperty(), 1)
                    )
            );
            timeline.setOnFinished(e -> {
                checkCollision();
            });
            timeline.play();
        }
    }

    private void drawBackground() {
        gameCanvas.getGraphicsContext().setFill(Color.LIGHTGRAY);
        gameCanvas.getGraphicsContext().fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
    }

    private void saveProgress() {
        // Save player's progress to storage
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("progress.txt"))) {
            writer.write(Integer.toString(scoreLabel.getScore()));
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately in a real application
        }
    }

    private void loadHighestScore() {
        File progressFile = new File("progress.txt");

        // Check if the file exists, read the highest score if it does
        if (progressFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(progressFile))) {
                highestScore = Integer.parseInt(reader.readLine());
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
                // Handle the exception appropriately in a real application
            }
        }
    }

    class GameCanvas extends Pane {

        public GameCanvas(double width, double height) {
            setPrefSize(width, height);
        }

        public GraphicsContext getGraphicsContext() {
            return getChildren().isEmpty() ? null : ((Canvas) getChildren().get(0)).getGraphicsContext2D();
        }
    }

    class ScoreLabel extends Label {

        private int score;

        public ScoreLabel() {
            this.score = 0;
            setText("Score: " + score);
        }

        public void reset() {
            score = 0;
            updateLabel();
        }

        public void increaseScore(int points) {
            score += points;
            updateLabel();
        }

        public int getScore() {
            return score;
        }

        private void updateLabel() {
            setText("Score: " + score);
        }
    }

    class CherriesLabel extends Label {

        private int cherries;

        public CherriesLabel() {
            this.cherries = 0;
            setText("Cherries: " + cherries);
        }

        public void reset() {
            cherries = 0;
            updateLabel();
        }

        public void increaseCherries(int collectedCherries) {
            cherries += collectedCherries;
            updateLabel();
        }

        private void updateLabel() {
            setText("Cherries: " + cherries);
        }
    }

    class StickProgressBar extends ProgressBar {

        public StickProgressBar() {
            setProgress(0);
            setMinWidth(200);
        }

        public void reset() {
            setProgress(0);
        }

        public void increaseProgress(double value) {
            setProgress(getProgress() + value);
        }
    }

    class SaveButton extends Button {

        public SaveButton() {
            setText("Save");
        }
    }

    class StickHero {

        private Rectangle hero;
        private boolean isFlipped;

        public StickHero() {
            initializeHero();
        }

        public void reset() {
            initializeHero();
            isFlipped = false;
        }

        public void flip() {
            // Placeholder for flipping logic
            // Replace this with the actual logic
            isFlipped = !isFlipped;
        }

        public void render(GraphicsContext gc) {
            // Placeholder for rendering logic
            // Replace this with the actual rendering logic
            gc.setFill(isFlipped ? Color.BLUE : Color.RED);
            gc.fillRect(hero.getX(), hero.getY(), hero.getWidth(), hero.getHeight());
        }

        public void extendStick(double length) {
            // Placeholder for extending stick logic
            // Replace this with the actual logic
        }

        public double getX() {
            return hero.getX();
        }

        public double getY() {
            return hero.getY();
        }

        public double getWidth() {
            return hero.getWidth();
        }

        public Rectangle getHero() {
            return hero;
        }

        private void initializeHero() {
            hero = new Rectangle(30, 30, Color.RED);
        }
    }

    class Pillar {

        public static final int PILLAR_HEIGHT = 20;

        private Rectangle pillar;

        public Pillar(double width) {
            initializePillar(width);
        }

        public void render(GraphicsContext gc) {
            // Placeholder for rendering logic
            // Replace this with the actual rendering logic
            gc.setFill(Color.GREEN);
            gc.fillRect(pillar.getX(), pillar.getY(), pillar.getWidth(), PILLAR_HEIGHT);
        }

        public double getX() {
            return pillar.getX();
        }

        public double getWidth() {
            return pillar.getWidth();
        }

        public void setPosition(double x, double y) {
            pillar.setX(x);
            pillar.setY(y);
        }

        private void initializePillar(double width) {
            pillar = new Rectangle(width, PILLAR_HEIGHT, Color.GREEN);
        }
    }
}
