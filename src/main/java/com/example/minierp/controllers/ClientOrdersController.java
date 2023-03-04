package com.example.minierp.controllers;

import com.example.minierp.model.Client;
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

public class ClientOrdersController implements Initializable {

    @FXML private AnchorPane anchor_PO;
    @FXML private TableView<ClientOrder> tv_PO;
    @FXML private TableColumn<ClientOrder, String> tc_PO_name;
    @FXML private TableColumn<ClientOrder, String> tc_PO_type;
    @FXML private TableColumn<ClientOrder, Integer> tc_PO_quantity;
    @FXML private TableColumn<ClientOrder, Double> tc_PO_price;
    @FXML private TableColumn<ClientOrder, Integer> tc_PO_deliveryWeek;

    @FXML private AnchorPane anchor_CO;
    @FXML private TableView<ClientOrder> tv_CO;
    @FXML private TableColumn<ClientOrder, String> tc_CO_name;
    @FXML private TableColumn<ClientOrder, String> tc_CO_type;
    @FXML private TableColumn<ClientOrder, Integer> tc_CO_quantity;
    @FXML private TableColumn<ClientOrder, Double> tc_CO_price;
    @FXML private TableColumn<ClientOrder, Integer> tc_CO_deliveryWeek;
    @FXML private TableColumn<ClientOrder, Integer> tc_CO_currentEstimation;
    @FXML private TableColumn<ClientOrder, String> tc_CO_status;


    ObservableList<ClientOrder> po_list = FXCollections.observableArrayList(
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente1", "BlueProductLid", 20, 100.00, 3, 5, "Delivered")
    );

    ObservableList<ClientOrder> co_list = FXCollections.observableArrayList(
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "In Progress"),
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "Delivered"),
            new ClientOrder("Cliente2", "GreenProductBase", 20, 100.00, 3, 5, "Progress")
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


        // Setup the table for CONFIRMED Orders
        tc_CO_name.setCellValueFactory(new PropertyValueFactory<ClientOrder, String>("client") );
        tc_CO_type.setCellValueFactory(new PropertyValueFactory<ClientOrder, String>("type") );
        tc_CO_quantity.setCellValueFactory(new PropertyValueFactory<ClientOrder, Integer>("quantity") );
        tc_CO_price.setCellValueFactory(new PropertyValueFactory<ClientOrder, Double>("price") );
        tc_CO_deliveryWeek.setCellValueFactory(new PropertyValueFactory<ClientOrder, Integer>("delivery_week") );
        tc_CO_currentEstimation.setCellValueFactory(new PropertyValueFactory<ClientOrder, Integer>("current_estimation") );
        tc_CO_status.setCellValueFactory(new PropertyValueFactory<ClientOrder, String>("status") );
        tv_CO.setItems(co_list);
        tv_CO.setPrefHeight( (tv_CO.getItems().size()+1.15) * tv_CO.getFixedCellSize() );

        // Constraint the TOP of the 2nd table to the Height of the first table
        double po_height = anchor_PO.getPrefHeight()+tv_PO.getPrefHeight();
        AnchorPane.setTopAnchor( anchor_CO, po_height );


    }

}