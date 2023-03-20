package com.example.minierp.controllers;

import com.example.minierp.database.DatabaseHandler;
import com.example.minierp.model.Client;
import com.example.minierp.model.ClientOrder;
import com.example.minierp.utils.Alerts;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class II_PendingConfirmationController implements Initializable {
    DatabaseHandler dbHandler = DatabaseHandler.getInstance();

    @FXML private TableView<ClientOrder> tv_PO;
    @FXML private TableColumn<ClientOrder, String> tc_PO_name;
    @FXML private TableColumn<ClientOrder, String> tc_PO_type;
    @FXML private TableColumn<ClientOrder, Integer> tc_PO_quantity;
    @FXML private TableColumn<ClientOrder, Double> tc_PO_price;
    @FXML private TableColumn<ClientOrder, Integer> tc_PO_deliveryWeek;



    @FXML void acceptButtonClicked(){
        ObservableList<ClientOrder> selectionList;
        selectionList               = tv_PO.getSelectionModel().getSelectedItems();

        if(selectionList.isEmpty() ){
            Alerts.showError("You need to select an order");
            return;
        }

        ClientOrder selected_order  = selectionList.get(0);
        if(!dbHandler.updateClientOrderStatus(selected_order, "pending_client")){
            Alerts.showError("An error occured. Confirmation failed.");
        }
        Alerts.showInfo("Order internally accepted");

        updateUI();
    }

    private void updateUI(){
        tv_PO.getItems().clear();

        ArrayList<ClientOrder> poList = dbHandler.getClientOrdersByStatus("pending_internal");
        if( poList != null ){
            tv_PO.getItems().addAll( poList );
            tv_PO.setPrefHeight( (tv_PO.getItems().size()+1.15) * tv_PO.getFixedCellSize() );
        }
    }

    // Initialize method
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setup the table for PENDING Orders
        tc_PO_name.setCellValueFactory(new PropertyValueFactory<ClientOrder, String>("client") );
        tc_PO_type.setCellValueFactory(new PropertyValueFactory<ClientOrder, String>("type") );
        tc_PO_quantity.setCellValueFactory(new PropertyValueFactory<ClientOrder, Integer>("quantity") );;
        tc_PO_price.setCellValueFactory(new PropertyValueFactory<ClientOrder, Double>("price") );
        tc_PO_deliveryWeek.setCellValueFactory(new PropertyValueFactory<ClientOrder, Integer>("delivery_week") );;

        updateUI();

    }

}