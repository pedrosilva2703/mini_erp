package com.example.minierp.controllers;

import com.example.minierp.database.DatabaseHandler;
import com.example.minierp.model.Client;
import com.example.minierp.model.Factory;
import com.example.minierp.model.Supplier;
import com.example.minierp.utils.Alerts;
import com.example.minierp.utils.RefreshPageManager;
import com.example.minierp.utils.Verifier;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class II_SuppliersController implements Initializable {
    DatabaseHandler dbHandler = DatabaseHandler.getInstance();

    Stage currentStage;
    @FXML private TableView<Supplier> tv_Suppliers;
    @FXML private TableColumn<Supplier, String> tc_Name;
    @FXML private TableColumn<Supplier, String> tc_MaterialType;
    @FXML private TableColumn<Supplier, Integer> tc_UPrice;
    @FXML private TableColumn<Supplier, Integer> tc_MinQty;
    @FXML private TableColumn<Supplier, Integer> tc_DeliveryTime;

    @FXML private TextField tf_name;

    @FXML private TextField tf_price_blue;
    @FXML private TextField tf_minqty_blue;
    @FXML private TextField tf_time_blue;
    @FXML private CheckBox check_blue;

    @FXML private TextField tf_price_green;
    @FXML private TextField tf_minqty_green;
    @FXML private TextField tf_time_green;
    @FXML private CheckBox check_green;

    @FXML private TextField tf_price_metal;
    @FXML private TextField tf_minqty_metal;
    @FXML private TextField tf_time_metal;
    @FXML private CheckBox check_metal;

    double price_blue  = 0; int minqty_blue  = 0, time_blue  = 0;
    double price_green = 0; int minqty_green = 0, time_green = 0;
    double price_metal = 0; int minqty_metal = 0, time_metal = 0;

    Thread refreshUI_Thread;

    public void interruptRefreshThread(){
        refreshUI_Thread.interrupt();
    }

    private void startRefreshUI_Thread(){
        refreshUI_Thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {

                if(!RefreshPageManager.getInstance().isRefreshedII()){
                    Platform.runLater(() -> {
                        updateUI();
                    });
                    RefreshPageManager.getInstance().setIiRefreshed();
                }

                System.out.println("II");
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


    @FXML void addSupplier(){
        currentStage = (Stage) tv_Suppliers.getScene().getWindow();

        String name = tf_name.getText();
        if( name.isEmpty() ){
            Alerts.showError(currentStage,"Supplier's name cannot be empty");
            return;
        }
        if( dbHandler.nameExists("supplier", name) ){
            Alerts.showError(currentStage,"This name is already registered");
            return;
        }
        if( !check_blue.isSelected() && !check_green.isSelected() && !check_metal.isSelected() ){
            Alerts.showError(currentStage,"Please select at least one material");
            return;
        }

        ArrayList<Supplier> aux_supplier = new ArrayList<>();
        if( check_blue.isSelected() ){
            boolean result = createSupplier(aux_supplier, name, tf_price_blue, tf_minqty_blue, tf_time_blue, "BlueRawMaterial");
            if(!result) return;
        }
        if( check_green.isSelected() ){
            boolean result = createSupplier(aux_supplier, name, tf_price_green, tf_minqty_green, tf_time_green, "GreenRawMaterial");
            if(!result) return;
        }
        if( check_metal.isSelected() ){
            boolean result = createSupplier(aux_supplier, name, tf_price_metal, tf_minqty_metal, tf_time_metal, "MetalRawMaterial");
            if(!result) return;
        }

        if(!dbHandler.createSupplier(aux_supplier)){
            Alerts.showError(currentStage,"An error ocurred, please try again");
        }

        Alerts.showInfo(currentStage,"Supplier added successfully");

        updateUI();
        RefreshPageManager.getInstance().sendRefreshRequest();
    }

    private boolean createSupplier(ArrayList<Supplier> aux_supplier, String name, TextField tf_price, TextField tf_minqty, TextField tf_time, String type){
        currentStage = (Stage) tv_Suppliers.getScene().getWindow();

        if(!Verifier.isDouble(tf_price)){
            Alerts.showError(currentStage,"Invalid price value");
            return false;
        }
        if(!Verifier.isInteger(tf_minqty) || !Verifier.isInteger(tf_time)){
            Alerts.showError(currentStage,"All values need to be integer");
            return false;
        }
        double price  = Double.parseDouble(tf_price.getText());
        int minqty = Integer.parseInt(tf_minqty.getText());
        int time   = Integer.parseInt(tf_time.getText());
        if(price<1 || minqty<1 || time<1){
            Alerts.showError(currentStage,"All values need to be higher than 0");
            return false;
        }
        if(minqty > Factory.getInstance().getWarehouse_capacity()){
            Alerts.showError(currentStage,"The factory can't accept a supplier that has a minimum quantity larger than the warehouse capacity");
            return false;
        }
        aux_supplier.add(new Supplier(null, name, type, price, minqty, time) );
        return true;
    }

    private void clearInputs(){
        tf_name.clear();

        check_blue.setSelected(false);
        tf_price_blue.clear();
        tf_minqty_blue.clear();
        tf_time_blue.clear();

        check_green.setSelected(false);
        tf_price_green.clear();
        tf_minqty_green.clear();
        tf_time_green.clear();

        check_metal.setSelected(false);
        tf_price_metal.clear();
        tf_minqty_metal.clear();
        tf_time_metal.clear();
    }
    private void updateUI(){
        clearInputs();

        tv_Suppliers.getItems().clear();
        ArrayList<Supplier> supplierList = dbHandler.getSuppliers();

        if( supplierList != null ){
            tv_Suppliers.getItems().addAll( supplierList );
            tv_Suppliers.setPrefHeight( (tv_Suppliers.getItems().size()+1.15) * tv_Suppliers.getFixedCellSize() );
        }
    }

    // Initialize method
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        tc_Name.setCellValueFactory(new PropertyValueFactory<Supplier, String>("name") );
        tc_MaterialType.setCellValueFactory(new PropertyValueFactory<Supplier, String>("material_type") );
        tc_UPrice.setCellValueFactory(new PropertyValueFactory<Supplier, Integer>("unit_price") );
        tc_MinQty.setCellValueFactory(new PropertyValueFactory<Supplier, Integer>("min_quantity") );
        tc_DeliveryTime.setCellValueFactory(new PropertyValueFactory<Supplier, Integer>("delivery_time") );

        updateUI();
        startRefreshUI_Thread();

    }

}