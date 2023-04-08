package com.example.minierp.controllers;

import com.example.minierp.Launcher;
import com.example.minierp.database.DatabaseHandler;
import com.example.minierp.model.Factory;
import com.example.minierp.utils.Alerts;
import com.example.minierp.utils.Verifier;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import java.util.prefs.Preferences;

public class MainMenuController implements Initializable {
    Factory factory = Factory.getInstance();
    DatabaseHandler dbHandler;
    Preferences dbPrefs = Preferences.userNodeForPackage(MainMenuController.class);

    @FXML private BorderPane mainPane;
    @FXML private Button internalButton;
    @FXML private Button supplierButton;
    @FXML private Button clientButton;
    @FXML private Button aboutButton;

    @FXML private TextField tf_url;
    @FXML private TextField tf_port;
    @FXML private TextField tf_name;
    @FXML private TextField tf_schema;
    @FXML private TextField tf_username;
    @FXML private TextField tf_password;
    @FXML private Button btn_dbConn;

    @FXML private RadioButton radio_single;
    @FXML private RadioButton radio_interm;
    @FXML private RadioButton radio_final;
    @FXML private TextField tf_wh;
    @FXML private TextField tf_prod;
    @FXML private Button btn_factorySave;



    @FXML
    private void goInternal(){changeScene(internalButton, "II_Layout", "Internal Interface");}
    @FXML
    private void goSupplier(){
        changeScene(supplierButton, "SI_Layout", "Supplier Interface");
    }
    @FXML
    private void goClient(){
        changeScene(clientButton, "CI_Layout", "Client Interface");
    }
    @FXML
    private void goAbout(){
        changeScene(aboutButton, "About", "About");
    }

    @FXML
    private void onDbConnectButtonClicked(){
        String url          = tf_url.getText();
        String databaseName = tf_name.getText();
        String schema       = tf_schema.getText();
        String username     = tf_username.getText();
        String password     = tf_password.getText();
        int port;
        try{
            port = Integer.parseInt(tf_port.getText());
        }
        catch (NumberFormatException e){
            Alerts.showError("Port value is not an integer!");
            return;
        }

        dbHandler = DatabaseHandler.getInstance(url, port, databaseName, schema, username, password);
        if(!dbHandler.setConnection()){
            Alerts.showError("Connection failed!");
            return;
        }

        saveDbPreferences(url, port, databaseName, schema, username, password);
        factory.setDbConnected();
        updateInputsState();
    }

    @FXML
    private void onFactorySaveButtonClicked(){
        if( !Verifier.isInteger(tf_wh) || !Verifier.isInteger(tf_prod) ){
            Alerts.showError("The value introduced is not an integer");
            return;
        }

        int capacity    =   Integer.parseInt(tf_wh.getText());
        int production  =   Integer.parseInt(tf_prod.getText());
        if( capacity < 1 || capacity > 54 ){
            Alerts.showError("Warehouse capacity needs to be between 1 and 54!");
            return;
        }
        if( production < 1 ){
            Alerts.showError("The maximum production needs to be at least 1");
            return;
        }

        // Save values
        if      ( radio_single.isSelected() )   factory.setWorking_mode("single");
        else if ( radio_interm.isSelected() )   factory.setWorking_mode("intermediate");
        else if ( radio_final.isSelected()  )   factory.setWorking_mode("final");
        factory.setWarehouse_capacity(  Integer.parseInt(tf_wh.getText())   );
        factory.setWeekly_production(   Integer.parseInt(tf_prod.getText()) );
        factory.setSetupDone();

        dbHandler.updateFactoryStatus();
        updateInputsState();
    }

