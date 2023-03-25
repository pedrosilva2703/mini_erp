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

public class II_LayoutController {
    @FXML
    private BorderPane mainPane;

    @FXML private Button scheduleButton;
    @FXML private Button pendingButton;
    @FXML private Button clientsButton;
    @FXML private Button clientOrdersButton;
    @FXML private Button suppliersButton;
    @FXML private Button supplierOrdersButton;
    @FXML private Button backButton;


    @FXML
    private void onScheduleButtonClick(){
        loadPage("II_Schedule");
        refreshButtonStates(scheduleButton);
    }
    @FXML
    private void onPendingButtonClick(){
        loadPage("II_PendingConfirmation");
        refreshButtonStates(pendingButton);
    }
    @FXML
    private void onClientsButtonClick(){
        loadPage("II_Clients");
        refreshButtonStates(clientsButton);
    }
    @FXML
    private void onClientOrdersButtonClick(){
        loadPage("II_ClientOrders");
        refreshButtonStates(clientOrdersButton);
    }
    @FXML
    private void onSuppliersButtonClick(){
        loadPage("II_Suppliers");
        refreshButtonStates(suppliersButton);
    }
    @FXML
    private void onSupplierOrdersButtonClick(){
        loadPage("II_SupplierOrders");
        refreshButtonStates(supplierOrdersButton);
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
        unselectButton(scheduleButton);
        unselectButton(pendingButton);
        unselectButton(clientsButton);
        unselectButton(clientOrdersButton);
        unselectButton(suppliersButton);
        unselectButton(supplierOrdersButton);

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