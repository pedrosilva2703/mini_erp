package com.example.minierp.controllers;

import com.example.minierp.database.DatabaseHandler;
import com.example.minierp.model.Client;
import com.example.minierp.model.Supplier;
import com.example.minierp.model.SupplierOrder;
import javafx.collections.FXCollections;
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

public class SI_OrdersController implements Initializable {
    DatabaseHandler dbHandler = DatabaseHandler.getInstance();

    @FXML private ComboBox<String> comboName;

    @FXML private TableView<SupplierOrder> tv_SupplierOrders;
    @FXML private TableColumn<SupplierOrder, String> tc_Name;
    @FXML private TableColumn<SupplierOrder, String> tc_MaterialType;
    @FXML private TableColumn<SupplierOrder, Integer> tc_Quantity;
    @FXML private TableColumn<SupplierOrder, Double> tc_Price;
    @FXML private TableColumn<SupplierOrder, Integer> tc_InitDelEst;
    @FXML private TableColumn<SupplierOrder, Integer> tc_Delay;
    @FXML private TableColumn<SupplierOrder, String> tc_Status;

    @FXML void filterSupplier(){
        tv_SupplierOrders.getItems().clear();
        String selected_name = comboName.getValue();

        ArrayList<SupplierOrder> soList = dbHandler.getSupplierOrdersByName(selected_name);
        if( soList != null ){
            tv_SupplierOrders.getItems().addAll( soList );
            tv_SupplierOrders.setPrefHeight( (tv_SupplierOrders.getItems().size()+1.15) * tv_SupplierOrders.getFixedCellSize() );
        }
    }

    private void fillNameFilter(){
        ArrayList<String> nameList = dbHandler.getSupplierNames();
        if( nameList == null ) return;
        for( String s : nameList ){
            comboName.getItems().add(s);
        }
    }

    // Initialize method
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        fillNameFilter();

        tc_Name.setCellValueFactory(new PropertyValueFactory<SupplierOrder, String>("name") );
        tc_MaterialType.setCellValueFactory(new PropertyValueFactory<SupplierOrder, String>("material_type") );
        tc_Quantity.setCellValueFactory(new PropertyValueFactory<SupplierOrder, Integer>("quantity") );
        tc_Price.setCellValueFactory(new PropertyValueFactory<SupplierOrder, Double>("price") );
        tc_InitDelEst.setCellValueFactory(new PropertyValueFactory<SupplierOrder, Integer>("delivery_week") );;
        tc_Delay.setCellValueFactory(new PropertyValueFactory<SupplierOrder, Integer>("delay") );
        tc_Status.setCellValueFactory(new PropertyValueFactory<SupplierOrder, String>("status") );
    }

}