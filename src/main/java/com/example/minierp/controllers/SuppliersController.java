package com.example.minierp.controllers;

import com.example.minierp.model.Client;
import com.example.minierp.model.Supplier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class SuppliersController implements Initializable {

    @FXML private TableView<Supplier> tv_Suppliers;
    @FXML private TableColumn<Supplier, String> tc_Name;
    @FXML private TableColumn<Supplier, String> tc_MaterialType;
    @FXML private TableColumn<Supplier, Integer> tc_UPrice;
    @FXML private TableColumn<Supplier, Integer> tc_MinQty;
    @FXML private TableColumn<Supplier, Integer> tc_DeliveryTime;
    @FXML private TableColumn<Supplier, Double> tc_DelayProb;

    ObservableList<Supplier> supp_list = FXCollections.observableArrayList(
            new Supplier("Supplier1", "BlueRawMaterial", 5, 10, 3, 5.00),
            new Supplier("Supplier1", "GreenRawMaterial", 5, 10, 3, 5.00),
            new Supplier("Supplier2", "MetalRawMaterial", 5, 10, 3, 5.00),
            new Supplier("Supplier3", "BlueRawMaterial", 5, 10, 3, 5.00),
            new Supplier("Supplier3", "GreenRawMaterial", 5, 10, 3, 5.00),
            new Supplier("Supplier3", "MetalRawMaterial", 5, 10, 3, 5.00)
    );


    // Initialize method
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        tc_Name.setCellValueFactory(new PropertyValueFactory<Supplier, String>("name") );
        tc_MaterialType.setCellValueFactory(new PropertyValueFactory<Supplier, String>("material_type") );
        tc_UPrice.setCellValueFactory(new PropertyValueFactory<Supplier, Integer>("unit_price") );
        tc_MinQty.setCellValueFactory(new PropertyValueFactory<Supplier, Integer>("min_quantity") );
        tc_DeliveryTime.setCellValueFactory(new PropertyValueFactory<Supplier, Integer>("delivery_time") );
        tc_DelayProb.setCellValueFactory(new PropertyValueFactory<Supplier, Double>("delay_prob") );;;

        tv_Suppliers.setItems(supp_list);
        tv_Suppliers.setPrefHeight( (tv_Suppliers.getItems().size()+1.15) * tv_Suppliers.getFixedCellSize() );

    }

}