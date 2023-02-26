package com.example.minierp.controllers;

import com.example.minierp.HelloApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class LayoutController {
    @FXML
    private BorderPane mainPane;

    @FXML
    private void onSettingsButtonClick(){
        loadPage("Settings");
    }

    private void loadPage(String page){
        try {
            FXMLLoader contentLoader = new FXMLLoader(HelloApplication.class.getResource(page+".fxml"));
            AnchorPane content = contentLoader.load();
            // Add the navigation menu to the left side of the BorderPane
            mainPane.setCenter(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}