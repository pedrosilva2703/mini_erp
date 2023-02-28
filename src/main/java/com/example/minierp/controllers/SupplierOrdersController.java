package com.example.minierp.controllers;

import com.example.minierp.model.Supplier;
import com.example.minierp.model.SupplierOrder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class SupplierOrdersController implements Initializable {

    @FXML private TableView<SupplierOrder> tv_SupplierOrders;
    @FXML private TableColumn<SupplierOrder, String> tc_Name;
    @FXML private TableColumn<SupplierOrder, String> tc_MaterialType;
    @FXML private TableColumn<SupplierOrder, Integer> tc_Quantity;
    @FXML private TableColumn<SupplierOrder, Double> tc_Price;
    @FXML private TableColumn<SupplierOrder, Integer> tc_InitDelEst;
    @FXML private TableColumn<SupplierOrder, Integer> tc_Delay;
    @FXML private TableColumn<SupplierOrder, String> tc_Status;

    ObservableList<SupplierOrder> supp_orders_list = FXCollections.observableArrayList(
            new SupplierOrder("Supplier1", "MetalRawMaterial", 30, 100, 5, 1, "In progress"),
            new SupplierOrder("Supplier1", "BlueRawMaterial", 30, 100, 5, 1, "In progress"),
            new SupplierOrder("Supplier2", "GreenRawMaterial", 30, 100, 5, 1, "In progress"),
            new SupplierOrder("Supplier1", "MetalRawMaterial", 30, 100, 5, 1, "In progress"),
            new SupplierOrder("Supplier1", "MetalRawMaterial", 30, 100, 5, 1, "In progress"),
            new SupplierOrder("Supplier1", "MetalRawMaterial", 30, 100, 5, 1, "In progress"),
            new SupplierOrder("Supplier1", "MetalRawMaterial", 30, 100, 5, 1, "In progress")
    );


    // Initialize method
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        tc_Name.setCellValueFactory(new PropertyValueFactory<SupplierOrder, String>("name") );
        tc_MaterialType.setCellValueFactory(new PropertyValueFactory<SupplierOrder, String>("material_type") );
        tc_Quantity.setCellValueFactory(new PropertyValueFactory<SupplierOrder, Integer>("quantity") );
        tc_Price.setCellValueFactory(new PropertyValueFactory<SupplierOrder, Double>("price") );
        tc_InitDelEst.setCellValueFactory(new PropertyValueFactory<SupplierOrder, Integer>("delivery_week") );;
        tc_Delay.setCellValueFactory(new PropertyValueFactory<SupplierOrder, Integer>("delay") );
        tc_Status.setCellValueFactory(new PropertyValueFactory<SupplierOrder, String>("status") );

        tv_SupplierOrders.setItems(supp_orders_list);
        tv_SupplierOrders.setPrefHeight( (tv_SupplierOrders.getItems().size()+1.15) * tv_SupplierOrders.getFixedCellSize() );
    }

}