package com.example.stickherogame_group105;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class StickHeroGame extends Application {

    // Constants for dimensions
    private static final double PLATFORM_WIDTH = 100.0;
    private static final double PLATFORM_HEIGHT = 10.0;
    private static final double HERO_WIDTH = 20.0;
    private static final double HERO_HEIGHT = 20.0;

    private Canvas gameCanvas;
    private GraphicsContext gc;

    // UI elements
    private Button startButton;
    private Label scoreLabel;
    private Label cherriesLabel;
    private Label levelLabel;
    private ProgressBar stickProgressBar;
    private Button restartButton;
    private Button powerUpButton;

    // Game variables
    private int score = 0;
    private int cherries = 0;
    private int level = 1;

    private Rectangle hero;
    private Rectangle platform;
    private double stickLength;

    private Color[] backgroundColors = {
            Color.LIGHTGRAY,
            Color.SKYBLUE,
            Color.LIGHTGREEN,
            Color.LIGHTCORAL
    };
    private int currentBackgroundColorIndex = 0;

    private boolean powerUpActive = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        createUI(primaryStage);
        animateBackgroundColorChange();
    }

    private void createUI(Stage primaryStage) {
        setupCanvas();
        setupButtons();
        setupLabels();
        setupPowerUpButton();
        setupLayout(primaryStage);
    }

    private void setupCanvas() {
        gameCanvas = new Canvas(400, 250);
        gc = gameCanvas.getGraphicsContext2D();
        drawBackground();
    }

    private void setupButtons() {
        startButton = new Button("Start");
        startButton.setOnAction(e -> startGame());

        restartButton = new Button("Restart");
        restartButton.setOnAction(e -> resetGame());

        powerUpButton = new Button("Power Up!");
        powerUpButton.setOnAction(e -> activatePowerUp());
    }

    private void setupLabels() {
        scoreLabel = new Label("Score: 0");
        cherriesLabel = new Label("Cherries: 0");
        levelLabel = new Label("Level: 1");

        stickProgressBar = new ProgressBar(0);
        stickProgressBar.setMinWidth(200);
    }

    private void setupPowerUpButton() {
        powerUpButton = new Button("Power Up!");
        powerUpButton.setOnAction(e -> activatePowerUp());
    }

    private void activatePowerUp() {
        if (!powerUpActive) {
            System.out.println("Power-Up Activated: Super Jump!");
            powerUpActive = true;
            jumpAnimation();
            powerUpButton.setDisable(true);
            Timeline powerUpTimeline = new Timeline(
                    new KeyFrame(Duration.seconds(5),
                            new KeyValue(powerUpButton.disableProperty(), false)
                    )
            );
            powerUpTimeline.play();
        }
    }

    private void jumpAnimation() {
        double initialHeroY = hero.getTranslateY();
        Timeline jumpTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5),
                        new KeyValue(hero.translateYProperty(), initialHeroY - 50, Interpolator.EASE_BOTH)
                ),
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(hero.translateYProperty(), initialHeroY, Interpolator.EASE_BOTH)
                )
        );
        jumpTimeline.play();
    }

    private void animateBackgroundColorChange() {
        Timeline colorChangeTimeline = new Timeline(
                new KeyFrame(Duration.seconds(10),
                        e -> changeBackgroundColor()
                )
        );
        colorChangeTimeline.setCycleCount(Timeline.INDEFINITE);
        colorChangeTimeline.play();
    }

    private void changeBackgroundColor() {
        currentBackgroundColorIndex = (currentBackgroundColorIndex + 1) % backgroundColors.length;
        Color newColor = backgroundColors[currentBackgroundColorIndex];
        gameCanvas.setId(new Background(new BackgroundFill(newColor, null, null)).toString());
    }

    private void setupLayout(Stage primaryStage) {
        HBox topContainer = new HBox(10, scoreLabel, cherriesLabel, levelLabel);
        topContainer.setAlignment(Pos.CENTER);

        HBox bottomContainer = new HBox(10, startButton, stickProgressBar, powerUpButton, restartButton);
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
    }

    private void startGame() {
        resetGame();
        generatePlatform();
        initializeHero(); // Add this line to initialize the hero
        extendStick();
    }

    private void initializeHero() {
        hero = new Rectangle(HERO_WIDTH, HERO_HEIGHT, Color.BLUE);
        hero.setTranslateX(gameCanvas.getWidth() / 2 - HERO_WIDTH / 2);
        hero.setTranslateY(gameCanvas.getHeight() - PLATFORM_HEIGHT - HERO_HEIGHT);
        gc.setFill(Color.BLUE);
        gc.fillRect(hero.getTranslateX(), hero.getTranslateY(), HERO_WIDTH, HERO_HEIGHT);
    }

    private void resetGame() {
        score = 0;
        cherries = 0;
        level = 1;
        scoreLabel.setText("Score: 0");
        cherriesLabel.setText("Cherries: 0");
        levelLabel.setText("Level: 1");
        stickProgressBar.setProgress(0);
        drawBackground();
        powerUpActive = false;
        powerUpButton.setDisable(false);
    }

    private void extendStick() {
        stickLength = new Random().nextDouble() * 150 + 50;
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(scoreLabel.textProperty(), "Score: " + (++score)),
                        new KeyValue(stickProgressBar.progressProperty(), 1)
                )
        );
        timeline.setOnFinished(e -> {
            if (score % 5 == 0) {
                increaseLevel();
            }
            checkCollision();
        });
        timeline.play();
    }

    private void generatePlatform() {
        double platformWidth = new Random().nextDouble() * 150 + 50;
        platform = new Rectangle(platformWidth, PLATFORM_HEIGHT, Color.GREEN);
        platform.setTranslateX(gameCanvas.getTranslateX() + gameCanvas.getWidth());
        platform.setTranslateY(gameCanvas.getHeight() - PLATFORM_HEIGHT);
        gc.setFill(Color.GREEN);
        gc.fillRect(platform.getTranslateX(), platform.getTranslateY(), platformWidth, PLATFORM_HEIGHT);
    }

    private void increaseLevel() {
        level++;
        cherries += level * 2;
        cherriesLabel.setText("Cherries: " + cherries);
        startButton.setDisable(true);
        Timeline levelUpTimeline = new Timeline(
                new KeyFrame(Duration.seconds(2),
                        new KeyValue(gameCanvas.translateXProperty(), gameCanvas.getTranslateX() + 50)
                )
        );
        levelUpTimeline.setOnFinished(e -> {
            startButton.setDisable(false);
            extendStick();
        });
        levelUpTimeline.play();
    }

    private void checkCollision() {
        double heroX = gameCanvas.getTranslateX() + stickLength;
        double platformX = platform.getTranslateX();
        double platformWidth = platform.getWidth();

        if (heroX >= platformX && heroX <= platformX + platformWidth) {
            retractStick();
        } else {
            fallAnimation();
        }
    }

    private void retractStick() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(gameCanvas.translateXProperty(), gameCanvas.getTranslateX() - stickLength)
                )
        );
        timeline.setOnFinished(e -> {
            generatePlatform();
            extendStick();
        });
        timeline.play();
    }

    private void fallAnimation() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(hero.translateYProperty(), hero.getTranslateY() + 100),
                        new KeyValue(gameCanvas.translateXProperty(), gameCanvas.getTranslateX() - stickLength)
                )
        );
        timeline.setOnFinished(e -> resetGame());
        timeline.play();
    }

    private void drawBackground() {
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
    }
}
