package com.example.minierp.utils;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public final class Alerts {

    private Alerts(){}

    public static void showError(Stage stage, String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("");
        alert.setContentText(message);
        alert.showAndWait();

        stage.toFront();
    }

    public static void showInfo(Stage stage, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("");
        alert.setContentText(message);
        alert.showAndWait();

        stage.toFront();
    }

    public static void showInfoMainMenu(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("");
        alert.setContentText(message);
        alert.showAndWait();
    }
    public static void showErrorMainMenu(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("");
        alert.setContentText(message);
        alert.showAndWait();
    }


}
