package com.example.minierp.controllers;

import com.example.minierp.database.DatabaseHandler;
import com.example.minierp.model.*;
import com.example.minierp.utils.Alerts;
import com.example.minierp.utils.Materials;
import com.example.minierp.utils.Verifier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SI_OrdersController implements Initializable {
    DatabaseHandler dbHandler = DatabaseHandler.getInstance();
    Factory factory = Factory.getInstance();

    @FXML private TextField tf_delay;

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

    @FXML void delayButtonPressed() {

        ObservableList<SupplierOrder> selectionList;
        selectionList               = tv_SupplierOrders.getSelectionModel().getSelectedItems();
        if(selectionList.isEmpty() ){
            Alerts.showError("You need to select an order!");
            return;
        }
        SupplierOrder selected_order  = selectionList.get(0);
        if(selected_order.getStatus().equals("waiting_confirmation") ){
            Alerts.showError("This order is not even confirmed yet!");
            return;
        }
        if(selected_order.getStatus().equals("canceled") ){
            Alerts.showError("You can't delay a canceled order!");
            return;
        }
        if(selected_order.getStatus().equals("arriving") ){
            Alerts.showError("This order is already arriving next week!");
            return;
        }
        if(selected_order.getStatus().equals("completed") ){
            Alerts.showError("This order was already completed!");
            return;
        }
        if(selected_order.getDelay()!=0){
            Alerts.showError("This order was already delayed once!");
            return;
        }

        if(!Verifier.isInteger(tf_delay)){
            Alerts.showError("Invalid delay value");
            return;
        }
        int delay = Integer.parseInt(tf_delay.getText());
        if(delay<1){
            Alerts.showError("Invalid delay value");
            return;
        }

        //Colocar o delay na SO
        selected_order.setDelay(delay);
        dbHandler.updateSupplierOrderDelay(selected_order, delay);
        //Alterar a week da IO que corresponde a esta SO
        int inbound_week = selected_order.getDelivery_week() + delay;
        dbHandler.updateInboundWeekBySupplierOrder(selected_order);

        //Obter uma lista de PO que tem peças desta SO
        ArrayList<ProductionOrder> po_list = dbHandler.getConfirmedProductionOrdersBySO(selected_order);
        //Para cada PO
        for(ProductionOrder po : po_list){
            //Para cada peça da PO
            for(Piece p : po.getPieces() ){
                //Desassociar da PO
                dbHandler.updatePiecePO(p, -1);
            }
            //Apagar a PO
            dbHandler.cancelProductionOrder(po);
        }
        dbHandler.deleteCanceledInternalOrders();


        //Obter uma lista de CO (WEEK ASC) que estao associadas a esta SO
        ArrayList<ClientOrder> co_list = dbHandler.getClientOrdersBySupplierOrder(selected_order);

        //Para cada CO
        for(ClientOrder co : co_list){
            //Obter uma lista de peças por CO e SO
            ArrayList<Piece> piece_CO_SO_list = dbHandler.getPiecesBySOandCO(selected_order, co);

            //Schedule the production of the pieces arriving
            ArrayList<ProductionOrder> POrdersList_new = new ArrayList<>();
            int pieces_scheduled = 0;
            int production_week = inbound_week+1;
            while(pieces_scheduled != piece_CO_SO_list.size() ){
                int week_available_capacity = factory.getWeekly_production() - dbHandler.getProductionCountByWeek(production_week);

                if(week_available_capacity == 0){
                    production_week++;
                    continue;
                }

                ArrayList<Piece> po_pieces = new ArrayList<>();
                for(int i=0; i<week_available_capacity; i++){
                    if(pieces_scheduled == piece_CO_SO_list.size()) break;
                    po_pieces.add( piece_CO_SO_list.get(pieces_scheduled) );
                    pieces_scheduled++;
                }
                ProductionOrder currPO = new ProductionOrder(null,
                                                                production_week,
                                                          "waiting_confirmation",
                                                                Materials.getRawType(co.getType()),
                                                                co.getType(),
                                                                po_pieces);

                POrdersList_new.add(currPO);

                production_week++;
            }

            //Atualizar as PO na Database
            for(ProductionOrder po : POrdersList_new){
                int PO_id = dbHandler.createConfirmedProductionOrder(po);
                for(Piece p : po.getPieces() ){
                    dbHandler.updatePiecePO(p, PO_id);
                }
            }

            //Obter a EO associada à CO
            ExpeditionOrder eo = dbHandler.getExpeditionOrderByClientOrder(co);
            //Se o ultimo production week(+1)>EO.week
            if(production_week > eo.getWeek() ){
                //Atualizar o delay da CO
                int co_delay = production_week - eo.getWeek();
                co.setCurrent_estimation(production_week);
                dbHandler.updateClientOrderDelay(co, co_delay);
                //Atualizar a week da EO
                dbHandler.updateExpeditionWeekByClientOrder(co);
            }

        }

        Alerts.showInfo("The system was informed about the delay successfully");

        filterSupplier();

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