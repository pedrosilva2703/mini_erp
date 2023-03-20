package com.example.minierp.controllers;

import com.example.minierp.database.DatabaseHandler;
import com.example.minierp.model.Client;
import com.example.minierp.model.Supplier;
import com.example.minierp.utils.Alerts;
import com.example.minierp.utils.Verifier;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SI_EditController implements Initializable {
    DatabaseHandler dbHandler = DatabaseHandler.getInstance();

    @FXML private ComboBox<String> comboName;
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

    @FXML
    void comboNameSelected(){
        clearInputs();
        String selected_name = comboName.getValue();

        ArrayList<Supplier> supplierList = dbHandler.getSuppliersByName(selected_name);
        for(Supplier s : supplierList){
            if(s.getMaterial_type().equals("BlueRawMaterial") ){
                fillParameters(s, tf_price_blue, tf_minqty_blue, tf_time_blue, check_blue);
            }
            else if(s.getMaterial_type().equals("GreenRawMaterial") ){
                fillParameters(s, tf_price_green, tf_minqty_green, tf_time_green, check_green);
            }
            else if(s.getMaterial_type().equals("MetalRawMaterial") ){
                fillParameters(s, tf_price_metal, tf_minqty_metal, tf_time_metal, check_metal);
            }
        }
    }

    @FXML
    void updateSupplier(){
        String name = comboName.getValue();
        if( name.isEmpty() ){
            Alerts.showError("Please select a supplier");
            return;
        }

        if( !check_blue.isSelected() && !check_green.isSelected() && !check_metal.isSelected() ){
            Alerts.showError("Please select at least one material");
            return;
        }


        ArrayList<Supplier> aux_supplier = new ArrayList<>();
        if( check_blue.isSelected() ){
            if(!Verifier.isDouble(tf_price_blue)){
                Alerts.showError("Invalid price value");
                return;
            }
            if(!Verifier.isInteger(tf_minqty_blue) || !Verifier.isInteger(tf_time_blue)){
                Alerts.showError("All values need to be integer");
                return;
            }
            price_blue  = Double.parseDouble(tf_price_blue.getText());
            minqty_blue = Integer.parseInt(tf_minqty_blue.getText());
            time_blue   = Integer.parseInt(tf_time_blue.getText());
            if(price_blue<1 || minqty_blue<1 || time_blue<1){
                Alerts.showError("All values need to be higher than 0");
                return;
            }
            aux_supplier.add(new Supplier(null, name, "BlueRawMaterial", price_blue, minqty_blue, time_blue) );
        }
        if( check_green.isSelected() ){
            if(!Verifier.isDouble(tf_price_green)){
                Alerts.showError("Invalid price value");
                return;
            }
            if(!Verifier.isInteger(tf_minqty_green) || !Verifier.isInteger(tf_time_green)){
                Alerts.showError("All values need to be integer");
                return;
            }
            price_green  = Double.parseDouble(tf_price_green.getText());
            minqty_green = Integer.parseInt(tf_minqty_green.getText());
            time_green   = Integer.parseInt(tf_time_green.getText());
            if(price_green<1 || minqty_green<1 || time_green<1){
                Alerts.showError("All values need to be higher than 0");
                return;
            }
            aux_supplier.add(new Supplier(null, name, "GreenRawMaterial", price_green, minqty_green, time_green) );
        }
        if( check_metal.isSelected() ){
            if(!Verifier.isDouble(tf_price_metal)){
                Alerts.showError("Invalid price value");
                return;
            }
            if(!Verifier.isInteger(tf_minqty_metal) || !Verifier.isInteger(tf_time_metal)){
                Alerts.showError("All values need to be integer");
                return;
            }
            price_metal  = Double.parseDouble(tf_price_metal.getText());
            minqty_metal = Integer.parseInt(tf_minqty_metal.getText());
            time_metal   = Integer.parseInt(tf_time_metal.getText());
            if(price_metal<1 || minqty_metal<1 || time_metal<1){
                Alerts.showError("All values need to be higher than 0");
                return;
            }
            aux_supplier.add(new Supplier(null, name, "MetalRawMaterial", price_metal, minqty_metal, time_metal) );
        }

        if(!dbHandler.updateSupplier(aux_supplier)){
            Alerts.showError("An error ocurred, please try again");
        }

        Alerts.showInfo("Supplier updated successfully");
    }

    private void fillParameters(Supplier s, TextField tf_price, TextField tf_minqty, TextField tf_time, CheckBox check){
        tf_price.setText(   Double.toString( s.getUnit_price())     );
        tf_minqty.setText(  Integer.toString(s.getMin_quantity())   );
        tf_time.setText(    Integer.toString(s.getMin_quantity())   );
        check.setSelected(true);
    }

    private void fillNameFilter(){
        ArrayList<String> nameList = dbHandler.getSupplierNames();
        if( nameList == null ) return;
        for( String s : nameList ){
            comboName.getItems().add(s);
        }
    }

    private void clearInputs(){
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

    // Initialize method
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fillNameFilter();

    }
}
