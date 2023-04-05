package com.example.minierp.controllers;

import com.example.minierp.database.DatabaseHandler;
import com.example.minierp.model.*;
import com.example.minierp.utils.Alerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CI_ConfirmedOrdersController implements Initializable {
    DatabaseHandler dbHandler = DatabaseHandler.getInstance();

    @FXML private ComboBox<String> comboName;

    @FXML private TableView<ClientOrder> tv_CO;
    @FXML private TableColumn<ClientOrder, String> tc_CO_name;
    @FXML private TableColumn<ClientOrder, String> tc_CO_type;
    @FXML private TableColumn<ClientOrder, Integer> tc_CO_quantity;
    @FXML private TableColumn<ClientOrder, Double> tc_CO_price;
    @FXML private TableColumn<ClientOrder, Integer> tc_CO_deliveryWeek;
    @FXML private TableColumn<ClientOrder, Integer> tc_CO_currentEstimation;
    @FXML private TableColumn<ClientOrder, String> tc_CO_status;

    @FXML void filterClient(){
        tv_CO.getItems().clear();
        String selected_name = comboName.getValue();

        ArrayList<ClientOrder> coList = dbHandler.getClientOrdersByName(selected_name, "confirmed");
        coList.addAll(dbHandler.getClientOrdersByName(selected_name, "canceled_client"));
        coList.addAll(dbHandler.getClientOrdersByName(selected_name, "canceled_internal"));
        if( coList != null ){
            tv_CO.getItems().addAll( coList );
            tv_CO.setPrefHeight( (tv_CO.getItems().size()+1.15) * tv_CO.getFixedCellSize() );
        }
    }

    @FXML void cancelButtonClicked() {
        ObservableList<ClientOrder> selectionList;
        selectionList = tv_CO.getSelectionModel().getSelectedItems();
        if (selectionList.isEmpty()) {
            Alerts.showError("You need to select an order");
            return;
        }

        ClientOrder selected_order  = selectionList.get(0);
        if(!dbHandler.cancelPendingClientOrder(selected_order, "canceled_client") ){
            Alerts.showError("An error occurred canceling client order.");
            return;
        }

        //Cancelar a EO que está associada à CO
        dbHandler.updateExpeditionStatusByClientOrder(selected_order, "canceled");

        //Cancelar as Confirmed PO das peças que têm esta CO
        ArrayList<ProductionOrder> po_confirmed = dbHandler.getConfirmedProductionOrdersByCO(selected_order);
        for(ProductionOrder po : po_confirmed){
            dbHandler.cancelProductionOrder(po);
            for(Piece p : po.getPieces() ){
                p.setFinal_type("");
                dbHandler.updatePiecePO(p, -1);
            }
        }
        //As que estao running nao faz nada

        //Cancelar as Confirmed IO das peças que têm esta CO e não estao associadas a mais nenhuma encome
        //*Obter SO associadas a esta CO
        ArrayList<SupplierOrder> so_list = dbHandler.getConfirmedSupplierOrdersByClientOrder(selected_order);
            //Para cada SO
            for(SupplierOrder so : so_list){
                //Verifica se tem outra CO diferente associada
                if(dbHandler.isOnlyClientOrderForSupplierOrder(selected_order, so)){
                    //Se nao existir, cancelar SO e IO, limpar o final_type das peças
                    dbHandler.updateSupplierOrderStatus(so, "canceled");
                    InboundOrder io = dbHandler.getInboundOrderBySupplierOrder(so);
                    dbHandler.cancelInboundOrder(io);
                }
                //Se existir, entao nao faz nada
            }


        //Para todas as peças da CO, apaga a FK da CO, PO, EO
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
        filterClient();

    }

    private void fillNameFilter(){
        ArrayList<Client> clientList = dbHandler.getClients();
        if( clientList == null ) return;
        for( Client c : clientList ){
            comboName.getItems().add(c.getName() );
        }
    }

    // Initialize method
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fillNameFilter();

        // Setup the table for CONFIRMED Orders
        tc_CO_name.setCellValueFactory(new PropertyValueFactory<ClientOrder, String>("client") );
        tc_CO_type.setCellValueFactory(new PropertyValueFactory<ClientOrder, String>("type") );
        tc_CO_quantity.setCellValueFactory(new PropertyValueFactory<ClientOrder, Integer>("quantity") );
        tc_CO_price.setCellValueFactory(new PropertyValueFactory<ClientOrder, Double>("price") );
        tc_CO_deliveryWeek.setCellValueFactory(new PropertyValueFactory<ClientOrder, Integer>("delivery_week") );
        tc_CO_currentEstimation.setCellValueFactory(new PropertyValueFactory<ClientOrder, Integer>("current_estimation") );
        tc_CO_status.setCellValueFactory(new PropertyValueFactory<ClientOrder, String>("status") );

    }

}