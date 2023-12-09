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
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.assertEquals;



// Platform-related code
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

    public static void runLater(Object o) {
    }

    abstract void draw(GraphicsContext graphicsContext);

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
    void draw(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.BROWN);
        graphicsContext.fillRect(getX(), getY(), getWidth(), getHeight());
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
    void draw(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.RED);
        graphicsContext.fillOval(getX(), getY(), getWidth(), getHeight());
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

    }
}

// Stick-related code
class Stick {
    private double length;

    Stick(double length) {
        this.length = length;
    }

    void extend() {
        length += 2; // Adjusting the speed of stick extension
    }

    double getLength() {
        return length;
    }
}

// Stick Hero Game Model
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

        Platform platform;
        if (factory instanceof PillarFactory) {
            double pillarWidth = new Random().nextDouble() * 100 + 50;
            platform = factory.createPlatform(pillarWidth);
        } else {
            platform = factory.createPlatform(new Random().nextDouble() * 150 + 50);
        }

        double canvasWidth = gameCanvas.getWidth();
        platform.setX(Math.random() * (canvasWidth - platform.getWidth()));
        platform.setY(gameCanvas.getHeight() - platform.getHeight());
        platforms.add(platform);

        // Draw should be handled in the view, not here.
    }

    void extendStick() {
        if (!isFalling) {
            stick.extend();
        }
    }

    void moveStick(double deltaX) {
        if (!isFalling) {
            double newStickLength = stick.getLength() + deltaX;
            if (newStickLength <= MAX_STICK_LENGTH) {
                stick.extend();
            }
        }
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
        score = Math.max(0, score - SCORE_REVIVE_USED);
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

    public Platform[] getPlatforms() {
        return platforms.toArray(new Platform[0]);
    }

    public Stick getStick() {
        return null;
    }
}

// Stick Hero Game Controller
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
        startGameLoop(gameView);
    }

    private void startGameLoop(StickHeroGameView gameView) {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                gameModel.checkCollision();
                gameModel.extendStick();
                gameView.updateCanvas(gameModel);
            }
        }.start();
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

// Stick Hero Game View
class StickHeroGameView {
    private Stage primaryStage;
    private StickHeroGameModel gameModel;
    private StickHeroGameController gameController;

    private Canvas gameCanvas;

    private Button startButton;
    private Button restartButton;
    private Button powerUpButton;
    private Button exitButton;

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
        startButton.setOnAction(e -> {
            gameController.startGame(this);
            startButton.setDisable(true);
        });

        restartButton = new Button("Restart");
        restartButton.setOnAction(e -> gameController.restartGame());

        powerUpButton = new Button("Power Up!");
        powerUpButton.setOnAction(e -> gameController.activatePowerUp());

        exitButton = new Button("Exit");
        exitButton.setOnAction(e -> primaryStage.close());
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

        HBox bottomContainer = new HBox(10, startButton, stickProgressBar, powerUpButton, restartButton, exitButton);
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
        } else if (code == KeyCode.RIGHT) {
            gameController.moveStick(2);
        } else if (code == KeyCode.LEFT) {
            gameController.moveStick(-2);
        }
    }

    void animateBackgroundColorChange() {
        // Implement background color change animation
    }

    void setupEventHandlers(StickHeroGameController stickHeroGameController) {
        primaryStage.getScene().setOnKeyPressed(e -> handleKeyPress(e.getCode()));
    }

    void disableStartButton() {
        startButton.setDisable(true);
    }

    void updateCanvas(StickHeroGameModel gameModel) {
        GraphicsContext graphicsContext = gameCanvas.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        for (Platform platform : gameModel.getPlatforms()) {
            platform.draw(graphicsContext);
        }


    }

    VBox getUIContainer() {
        return uiContainer;
    }

    Canvas getGameCanvas() {
        return gameCanvas;
    }
}

// Stick Hero Character Class
class StickHeroCharacter {
    private Image characterImage;
    private double x;
    private double y;
    private double stickLength;
    private double stickExtension;
    private boolean isExtending;
    private boolean isRetracting;

    public StickHeroCharacter(Image characterImage, double x, double y) {
        this.characterImage = characterImage;
        this.x = x;
        this.y = y;
        this.stickLength = 0;
        this.stickExtension = 0;
        this.isExtending = false;
        this.isRetracting = false;
    }

    public void update() {
        if (isExtending && stickLength < StickHeroGameModel.MAX_STICK_LENGTH) {
            stickExtension += 2; // Adjust the speed of stick extension
        } else if (isRetracting && stickLength > 0) {
            stickExtension -= 2; // Adjust the speed of stick retraction
        }

        if (stickLength > StickHeroGameModel.MAX_STICK_LENGTH) {
            // The stick has reached the maximum length, start retracting
            isExtending = false;
            isRetracting = true;
        }

        x += 1; // Adjusting the character's movement speed
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(characterImage, x, y);
        gc.setFill(Color.BLACK);
        gc.fillRect(x + characterImage.getWidth() / 2 - 1, y + characterImage.getHeight(),
                stickExtension, 5); // Draw the stick
    }

    public boolean intersects(Platform platform) {
        // Implement collision detection logic here
        return false;
    }
}

// Stick Hero Game Class
public class StickHeroGame extends Application {
    public static final double WIDTH = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        StickHeroGameModel gameModel = new StickHeroGameModel();
        StickHeroGameController gameController = new StickHeroGameController(primaryStage, gameModel);
        StickHeroGameView gameView = new StickHeroGameView(primaryStage, gameModel, gameController);

        gameController.initializeGame(gameView);

        primaryStage.setOnCloseRequest(event -> {
            // Handle any cleanup or save operations before closing the application
        });

        primaryStage.setTitle("Stick Hero Game");
        StackPane root = new StackPane(gameView.getGameCanvas(), gameView.getUIContainer());
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public StickHeroGameModel getGameModel() {
        return null;
    }
}
class StickHeroGameTest {

    @Test
    public void testStickExtension() {
        StickHeroGameModel gameModel = new StickHeroGameModel();
        gameModel.extendStick();
        double expectedLength = 2.0; // Assuming the initial stick length is 0
        assertEquals(expectedLength, gameModel.getStickLength(), 0.01);
    }

    @Test
    public void testPlatformCollision() {
        StickHeroGameModel gameModel = new StickHeroGameModel();
        gameModel.generatePlatform();
        gameModel.checkCollision();
        int expectedScore = 50; // Assuming the collision leads to score increment
        assertEquals(expectedScore, gameModel.getScore());
    }

    @Test
    public void testCherryCollection() {
        StickHeroGameModel gameModel = new StickHeroGameModel();
        Cherry cherry = new Cherry();
        cherry.spawn(new Pillar(100.0)); // Assuming a cherry spawns on a pillar
        cherry.reactToCollision(gameModel);
        int expectedScore = 50; // Assuming collecting a cherry increments the score
        assertEquals(expectedScore, gameModel.getScore());
    }

    @Test
    public void testGameReset() {
        StickHeroGameModel gameModel = new StickHeroGameModel();
        gameModel.generatePlatform();
        gameModel.extendStick();
        gameModel.resetGame();
        double expectedStickLength = 0.0;
        assertEquals(expectedStickLength, gameModel.getStickLength(), 0.01);
        assertEquals(0, gameModel.getScore());
        assertEquals(0, gameModel.getCherries());
    }
}
