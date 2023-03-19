package com.example.minierp.controllers;


import com.example.minierp.database.DatabaseHandler;
import com.example.minierp.model.*;
import com.example.minierp.utils.Alerts;
import com.example.minierp.utils.Materials;
import com.example.minierp.utils.Verifier;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;


import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CI_NewOrderController implements Initializable {
    Factory factory = Factory.getInstance();
    DatabaseHandler dbHandler = DatabaseHandler.getInstance();

    @FXML private ComboBox<String> comboClient;
    @FXML private ComboBox<String> comboType;
    @FXML private ComboBox<String> comboBase;
    @FXML private ComboBox<String> comboLid;
    @FXML private TextField tf_qty;
    @FXML private Text textLid;
    @FXML private Text textBase;

    @FXML void onTypeSelected(){
        updateComboVisibility();
    }

    @FXML void createOrder(){
        String type = comboType.getValue();
        String client_name = comboClient.getValue();
        if( client_name == null){
            Alerts.showError("Please select a client");
            return;
        }

        if(!Verifier.isInteger(tf_qty) ){
            Alerts.showError("The value introduced is not an integer");
            return;
        }

        int quantity = Integer.parseInt(tf_qty.getText());
        if(quantity<1){
            Alerts.showError("THe quantity needs to be higher than 0");
            return;
        }

        // TO DO: create order

        //verify if has free pieces from that type in wh
        //verify if has free pieces from that type arriving


        // DONE
        //***** FROM SUPPLIER *****//

        //*** Choose supplier from type ***//
        String raw_type = Materials.getRawType(type);
        ArrayList<Supplier> supplierList = dbHandler.getSuppliersByTypeAndQty(raw_type, quantity);
        if( supplierList.size() == 0){
            Alerts.showError("There are still no suppliers for this type of product");
            return;
        }
        Supplier s = supplierList.get(0);

        //*** Create pieces array ***//
        ArrayList<Piece> pieces = new ArrayList<>();
        for(int i=0; i<quantity; i++){
            pieces.add(new Piece(null, raw_type, "ordered", type, 0, 0, 0, false, 0) );
        }

        //*** Calculate when materials arrive ***//
        int arriving_week = factory.getCurrent_week() + s.getDelivery_time();

        //*** Create supplier order ***//
        int SO_id = dbHandler.createSupplierOrder(s, quantity, arriving_week);

        //*** Create inbound order ***//
        InboundOrder io = new InboundOrder(null, arriving_week, pieces, null);
        int IO_id = dbHandler.createInboundOrder(io, SO_id);

        //*** Calculates when production can start ***//
        int production_week = arriving_week + 1;

        //*** Schedule production starting from that week ***//
        ArrayList<ProductionOrder> POrdersList = new ArrayList<>();
        int pieces_scheduled = 0;
        while(pieces_scheduled != quantity ){
            int week_available_capacity = factory.getWeekly_production() - dbHandler.getProductionCountByWeek(production_week);

            if(week_available_capacity == 0){
                production_week++;
                continue;
            }

            ProductionOrder currPO = new ProductionOrder(null, production_week);
            for(int i=0; i<week_available_capacity; i++){
                if(pieces_scheduled == quantity) break;
                currPO.addPiece( pieces.get(pieces_scheduled) );
                pieces_scheduled++;
            }
            POrdersList.add(currPO);

            production_week++;
        }

        //*** Calculate expedition and price ***//
        int expedition_week = production_week+1;
        double final_price = s.getUnit_price() * quantity; //adicionar custos aqui eventualmente !!!!

        //*** Create client order with pending_internal status ***//
        ClientOrder CO = new ClientOrder(client_name, type, quantity, final_price, expedition_week, expedition_week, "pending_internal");
        int CO_id = dbHandler.createClientOrder(CO);

        //*** Create expedition order ***//
        ExpeditionOrder eo = new ExpeditionOrder(null, expedition_week, pieces, null);
        int EO_id = dbHandler.createExpeditionOrder(eo, SO_id);




        //*** Create piece data in DataBase ***//
        for(ProductionOrder po : POrdersList){
            int PO_id = dbHandler.createProductionOrder(po);
            for(Piece p : po.getPieces() ){
                dbHandler.createPiece(p, SO_id, CO_id, IO_id, PO_id, EO_id);
            }
        }



        //just print
        for(ProductionOrder po : POrdersList){
            System.out.println("Semana: " + po.getWeek() + "   Produz: " + po.getPieces().size() );
        }

    }


    // Combo box methods
    private void fillTypeCombos(){
        //Type combo
        comboType.getItems().add("BlueProductBase");
        comboType.getItems().add("GreenProductBase");
        comboType.getItems().add("MetalProductBase");
        comboType.getItems().add("BlueProductLid");
        comboType.getItems().add("GreenProductLid");
        comboType.getItems().add("MetalProductLid");
        comboType.getItems().add("Assembled");
        //Base combo
        comboBase.getItems().add("BlueProductBase");
        comboBase.getItems().add("GreenProductBase");
        comboBase.getItems().add("MetalProductBase");
        //Lid combo
        comboLid.getItems().add("BlueProductLid");
        comboLid.getItems().add("GreenProductLid");
        comboLid.getItems().add("MetalProductLid");
    }
    private void updateComboVisibility(){
        String type = comboType.getValue();
        if(  type == null){
            showAssembleCombos(false);
            return;
        }
        else if( !type.equals("Assembled") ){
            showAssembleCombos(false);
            return;
        }

        showAssembleCombos(true);


    }
    private void showAssembleCombos(boolean b){
        textLid.setVisible(b);
        textBase.setVisible(b);

        comboLid.getSelectionModel().clearSelection();
        comboBase.getSelectionModel().clearSelection();
        comboLid.setVisible(b);
        comboBase.setVisible(b);
    }

    // Initialize method
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fillTypeCombos();
        updateComboVisibility();

        ArrayList<Client> clientList = dbHandler.getClients();
        if( clientList == null ) return;
        for( Client c : clientList ){
            comboClient.getItems().add(c.getName());
        }


    }

}