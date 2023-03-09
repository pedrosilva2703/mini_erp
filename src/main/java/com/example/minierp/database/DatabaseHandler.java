package com.example.minierp.database;

import com.example.minierp.model.Factory;
import org.postgresql.util.PSQLException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private static DatabaseHandler instance;
    private String url;
    private int port;
    private String databaseName;
    private String schema;
    private String username;
    private String password;
    private Connection connection = null;
    /*dbHandler = new DatabaseHandler(
            "db.fe.up.pt",
            5432,
            "sie2252",
            "siefinal",
            "sie2252",
            "GDQllMDQ");*/
    private DatabaseHandler(String url, int port, String databaseName, String schema, String username, String password){
        this.url = url;
        this.port = port;
        this.databaseName = databaseName;
        this.schema = schema;
        this.username = username;
        this.password = password;
    }


    public static DatabaseHandler getInstance(String url, int port, String databaseName, String schema, String username, String password){
        instance = new DatabaseHandler(url, port, databaseName, schema, username, password);
        return instance;
    }
    public static DatabaseHandler getInstance(){
        return instance;
    }

    public boolean setConnection(){
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
            connection = DriverManager.getConnection("jdbc:postgresql://" + url + ":" + port + "/" + databaseName +
                                                         "?currentSchema="+schema, username, password);
        } catch (SQLException | IllegalArgumentException ex) {
            //ex.printStackTrace(System.err);
            System.out.println(ex);
        } finally {
            if (connection == null) {
                return false;
            }
            if (!initializeFactoryStatus()){
                return false;
            }
            if (!clearPreviousData() ){
                return false;
            }
            if (!createTables()){
                return false;
            }
            if(!addForeignKeys()){
                return false;
            }
            return true;
        }
    }

    //Factory Lookup table functions
    public boolean initializeFactoryStatus(){
        Factory factory = Factory.getInstance();
        String sql = "UPDATE factory SET setup_status = ?, sim_status = ?;";
        try {
            PreparedStatement updateStatement = connection.prepareStatement(sql);
            updateStatement.setString(1,    factory.getSetup_status()   );
            updateStatement.setString(2,    factory.getSim_status()     );
            updateStatement.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }
    public void retrieveFactoryStatus() {
        Factory factory = Factory.getInstance();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM factory");
            ResultSet sqlReturnValues = stmt.executeQuery();
            sqlReturnValues.next();

            factory.setWorking_mode(sqlReturnValues.getString(3) );
            factory.setWarehouse_capacity(sqlReturnValues.getInt(4) );
            factory.setWeekly_production(sqlReturnValues.getInt(5) );

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

    }
    public boolean updateFactoryStatus(){
        Factory factory = Factory.getInstance();
        String sql = "UPDATE factory SET     setup_status = ?,       " +
                                            "sim_status = ?,         " +
                                            "working_mode = ?,       " +
                                            "warehouse_capacity = ?, " +
                                            "weekly_production = ?;  " ;
        try {
            PreparedStatement updateStatement = connection.prepareStatement(sql);
            updateStatement.setString(1,    factory.getSetup_status()       );
            updateStatement.setString(2,    factory.getSim_status()         );
            updateStatement.setString(3,    factory.getWorking_mode()       );
            updateStatement.setInt(   4,    factory.getWarehouse_capacity() );
            updateStatement.setInt(   5,    factory.getWeekly_production()  );
            updateStatement.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }

    //DDL functions
    public boolean clearPreviousData(){
        String sql =    "DROP TABLE IF EXISTS material          CASCADE; " +
                        "DROP TABLE IF EXISTS supplier_material CASCADE; " +
                        "DROP TABLE IF EXISTS supplier          CASCADE; " +
                        "DROP TABLE IF EXISTS supplier_order    CASCADE; " +
                        "DROP TABLE IF EXISTS inbound_order     CASCADE; " +
                        "DROP TABLE IF EXISTS rawpiece          CASCADE; " +
                        "DROP TABLE IF EXISTS production_order  CASCADE; " +
                        "DROP TABLE IF EXISTS product           CASCADE; " +
                        "DROP TABLE IF EXISTS expedition_order  CASCADE; " +
                        "DROP TABLE IF EXISTS client_order      CASCADE; " +
                        "DROP TABLE IF EXISTS client            CASCADE;";
        try {
            PreparedStatement dropStatement = connection.prepareStatement(sql);
            dropStatement.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean createTables(){
        String sql =    " CREATE TABLE material (\n" +
                        "    id      INT NOT NULL,\n" +
                        "    type    VARCHAR(50) NOT NULL,\n" +
                        "\n" +
                        "    CONSTRAINT PK_material PRIMARY KEY (id)\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE supplier_material (\n" +
                        "    min_quantity    INT NOT NULL,\n" +
                        "    unit_price      FLOAT(6),\n" +
                        "    delivery_time   INT NOT NULL,\n" +
                        "    FK_supplier     INT NOT NULL,\n" +
                        "    FK_material     INT NOT NULL\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE supplier (\n" +
                        "    id      INT NOT NULL,\n" +
                        "    name    VARCHAR(50) NOT NULL,\n" +
                        "\n" +
                        "    CONSTRAINT PK_supplier PRIMARY KEY (id)\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE supplier_order (\n" +
                        "    id                  INT NOT NULL,\n" +
                        "    type                VARCHAR(50) NOT NULL,\n" +
                        "    quantity            INT NOT NULL,\n" +
                        "    unit_price          FLOAT(6),\n" +
                        "    week_est_delivery   INT NOT NULL,\n" +
                        "    delay               INT NOT NULL,\n" +
                        "\n" +
                        "    FK_supplier         INT NOT NULL,\n" +
                        "\n" +
                        "    CONSTRAINT PK_supplier_order PRIMARY KEY (id)\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE rawpiece (\n" +
                        "    id              INT NOT NULL,\n" +
                        "    type            VARCHAR(50) NOT NULL,\n" +
                        "    status          VARCHAR(50) NOT NULL,\n" +
                        "    week_arrived    INT NOT NULL,\n" +
                        "    safety_stock    BOOLEAN,\n" +
                        "    wh_pos          INT NOT NULL,\n" +
                        "\n" +
                        "    FK_supplier_order   INT NOT NULL,\n" +
                        "    FK_inbound_order    INT NOT NULL,\n" +
                        "    FK_production_order INT NOT NULL,\n" +
                        "    FK_product          INT NOT NULL,\n" +
                        "\n" +
                        "    CONSTRAINT PK_rawpiece PRIMARY KEY (id)\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE inbound_order (\n" +
                        "    id      INT NOT NULL,\n" +
                        "    week    INT NOT NULL,\n" +
                        "\n" +
                        "    FK_supplier_order   INT NOT NULL,\n" +
                        "\n" +
                        "    CONSTRAINT PK_inbound_order PRIMARY KEY (id)\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE production_order (\n" +
                        "    id      INT NOT NULL,\n" +
                        "    week    INT NOT NULL,\n" +
                        "\n" +
                        "    CONSTRAINT PK_production_order PRIMARY KEY (id)\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE expedition_order (\n" +
                        "    id      INT NOT NULL,\n" +
                        "    week    INT NOT NULL,\n" +
                        "\n" +
                        "    FK_client_order INT NOT NULL,\n" +
                        "    CONSTRAINT PK_expedition_order PRIMARY KEY (id)\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE product (\n" +
                        "    id                  INT NOT NULL,\n" +
                        "    type                VARCHAR(50) NOT NULL,\n" +
                        "    status              VARCHAR(50) NOT NULL,\n" +
                        "    week_produced       INT NOT NULL,\n" +
                        "    duration_production FLOAT(6),\n" +
                        "    wh_pos              INT NOT NULL,\n" +
                        "\n" +
                        "    FK_client_order     INT NOT NULL,\n" +
                        "    FK_production_order INT NOT NULL,\n" +
                        "    FK_expedition_order INT NOT NULL,\n" +
                        "\n" +
                        "    CONSTRAINT PK_product PRIMARY KEY (id)\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE client_order (\n" +
                        "    id                  INT NOT NULL,\n" +
                        "    type                VARCHAR(50) NOT NULL,\n" +
                        "    type_base           VARCHAR(50) NOT NULL,\n" +
                        "    type_lid            VARCHAR(50) NOT NULL,\n" +
                        "    price               FLOAT(6),\n" +
                        "    week_est_delivery   INT NOT NULL,\n" +
                        "    delay               INT NOT NULL,\n" +
                        "    status              VARCHAR(50),\n" +
                        "\n" +
                        "    FK_client           INT NOT NULL,\n" +
                        "\n" +
                        "    CONSTRAINT PK_client_order PRIMARY KEY (id)\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE client (\n" +
                        "    id      INT NOT NULL,\n" +
                        "    name    VARCHAR(50),\n" +
                        "\n" +
                        "    CONSTRAINT PK_client PRIMARY KEY (id)\n" +
                        ");";
        try {
            PreparedStatement dropStatement = connection.prepareStatement(sql);
            dropStatement.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean addForeignKeys(){
        String sql =    "/*      supplier_material                           */\n" +
                        "ALTER TABLE supplier_material ADD CONSTRAINT FK_supplier_material_id_material\n" +
                        "    FOREIGN KEY (FK_material) REFERENCES material (id) ON DELETE CASCADE ON UPDATE NO ACTION;\n" +
                        "ALTER TABLE supplier_material ADD CONSTRAINT FK_supplier_material_id_supplier\n" +
                        "    FOREIGN KEY (FK_supplier) REFERENCES supplier (id) ON DELETE CASCADE ON UPDATE NO ACTION;\n" +
                        "\n" +
                        "/*      supplier(1) -- (*)supplier_order      */\n" +
                        "ALTER TABLE supplier_order ADD CONSTRAINT FK_supplier_order_id_supplier\n" +
                        "    FOREIGN KEY (FK_supplier) REFERENCES supplier (id) ON DELETE CASCADE ON UPDATE NO ACTION;\n" +
                        "\n" +
                        "/*      supplier_order(1) -- (1)inbound_order      */\n" +
                        "ALTER TABLE inbound_order ADD CONSTRAINT FK_inbound_order_id_supplier_order\n" +
                        "    FOREIGN KEY (FK_supplier_order) REFERENCES supplier_order (id) ON DELETE CASCADE ON UPDATE NO ACTION;\n" +
                        "\n" +
                        "/*      supplier_order(1) -- (*)rawpiece      */\n" +
                        "ALTER TABLE rawpiece ADD CONSTRAINT FK_rawpiece_id_supplier_order\n" +
                        "    FOREIGN KEY (FK_supplier_order) REFERENCES supplier_order (id) ON DELETE CASCADE ON UPDATE NO ACTION;\n" +
                        "\n" +
                        "/*      inbound_order(1) -- (*)rawpiece      */\n" +
                        "ALTER TABLE rawpiece ADD CONSTRAINT FK_rawpiece_id_inbound_order\n" +
                        "    FOREIGN KEY (FK_inbound_order) REFERENCES inbound_order (id) ON DELETE CASCADE ON UPDATE NO ACTION;\n" +
                        "\n" +
                        "/*      production_order(1) -- (*)rawpiece      */\n" +
                        "ALTER TABLE rawpiece ADD CONSTRAINT FK_rawpiece_id_production_order\n" +
                        "    FOREIGN KEY (FK_production_order) REFERENCES production_order (id) ON DELETE CASCADE ON UPDATE NO ACTION;\n" +
                        "\n" +
                        "/*      product(1) -- (*1..2)rawpiece      */\n" +
                        "ALTER TABLE rawpiece ADD CONSTRAINT FK_rawpiece_id_product\n" +
                        "    FOREIGN KEY (FK_product) REFERENCES product (id) ON DELETE CASCADE ON UPDATE NO ACTION;\n" +
                        "\n" +
                        "/*      production_order(1) -- (*)product      */\n" +
                        "ALTER TABLE product ADD CONSTRAINT FK_product_id_production_order\n" +
                        "    FOREIGN KEY (FK_production_order) REFERENCES production_order (id) ON DELETE CASCADE ON UPDATE NO ACTION;\n" +
                        "\n" +
                        "/*      expedition_order(1) -- (*)product      */\n" +
                        "ALTER TABLE product ADD CONSTRAINT FK_product_id_expedition_order\n" +
                        "    FOREIGN KEY (FK_expedition_order) REFERENCES expedition_order (id) ON DELETE CASCADE ON UPDATE NO ACTION;\n" +
                        "\n" +
                        "/*      client_order(1) -- (*)product      */\n" +
                        "ALTER TABLE product ADD CONSTRAINT FK_product_id_client_order\n" +
                        "    FOREIGN KEY (FK_client_order) REFERENCES client_order (id) ON DELETE CASCADE ON UPDATE NO ACTION;\n" +
                        "\n" +
                        "/*      expedition_order(1) -- (1)client_order      */\n" +
                        "ALTER TABLE expedition_order ADD CONSTRAINT FK_expedition_order_id_client_order\n" +
                        "    FOREIGN KEY (FK_client_order) REFERENCES client_order (id) ON DELETE CASCADE ON UPDATE NO ACTION;\n" +
                        "\n" +
                        "/*      client(1) -- (*)client_order      */\n" +
                        "ALTER TABLE client_order ADD CONSTRAINT FK_client_order_id_client\n" +
                        "    FOREIGN KEY (FK_client) REFERENCES client (id) ON DELETE CASCADE ON UPDATE NO ACTION;";
        try {
            PreparedStatement dropStatement = connection.prepareStatement(sql);
            dropStatement.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }




}
