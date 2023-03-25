package com.example.minierp.controllers;

import com.example.minierp.database.DatabaseHandler;
import com.example.minierp.model.ClientOrder;
import com.example.minierp.model.ExpeditionOrder;
import com.example.minierp.model.InboundOrder;
import com.example.minierp.model.ProductionOrder;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class II_ScheduleController implements Initializable {
    DatabaseHandler dbHandler = DatabaseHandler.getInstance();

    @FXML private AnchorPane anchor_EO;
    @FXML private AnchorPane anchor_IO;
    @FXML private AnchorPane anchor_PO;

    @FXML private TableColumn<ExpeditionOrder, String> tc_EO_client;
    @FXML private TableColumn<ExpeditionOrder, Integer> tc_EO_quantity;
    @FXML private TableColumn<ExpeditionOrder, String> tc_EO_status;
    @FXML private TableColumn<ExpeditionOrder, Integer> tc_EO_type;
    @FXML private TableColumn<ExpeditionOrder, Integer> tc_EO_week;

    @FXML private TableColumn<InboundOrder, Integer> tc_IO_quantity;
    @FXML private TableColumn<InboundOrder, String> tc_IO_status;
    @FXML private TableColumn<InboundOrder, String> tc_IO_supplier;
    @FXML private TableColumn<InboundOrder, String> tc_IO_type;
    @FXML private TableColumn<InboundOrder, Integer> tc_IO_week;

    @FXML private TableColumn<ProductionOrder, String> tc_PO_final_type;
    @FXML private TableColumn<ProductionOrder, String> tc_PO_init_type;
    @FXML private TableColumn<ProductionOrder, Integer> tc_PO_quantity;
    @FXML private TableColumn<ProductionOrder, String> tc_PO_status;
    @FXML private TableColumn<ProductionOrder, Integer> tc_PO_week;

    @FXML private TableView<ExpeditionOrder> tv_EO;
    @FXML private TableView<InboundOrder> tv_IO;
    @FXML private TableView<ProductionOrder> tv_PO;


    private void updateUI(){
        //Inbound orders
        tv_IO.getItems().clear();
        ArrayList<InboundOrder> ioList = dbHandler.getInboundOrders();
        if( ioList != null ){
            tv_IO.getItems().addAll( ioList );
            tv_IO.setPrefHeight( (tv_IO.getItems().size()+1.15) * tv_IO.getFixedCellSize() );
        }

        //Production Orders
        tv_PO.getItems().clear();
        ArrayList<ProductionOrder> poList = dbHandler.getProductionOrders();
        if( poList != null ){
            tv_PO.getItems().addAll( poList );
            tv_PO.setPrefHeight( (tv_PO.getItems().size()+1.15) * tv_PO.getFixedCellSize() );
        }
        // Constraint the TOP of the 2nd table to the Height of the first table
        double io_height = anchor_IO.getPrefHeight()+tv_IO.getPrefHeight();
        AnchorPane.setTopAnchor(anchor_PO, io_height);

        //Expedition Orders
        tv_EO.getItems().clear();
        ArrayList<ExpeditionOrder> eoList = dbHandler.getExpeditionOrders();
        if( eoList != null ){
            tv_EO.getItems().addAll( eoList );
            tv_EO.setPrefHeight( (tv_EO.getItems().size()+1.15) * tv_EO.getFixedCellSize() );
        }
        // Constraint the TOP of the 3nd table to the Height of the 2nd table
        double po_height = anchor_PO.getPrefHeight()+tv_PO.getPrefHeight() + io_height;
        AnchorPane.setTopAnchor(anchor_EO, po_height);

    }

    // Initialize method
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tc_IO_quantity.setCellValueFactory(new PropertyValueFactory<InboundOrder, Integer>("quantity") );
        tc_IO_status.setCellValueFactory(new PropertyValueFactory<InboundOrder, String>("status") );
        tc_IO_supplier.setCellValueFactory(new PropertyValueFactory<InboundOrder, String>("supplier") );;
        tc_IO_type.setCellValueFactory(new PropertyValueFactory<InboundOrder, String>("type") );
        tc_IO_week.setCellValueFactory(new PropertyValueFactory<InboundOrder, Integer>("week") );;

        tc_PO_final_type.setCellValueFactory(new PropertyValueFactory<ProductionOrder, String>("final_type") );
        tc_PO_init_type.setCellValueFactory(new PropertyValueFactory<ProductionOrder, String>("initial_type") );
        tc_PO_quantity.setCellValueFactory(new PropertyValueFactory<ProductionOrder, Integer>("quantity") );
        tc_PO_status.setCellValueFactory(new PropertyValueFactory<ProductionOrder, String>("status") );
        tc_PO_week.setCellValueFactory(new PropertyValueFactory<ProductionOrder, Integer>("week") );

        tc_EO_client.setCellValueFactory(new PropertyValueFactory<ExpeditionOrder, String>("client") );
        tc_EO_quantity.setCellValueFactory(new PropertyValueFactory<ExpeditionOrder, Integer>("quantity") );
        tc_EO_status.setCellValueFactory(new PropertyValueFactory<ExpeditionOrder, String>("status") );;
        tc_EO_type.setCellValueFactory(new PropertyValueFactory<ExpeditionOrder, Integer>("type") );
        tc_EO_week.setCellValueFactory(new PropertyValueFactory<ExpeditionOrder, Integer>("week") );;

        updateUI();
    }

}