package com.example.minierp.controllers;

import com.example.minierp.database.DatabaseHandler;
import com.example.minierp.model.Client;
import com.example.minierp.model.ClientOrder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CI_ConfirmedOrdersController implements Initializable {
    DatabaseHandler dbHandler = DatabaseHandler.getInstance();

    @FXML private ComboBox<String> comboName;

    @FXML private TableView<ClientOrder> tv_CO;
    @FXML private TableColumn<ClientOrder, String> tc_CO_name;
    @FXML private TableColumn<ClientOrder, String> tc_CO_type;
    @FXML private TableColumn<ClientOrder, Integer> tc_CO_quantity;
    @FXML private TableColumn<ClientOrder, Double> tc_CO_price;
    @FXML private TableColumn<ClientOrder, Integer> tc_CO_deliveryWeek;
    @FXML private TableColumn<ClientOrder, Integer> tc_CO_currentEstimation;
    @FXML private TableColumn<ClientOrder, String> tc_CO_status;

    @FXML void filterClient(){
        tv_CO.getItems().clear();
        String selected_name = comboName.getValue();

        ArrayList<ClientOrder> coList = dbHandler.getClientOrdersByName(selected_name, "confirmed");
        coList.addAll(dbHandler.getClientOrdersByName(selected_name, "canceled_client"));
        coList.addAll(dbHandler.getClientOrdersByName(selected_name, "canceled_internal"));
        if( coList != null ){
            tv_CO.getItems().addAll( coList );
            tv_CO.setPrefHeight( (tv_CO.getItems().size()+1.15) * tv_CO.getFixedCellSize() );
        }
    }

    private void fillNameFilter(){
        ArrayList<Client> clientList = dbHandler.getClients();
        if( clientList == null ) return;
        for( Client c : clientList ){
            comboName.getItems().add(c.getName() );
        }
    }

    // Initialize method
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fillNameFilter();

        // Setup the table for CONFIRMED Orders
        tc_CO_name.setCellValueFactory(new PropertyValueFactory<ClientOrder, String>("client") );
        tc_CO_type.setCellValueFactory(new PropertyValueFactory<ClientOrder, String>("type") );
        tc_CO_quantity.setCellValueFactory(new PropertyValueFactory<ClientOrder, Integer>("quantity") );
        tc_CO_price.setCellValueFactory(new PropertyValueFactory<ClientOrder, Double>("price") );
        tc_CO_deliveryWeek.setCellValueFactory(new PropertyValueFactory<ClientOrder, Integer>("delivery_week") );
        tc_CO_currentEstimation.setCellValueFactory(new PropertyValueFactory<ClientOrder, Integer>("current_estimation") );
        tc_CO_status.setCellValueFactory(new PropertyValueFactory<ClientOrder, String>("status") );

    }

}