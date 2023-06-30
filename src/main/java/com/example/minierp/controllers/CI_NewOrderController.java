package com.example.minierp.controllers;


import com.example.minierp.database.DatabaseHandler;
import com.example.minierp.model.*;
import com.example.minierp.utils.Alerts;
import com.example.minierp.utils.Materials;
import com.example.minierp.utils.RefreshPageManager;
import com.example.minierp.utils.Verifier;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CI_NewOrderController implements Initializable {
    Factory factory = Factory.getInstance();
    DatabaseHandler dbHandler = DatabaseHandler.getInstance();
    Thread refreshUI_Thread;

    Stage currentStage;
    @FXML private ComboBox<String> comboClient;
    @FXML private ComboBox<String> comboType;
    @FXML private TextField tf_qty;


    @FXML private RadioButton earlierRadio;
    @FXML private RadioButton cheaperRadio;


    @FXML void createOrder(){
        currentStage = (Stage) comboClient.getScene().getWindow();

        String type = comboType.getValue();
        String client_name = comboClient.getValue();
        if( client_name == null){
            Alerts.showError(currentStage,"Please select a client");
            return;
        }

        if(!Verifier.isInteger(tf_qty) ){
            Alerts.showError(currentStage,"The value introduced is not an integer");
            return;
        }

        int desired_quantity = Integer.parseInt(tf_qty.getText());
        if(desired_quantity<1){
            Alerts.showError(currentStage,"The desired_quantity needs to be higher than 0");
            return;
        }
        if(desired_quantity> factory.getWarehouse_capacity()){
            Alerts.showError(currentStage,"The factory can't accept an order that has a quantity larger than the warehouse capacity");
            return;
        }

        if(!earlierRadio.isSelected() && !cheaperRadio.isSelected() ){
            Alerts.showError(currentStage,"Please select preference");
            return;
        }

        String preference = "";
        if(earlierRadio.isSelected()) preference = "earlier";
        if(cheaperRadio.isSelected()) preference = "cheaper";

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
            int week_available_storage = factory.getWarehouse_capacity() - dbHandler.getFinalWarehouseOccupation(production_week);

            if(week_available_capacity == 0 || week_available_storage == 0){
                production_week++;
                continue;
            }

            ArrayList<Piece> po_pieces = new ArrayList<>();
            for(int i=0; i<week_available_capacity && i<week_available_storage; i++){
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
            int PO_id = dbHandler.createProductionOrder(po);
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
            int week_available_storage = factory.getWarehouse_capacity() - dbHandler.getFinalWarehouseOccupation(production_week);

            if(week_available_capacity == 0 || week_available_storage == 0){
                production_week++;
                continue;
            }

            ArrayList<Piece> po_pieces = new ArrayList<>();
            for(int i=0; i<week_available_capacity && i<week_available_storage; i++){
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
            int PO_id = dbHandler.createProductionOrder(po);
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
            ArrayList<Supplier> supplierList = dbHandler.getSuppliersByExactQty(raw_type, quantity_in_need, preference);
            if(supplierList.size() == 0){
                //If there is no way to order the exact quantity_in_need, we need to order some extra pieces to fill the minimum quantity_in_need
                supplierList = dbHandler.getSuppliersByExcessQty(raw_type, quantity_in_need, preference);

                if(supplierList.size() == 0){
                    Alerts.showError(currentStage,"There are still no suppliers for this type of product");
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

            //*** Calculate when materials can arrive ***//
            int arriving_week = factory.getCurrent_week() + s.getDelivery_time();
            while( (factory.getWarehouse_capacity() - dbHandler.getMaterialWarehouseOccupation(arriving_week)) < ordered_quantity){
                arriving_week++;
            }


            //*** Create supplier order ***//
            SupplierOrder SO = new SupplierOrder(null, s.getName(), s.getMaterial_type(), ordered_quantity, s.getUnit_price(), arriving_week, 0, "waiting_confirmation");
            int SO_id = dbHandler.createSupplierOrder(s, ordered_quantity, arriving_week);

            //*** Create inbound order ***//
            InboundOrder io = new InboundOrder(null, arriving_week, "waiting_confirmation", pieces_ordered, SO);
            int IO_id = dbHandler.createInboundOrder(io, SO_id);

            //*** Calculates when production can start ***//
            production_week = arriving_week + 1;

            //*** Schedule production starting from that week ***//
            ArrayList<ProductionOrder> POrdersList = new ArrayList<>();
            pieces_scheduled = 0;
            while(pieces_scheduled != quantity_in_need ){
                int week_available_capacity = factory.getWeekly_production() - dbHandler.getProductionCountByWeek(production_week);
                int week_available_storage = factory.getWarehouse_capacity() - dbHandler.getFinalWarehouseOccupation(production_week);

                if(week_available_capacity == 0 || week_available_storage == 0){
                    production_week++;
                    continue;
                }


                ArrayList<Piece> po_pieces = new ArrayList<>();
                for(int i=0; i<week_available_capacity && i<week_available_storage; i++){
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

            //*** Create client order with pending_internal status ***//
            ClientOrder CO = new ClientOrder(null, client_name, type, desired_quantity, final_price, expedition_week, expedition_week, "pending_internal");
            int CO_id = dbHandler.createClientOrder(CO);

            //*** Create expedition order ***//
            ExpeditionOrder eo = new ExpeditionOrder(null, expedition_week, "waiting_confirmation", pieces_desired, CO);
            int EO_id = dbHandler.createExpeditionOrder(eo, CO_id);


            //*** Create piece data in DataBase ***//
            for(ProductionOrder po : POrdersList){
                int PO_id = dbHandler.createProductionOrder(po);
                for(Piece p : po.getPieces() ){
                    dbHandler.createPiece(p, SO_id, CO_id, IO_id, PO_id, EO_id);
                }
            }
            for(Piece p : pieces_extra){
                dbHandler.createPiece(p, SO_id, -1, IO_id, -1, -1);
            }


            //Update CO and EO from the final pieces in wh
            for(Piece p : finalpieces_in_wh_allocated ){
                dbHandler.updatePieceCO(p, CO_id);
                dbHandler.updatePieceEO(p, EO_id);
            }
            //Update CO and EO from the pieces in wh
            for(ProductionOrder po : POrdersList_WH_pieces){
                for(Piece p : po.getPieces() ){
                    dbHandler.updatePieceCO(p, CO_id);
                    dbHandler.updatePieceEO(p, EO_id);
                }
            }
            //Update CO and EO from arriving pieces
            for(ProductionOrder po : POrdersList_Arriving_pieces){
                for(Piece p : po.getPieces() ){
                    dbHandler.updatePieceCO(p, CO_id);
                    dbHandler.updatePieceEO(p, EO_id);
                }
            }

            System.out.println("An order to supplier will be made");
        }
        else{
            //If there is no need for supplier ordering:

            //*** Create client order with pending_internal status ***//
            ClientOrder CO = new ClientOrder(null, client_name, type, desired_quantity, final_price, expedition_week, expedition_week, "pending_internal");
            int CO_id = dbHandler.createClientOrder(CO);

            //*** Create expedition order ***//
            ExpeditionOrder eo = new ExpeditionOrder(null, expedition_week, "waiting_confirmation", CO_all_pieces, CO);
            int EO_id = dbHandler.createExpeditionOrder(eo, CO_id);


            //Update CO and EO from the final pieces in wh
            for(Piece p : finalpieces_in_wh_allocated ){
                dbHandler.updatePieceCO(p, CO_id);
                dbHandler.updatePieceEO(p, EO_id);
            }
            //Update CO and EO from the pieces in wh
            for(ProductionOrder po : POrdersList_WH_pieces){
                for(Piece p : po.getPieces() ){
                    dbHandler.updatePieceCO(p, CO_id);
                    dbHandler.updatePieceEO(p, EO_id);
                }
            }
            //Update CO and EO from arriving pieces
            for(ProductionOrder po : POrdersList_Arriving_pieces){
                for(Piece p : po.getPieces() ){
                    dbHandler.updatePieceCO(p, CO_id);
                    dbHandler.updatePieceEO(p, EO_id);
                }
            }

        }

        RefreshPageManager.getInstance().sendRefreshRequest();

        Alerts.showInfo(currentStage,"Order was sent successfully. Please wait for our internal approval.");
    }

    public void interruptRefreshThread(){
        refreshUI_Thread.interrupt();
    }
    private void startRefreshUI_Thread(){
        refreshUI_Thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if(!RefreshPageManager.getInstance().isRefreshedCI()){
                    Platform.runLater(() -> {
                        fillTypeCombos();
                        fillClientCombo();
                        tf_qty.clear();
                        cheaperRadio.setSelected(false);
                        earlierRadio.setSelected(false);
                    });
                    RefreshPageManager.getInstance().setCiRefreshed();
                }

                System.out.println("CI");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });

        refreshUI_Thread.setDaemon(true);
        refreshUI_Thread.start();
    }



    // Combo box methods
    private void fillTypeCombos(){
        comboType.getItems().clear();
        //Type combo
        comboType.getItems().add("BlueProductBase");
        comboType.getItems().add("GreenProductBase");
        comboType.getItems().add("MetalProductBase");
        comboType.getItems().add("BlueProductLid");
        comboType.getItems().add("GreenProductLid");
        comboType.getItems().add("MetalProductLid");

    }
    private void fillClientCombo(){
        ArrayList<Client> clientList = dbHandler.getClients();
        comboClient.getItems().clear();
        if( clientList == null ) return;
        for( Client c : clientList ){
            comboClient.getItems().add(c.getName());
        }
    }


    // Initialize method
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fillTypeCombos();
        fillClientCombo();

        startRefreshUI_Thread();
    }

}