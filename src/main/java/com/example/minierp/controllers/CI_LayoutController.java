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
    CI_ConfirmedOrdersController confirmedOrdersController;
    CI_NewOrderController newOrderController;
    CI_PendingOrdersController pendingOrdersController;

    @FXML private BorderPane mainPane;
    @FXML private Button newOrderButton;
    @FXML private Button pendingButton;
    @FXML private Button confirmedButton;


    @FXML
    private void onNewOrderButtonClick(){
        interruptActiveThreads();
        loadPage("CI_NewOrder");
        refreshButtonStates(newOrderButton);
    }
    @FXML
    private void onPendingButtonClick(){
        interruptActiveThreads();
        loadPage("CI_PendingOrders");
        refreshButtonStates(pendingButton);
    }
    @FXML
    private void onConfirmedButtonClick(){
        interruptActiveThreads();
        loadPage("CI_ConfirmedOrders");
        refreshButtonStates(confirmedButton);
    }


    private void interruptActiveThreads(){
        if(confirmedOrdersController!=null){
            confirmedOrdersController.interruptRefreshThread();
        }
        if(newOrderController!=null){
            newOrderController.interruptRefreshThread();
        }
        if(pendingOrdersController!=null){
            pendingOrdersController.interruptRefreshThread();
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

            if(page.equals("CI_ConfirmedOrders") ){
                CI_ConfirmedOrdersController confirmedOrdersController = contentLoader.getController();
                this.confirmedOrdersController = confirmedOrdersController;
            }
            if(page.equals("CI_NewOrder") ){
                CI_NewOrderController newOrderController = contentLoader.getController();
                this.newOrderController = newOrderController;
            }
            if(page.equals("CI_PendingOrders") ){
                CI_PendingOrdersController pendingOrdersController = contentLoader.getController();
                this.pendingOrdersController = pendingOrdersController;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}