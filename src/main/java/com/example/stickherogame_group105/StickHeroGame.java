package com.example.stickherogame_group105;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

interface PlatformFactory {
    Platform createPlatform(double width);
}

class PillarFactory implements PlatformFactory {
    @Override
    public Platform createPlatform(double width) {
        return new Pillar(width);
    }
}

class CherryFactory implements PlatformFactory {
    @Override
    public Platform createPlatform(double width) {
        return new Cherry();
    }
}

abstract class Platform implements Serializable {
    private final double width;
    private final double height;
    private double x;
    private double y;

    Platform(double width) {
        this.width = width;
        this.height = 10.0;
    }

    abstract void draw();

    abstract boolean collidesWith(double heroX, double heroY);

    abstract void reactToCollision(StickHeroGameModel gameModel);

    double getWidth() {
        return width;
    }

    double getHeight() {
        return height;
    }

    double getX() {
        return x;
    }

    double getY() {
        return y;
    }

    void setX(double x) {
        this.x = x;
    }

    void setY(double y) {
        this.y = y;
    }
}

class Pillar extends Platform {
    Pillar(double width) {
        super(width);
    }

    @Override
    void draw() {
        // Implement pillar drawing logic with varying widths
    }

    @Override
    boolean collidesWith(double heroX, double heroY) {
        return heroX >= getX() && heroX <= getX() + getWidth() &&
                heroY >= getY() && heroY <= getY() + getHeight();
    }

    @Override
    void reactToCollision(StickHeroGameModel gameModel) {
        if (gameModel.isRevived()) {
            gameModel.incrementCherries();
            gameModel.incrementScore(50);
        } else {
            gameModel.fallIntoAbyss();
        }
    }
}

class Cherry extends Platform {
    Cherry() {
        super(StickHeroGameModel.PLATFORM_WIDTH);
    }

    @Override
    void draw() {
        // Implement cherry drawing logic
    }

    @Override
    boolean collidesWith(double heroX, double heroY) {
        return heroX >= getX() && heroX <= getX() + getWidth() &&
                heroY >= getY() && heroY <= getY() + getHeight();
    }

    @Override
    void reactToCollision(StickHeroGameModel gameModel) {
        gameModel.collectCherry();
    }

    void spawn(Platform platform) {
        setX(platform.getX() + (platform.getWidth() - getWidth()) / 2);
        setY(platform.getY() - getHeight());
        draw();
    }
}

class Stick {
    private final double length;

    Stick(double length) {
        this.length = length;
    }

    void extend() {
        // Implement stick extension logic
    }

    double getLength() {
        return length;
    }
}

class StickHeroGameModel implements Serializable {
    static final double PLATFORM_WIDTH = 100.0;
    static final double MAX_STICK_LENGTH = 200.0;
    private transient Canvas gameCanvas;
    private List<Platform> platforms;
    private List<PlatformFactory> platformFactories;
    private Stick stick;
    private int score = 0;
    private int cherriesCollected = 0;
    private boolean isRevived = false;
    private boolean isFalling = false;

    private static final int SCORE_REVIVE_USED = 50;

    StickHeroGameModel() {
        this.platforms = new ArrayList<>();
        this.platformFactories = new ArrayList<>();
        this.platformFactories.add(new PillarFactory());
        this.platformFactories.add(new CherryFactory());
        this.stick = new Stick(0);
    }

    void resetGame() {
        platforms.clear();
        isRevived = false;
        isFalling = false;
        score = 0;
        cherriesCollected = 0;
        stick = new Stick(0);
    }

    void generatePlatform() {
        int randomFactoryIndex = new Random().nextInt(platformFactories.size());
        PlatformFactory factory = platformFactories.get(randomFactoryIndex);

        if (factory instanceof PillarFactory) {
            double pillarWidth = new Random().nextDouble() * 100 + 50;
            Platform platform = factory.createPlatform(pillarWidth);
            platforms.add(platform);
        } else {
            Platform platform = factory.createPlatform(new Random().nextDouble() * 150 + 50);
            platforms.add(platform);
        }

        platforms.get(platforms.size() - 1).draw();
    }

    void extendStick() {
        if (!isFalling) {
            stick.extend();
        }
    }

    void moveStick(double deltaX) {
        // Implement stick movement logic based on user input
    }

    void checkCollision() {
        if (!isFalling) {
            double stickLength = stick.getLength();
            double heroX = gameCanvas.getTranslateX() + stickLength;
            double heroY = gameCanvas.getTranslateY();

            for (Platform platform : platforms) {
                if (platform.collidesWith(heroX, heroY)) {
                    platform.reactToCollision(this);
                    break;
                }
            }
        }
    }

    void revivePlayer() {
        int cherriesRequiredForRevival = 5;
        if (cherriesCollected >= cherriesRequiredForRevival && !isRevived) {
            System.out.println("Reviving player!");
            isRevived = true;
            cherriesCollected -= cherriesRequiredForRevival;
            decrementScore();
        }
    }

    void fallIntoAbyss() {
        System.out.println("Falling into the abyss!");
        isFalling = true;
        // Implement logic for falling animation or game over screen
    }

    void collectCherry() {
        incrementScore(50);
        incrementCherries();
    }

    private void decrementScore() {
        score = Math.max(0, score - StickHeroGameModel.SCORE_REVIVE_USED);
    }

    int getScore() {
        return score;
    }

    int getCherries() {
        return cherriesCollected;
    }

    int getLevel() {
        return platforms.size(); // Assuming each platform represents a level
    }

    void incrementScore(int points) {
        score += points;
    }

    void incrementCherries() {
        cherriesCollected++;
    }

