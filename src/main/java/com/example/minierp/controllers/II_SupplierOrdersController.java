package com.example.minierp.controllers;

import com.example.minierp.database.DatabaseHandler;
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
import java.util.ArrayList;
import java.util.ResourceBundle;

public class II_SupplierOrdersController implements Initializable {
    DatabaseHandler dbHandler = DatabaseHandler.getInstance();

    @FXML private TableView<SupplierOrder> tv_SupplierOrders;
    @FXML private TableColumn<SupplierOrder, String> tc_Name;
    @FXML private TableColumn<SupplierOrder, String> tc_MaterialType;
    @FXML private TableColumn<SupplierOrder, Integer> tc_Quantity;
    @FXML private TableColumn<SupplierOrder, Double> tc_Price;
    @FXML private TableColumn<SupplierOrder, Integer> tc_InitDelEst;
    @FXML private TableColumn<SupplierOrder, Integer> tc_Delay;
    @FXML private TableColumn<SupplierOrder, String> tc_Status;


    private void updateUI(){
        tv_SupplierOrders.getItems().clear();

        ArrayList<SupplierOrder> soList = dbHandler.getSupplierOrders();
        if( soList != null ){
            tv_SupplierOrders.getItems().addAll( soList );
            tv_SupplierOrders.setPrefHeight( (tv_SupplierOrders.getItems().size()+1.15) * tv_SupplierOrders.getFixedCellSize() );
        }
    }
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

        updateUI();
    }

}