package com.example.minierp.controllers;

import com.example.minierp.database.DatabaseHandler;
import com.example.minierp.model.Client;
import com.example.minierp.model.ClientOrder;
import com.example.minierp.model.Piece;
import com.example.minierp.model.SupplierOrder;
import com.example.minierp.utils.Alerts;
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

public class II_PendingConfirmationController implements Initializable {
    DatabaseHandler dbHandler = DatabaseHandler.getInstance();

    @FXML private TableView<ClientOrder> tv_PO;
    @FXML private TableColumn<ClientOrder, String> tc_PO_name;
    @FXML private TableColumn<ClientOrder, String> tc_PO_type;
    @FXML private TableColumn<ClientOrder, Integer> tc_PO_quantity;
    @FXML private TableColumn<ClientOrder, Double> tc_PO_price;
    @FXML private TableColumn<ClientOrder, Integer> tc_PO_deliveryWeek;



    @FXML void acceptButtonClicked(){
        ObservableList<ClientOrder> selectionList;
        selectionList               = tv_PO.getSelectionModel().getSelectedItems();

        if(selectionList.isEmpty() ){
            Alerts.showError("You need to select an order");
            return;
        }

        ClientOrder selected_order  = selectionList.get(0);
        if(!dbHandler.updateClientOrderStatus(selected_order, "pending_client")){
            Alerts.showError("An error occurred. Confirmation failed.");
        }
        Alerts.showInfo("Order internally accepted");

        updateUI();
    }

    @FXML void declineButtonClicked(){
        ObservableList<ClientOrder> selectionList;
        selectionList               = tv_PO.getSelectionModel().getSelectedItems();

        if(selectionList.isEmpty() ){
            Alerts.showError("You need to select an order");
            return;
        }

        ClientOrder selected_order  = selectionList.get(0);
        if(!dbHandler.cancelPendingClientOrder(selected_order, "canceled_internal") ){
            Alerts.showError("An error occurred canceling client order.");
            return;
        }

        //Cancel supplier order associated with his client order (if exists)
        SupplierOrder pending_supplier_order = dbHandler.getPendingSupplierOrderByClientOrder(selected_order);
        if(pending_supplier_order!=null){
            if(!dbHandler.cancelPendingSupplierOrder(pending_supplier_order)){
                Alerts.showError("An error occurred canceling a supplier order");
                return;
            }
        }

        //Set temporary status as canceled, for future deletion
        if(     !dbHandler.updateInboundStatusByClientOrder(selected_order, "canceled")
            ||  !dbHandler.updateProductionStatusByClientOrder(selected_order, "canceled")
            ||  !dbHandler.updateExpeditionStatusByClientOrder(selected_order, "canceled")   ){

            Alerts.showError("An error occurred updating internal orders");
            return;
        }


        //Free pieces foreign keys
        ArrayList<Piece> piecesFromCO = dbHandler.getPiecesByCO(selected_order.getId() );
        for(Piece p : piecesFromCO){
            if(     !dbHandler.updatePiecePO(p, -1)
                ||  !dbHandler.updatePieceEO(p, -1)
                ||  !dbHandler.updatePieceCO(p, -1)  ){

                Alerts.showError("An error occurred updating piece data");
                return;
            }

        }

        //Delete internal orders (when deleting Inbound, it also deletes pieces)
        if(!dbHandler.deleteCanceledInternalOrders()){
            Alerts.showError("An error occurred deleting internal order");
            return;
        }

        Alerts.showInfo("Order was canceled successfully");
        updateUI();
    }

    private void updateUI(){
        tv_PO.getItems().clear();

        ArrayList<ClientOrder> poList = dbHandler.getClientOrdersByStatus("pending_internal");
        if( poList != null ){
            tv_PO.getItems().addAll( poList );
            tv_PO.setPrefHeight( (tv_PO.getItems().size()+1.15) * tv_PO.getFixedCellSize() );
        }
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

        updateUI();

    }

}