    // Manage input textfields and buttons
    private void updateInputsState(){
        if(     factory.isWaitingForDbConn() ){
            setFactoryInputs(true);
            disableNavMenu(true);
        }
        else if( factory.isWaitingForParams() ){
            disableDbInputs();
            dbHandler = DatabaseHandler.getInstance();
            loadFactoryParams();
            setFactoryInputs(false);
            disableNavMenu(true);
        }
        else if( factory.isSetupDone() ){
            disableDbInputs();
            dbHandler = DatabaseHandler.getInstance();
            loadFactoryParams();
            setFactoryInputs(true);
            savedFactoryInputs();
            disableNavMenu(false);
        }
    }
    private void disableNavMenu(boolean b){
        internalButton.setDisable(b);
        supplierButton.setDisable(b);
        clientButton.setDisable(b);
    }
    private void disableDbInputs(){
        tf_url.setDisable(true);
        tf_username.setDisable(true);
        tf_name.setDisable(true);
        tf_port.setDisable(true);
        tf_schema.setDisable(true);
        tf_username.setDisable(true);
        tf_password.setDisable(true);

        btn_dbConn.setText("Connected");
        btn_dbConn.setStyle("-fx-background-color: green");
        btn_dbConn.setDisable(true);
    }
    private void setFactoryInputs(boolean status){
        radio_single.setDisable(status);
        radio_interm.setDisable(status);
        radio_final.setDisable(status);
        tf_wh.setDisable(status);
        tf_prod.setDisable(status);
        btn_factorySave.setDisable(status);
    }
    private void savedFactoryInputs(){
        btn_factorySave.setText("Saved");
        btn_factorySave.setStyle("-fx-background-color: green");
    }

    // Preferences functions
    private void saveDbPreferences(String url, int port, String databaseName, String schema, String username, String password){
        dbPrefs.put(    "url",          url);
        dbPrefs.putInt( "port",         port);
        dbPrefs.put(    "databaseName", databaseName);
        dbPrefs.put(    "schema",       schema);
        dbPrefs.put(    "username",     username);
        dbPrefs.put(    "password",     password);
    }
    private void loadDbPreferences(){
        if(dbPrefs.get("url", "").isEmpty()) return;

        tf_url.setText(      dbPrefs.get("url", "")                         );
        tf_port.setText(     Integer.toString(dbPrefs.getInt("port", 0))    );
        tf_name.setText(     dbPrefs.get("databaseName", "")                );
        tf_schema.setText(   dbPrefs.get("schema", "")                      );
        tf_username.setText( dbPrefs.get("username", "")                    );
        tf_password.setText( dbPrefs.get("password", "")                    );
    }

    // Write previous configuration on Factory fields
    private void loadFactoryParams(){
        dbHandler.retrieveFactoryPreviousConfig();
        if      ( factory.getWorking_mode().equals("single")        )   radio_single.setSelected(true);
        else if ( factory.getWorking_mode().equals("intermediate")  )   radio_interm.setSelected(true);
        else if ( factory.getWorking_mode().equals("final")         )   radio_final.setSelected(true);

        tf_wh.setText(      Integer.toString(factory.getWarehouse_capacity())   );
        tf_prod.setText(    Integer.toString(factory.getWeekly_production())    );

    }
    // Navigation functions
    private void changeScene(Button button, String scene, String title) {
        try {
            Stage stage = (Stage) internalButton.getScene().getWindow();
            Parent root = FXMLLoader.load(Launcher.class.getResource(scene + ".fxml"));
            stage.setScene(new Scene(root));


//            Stage stage = (Stage) internalButton.getScene().getWindow();
//            stage.setIconified(true);
//
//            Stage new_stage = new Stage();
//            Parent root = FXMLLoader.load(Launcher.class.getResource(scene + ".fxml"));
//            new_stage.setScene(new Scene(root));
//
//            new_stage.setTitle(title);
//            new_stage.setResizable(false);
//            new_stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadPane(String page){
        try {
            FXMLLoader contentLoader = new FXMLLoader(Launcher.class.getResource(page+".fxml"));
            AnchorPane content = contentLoader.load();
            // Add the navigation menu to the left side of the BorderPane
            mainPane.setCenter(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Initialize method
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDbPreferences();
        updateInputsState();
    }



}