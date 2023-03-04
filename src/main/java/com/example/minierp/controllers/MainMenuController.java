package com.example.minierp.controllers;

import com.example.minierp.Launcher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {
    @FXML private BorderPane mainPane;
    @FXML private Button internalButton;
    @FXML private Button supplierButton;
    @FXML private Button clientButton;
    @FXML private Button settingsButton;
    @FXML private Button aboutButton;

    private void changeScene(Button button, String scene) {
        try {
            Stage stage = (Stage) internalButton.getScene().getWindow();
            Parent root = FXMLLoader.load(Launcher.class.getResource(scene + ".fxml"));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPane(String page){
        try {
            FXMLLoader contentLoader = new FXMLLoader(Launcher.class.getResource(page+".fxml"));
            AnchorPane content = contentLoader.load();
            // Add the navigation menu to the left side of the BorderPane
            mainPane.setCenter(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goInternal(){
        changeScene(internalButton, "Layout");
    }
    @FXML
    private void goSupplier(){
        changeScene(supplierButton, "S_Layout");
    }
    @FXML
    private void goClient(){
        changeScene(clientButton, "CI_Layout");
    }
    @FXML
    private void goSettings(){
        loadPane("Settings");
    }
    @FXML
    private void goAbout(){
        changeScene(aboutButton, "About");
    }




}