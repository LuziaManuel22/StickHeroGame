package com.example.stickherogame_group105;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Platform extends Rectangle {
    public Platform(double width, double height, double translateY) {
        super(width, height, Color.GREEN);
        setTranslateY(translateY);
    }
}
