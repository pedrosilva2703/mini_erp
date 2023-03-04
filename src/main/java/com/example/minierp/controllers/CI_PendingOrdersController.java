package com.example.minierp.controllers;

import com.example.minierp.model.ClientOrder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class CI_PendingOrdersController implements Initializable {

    @FXML private AnchorPane anchor_PO;
    @FXML private TableView<ClientOrder> tv_PO;
    @FXML private TableColumn<ClientOrder, String> tc_PO_name;
    @FXML private TableColumn<ClientOrder, String> tc_PO_type;
    @FXML private TableColumn<ClientOrder, Integer> tc_PO_quantity;
    @FXML private TableColumn<ClientOrder, Double> tc_PO_price;
    @FXML private TableColumn<ClientOrder, Integer> tc_PO_deliveryWeek;

    ObservableList<ClientOrder> po_list = FXCollections.observableArrayList(
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),

            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered")
    );

    // Initialize method
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Setup the table for PENDING Orders
        tc_PO_name.setCellValueFactory(new PropertyValueFactory<ClientOrder, String>("client") );
        tc_PO_type.setCellValueFactory(new PropertyValueFactory<ClientOrder, String>("type") );
        tc_PO_quantity.setCellValueFactory(new PropertyValueFactory<ClientOrder, Integer>("quantity") );;
        tc_PO_price.setCellValueFactory(new PropertyValueFactory<ClientOrder, Double>("price") );
        tc_PO_deliveryWeek.setCellValueFactory(new PropertyValueFactory<ClientOrder, Integer>("delivery_week") );;
        tv_PO.setItems(po_list);
        tv_PO.setPrefHeight( (tv_PO.getItems().size()+1.15) * tv_PO.getFixedCellSize() );



    }

}