package com.example.stickherogame_group105;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameController {
    private Stage stage;
    private StickHero stickHero;
    private Platform platform;
    private Ball ball;
    private Button startButton;
    private Button restartButton;
    private Label scoreLabel;
    private int score = 0;

    private boolean isGameRunning = false;

    public GameController(Stage stage) {
        this.stage = stage;
        initialize();
    }

    private void initialize() {
        stickHero = new StickHero();
        platform = new Platform(100, 10, 200); // Ajuste conforme necessário
        ball = new Ball(10);
        startButton = new Button("Start");
        restartButton = new Button("Restart");
        startButton.setOnAction(e -> startGame());
        restartButton.setOnAction(e -> restartGame());
        scoreLabel = new Label("Score: 0");

        setupLayout();
    }

    private void setupLayout() {
        BorderPane root = new BorderPane();
        root.getChildren().addAll(stickHero, platform, ball);
        root.setBottom(startButton);
        root.setTop(scoreLabel);
        root.setRight(restartButton);

        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
        stage.setTitle("Stick Hero Game");
        stage.show();
    }

    private void startGame() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(2), e -> {
                    stickHero.getHeroImageView().fitHeightProperty().bind(stickHero.heightProperty());
                    stickHero.retractStick();
                }),
                new KeyFrame(Duration.seconds(4),
                        new KeyValue(scoreLabel.textProperty(), "Score: " + (++score))
                )
        );
        timeline.play();
    }

    public ImageView getHeroImageView() {
        return hero;
    }

    void restartGame() {
        score = 0;
        scoreLabel.setText("Score: 0");
        stickHero.retractStick();
    }
}
