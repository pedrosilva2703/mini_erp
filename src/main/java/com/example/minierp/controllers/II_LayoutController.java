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
    II_ClientOrdersController clientOrdersController;
    II_ClientsController clientsController;
    II_PendingConfirmationController pendingConfirmationController;
    II_ScheduleController scheduleController;
    II_SupplierOrdersController supplierOrdersController;
    II_SuppliersController suppliersController;


    @FXML private BorderPane mainPane;
    @FXML private Button scheduleButton;
    @FXML private Button pendingButton;
    @FXML private Button clientsButton;
    @FXML private Button clientOrdersButton;
    @FXML private Button suppliersButton;
    @FXML private Button supplierOrdersButton;
    @FXML private Button backButton;


    @FXML
    private void onScheduleButtonClick(){
        interruptActiveThreads();
        loadPage("II_Schedule");
        refreshButtonStates(scheduleButton);
    }
    @FXML
    private void onPendingButtonClick(){
        interruptActiveThreads();
        loadPage("II_PendingConfirmation");
        refreshButtonStates(pendingButton);
    }
    @FXML
    private void onClientsButtonClick(){
        interruptActiveThreads();
        loadPage("II_Clients");
        refreshButtonStates(clientsButton);
    }
    @FXML
    private void onClientOrdersButtonClick(){
        interruptActiveThreads();
        loadPage("II_ClientOrders");
        refreshButtonStates(clientOrdersButton);
    }
    @FXML
    private void onSuppliersButtonClick(){
        interruptActiveThreads();
        loadPage("II_Suppliers");
        refreshButtonStates(suppliersButton);
    }
    @FXML
    private void onSupplierOrdersButtonClick(){
        interruptActiveThreads();
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


    private void interruptActiveThreads(){
        if(clientOrdersController!=null){
            clientOrdersController.interruptRefreshThread();
        }
        if(clientsController!=null){
            clientsController.interruptRefreshThread();
        }
        if(pendingConfirmationController!=null){
            pendingConfirmationController.interruptRefreshThread();
        }
        if(scheduleController!=null){
            scheduleController.interruptRefreshThread();
        }
        if(supplierOrdersController!=null){
            supplierOrdersController.interruptRefreshThread();
        }
        if(suppliersController!=null){
            suppliersController.interruptRefreshThread();
        }
    }

    private void unselectButton(Button b){
        b.getStyleClass().remove("menu-app-button-selected");
        b.getStyleClass().add("menu-app-button");
        b.setDisable(false);
    }
    private void selectButton(Button b){
        b.getStyleClass().remove("menu-app-button");
        b.getStyleClass().add("menu-app-button-selected");
        b.setDisable(true);
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

            if(page.equals("II_ClientOrders") ){
                II_ClientOrdersController clientOrdersController = contentLoader.getController();
                this.clientOrdersController = clientOrdersController;
            }
            if(page.equals("II_Clients") ){
                II_ClientsController clientsController = contentLoader.getController();
                this.clientsController = clientsController;
            }
            if(page.equals("II_PendingConfirmation") ){
                II_PendingConfirmationController pendingConfirmationController = contentLoader.getController();
                this.pendingConfirmationController = pendingConfirmationController;
            }
            if(page.equals("II_Schedule") ){
                II_ScheduleController scheduleController = contentLoader.getController();
                this.scheduleController = scheduleController;
            }
            if(page.equals("II_SupplierOrders") ){
                II_SupplierOrdersController supplierOrdersController = contentLoader.getController();
                this.supplierOrdersController = supplierOrdersController;
            }
            if(page.equals("II_Suppliers") ){
                II_SuppliersController suppliersController = contentLoader.getController();
                this.suppliersController = suppliersController;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}