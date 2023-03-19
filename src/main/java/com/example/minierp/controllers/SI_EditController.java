package com.example.minierp.controllers;

import com.example.minierp.database.DatabaseHandler;
import com.example.minierp.model.Client;
import com.example.minierp.model.Supplier;
import com.example.minierp.utils.Alerts;
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
        if( !check_blue.isSelected() && !check_green.isSelected() && !check_metal.isSelected() ){
            Alerts.showError("Please select at least one material");
            return;
        }


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

    // Initialize method
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fillNameFilter();

    }
}
