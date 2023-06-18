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

public class SI_LayoutController {
    SI_EditController editController;
    SI_OrdersController ordersController;

    @FXML private BorderPane mainPane;
    @FXML private Button ordersButton;
    @FXML private Button editParamsButton;


    @FXML
    private void onOrdersButtonClick(){
        interruptActiveThreads();
        loadPage("SI_Orders");
        refreshButtonStates(ordersButton);
    }
    @FXML
    private void onEditParamsButtonClick(){
        interruptActiveThreads();
        loadPage("SI_Edit");
        refreshButtonStates(editParamsButton);
    }


    private void interruptActiveThreads(){
        if(editController!=null){
            editController.interruptRefreshThread();
        }
        if(ordersController!=null){
            ordersController.interruptRefreshThread();
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
        unselectButton(ordersButton);
        unselectButton(editParamsButton);

        // Select clicked button
        selectButton(clickedButton);
    }

    private void loadPage(String page){
        try {
            FXMLLoader contentLoader = new FXMLLoader(Launcher.class.getResource(page+".fxml"));
            AnchorPane content = contentLoader.load();
            // Add the navigation menu to the left side of the BorderPane
            mainPane.setCenter(content);

            if(page.equals("SI_Orders") ){
                SI_OrdersController ordersController = contentLoader.getController();
                this.ordersController = ordersController;
            }
            if(page.equals("SI_Edit") ){
                SI_EditController editController = contentLoader.getController();
                this.editController = editController;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}