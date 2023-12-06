module com.example.stickherogame_group105 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.stickherogame_group105 to javafx.fxml;
    exports com.example.stickherogame_group105;
}