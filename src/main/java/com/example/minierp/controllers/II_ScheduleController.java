package com.example.minierp.controllers;

import com.example.minierp.database.DatabaseHandler;
import com.example.minierp.model.*;
import com.example.minierp.utils.Alerts;
import com.example.minierp.utils.Materials;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class II_ScheduleController implements Initializable {
    DatabaseHandler dbHandler = DatabaseHandler.getInstance();
    Factory factory = Factory.getInstance();

    @FXML private Text statusText;
    @FXML private Text weekText;

    @FXML private Button nextButton;
    @FXML private Button mesButton;

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

    @FXML
    void nextButtonClicked() {
        if(!dbHandler.hasClientOrders() ) {
            Alerts.showError("Simulation cannot start without client orders in the system");
            return;
        }
        if(dbHandler.hasPendingClientOrders()){
            Alerts.showError("There are still pending client orders!");
            return;
        }


        //ALGORITMOS A CORRER ANTES DE ACABAR A SEMANA

        int current_week = factory.getCurrent_week();

        //Set SupplierOrders that are arriving
        ArrayList<SupplierOrder> arrivingOrders = dbHandler.getSupplierOrdersByCurrentDelivery(current_week+2);
        for(SupplierOrder so : arrivingOrders){
            dbHandler.updateSupplierOrderStatus(so, "arriving");
        }

        //Set SupplierOrders that are completed (arrive this next week)
        ArrayList<SupplierOrder> completedOrders = dbHandler.getSupplierOrdersByCurrentDelivery(current_week+1);
        for(SupplierOrder so : completedOrders){
            dbHandler.updateSupplierOrderStatus(so, "completed");
        }

        //Read feedback from Production Orders in MES and process it
        processProductionFeedback();


        //FIM DOS ALGORITMOS

        dbHandler.retrieveFactoryStatus();
        factory.incrementWeek();
        factory.setSim_status("ongoing_week");
        dbHandler.updateFactoryStatus();

        //MES simulation
        dbHandler.setInboundRunning(factory.getCurrent_week());
        dbHandler.setProductionRunning(factory.getCurrent_week());
        dbHandler.setExpeditionRunning(factory.getCurrent_week());
        //End mes simulation

        updateUI();

    }

    @FXML
    void mesButtonClicked() {
        dbHandler.retrieveFactoryStatus();

        int week = factory.getCurrent_week();

        //set internal orders as completed
        dbHandler.setInboundCompleted(week);
        dbHandler.setPiecesInbound(week);
        dbHandler.setProductionCompleted(week);
        dbHandler.setPiecesProduction(week);
        dbHandler.setExpeditionCompleted(week);
        dbHandler.setPiecesExpedition(week);


        factory.setSim_status("waiting_week_start");
        dbHandler.updateFactoryStatus();

        updateUI();

    }

    private void updateUI(){
        //Factory status
        dbHandler.retrieveFactoryStatus();
        if(factory.isOngoingWeek()){
            nextButton.setDisable(true);
            mesButton.setDisable(false);
        }
        else if(factory.isWaitingSimStart() || factory.isWaitingWeekStart() ){
            nextButton.setDisable(false);
            mesButton.setDisable(true);
        }

        weekText.setText( Integer.toString(factory.getCurrent_week() ));
        statusText.setText( factory.getSim_status() );




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

    private void processProductionFeedback(){
        //Obter as Client Orders em que PO.week foi esta semana
        ArrayList<ClientOrder> co_list = dbHandler.getClientOrdersByProdWeek(factory.getCurrent_week());
        //Para cada CO:
        for(ClientOrder co : co_list){
            //Contar quantas peças estao "defective"
            ArrayList<Piece> defective_pieces = dbHandler.getDefectivePiecesByCO(co);
            if(defective_pieces.size() == 0) continue;

            dbHandler.freeDefectivePiecesFromClientOrder(co);
            for(Piece p : defective_pieces){
                dbHandler.updatePieceEO(p, -1);
            }


            //Contar o numero de peças em falta
            int quantity_in_need = defective_pieces.size();
            //Correr o algoritmo de escalonamento para o nº de peças em falta
            scheduleProduction(co, quantity_in_need);
        }

    }

    private void scheduleProduction(ClientOrder co, int desired_quantity){
        String type = co.getType();
        String client_name = co.getClient();

        //Create order
        String raw_type = Materials.getRawType(type);
        double final_price = 0;
        ArrayList<Piece> CO_all_pieces = new ArrayList<>();
        int expedition_week = factory.getCurrent_week()+1;


        //*********************************************** Allocate free finished pieces in WH***********************************************//
        //Retrieve available Finished pieces in WH
        ArrayList<Piece> finalpieces_in_wh_allocated = new ArrayList<>();
        ArrayList<Piece> finalieces_in_wh_free = dbHandler.getAvailableFinalPiecesInWH(type);
        //Allocate necessary final pieces in WH and gets their costs
        for(Piece p : finalieces_in_wh_free){
            if(finalpieces_in_wh_allocated.size()== desired_quantity-CO_all_pieces.size() ) break;
            final_price += dbHandler.getPieceCost(p);
            finalpieces_in_wh_allocated.add(p);
        }
        CO_all_pieces.addAll(finalpieces_in_wh_allocated);


        //*********************************************** Allocate free RAW pieces in WH***********************************************//
        //Retrieve available pieces in WH
        ArrayList<Piece> rawpieces_in_wh_allocated = new ArrayList<>();
        ArrayList<Piece> rawpieces_in_wh_free = dbHandler.getAvailablePiecesInWH(raw_type);
        //Allocate necessary pieces in WH and gets their costs
        for(Piece p : rawpieces_in_wh_free){
            if(rawpieces_in_wh_allocated.size()== desired_quantity-CO_all_pieces.size() ) break;

            final_price += dbHandler.getPieceCost(p);
            p.setFinal_type(type);
            rawpieces_in_wh_allocated.add(p);
        }

        //Schedule the production of the pieces already in WH
        ArrayList<ProductionOrder> POrdersList_WH_pieces = new ArrayList<>();
        int pieces_scheduled = 0;
        int production_week = factory.getCurrent_week() + 1;
        while(pieces_scheduled != rawpieces_in_wh_allocated.size() ){
            int week_available_capacity = factory.getWeekly_production() - dbHandler.getProductionCountByWeek(production_week);

            if(week_available_capacity == 0){
                production_week++;
                continue;
            }

            ArrayList<Piece> po_pieces = new ArrayList<>();
            for(int i=0; i<week_available_capacity; i++){
                if(pieces_scheduled == rawpieces_in_wh_allocated.size()) break;
                po_pieces.add( rawpieces_in_wh_allocated.get(pieces_scheduled) );
                pieces_scheduled++;
            }
            ProductionOrder currPO = new ProductionOrder(null, production_week, "waiting_confirmation", raw_type, type, po_pieces);

            POrdersList_WH_pieces.add(currPO);

            production_week++;
        }
        CO_all_pieces.addAll(rawpieces_in_wh_allocated);

        //*** Update the pieces status ***//
        for(ProductionOrder po : POrdersList_WH_pieces){
            int PO_id = dbHandler.createConfirmedProductionOrder(po);
            for(Piece p : po.getPieces() ){
                dbHandler.updatePiecePO(p, PO_id);
            }
        }

        if(production_week > expedition_week) expedition_week = production_week;


        //*********************************************** Allocate free pieces that are arriving ***********************************************//
        //Retrieve available pieces arriving
        ArrayList<Piece> rawpieces_arriving_allocated = new ArrayList<>();
        ArrayList<Piece> rawpieces_arriving_free = dbHandler.getAvailablePiecesArriving(raw_type);

        //Allocate necessary pieces in WH and gets their costs
        for(Piece p : rawpieces_arriving_free){
            if(rawpieces_arriving_allocated.size()== desired_quantity - CO_all_pieces.size() ) break;
            production_week = factory.getCurrent_week() + 2; //+1 for arriving, +1 for inbound
            final_price += dbHandler.getPieceCost(p);
            p.setFinal_type(type);
            rawpieces_arriving_allocated.add(p);
        }

        //Schedule the production of the pieces arriving
        ArrayList<ProductionOrder> POrdersList_Arriving_pieces = new ArrayList<>();
        pieces_scheduled = 0;

        while(pieces_scheduled != rawpieces_arriving_allocated.size() ){
            int week_available_capacity = factory.getWeekly_production() - dbHandler.getProductionCountByWeek(production_week);

            if(week_available_capacity == 0){
                production_week++;
                continue;
            }

            ArrayList<Piece> po_pieces = new ArrayList<>();
            for(int i=0; i<week_available_capacity; i++){
                if(pieces_scheduled == rawpieces_arriving_allocated.size()) break;
                po_pieces.add( rawpieces_arriving_allocated.get(pieces_scheduled) );
                pieces_scheduled++;
            }
            ProductionOrder currPO = new ProductionOrder(null, production_week, "waiting_confirmation", raw_type, type, po_pieces);

            POrdersList_Arriving_pieces.add(currPO);

            production_week++;
        }
        CO_all_pieces.addAll(rawpieces_arriving_allocated);

        //*** Update the pieces status ***//
        for(ProductionOrder po : POrdersList_Arriving_pieces){
            int PO_id = dbHandler.createConfirmedProductionOrder(po);
            for(Piece p : po.getPieces() ){
                dbHandler.updatePiecePO(p, PO_id);
            }
        }

        if(production_week > expedition_week) expedition_week = production_week;



        //*********************************************** CHECK PIECES IN NEED AND "CHECKOUT" ***********************************************//
        int quantity_in_need = desired_quantity - CO_all_pieces.size();
        if(quantity_in_need!=0){
            //*********************************************** FROM SUPPLIER ***********************************************//
            //*** Choose supplier from type ***//
            ArrayList<Supplier> supplierList = dbHandler.getSuppliersByExactQty(raw_type, quantity_in_need, "earlier");
            if(supplierList.size() == 0){
                //If there is no way to order the exact quantity_in_need, we need to order some extra pieces to fill the minimum quantity_in_need
                supplierList = dbHandler.getSuppliersByExcessQty(raw_type, quantity_in_need, "earlier");

                if(supplierList.size() == 0){
                    Alerts.showError("There are still no suppliers for this type of product");
                    return;
                }
            }

            Supplier s = supplierList.get(0);
            int ordered_quantity;
            if( s.getMin_quantity() > quantity_in_need) ordered_quantity = s.getMin_quantity();
            else ordered_quantity = quantity_in_need;

            //*** Create pieces array ***//
            ArrayList<Piece> pieces_desired = new ArrayList<>();
            ArrayList<Piece> pieces_extra = new ArrayList<>();
            ArrayList<Piece> pieces_ordered = new ArrayList<>();
            for(int i=0; i<ordered_quantity; i++){
                if(i<quantity_in_need){
                    pieces_desired.add(new Piece(null,
                            raw_type,
                            "waiting_confirmation",
                            type,
                            null,
                            null,
                            null,
                            false,
                            null) );
                }
                else{
                    pieces_extra.add(new Piece(null,
                            raw_type,
                            "waiting_confirmation",
                            "",
                            null,
                            null,
                            null,
                            true,
                            null) );
                }
            }
            pieces_ordered.addAll(pieces_desired);
            pieces_ordered.addAll(pieces_extra);

            //*** Calculate when materials arrive ***//
            int arriving_week = factory.getCurrent_week() + s.getDelivery_time();

            //*** Create supplier order ***//
            SupplierOrder SO = new SupplierOrder(null, s.getName(), s.getMaterial_type(), ordered_quantity, s.getUnit_price(), arriving_week, 0, "waiting_confirmation");
            int SO_id = dbHandler.createSupplierOrder(s, ordered_quantity, arriving_week);

            //*** Create inbound order ***//
            InboundOrder io = new InboundOrder(null, arriving_week, "confirmed", pieces_ordered, SO);
            int IO_id = dbHandler.createInboundOrder(io, SO_id);

            //*** Calculates when production can start ***//
            production_week = arriving_week + 1;

            //*** Schedule production starting from that week ***//
            ArrayList<ProductionOrder> POrdersList = new ArrayList<>();
            pieces_scheduled = 0;
            while(pieces_scheduled != quantity_in_need ){
                int week_available_capacity = factory.getWeekly_production() - dbHandler.getProductionCountByWeek(production_week);

                if(week_available_capacity == 0){
                    production_week++;
                    continue;
                }


                ArrayList<Piece> po_pieces = new ArrayList<>();
                for(int i=0; i<week_available_capacity; i++){
                    if(pieces_scheduled == quantity_in_need) break;
                    po_pieces.add( pieces_desired.get(pieces_scheduled) );
                    pieces_scheduled++;
                }
                ProductionOrder currPO = new ProductionOrder(null, production_week, "waiting_confirmation", raw_type, type, po_pieces);

                POrdersList.add(currPO);

                production_week++;
            }

            //Update final_price with the new order
            final_price += s.getUnit_price() * quantity_in_need; //adicionar custos aqui eventualmente !!!!

            //*** Calculate expedition and price ***//
            if(production_week > expedition_week) expedition_week = production_week;

            //*** Update client order***//
            dbHandler.updateClientOrderDelay(co, expedition_week - co.getCurrent_estimation() );
            co.setCurrent_estimation(expedition_week);

            //*** Update expedition order ***//
            dbHandler.updateExpeditionWeekByClientOrder(co);
            ExpeditionOrder eo = dbHandler.getExpeditionOrderByClientOrder(co);

            //*** Create piece data in DataBase ***//
            for(ProductionOrder po : POrdersList){
                int PO_id = dbHandler.createConfirmedProductionOrder(po);
                for(Piece p : po.getPieces() ){
                    dbHandler.createPiece(p, SO_id, co.getId(), IO_id, PO_id, eo.getId());
                }
            }
            for(Piece p : pieces_extra){
                dbHandler.createPiece(p, SO_id, -1, IO_id, -1, -1);
            }


            //Update CO and EO from the final pieces in wh
            for(Piece p : finalpieces_in_wh_allocated ){
                dbHandler.updatePieceCO(p, co.getId());
                dbHandler.updatePieceEO(p, eo.getId());
            }
            //Update CO and EO from the pieces in wh
            for(ProductionOrder po : POrdersList_WH_pieces){
                for(Piece p : po.getPieces() ){
                    dbHandler.updatePieceCO(p, co.getId());
                    dbHandler.updatePieceEO(p, eo.getId());
                }
            }
            //Update CO and EO from arriving pieces
            for(ProductionOrder po : POrdersList_Arriving_pieces){
                for(Piece p : po.getPieces() ){
                    dbHandler.updatePieceCO(p, co.getId());
                    dbHandler.updatePieceEO(p, eo.getId());
                }
            }

            System.out.println("An order to supplier will be made");
        }
        else{
            //If there is no need for supplier ordering:

            //*** Update client order***//
            dbHandler.updateClientOrderDelay(co, expedition_week - co.getCurrent_estimation() );
            co.setCurrent_estimation(expedition_week);

            //*** Update expedition order ***//
            dbHandler.updateExpeditionWeekByClientOrder(co);
            ExpeditionOrder eo = dbHandler.getExpeditionOrderByClientOrder(co);


            //Update CO and EO from the final pieces in wh
            for(Piece p : finalpieces_in_wh_allocated ){
                dbHandler.updatePieceCO(p, co.getId());
                dbHandler.updatePieceEO(p, eo.getId());
            }
            //Update CO and EO from the pieces in wh
            for(ProductionOrder po : POrdersList_WH_pieces){
                for(Piece p : po.getPieces() ){
                    dbHandler.updatePieceCO(p, co.getId());
                    dbHandler.updatePieceEO(p, eo.getId());
                }
            }
            //Update CO and EO from arriving pieces
            for(ProductionOrder po : POrdersList_Arriving_pieces){
                for(Piece p : po.getPieces() ){
                    dbHandler.updatePieceCO(p, co.getId());
                    dbHandler.updatePieceEO(p, eo.getId());
                }
            }

        }

        return;
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