package com.example.minierp.controllers;

import com.example.minierp.database.DatabaseHandler;
import com.example.minierp.model.Client;
import com.example.minierp.model.Supplier;
import com.example.minierp.utils.Alerts;
import com.example.minierp.utils.Verifier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class II_SuppliersController implements Initializable {
    DatabaseHandler dbHandler = DatabaseHandler.getInstance();

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

    int price_blue  = 0, minqty_blue  = 0, time_blue  = 0;
    int price_green = 0, minqty_green = 0, time_green = 0;
    int price_metal = 0, minqty_metal = 0, time_metal = 0;


    @FXML void addSupplier(){
        String name = tf_name.getText();
        if( name.isEmpty() ){
            Alerts.showError("Supplier's name cannot be empty");
            return;
        }
        if( dbHandler.nameExists("supplier", name) ){
            Alerts.showError("This name is already registered");
            return;
        }
        if( !check_blue.isSelected() && !check_green.isSelected() && !check_metal.isSelected() ){
            Alerts.showError("Please select at least one material");
            return;
        }

        ArrayList<Supplier> aux_supplier = new ArrayList<>();
        if( check_blue.isSelected() ){
            if(!Verifier.isInteger(tf_price_blue) || !Verifier.isInteger(tf_minqty_blue) || !Verifier.isInteger(tf_time_blue)){
                Alerts.showError("All values need to be integer");
                return;
            }
            price_blue  = Integer.parseInt(tf_price_blue.getText());
            minqty_blue = Integer.parseInt(tf_minqty_blue.getText());
            time_blue   = Integer.parseInt(tf_time_blue.getText());
            if(price_blue<1 || minqty_blue<1 || time_blue<1){
                Alerts.showError("All values need to be higher than 0");
                return;
            }
            aux_supplier.add(new Supplier(null, name, "BlueRawMaterial", price_blue, minqty_blue, time_blue) );
        }
        if( check_green.isSelected() ){
            if(!Verifier.isInteger(tf_price_green) || !Verifier.isInteger(tf_minqty_green) || !Verifier.isInteger(tf_time_green)){
                Alerts.showError("All values need to be integer");
                return;
            }
            price_green  = Integer.parseInt(tf_price_green.getText());
            minqty_green = Integer.parseInt(tf_minqty_green.getText());
            time_green   = Integer.parseInt(tf_time_green.getText());
            if(price_green<1 || minqty_green<1 || time_green<1){
                Alerts.showError("All values need to be higher than 0");
                return;
            }
            aux_supplier.add(new Supplier(null, name, "GreenRawMaterial", price_green, minqty_green, time_green) );
        }
        if( check_metal.isSelected() ){
            if(!Verifier.isInteger(tf_price_metal) || !Verifier.isInteger(tf_minqty_metal) || !Verifier.isInteger(tf_time_metal)){
                Alerts.showError("All values need to be integer");
                return;
            }
            price_metal  = Integer.parseInt(tf_price_metal.getText());
            minqty_metal = Integer.parseInt(tf_minqty_metal.getText());
            time_metal   = Integer.parseInt(tf_time_metal.getText());
            if(price_metal<1 || minqty_metal<1 || time_metal<1){
                Alerts.showError("All values need to be higher than 0");
                return;
            }
            aux_supplier.add(new Supplier(null, name, "MetalRawMaterial", price_metal, minqty_metal, time_metal) );
        }

        if(!dbHandler.createSupplier(aux_supplier)){
            Alerts.showError("An error ocurred, please try again");
        }

        Alerts.showInfo("Supplier added successfully");

        updateUI();
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

    }

}