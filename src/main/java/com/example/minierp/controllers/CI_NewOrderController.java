package com.example.minierp.controllers;


import com.example.minierp.database.DatabaseHandler;
import com.example.minierp.model.Client;
import com.example.minierp.utils.Alerts;
import com.example.minierp.utils.Verifier;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;


import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CI_NewOrderController implements Initializable {
    DatabaseHandler dbHandler = DatabaseHandler.getInstance();

    @FXML private ComboBox<String> comboClient;
    @FXML private ComboBox<String> comboType;
    @FXML private ComboBox<String> comboBase;
    @FXML private ComboBox<String> comboLid;
    @FXML private TextField tf_qty;

    @FXML void createOrder(){
        String client_name = comboClient.getValue();
        if( client_name == null){
            Alerts.showError("Please select a client");
            return;
        }

        if(!Verifier.isInteger(tf_qty) ){
            Alerts.showError("The value introduced is not an integer");
            return;
        }
    }

    @FXML void onTypeSelected(){
        updateComboVisibility();
    }

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
            disableAssembleCombos(true);
            return;
        }
        else if( !type.equals("Assembled") ){
            disableAssembleCombos(true);
            return;
        }

        disableAssembleCombos(false);


    }

    private void disableAssembleCombos(boolean b){
        comboLid.setDisable(b);
        comboBase.setDisable(b);
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