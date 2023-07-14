package com.example.minierp.controllers;

import com.example.minierp.database.DatabaseHandler;
import com.example.minierp.model.Client;
import com.example.minierp.model.Supplier;
import com.example.minierp.utils.Alerts;
import com.example.minierp.utils.RefreshPageManager;
import com.example.minierp.utils.Verifier;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SI_EditController implements Initializable {
    DatabaseHandler dbHandler = DatabaseHandler.getInstance();

    Stage currentStage;
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

    Thread refreshUI_Thread;

    public void interruptRefreshThread(){
        refreshUI_Thread.interrupt();
    }

    private void startRefreshUI_Thread(){
        refreshUI_Thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {

                if(!RefreshPageManager.getInstance().isRefreshedSI()){
                    Platform.runLater(() -> {
                        fillNameFilter();
                    });
                    RefreshPageManager.getInstance().setSiRefreshed();
                }

                //System.out.println("SI");
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


    @FXML
    void comboNameSelected(){
        clearInputs();
        String selected_name = comboName.getValue();
        if(selected_name==null) return;

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
        currentStage = (Stage) comboName.getScene().getWindow();

        String name = comboName.getValue();
        if( name.isEmpty() ){
            Alerts.showError(currentStage,"Please select a supplier");
            return;
        }

        if( !check_blue.isSelected() && !check_green.isSelected() && !check_metal.isSelected() ){
            Alerts.showError(currentStage,"Please select at least one material");
            return;
        }


        ArrayList<Supplier> aux_supplier = new ArrayList<>();
        if( check_blue.isSelected() ){
            if(!Verifier.isDouble(tf_price_blue)){
                Alerts.showError(currentStage,"Invalid price value");
                return;
            }
            if(!Verifier.isInteger(tf_minqty_blue) || !Verifier.isInteger(tf_time_blue)){
                Alerts.showError(currentStage,"All values need to be integer");
                return;
            }
            price_blue  = Double.parseDouble(tf_price_blue.getText());
            minqty_blue = Integer.parseInt(tf_minqty_blue.getText());
            time_blue   = Integer.parseInt(tf_time_blue.getText());
            if(price_blue<1 || minqty_blue<1 || time_blue<1){
                Alerts.showError(currentStage,"All values need to be higher than 0");
                return;
            }
            aux_supplier.add(new Supplier(null, name, "BlueRawMaterial", price_blue, minqty_blue, time_blue) );
        }
        if( check_green.isSelected() ){
            if(!Verifier.isDouble(tf_price_green)){
                Alerts.showError(currentStage,"Invalid price value");
                return;
            }
            if(!Verifier.isInteger(tf_minqty_green) || !Verifier.isInteger(tf_time_green)){
                Alerts.showError(currentStage,"All values need to be integer");
                return;
            }
            price_green  = Double.parseDouble(tf_price_green.getText());
            minqty_green = Integer.parseInt(tf_minqty_green.getText());
            time_green   = Integer.parseInt(tf_time_green.getText());
            if(price_green<1 || minqty_green<1 || time_green<1){
                Alerts.showError(currentStage,"All values need to be higher than 0");
                return;
            }
            aux_supplier.add(new Supplier(null, name, "GreenRawMaterial", price_green, minqty_green, time_green) );
        }
        if( check_metal.isSelected() ){
            if(!Verifier.isDouble(tf_price_metal)){
                Alerts.showError(currentStage,"Invalid price value");
                return;
            }
            if(!Verifier.isInteger(tf_minqty_metal) || !Verifier.isInteger(tf_time_metal)){
                Alerts.showError(currentStage,"All values need to be integer");
                return;
            }
            price_metal  = Double.parseDouble(tf_price_metal.getText());
            minqty_metal = Integer.parseInt(tf_minqty_metal.getText());
            time_metal   = Integer.parseInt(tf_time_metal.getText());
            if(price_metal<1 || minqty_metal<1 || time_metal<1){
                Alerts.showError(currentStage,"All values need to be higher than 0");
                return;
            }
            aux_supplier.add(new Supplier(null, name, "MetalRawMaterial", price_metal, minqty_metal, time_metal) );
        }

        if(!dbHandler.updateSupplier(aux_supplier)){
            Alerts.showError(currentStage,"An error ocurred, please try again");
        }

        Alerts.showInfo(currentStage,"Supplier updated successfully");

        RefreshPageManager.getInstance().sendRefreshRequest();
    }

    private void fillParameters(Supplier s, TextField tf_price, TextField tf_minqty, TextField tf_time, CheckBox check){
        tf_price.setText(   Double.toString( s.getUnit_price())     );
        tf_minqty.setText(  Integer.toString(s.getMin_quantity())   );
        tf_time.setText(    Integer.toString(s.getMin_quantity())   );
        check.setSelected(true);
    }

    private void fillNameFilter(){
        comboName.getItems().clear();
        String previousSelected = comboName.getValue();

        ArrayList<String> nameList = dbHandler.getSupplierNames();
        if( nameList == null ) return;
        for( String s : nameList ){
            comboName.getItems().add(s);
        }

        boolean stillExists = false;
        if(previousSelected==null) return;
        for( String s : nameList ){
            if(s.equals(previousSelected) ) stillExists = true;
        }
        if(stillExists) comboName.setValue(previousSelected);

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

        startRefreshUI_Thread();
    }
}
