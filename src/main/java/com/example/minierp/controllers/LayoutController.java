package com.example.minierp.controllers;

import com.example.minierp.HelloApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


import java.io.IOException;

public class LayoutController {
    @FXML
    private BorderPane mainPane;

    @FXML private Button scheduleButton;
    @FXML private Button clientsButton;
    @FXML private Button clientOrdersButton;
    @FXML private Button suppliersButton;
    @FXML private Button supplierOrdersButton;
    @FXML private Button settingsButton;
    @FXML private Button aboutButton;


    @FXML
    private void onScheduleButtonClick(){
        refreshButtonStates(scheduleButton);
    }
    @FXML
    private void onClientsButtonClick(){
        refreshButtonStates(clientsButton);
    }
    @FXML
    private void onClientOrdersButtonClick(){
        refreshButtonStates(clientOrdersButton);
    }
    @FXML
    private void onSuppliersButtonClick(){
        refreshButtonStates(suppliersButton);
    }
    @FXML
    private void onSupplierOrdersButtonClick(){
        refreshButtonStates(supplierOrdersButton);
    }
    @FXML
    private void onSettingsButtonClick(){
        loadPage("Settings");
        refreshButtonStates(settingsButton);
    }
    @FXML
    private void onAboutButtonClick(){
        refreshButtonStates(aboutButton);
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
        unselectButton(scheduleButton);
        unselectButton(clientsButton);
        unselectButton(clientOrdersButton);
        unselectButton(suppliersButton);
        unselectButton(supplierOrdersButton);
        unselectButton(settingsButton);
        unselectButton(aboutButton);

        // Select clicked button
        selectButton(clickedButton);
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