    boolean isRevived() {
        return isRevived;
    }

    void saveGame() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("stick_hero_save.dat"))) {
            oos.writeObject(this);
            System.out.println("Game saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void loadGame() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("stick_hero_save.dat"))) {
            StickHeroGameModel loadedGame = (StickHeroGameModel) ois.readObject();
            this.platforms = loadedGame.platforms;
            this.platformFactories = loadedGame.platformFactories;
            this.score = loadedGame.score;
            this.cherriesCollected = loadedGame.cherriesCollected;
            this.isRevived = loadedGame.isRevived;
            this.isFalling = loadedGame.isFalling;
            this.stick = loadedGame.stick;

            System.out.println("Game loaded successfully!");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void changeBackgroundColor() {
        // Implement background color change logic
    }

    public void flipPlayer() {
        // Implement player flipping logic
    }

    public void setGameCanvas(Canvas gameCanvas) {
        this.gameCanvas = gameCanvas;
    }

    public void spawnCherryOnPlatform() {
        // Implement cherry spawning logic on a platform
    }

    public double getStickLength() {
        return stick.getLength();
    }
}

class StickHeroGameController {
    private final StickHeroGameModel gameModel;

    StickHeroGameController(Stage primaryStage, StickHeroGameModel gameModel) {
        this.gameModel = gameModel;
    }

    void initializeGame(StickHeroGameView gameView) {
        gameView.setupUI(this);
        gameView.animateBackgroundColorChange();
        gameView.setupEventHandlers(this);
    }

    void startGame(StickHeroGameView gameView) {
        gameModel.resetGame();
        gameModel.generatePlatform();
        gameModel.extendStick();
        gameModel.spawnCherryOnPlatform();
        gameView.disableStartButton();
    }

    void restartGame() {
        gameModel.resetGame();
    }

    void extendStick() {
        gameModel.extendStick();
    }

    void moveStick(double deltaX) {
        gameModel.moveStick(deltaX);
    }

    void flipPlayer() {
        gameModel.flipPlayer();
    }

    void revivePlayer() {
        gameModel.revivePlayer();
    }

    void activatePowerUp() {
        // Implement power-up logic
    }

    void saveGame() {
        gameModel.saveGame();
    }

    void loadGame() {
        gameModel.loadGame();
    }
}

class StickHeroGameView {
    private Stage primaryStage;
    private StickHeroGameModel gameModel;
    private StickHeroGameController gameController;

    private Canvas gameCanvas;

    private Button startButton;
    private Button restartButton;
    private Button powerUpButton;

    private Label scoreLabel;
    private Label cherriesLabel;
    private Label levelLabel;
    private ProgressBar stickProgressBar;

    private VBox uiContainer;

    StickHeroGameView(Stage primaryStage, StickHeroGameModel gameModel, StickHeroGameController gameController) {
        this.primaryStage = primaryStage;
        this.gameModel = gameModel;
        this.gameController = gameController;
    }

    void setupUI(StickHeroGameController gameController) {
        setupCanvas();
        setupButtons();
        setupLabels();
        setupLayout();
    }

    void setupCanvas() {
        gameCanvas = new Canvas(800, 600);
        gameModel.setGameCanvas(gameCanvas);
    }

    void setupButtons() {
        startButton = new Button("Start");
        startButton.setOnAction(e -> gameController.startGame(this));

        restartButton = new Button("Restart");
        restartButton.setOnAction(e -> gameController.restartGame());

        powerUpButton = new Button("Power Up!");
        powerUpButton.setOnAction(e -> gameController.activatePowerUp());
    }

    void setupLabels() {
        scoreLabel = new Label("Score: 0");
        cherriesLabel = new Label("Cherries: 0");
        levelLabel = new Label("Level: 1");

        stickProgressBar = new ProgressBar(0);
        stickProgressBar.setMinWidth(200);
    }

    void setupLayout() {
        HBox topContainer = new HBox(10, scoreLabel, cherriesLabel, levelLabel);
        topContainer.setAlignment(Pos.CENTER);

        HBox bottomContainer = new HBox(10, startButton, stickProgressBar, powerUpButton, restartButton);
        bottomContainer.setAlignment(Pos.CENTER);

        uiContainer = new VBox(20, topContainer, bottomContainer);
        uiContainer.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(gameCanvas);
        root.getChildren().add(uiContainer);

        Scene scene = new Scene(root, 800, 600);
        scene.setOnKeyPressed(e -> handleKeyPress(e.getCode()));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Stick Hero Game");
    }

    private void handleKeyPress(KeyCode code) {
        if (code == KeyCode.SPACE) {
            gameController.flipPlayer();
        }
    }

    void animateBackgroundColorChange() {
        // Implement background color change animation
    }

    void setupEventHandlers(StickHeroGameController stickHeroGameController) {
        // Implement event handlers
    }

    void disableStartButton() {
        startButton.setDisable(true);
    }

    void updateCanvas() {
        // Implement canvas update logic
    }

    VBox getUIContainer() {
        return uiContainer;
    }

    Canvas getGameCanvas() {
        return gameCanvas;
    }
}

public class StickHeroGame extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        StickHeroGameModel gameModel = new StickHeroGameModel();
        StickHeroGameController gameController = new StickHeroGameController(primaryStage, gameModel);
        StickHeroGameView gameView = new StickHeroGameView(primaryStage, gameModel, gameController);

        gameController.initializeGame(gameView);

        primaryStage.setTitle("Stick Hero Game");
        StackPane root = new StackPane(gameView.getGameCanvas(), gameView.getUIContainer());
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
