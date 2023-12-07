package com.example.stickherogame_group105;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SkeletonScreen extends Application {

    private static final double PLATFORM_WIDTH = 100.0;
    private static final double PLATFORM_HEIGHT = 10.0;
    private static final double HERO_WIDTH = 20.0;
    private static final double HERO_HEIGHT = 20.0;

    private Canvas gameCanvas;
    private GraphicsContext gc;

    private Button startButton;
    private Label scoreLabel;
    private Label cherriesLabel;
    private Label levelLabel;
    private ProgressBar stickProgressBar;
    private Button restartButton;
    private Button powerUpButton;

    private int score = 0;
    private int cherries = 0;
    private int level = 1;

    private Rectangle hero;
    private Rectangle platform;
    private double stickLength;

    private List<Image> cherriesImages;
    private List<ImageView> cherriesOnPlatform;

    private Color[] backgroundColors = {
            Color.LIGHTGRAY,
            Color.SKYBLUE,
            Color.LIGHTGREEN,
            Color.LIGHTCORAL
    };
    private int currentBackgroundColorIndex = 0;

    private boolean powerUpActive = false;
    private List<ImageView> spikesOnPlatform;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        initializeImages();
        createUI(primaryStage);
    }

    private void initializeImages() {
        cherriesImages = new ArrayList<>();
        cherriesImages.add(new Image("cherry1.png"));
        cherriesImages.add(new Image("cherry2.png"));
        cherriesImages.add(new Image("cherry3.png"));
    }

    private void createUI(Stage primaryStage) {
        setupCanvas();
        setupButtons();
        setupLabels();
        setupPowerUpButton();
        setupLayout(primaryStage);
        animateBackgroundColorChange();
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
        restartButton.setOnAction(e -> restartGame());

        powerUpButton = new Button("Power Up!");
        powerUpButton.setOnAction(e -> activatePowerUp());
    }

    private void restartGame() {
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
        if (!powerUpActive && cherries >= 5) {
            System.out.println("Power-Up Activated: Super Jump!");
            powerUpActive = true;
            jumpAnimation();
            // Deduct 5 cherries for the power-up
            cherries -= 5;
            cherriesLabel.setText("Cherries: " + cherries);

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
        extendStick();
        spawnCherryOnPlatform();
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
        cherriesOnPlatform = new ArrayList<>();
        spikesOnPlatform = new ArrayList<>();
        powerUpActive = false;
        powerUpButton.setDisable(false);
    }

    private void extendStick() {
        stickLength = new Random().nextDouble() * 150 + 50;
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(gameCanvas.translateXProperty(), gameCanvas.getTranslateX() + stickLength),
                        new KeyValue(scoreLabel.textProperty(), "Score: " + (++score)),
                        new KeyValue(stickProgressBar.progressProperty(), 1)
                )
        );
        timeline.setOnFinished(e -> {
            if (score % 5 == 0) {
                increaseLevel();
            }
            checkCollision();
            // Collect cherries while extending the stick
            collectCherries();
        });
        timeline.play();
    }

    private void generatePlatform() {
        double platformWidth = new Random().nextDouble() * 150 + 50;
        platform = new Rectangle(platformWidth, PLATFORM_HEIGHT, Color.GREEN);

        // Add variations to platform appearance
        if (new Random().nextBoolean()) {
            // 50% chance to change platform color to brown
            platform.setFill(Color.BROWN);
        }

        platform.setTranslateX(gameCanvas.getTranslateX() + gameCanvas.getWidth());
        platform.setTranslateY(gameCanvas.getHeight() - PLATFORM_HEIGHT);
        gc.setFill(platform.getFill());
        gc.fillRect(platform.getTranslateX(), platform.getTranslateY(), platformWidth, PLATFORM_HEIGHT);
    }

    private void spawnCherryOnPlatform() {
        if (new Random().nextInt(4) == 0) {
            // 25% chance to spawn a cherry
            Image cherryImage = cherriesImages.get(new Random().nextInt(cherriesImages.size()));
            ImageView cherry = new ImageView(cherryImage);
            cherry.setFitWidth(HERO_WIDTH);
            cherry.setFitHeight(HERO_HEIGHT);
            cherry.setTranslateX(platform.getTranslateX() + platform.getWidth() / 2 - HERO_WIDTH / 2);
            cherry.setTranslateY(platform.getTranslateY() - HERO_HEIGHT);
            cherriesOnPlatform.add(cherry);
            gc.drawImage(cherryImage, cherry.getTranslateX(), cherry.getTranslateY(), HERO_WIDTH, HERO_HEIGHT);
        }
    }

    private void collectCherries() {
        List<ImageView> collectedCherries = new ArrayList<>();
        for (ImageView cherry : cherriesOnPlatform) {
            // Check if the hero collects the cherry
            double heroX = gameCanvas.getTranslateX() + stickLength;
            double cherryX = cherry.getTranslateX();
            if (heroX >= cherryX && heroX <= cherryX + HERO_WIDTH) {
                collectedCherries.add(cherry);
            }
        }

        // Remove collected cherries
        cherriesOnPlatform.removeAll(collectedCherries);
        cherries += collectedCherries.size();
        cherriesLabel.setText("Cherries: " + cherries);
    }

    private void increaseLevel() {
        level++;
        cherries += level * 2;
        cherriesLabel.setText("Cherries: " + cherries);
        cherriesOnPlatform.clear();
        spikesOnPlatform.clear();
        spawnCherryOnPlatform();
        spawnSpikeOnPlatform();
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
            for (ImageView cherry : cherriesOnPlatform) {
                cherry.setTranslateX(cherry.getTranslateX() - stickLength);
            }
            for (ImageView spike : spikesOnPlatform) {
                spike.setTranslateX(spike.getTranslateX() - stickLength);
            }
            generatePlatform();
            extendStick();
            spawnCherryOnPlatform();
            spawnSpikeOnPlatform();
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

    private void spawnSpikeOnPlatform() {
        ImageView spike = new ImageView(new Image("spike.png"));
        spike.setFitWidth(HERO_WIDTH);
        spike.setFitHeight(HERO_HEIGHT);
        spike.setTranslateX(platform.getTranslateX() + platform.getWidth() / 2 - HERO_WIDTH / 2);
        spike.setTranslateY(platform.getTranslateY() - HERO_HEIGHT);
        spikesOnPlatform.add(spike);
        gc.drawImage(spike.getImage(), spike.getTranslateX(), spike.getTranslateY(), HERO_WIDTH, HERO_HEIGHT);
    }

    private void drawBackground() {
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
    }
}

