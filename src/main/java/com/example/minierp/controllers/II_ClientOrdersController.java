package com.example.minierp.controllers;

import com.example.minierp.database.DatabaseHandler;
import com.example.minierp.model.Client;
import com.example.minierp.model.ClientOrder;
import com.example.minierp.model.SupplierOrder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class II_ClientOrdersController implements Initializable {
    DatabaseHandler dbHandler = DatabaseHandler.getInstance();

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


    private void updateUI(){
        tv_PO.getItems().clear();
        ArrayList<ClientOrder> poList = dbHandler.getClientOrdersByStatus("pending_client");
        if( poList != null ){
            tv_PO.getItems().addAll( poList );
            tv_PO.setPrefHeight( (tv_PO.getItems().size()+1.15) * tv_PO.getFixedCellSize() );
        }

        tv_CO.getItems().clear();
        ArrayList<ClientOrder> coList = dbHandler.getClientOrdersByStatus("confirmed");
        if( coList != null ){
            tv_CO.getItems().addAll( coList );
            tv_CO.setPrefHeight( (tv_CO.getItems().size()+1.15) * tv_CO.getFixedCellSize() );
        }

        // Constraint the TOP of the 2nd table to the Height of the first table
        double po_height = anchor_PO.getPrefHeight()+tv_PO.getPrefHeight();
        AnchorPane.setTopAnchor( anchor_CO, po_height );

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

        // Setup the table for CONFIRMED Orders
        tc_CO_name.setCellValueFactory(new PropertyValueFactory<ClientOrder, String>("client") );
        tc_CO_type.setCellValueFactory(new PropertyValueFactory<ClientOrder, String>("type") );
        tc_CO_quantity.setCellValueFactory(new PropertyValueFactory<ClientOrder, Integer>("quantity") );
        tc_CO_price.setCellValueFactory(new PropertyValueFactory<ClientOrder, Double>("price") );
        tc_CO_deliveryWeek.setCellValueFactory(new PropertyValueFactory<ClientOrder, Integer>("delivery_week") );
        tc_CO_currentEstimation.setCellValueFactory(new PropertyValueFactory<ClientOrder, Integer>("current_estimation") );
        tc_CO_status.setCellValueFactory(new PropertyValueFactory<ClientOrder, String>("status") );

        updateUI();


    }

}