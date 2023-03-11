package com.example.minierp.database;

import com.example.minierp.model.Client;
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

    //Manage connection methods
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
        }

        if (connection == null) {
            return false;
        }
        if( !initializeFactoryStatus()  )   return false;
        if( !clearPreviousData()        )   return false;
        if( !createTables()             )   return false;
        if( !addForeignKeys()           )   return false;
        if( !insertStaticData()         )   return false;
        return true;
    }

    //Factory Lookup table methods
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

    //DDL methods
    public boolean clearPreviousData(){
        String sql =    "DROP TABLE IF EXISTS material          CASCADE; " +
                        "DROP TABLE IF EXISTS supplier_material CASCADE; " +
                        "DROP TABLE IF EXISTS supplier          CASCADE; " +
                        "DROP TABLE IF EXISTS supplier_order    CASCADE; " +
                        "DROP TABLE IF EXISTS inbound_order     CASCADE; " +
                        "DROP TABLE IF EXISTS piece             CASCADE; " +
                        "DROP TABLE IF EXISTS production_order  CASCADE; " +
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
        String sql =    "CREATE TABLE material (\n" +
                        "    id      SERIAL NOT NULL,\n" +
                        "    type    VARCHAR(50) NOT NULL,\n" +
                        "\n" +
                        "    CONSTRAINT PK_material PRIMARY KEY (id)\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE supplier_material (\n" +
                        "    min_quantity    INT,\n" +
                        "    unit_price      FLOAT(6),\n" +
                        "    delivery_time   INT,\n" +
                        "    FK_supplier     INT NOT NULL,\n" +
                        "    FK_material     INT NOT NULL\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE supplier (\n" +
                        "    id      SERIAL NOT NULL,\n" +
                        "    name    VARCHAR(50) NOT NULL,\n" +
                        "\n" +
                        "    CONSTRAINT PK_supplier PRIMARY KEY (id)\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE supplier_order (\n" +
                        "    id                  SERIAL NOT NULL,\n" +
                        "    type                VARCHAR(50) NOT NULL,\n" +
                        "    quantity            INT NOT NULL,\n" +
                        "    unit_price          FLOAT(6),\n" +
                        "    week_est_delivery   INT,\n" +
                        "    delay               INT,\n" +
                        "\n" +
                        "    FK_supplier         INT NOT NULL,\n" +
                        "\n" +
                        "    CONSTRAINT PK_supplier_order PRIMARY KEY (id)\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE piece (\n" +
                        "    id                  SERIAL NOT NULL,\n" +
                        "    type                VARCHAR(50) NOT NULL,\n" +
                        "    status              VARCHAR(50) NOT NULL,\n" +
                        "    final_type          VARCHAR(50),\n" +
                        "    week_arrived        INT,\n" +
                        "    week_produced       INT,\n" +
                        "    duration_production FLOAT(6),\n" +
                        "    safety_stock        BOOLEAN,\n" +
                        "    wh_pos              INT,\n" +
                        "\n" +
                        "    FK_supplier_order   INT NOT NULL,\n" +
                        "    FK_client_order     INT NOT NULL,\n" +
                        "    FK_inbound_order    INT NOT NULL,\n" +
                        "    FK_production_order INT NOT NULL,\n" +
                        "    FK_expedition_order INT NOT NULL,\n" +
                        "\n" +
                        "    CONSTRAINT PK_piece PRIMARY KEY (id)\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE inbound_order (\n" +
                        "    id      SERIAL NOT NULL,\n" +
                        "    week    INT NOT NULL,\n" +
                        "\n" +
                        "    FK_supplier_order   INT NOT NULL,\n" +
                        "\n" +
                        "    CONSTRAINT PK_inbound_order PRIMARY KEY (id)\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE production_order (\n" +
                        "    id      SERIAL NOT NULL,\n" +
                        "    week    INT NOT NULL,\n" +
                        "\n" +
                        "    CONSTRAINT PK_production_order PRIMARY KEY (id)\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE expedition_order (\n" +
                        "    id      SERIAL NOT NULL,\n" +
                        "    week    INT NOT NULL,\n" +
                        "\n" +
                        "    FK_client_order INT NOT NULL,\n" +
                        "    CONSTRAINT PK_expedition_order PRIMARY KEY (id)\n" +
                        ");\n" +
                        "\n" +
                        "\n" +
                        "CREATE TABLE client_order (\n" +
                        "    id                  SERIAL NOT NULL,\n" +
                        "    type                VARCHAR(50) NOT NULL,\n" +
                        "    type_base           VARCHAR(50),\n" +
                        "    type_lid            VARCHAR(50),\n" +
                        "    price               FLOAT(6),\n" +
                        "    week_est_delivery   INT,\n" +
                        "    delay               INT,\n" +
                        "    status              VARCHAR(50) NOT NULL,\n" +
                        "\n" +
                        "    FK_client           INT NOT NULL,\n" +
                        "\n" +
                        "    CONSTRAINT PK_client_order PRIMARY KEY (id)\n" +
                        ");\n" +
                        "\n" +
                        "CREATE TABLE client (\n" +
                        "    id      SERIAL NOT NULL,\n" +
                        "    name    VARCHAR(50) NOT NULL,\n" +
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
                        "/*      supplier_order(1) -- (*)piece      */\n" +
                        "ALTER TABLE piece ADD CONSTRAINT FK_piece_id_supplier_order\n" +
                        "    FOREIGN KEY (FK_supplier_order) REFERENCES supplier_order (id) ON DELETE CASCADE ON UPDATE NO ACTION;\n" +
                        "\n" +
                        "/*      inbound_order(1) -- (*)piece      */\n" +
                        "ALTER TABLE piece ADD CONSTRAINT FK_piece_id_inbound_order\n" +
                        "    FOREIGN KEY (FK_inbound_order) REFERENCES inbound_order (id) ON DELETE CASCADE ON UPDATE NO ACTION;\n" +
                        "\n" +
                        "/*      production_order(1) -- (*)piece      */\n" +
                        "ALTER TABLE piece ADD CONSTRAINT FK_piece_id_production_order\n" +
                        "    FOREIGN KEY (FK_production_order) REFERENCES production_order (id) ON DELETE CASCADE ON UPDATE NO ACTION;\n" +
                        "\n" +
                        "/*      expedition_order(1) -- (*)piece      */\n" +
                        "ALTER TABLE piece ADD CONSTRAINT FK_piece_id_expedition_order\n" +
                        "    FOREIGN KEY (FK_expedition_order) REFERENCES expedition_order (id) ON DELETE CASCADE ON UPDATE NO ACTION;\n" +
                        "\n" +
                        "/*      client_order(1) -- (*)piece      */\n" +
                        "ALTER TABLE piece ADD CONSTRAINT FK_piece_id_client_order\n" +
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
            PreparedStatement alterStatement = connection.prepareStatement(sql);
            alterStatement.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean insertStaticData(){
        String sql =    "INSERT INTO material (type) VALUES (\'BlueRawMaterial\'    ); " +
                        "INSERT INTO material (type) VALUES (\'GreenRawMaterial\'   ); " +
                        "INSERT INTO material (type) VALUES (\'MetalRawMaterial\'   ); " +
                        "INSERT INTO material (type) VALUES (\'BlueProductBase\'    ); " +
                        "INSERT INTO material (type) VALUES (\'GreenProductBase\'   ); " +
                        "INSERT INTO material (type) VALUES (\'MetalProductBase\'   ); " +
                        "INSERT INTO material (type) VALUES (\'BlueProductLid\'     ); " +
                        "INSERT INTO material (type) VALUES (\'GreenProductLid\'    ); " +
                        "INSERT INTO material (type) VALUES (\'MetalProductLid\'    ); ";
        try {
            PreparedStatement insertStatement = connection.prepareStatement(sql);
            insertStatement.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }

    //Client methods
    public boolean createClient(Client client) {
        try {
            PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO client (name) VALUES (?);");
            insertStatement.setString(1, client.getName());

            insertStatement.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean clientExists(String name){
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM client WHERE client.name = ?");
            stmt.setString(1, name);
            ResultSet sqlReturnValues = stmt.executeQuery();
            sqlReturnValues.next();

            if( sqlReturnValues.getInt(1) > 0 ){
                return true;
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return false;
    }
    public ArrayList<Client> getClients() {
        //The LEFT on JOIN prevents the case when there are still no orders
        String sql =    "SELECT  client.id   AS id,\n" +
                        "        client.name AS name,\n" +
                        "        COUNT(client_order.id) AS orders_qty\n" +
                        "FROM client\n" +
                        "LEFT JOIN client_order ON client.id = client_order.FK_client\n" +
                        "GROUP BY client.id";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<Client> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(1);
                String name = sqlReturnValues.getString(2);
                int order_qty = sqlReturnValues.getInt(3);
                returnValues.add(new Client(id,name,order_qty));
            }
            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }


}
