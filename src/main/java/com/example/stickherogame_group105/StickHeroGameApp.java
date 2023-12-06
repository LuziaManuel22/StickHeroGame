package com.example.stickherogame_group105;

import javafx.application.Application;
import javafx.stage.Stage;

public class StickHeroGameApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        GameController gameController = new GameController(primaryStage);
        gameController.startGame();
    }
}
