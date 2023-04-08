package com.example.minierp.controllers;

import com.example.minierp.database.DatabaseHandler;
import com.example.minierp.model.*;
import com.example.minierp.utils.Alerts;
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
        //Para cada CO:
            //Contar quantas peças estao "damaged"
            //Contar o numero de peças em falta
            //Correr o algoritmo de escalonamento para o nº de peças em falta
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