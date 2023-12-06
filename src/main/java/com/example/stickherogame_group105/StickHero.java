package com.example.stickherogame_group105;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class StickHero extends StackPane {
    private static final String HERO_IMAGE_PATH = "/resources/hero.png"; // Ajuste o caminho conforme necessário
    private static final double HERO_WIDTH = 20.0;
    private static final double HERO_HEIGHT = 20.0;

    private ImageView hero;

    public StickHero() {
        initialize();
    }

    private void initialize() {
        hero = new ImageView(new Image(getClass().getResourceAsStream(HERO_IMAGE_PATH)));
        hero.setFitWidth(HERO_WIDTH);
        hero.setFitHeight(HERO_HEIGHT);
        getChildren().add(hero);
    }
    public void retractStick() {
         // Altura padrão
    }
}
