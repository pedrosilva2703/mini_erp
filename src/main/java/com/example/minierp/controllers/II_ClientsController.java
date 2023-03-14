package com.example.minierp.controllers;

import com.example.minierp.database.DatabaseHandler;
import com.example.minierp.model.Client;
import com.example.minierp.utils.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class II_ClientsController implements Initializable {
    DatabaseHandler dbHandler = DatabaseHandler.getInstance();

    @FXML private TableView<Client> tv_Clients;
    @FXML private TableColumn<Client, String> tc_Name;
    @FXML private TableColumn<Client, Integer> tc_Orders;
    @FXML private TextField tf_name;

    @FXML void addClient(){
        String name = tf_name.getText();
        if( name.isEmpty() ){
            Alerts.showError("Client's name cannot be empty");
            return;
        }
        if( dbHandler.nameExists("client", name) ){
            Alerts.showError("This name is already registered");
            return;
        }

        Client client = new Client(null, name, 0);
        if(!dbHandler.createClient(client)){
            Alerts.showError("An error ocurred, please try again");
        }

        Alerts.showInfo("Client added successfully");
        updateUI();
    }


    private void updateUI(){
        tf_name.clear();
        tv_Clients.getItems().clear();

        ArrayList<Client> clientList = dbHandler.getClients();
        if( clientList != null ){
            tv_Clients.getItems().addAll( clientList );
            tv_Clients.setPrefHeight( (tv_Clients.getItems().size()+1.15) * tv_Clients.getFixedCellSize() );
        }
    }

    // Initialize method
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tc_Name.setCellValueFactory(new PropertyValueFactory<Client, String>("Name") );
        tc_Orders.setCellValueFactory(new PropertyValueFactory<Client, Integer>("Orders") );

        updateUI();
    }

}