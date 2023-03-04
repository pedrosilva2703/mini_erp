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

public class CI_LayoutController {
    @FXML
    private BorderPane mainPane;

    @FXML private Button newOrderButton;
    @FXML private Button pendingButton;
    @FXML private Button confirmedButton;
    @FXML private Button backButton;


    @FXML
    private void onNewOrderButtonClick(){
        loadPage("CI_NewOrder");
        refreshButtonStates(newOrderButton);
    }
    @FXML
    private void onPendingButtonClick(){
        loadPage("CI_PendingOrders");
        refreshButtonStates(pendingButton);
    }
    @FXML
    private void onConfirmedButtonClick(){
        loadPage("CI_ConfirmedOrders");
        refreshButtonStates(confirmedButton);
    }

    @FXML
    private void onBackButtonClick(){
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            Parent root = FXMLLoader.load(Launcher.class.getResource("MainMenu.fxml"));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void unselectButton(Button b){
        b.getStyleClass().remove("menu-app-button-selected");
        b.getStyleClass().add("menu-app-button");
    }
    private void selectButton(Button b){
        b.getStyleClass().remove("menu-app-button");
        b.getStyleClass().add("menu-app-button-selected");
    }

    private void refreshButtonStates(Button clickedButton){
        // Unselect all buttons
        unselectButton(newOrderButton);
        unselectButton(pendingButton);
        unselectButton(confirmedButton);


        // Select clicked button
        selectButton(clickedButton);
    }

    private void loadPage(String page){
        try {
            FXMLLoader contentLoader = new FXMLLoader(Launcher.class.getResource(page+".fxml"));
            AnchorPane content = contentLoader.load();
            // Add the navigation menu to the left side of the BorderPane
            mainPane.setCenter(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}