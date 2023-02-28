package com.example.minierp.controllers;

import com.example.minierp.model.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientsController implements Initializable {

    @FXML
    private TableColumn<Client, String> tc_Name;

    @FXML
    private TableColumn<Client, Integer> tc_Orders;

    @FXML
    private TableView<Client> tv_Clients;

    ObservableList<Client> list = FXCollections.observableArrayList(
            new Client("João", 1),
            new Client("João", 1),
            new Client("João", 1),
            new Client("João", 1),
            new Client("João", 1),
            new Client("João", 1),
            new Client("João", 1),
            new Client("João", 1),
            new Client("João", 1),
            new Client("João", 1),
            new Client("João", 1),
            new Client("João", 1),
            new Client("João", 1),
            new Client("João", 1),

            new Client("João", 1),
            new Client("Pedro", 2)
    );


    // Initialize method
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tc_Name.setCellValueFactory(new PropertyValueFactory<Client, String>("Name") );
        tc_Orders.setCellValueFactory(new PropertyValueFactory<Client, Integer>("Orders") );

        tv_Clients.setItems(list);

        tv_Clients.setPrefHeight( (tv_Clients.getItems().size()+1.15) * tv_Clients.getFixedCellSize() );

    }

}