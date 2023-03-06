module com.example.minierp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires postgresql;
    requires java.prefs;


    opens com.example.minierp to javafx.fxml;
    exports com.example.minierp;
    exports com.example.minierp.controllers;
    opens com.example.minierp.controllers to javafx.fxml;

    exports com.example.minierp.model;
    opens com.example.minierp.model to javafx.fxml;
}