package com.example.minierp.database;

import com.example.minierp.model.*;
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
    public void retrieveFactoryPreviousConfig() {
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
    public boolean retrieveFactoryStatus() {
        Factory factory = Factory.getInstance();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM factory");
            ResultSet sqlReturnValues = stmt.executeQuery();
            sqlReturnValues.next();

            factory.setSetup_status(sqlReturnValues.getString(1));
            factory.setSim_status(sqlReturnValues.getString(2));
            factory.setWorking_mode(sqlReturnValues.getString(3) );
            factory.setWarehouse_capacity(sqlReturnValues.getInt(4) );
            factory.setWeekly_production(sqlReturnValues.getInt(5) );

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
                "    status              VARCHAR(50) NOT NULL,\n" +
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
                "    FK_client_order     INT,\n" +
                "    FK_inbound_order    INT NOT NULL,\n" +
                "    FK_production_order INT,\n" +
                "    FK_expedition_order INT,\n" +
                "\n" +
                "    CONSTRAINT PK_piece PRIMARY KEY (id)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE inbound_order (\n" +
                "    id      SERIAL NOT NULL,\n" +
                "    week    INT NOT NULL,\n" +
                "    status  VARCHAR(50),\n" +
                "    \n" +
                "    FK_supplier_order   INT NOT NULL,\n" +
                "\n" +
                "    CONSTRAINT PK_inbound_order PRIMARY KEY (id)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE production_order (\n" +
                "    id      SERIAL NOT NULL,\n" +
                "    week    INT NOT NULL,\n" +
                "    status  VARCHAR(50),\n" +
                "\n" +
                "    CONSTRAINT PK_production_order PRIMARY KEY (id)\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE expedition_order (\n" +
                "    id      SERIAL NOT NULL,\n" +
                "    week    INT NOT NULL,\n" +
                "    status  VARCHAR(50),\n" +
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
                "    quantity            INT NOT NULL,\n" +
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

    //External entities methods
    public boolean nameExists(String entity, String name){
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM "+entity+" WHERE name = ?");
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

    //Client order methods
    public int createClientOrder(ClientOrder co){
        int id = -1;
        try {
            PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO client_order (type, price, week_est_delivery, delay, status, quantity, FK_client) \n" +
                            "    SELECT ?, ?, ?, ?, ?, ?, client.id \n" +
                            "    FROM client \n" +
                            "    WHERE client.name = ? ", Statement.RETURN_GENERATED_KEYS);
            insertStatement.setString(1, co.getType());
            insertStatement.setDouble(2, co.getPrice());
            insertStatement.setInt(3, co.getDelivery_week());
            insertStatement.setInt(4, 0);
            insertStatement.setString(5, co.getStatus());
            insertStatement.setInt(6, co.getQuantity());
            insertStatement.setString(7, co.getClient());
            insertStatement.executeUpdate();

            ResultSet rs_id = insertStatement.getGeneratedKeys();
            rs_id.next();
            id = rs_id.getInt(1);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return id;
        }
        return id;
    }
    public ArrayList<ClientOrder> getClientOrdersByStatus(String filter_status){
        String sql =    "SELECT  client_order.id,\n" +
                "        client.name,\n" +
                "        client_order.type,\n" +
                "        client_order.quantity,\n" +
                "        client_order.price,\n" +
                "        client_order.week_est_delivery,\n" +
                "        client_order.delay,\n" +
                "        client_order.status\n" +
                "FROM client_order\n" +
                "JOIN client on client.id = client_order.FK_client\n" +
                "WHERE client_order.status = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, filter_status);
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<ClientOrder> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(1);
                String name = sqlReturnValues.getString(2);
                String type = sqlReturnValues.getString(3);
                int qty = sqlReturnValues.getInt(4);
                double price = sqlReturnValues.getDouble(5);
                int initial_estimation = sqlReturnValues.getInt(6);
                int current_estimation = sqlReturnValues.getInt(7) + initial_estimation;
                String status = sqlReturnValues.getString(8);

                returnValues.add(new ClientOrder(id, name, type, qty, price, initial_estimation, current_estimation, status) );
            }
            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public ArrayList<ClientOrder> getClientOrdersByName(String filter_name, String filter_status){
        String sql =    "SELECT  client_order.id,\n" +
                "        client.name,\n" +
                "        client_order.type,\n" +
                "        client_order.quantity,\n" +
                "        client_order.price,\n" +
                "        client_order.week_est_delivery,\n" +
                "        client_order.delay,\n" +
                "        client_order.status\n" +
                "FROM client_order\n" +
                "JOIN client on client.id = client_order.FK_client\n" +
                "WHERE client_order.status = ? AND client.name = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, filter_status);
            stmt.setString(2, filter_name);
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<ClientOrder> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(1);
                String name = sqlReturnValues.getString(2);
                String type = sqlReturnValues.getString(3);
                int qty = sqlReturnValues.getInt(4);
                double price = sqlReturnValues.getDouble(5);
                int initial_estimation = sqlReturnValues.getInt(6);
                int current_estimation = sqlReturnValues.getInt(7) + initial_estimation;
                String status = sqlReturnValues.getString(8);

                returnValues.add(new ClientOrder(id, name, type, qty, price, initial_estimation, current_estimation, status) );
            }
            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public ArrayList<ClientOrder> getClientOrdersBySupplierOrder(SupplierOrder so){
        String sql =    "SELECT  client_order.id,\n" +
                "        client.name,\n" +
                "        client_order.type,\n" +
                "        client_order.quantity,\n" +
                "        client_order.price,\n" +
                "        client_order.week_est_delivery,\n" +
                "        client_order.delay,\n" +
                "        client_order.status\n" +
                "FROM client_order\n" +
                "JOIN client on client.id = client_order.FK_client\n" +
                "WHERE client_order.id IN \n" +
                "        (SELECT piece.FK_client_order \n" +
                "        FROM piece \n" +
                "        WHERE piece.FK_supplier_order = ?);";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, so.getId());
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<ClientOrder> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(1);
                String name = sqlReturnValues.getString(2);
                String type = sqlReturnValues.getString(3);
                int qty = sqlReturnValues.getInt(4);
                double price = sqlReturnValues.getDouble(5);
                int initial_estimation = sqlReturnValues.getInt(6);
                int current_estimation = sqlReturnValues.getInt(7) + initial_estimation;
                String status = sqlReturnValues.getString(8);

                returnValues.add(new ClientOrder(id, name, type, qty, price, initial_estimation, current_estimation, status) );
            }
            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public ArrayList<ClientOrder> getClientOrdersByProdWeek(int week){
        String sql =    "SELECT  client_order.id,\n" +
                        "        client.name,\n" +
                        "        client_order.type,\n" +
                        "        client_order.quantity,\n" +
                        "        client_order.price,\n" +
                        "        client_order.week_est_delivery,\n" +
                        "        client_order.delay,\n" +
                        "        client_order.status\n" +
                        "FROM client_order\n" +
                        "JOIN client on client.id = client_order.FK_client\n" +
                        "WHERE client_order.id IN\n" +
                        "(SELECT fk_client_order\n" +
                        "FROM piece\n" +
                        "JOIN production_order ON production_order.id = piece.fk_production_order\n" +
                        "WHERE production_order.week = ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, week);
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<ClientOrder> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(1);
                String name = sqlReturnValues.getString(2);
                String type = sqlReturnValues.getString(3);
                int qty = sqlReturnValues.getInt(4);
                double price = sqlReturnValues.getDouble(5);
                int initial_estimation = sqlReturnValues.getInt(6);
                int current_estimation = sqlReturnValues.getInt(7) + initial_estimation;
                String status = sqlReturnValues.getString(8);

                returnValues.add(new ClientOrder(id, name, type, qty, price, initial_estimation, current_estimation, status) );
            }
            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public boolean updateClientOrderStatus(ClientOrder co, String new_status){
        String sql =    "UPDATE client_order\n" +
                        "SET    status = ?\n" +
                        "WHERE  client_order.id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, new_status);
            stmt.setInt(2, co.getId());
            stmt.execute();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean updateClientOrderDelay(ClientOrder co, int new_delay){
        String sql =    "UPDATE client_order\n" +
                "SET    delay = ?\n" +
                "WHERE  client_order.id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, new_delay);
            stmt.setInt(2, co.getId());
            stmt.execute();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }
    public ClientOrder getClientOrder(int filter_id){
        String sql =    "SELECT  client_order.id,\n" +
                "        client.name,\n" +
                "        client_order.type,\n" +
                "        client_order.quantity,\n" +
                "        client_order.price,\n" +
                "        client_order.week_est_delivery,\n" +
                "        client_order.delay,\n" +
                "        client_order.status\n" +
                "FROM client_order\n" +
                "JOIN client on client.id = client_order.FK_client\n" +
                "WHERE client_order.id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, filter_id);
            ResultSet sqlReturnValues = stmt.executeQuery();
            sqlReturnValues.next();

            Integer id = sqlReturnValues.getInt(1);
            String name = sqlReturnValues.getString(2);
            String type = sqlReturnValues.getString(3);
            int qty = sqlReturnValues.getInt(4);
            double price = sqlReturnValues.getDouble(5);
            int initial_estimation = sqlReturnValues.getInt(6);
            int current_estimation = sqlReturnValues.getInt(7) + initial_estimation;
            String status = sqlReturnValues.getString(8);

            ClientOrder returnValue = new ClientOrder(id, name, type, qty, price, initial_estimation, current_estimation, status);

            return returnValue;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public boolean cancelPendingClientOrder(ClientOrder co, String cancel_type){
        String sql =    "UPDATE client_order\n" +
                        "SET    status = ?\n" +
                        "WHERE  client_order.id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, cancel_type);
            stmt.setInt(2, co.getId());
            stmt.execute();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean hasPendingClientOrders(){
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM client_order WHERE status = ? OR status = ?");
            stmt.setString(1, "pending_internal");
            stmt.setString(2, "pending_client");
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
    public boolean hasClientOrders(){
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM client_order");
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
    public boolean isOnlyClientOrderForSupplierOrder(ClientOrder co, SupplierOrder so){
        String sql =    "SELECT COUNT(*)\n" +
                        "FROM piece\n" +
                        "WHERE fk_supplier_order = ? AND fk_client_order != ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, so.getId());
            stmt.setInt(2, co.getId());
            ResultSet sqlReturnValues = stmt.executeQuery();
            sqlReturnValues.next();

            if( sqlReturnValues.getInt(1) > 0 ){
                return false;
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return true;
    }

    //Supplier methods
    public boolean createSupplier(ArrayList<Supplier> new_supplier){
        //Create entry on the supplier table
        String sql_supp  = "INSERT INTO supplier (name) VALUES (?);";
        String name = new_supplier.get(0).getName();
        try {
            PreparedStatement insertStatement = connection.prepareStatement(sql_supp);
            insertStatement.setString(1, name);
            insertStatement.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }

        //Create entry on the supplier_material table
        String sql_supp_mat =   "INSERT INTO supplier_material (min_quantity, unit_price, delivery_time, FK_supplier, FK_material)\n" +
                                "    SELECT ?, ?, ?, supplier.id, material.id\n" +
                                "    FROM supplier, material\n" +
                                "    WHERE supplier.name = ? AND material.type = ?;";
        for(Supplier s : new_supplier){
            try {
                PreparedStatement insertStatement = connection.prepareStatement(sql_supp_mat);
                insertStatement.setInt(1, s.getMin_quantity());
                insertStatement.setDouble(2, s.getUnit_price());
                insertStatement.setInt(3, s.getDelivery_time());
                insertStatement.setString(4, s.getName());
                insertStatement.setString(5, s.getMaterial_type());
                insertStatement.execute();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
                return false;
            }

        }
        return true;
    }
    public boolean materialSupplierExists(String name, String material){
        String sql =    "SELECT COUNT(*)\n" +
                        "FROM supplier_material\n" +
                        "WHERE   FK_supplier IN (SELECT supplier.id FROM supplier WHERE supplier.name = ?)\n" +
                        "        AND\n" +
                        "        FK_material IN (SELECT material.id FROM material WHERE material.type = ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, material);
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
    public boolean updateSupplier(ArrayList<Supplier> supplier){

        String sql_supp_mat =   "INSERT INTO supplier_material (min_quantity, unit_price, delivery_time, FK_supplier, FK_material)\n" +
                                "    SELECT ?, ?, ?, supplier.id, material.id\n" +
                                "    FROM supplier, material\n" +
                                "    WHERE supplier.name = ? AND material.type = ?;";

        String sql_update   =   "UPDATE supplier_material\n" +
                                "SET min_quantity = ?, unit_price = ?, delivery_time = ? \n" +
                                "WHERE   FK_supplier IN (SELECT supplier.id FROM supplier WHERE supplier.name = ?)\n" +
                                "        AND\n" +
                                "        FK_material IN (SELECT material.id FROM material WHERE material.type = ?)";

        String sql_delete   =   "DELETE  FROM supplier_material\n" +
                                "WHERE   FK_supplier IN (SELECT supplier.id FROM supplier WHERE supplier.name = ?)\n" +
                                "        AND\n" +
                                "        FK_material IN (SELECT material.id FROM material WHERE material.type = ?)";
        String sql = "";

        ArrayList<String> materials = new ArrayList<>();
        materials.add("BlueRawMaterial");
        materials.add("GreenRawMaterial");
        materials.add("MetalRawMaterial");

        for(String m : materials){
            try {
                boolean selected = false;
                for(Supplier s : supplier){
                    if(s.getMaterial_type().equals(m) ){
                        //Material was selected
                        selected = true;
                        if( materialSupplierExists(s.getName(), s.getMaterial_type()) ){
                            //Material exists and needs to be updated
                            sql = sql_update;
                        }
                        else{
                            //Material doesnt exist and needs to be created;
                            sql = sql_supp_mat;
                        }
                        PreparedStatement insertStatement = connection.prepareStatement(sql);
                        insertStatement.setInt(1, s.getMin_quantity());
                        insertStatement.setDouble(2, s.getUnit_price());
                        insertStatement.setInt(3, s.getDelivery_time());
                        insertStatement.setString(4, s.getName());
                        insertStatement.setString(5, s.getMaterial_type());
                        insertStatement.execute();
                    }
                }

                if( !selected ){
                    //Material was not selected, needs delete
                    PreparedStatement insertStatement = connection.prepareStatement(sql_delete);
                    insertStatement.setString(1, supplier.get(0).getName());
                    insertStatement.setString(2, m);
                    insertStatement.execute();
                }


            } catch (SQLException throwable) {
                throwable.printStackTrace();
                return false;
            }

        }
        return true;
    }
    public ArrayList<Supplier> getSuppliers() {
        String sql =    "SELECT  supplier.id,\n" +
                        "        supplier.name,\n" +
                        "        material.type,\n" +
                        "        supplier_material.unit_price,\n" +
                        "        supplier_material.min_quantity,\n" +
                        "        supplier_material.delivery_time\n" +
                        "FROM supplier_material\n" +
                        "JOIN supplier on supplier.id = supplier_material.FK_supplier\n" +
                        "JOIN material on material.id = supplier_material.FK_material " +
                        "ORDER BY supplier.id ASC";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<Supplier> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(1);
                String name = sqlReturnValues.getString(2);
                String type = sqlReturnValues.getString(3);
                double price = sqlReturnValues.getDouble(4);
                int min_qty = sqlReturnValues.getInt(5);
                int delivery_time = sqlReturnValues.getInt(6);

                returnValues.add(new Supplier(id,name,type,price, min_qty, delivery_time) );
            }
            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public ArrayList<Supplier> getSuppliersByName(String filter_name) {
        String sql =    "SELECT  supplier.id,\n" +
                        "        supplier.name,\n" +
                        "        material.type,\n" +
                        "        supplier_material.unit_price,\n" +
                        "        supplier_material.min_quantity,\n" +
                        "        supplier_material.delivery_time\n" +
                        "FROM supplier_material\n" +
                        "JOIN supplier on supplier.id = supplier_material.FK_supplier\n" +
                        "JOIN material on material.id = supplier_material.FK_material\n" +
                        "WHERE supplier.name = ?\n";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, filter_name);
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<Supplier> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(1);
                String name = sqlReturnValues.getString(2);
                String type = sqlReturnValues.getString(3);
                double price = sqlReturnValues.getDouble(4);
                int min_qty = sqlReturnValues.getInt(5);
                int delivery_time = sqlReturnValues.getInt(6);

                returnValues.add(new Supplier(id,name,type,price, min_qty, delivery_time) );
            }
            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public ArrayList<Supplier> getSuppliersByExactQty(String filter_type, int filter_qty, String preference) {
        String sql =    "SELECT  supplier.id,\n" +
                        "        supplier.name,\n" +
                        "        material.type,\n" +
                        "        supplier_material.unit_price,\n" +
                        "        supplier_material.min_quantity,\n" +
                        "        supplier_material.delivery_time\n" +
                        "FROM supplier_material\n" +
                        "JOIN supplier on supplier.id = supplier_material.FK_supplier\n" +
                        "JOIN material on material.id = supplier_material.FK_material\n" +
                        "WHERE material.type= ? AND supplier_material.min_quantity <= ?\n" +
                        "\n";

        if(preference.equals("cheaper") ) sql = sql + "ORDER BY supplier_material.unit_price ASC";
        if(preference.equals("earlier") ) sql = sql + "ORDER BY supplier_material.delivery_time ASC";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, filter_type);
            stmt.setInt(2, filter_qty);
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<Supplier> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(1);
                String name = sqlReturnValues.getString(2);
                String type = sqlReturnValues.getString(3);
                double price = sqlReturnValues.getDouble(4);
                int min_qty = sqlReturnValues.getInt(5);
                int delivery_time = sqlReturnValues.getInt(6);

                returnValues.add(new Supplier(id,name,type,price, min_qty, delivery_time) );
            }
            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public ArrayList<Supplier> getSuppliersByExcessQty(String filter_type, int filter_qty, String preference) {
        String sql =    "SELECT  supplier.id,\n" +
                "        supplier.name,\n" +
                "        material.type,\n" +
                "        supplier_material.unit_price,\n" +
                "        supplier_material.min_quantity,\n" +
                "        supplier_material.delivery_time\n" +
                "FROM supplier_material\n" +
                "JOIN supplier on supplier.id = supplier_material.FK_supplier\n" +
                "JOIN material on material.id = supplier_material.FK_material\n" +
                "WHERE material.type= ? AND supplier_material.min_quantity >= ?\n" +
                "\n";

        if(preference.equals("cheaper") ) sql = sql + "ORDER BY supplier_material.min_quantity ASC, supplier_material.unit_price ASC";
        if(preference.equals("earlier") ) sql = sql + "ORDER BY supplier_material.min_quantity ASC, supplier_material.delivery_time ASC";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, filter_type);
            stmt.setInt(2, filter_qty);
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<Supplier> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(1);
                String name = sqlReturnValues.getString(2);
                String type = sqlReturnValues.getString(3);
                double price = sqlReturnValues.getDouble(4);
                int min_qty = sqlReturnValues.getInt(5);
                int delivery_time = sqlReturnValues.getInt(6);

                returnValues.add(new Supplier(id,name,type,price, min_qty, delivery_time) );
            }
            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public ArrayList<String> getSupplierNames(){
        String sql =    "SELECT supplier.name \n" +
                        "FROM supplier \n" +
                        "GROUP BY supplier.name";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<String> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                String name = sqlReturnValues.getString(1);

                returnValues.add(name);
            }
            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    //Supplier order methods
    public int createSupplierOrder(Supplier s, int quantity, int week_est_delivery){
        int id = -1;
        try {
            PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO supplier_order (type, quantity, unit_price, week_est_delivery, delay, status, FK_supplier)\n" +
                        "VALUES  (?, ?, ?, ?, ?, ?, ?)" +
                            "", Statement.RETURN_GENERATED_KEYS);
            insertStatement.setString(1, s.getMaterial_type());
            insertStatement.setInt(2, quantity);
            insertStatement.setDouble(3, s.getUnit_price());
            insertStatement.setInt(4, week_est_delivery);
            insertStatement.setInt(5, 0);
            insertStatement.setString(6, "waiting_confirmation");
            insertStatement.setInt(7, s.getId());
            insertStatement.executeUpdate();

            ResultSet rs_id = insertStatement.getGeneratedKeys();
            rs_id.next();
            id = rs_id.getInt(1);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return id;
        }
        return id;
    }
    public ArrayList<SupplierOrder> getSupplierOrders(){
        String sql =    "SELECT  supplier.name,\n" +
                        "        supplier_order.id,\n" +
                        "        supplier_order.type,\n" +
                        "        supplier_order.quantity,\n" +
                        "        supplier_order.unit_price,\n" +
                        "        supplier_order.week_est_delivery,\n" +
                        "        supplier_order.delay,\n" +
                        "        supplier_order.status \n" +
                        "FROM supplier_order\n" +
                        "JOIN supplier ON supplier.id = supplier_order.FK_supplier";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<SupplierOrder> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(2);
                String name = sqlReturnValues.getString(1);
                String type = sqlReturnValues.getString(3);
                int qty = sqlReturnValues.getInt(4);
                double price = sqlReturnValues.getDouble(5);
                int delivery_week = sqlReturnValues.getInt(6);
                int delay = sqlReturnValues.getInt(7);
                String status = sqlReturnValues.getString(8);

                returnValues.add(new SupplierOrder(id, name, type, qty, price, delivery_week, delay, status) );
            }
            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public SupplierOrder getSupplierOrder(int filter_id){
        String sql =    "SELECT  supplier.name,\n" +
                        "        supplier_order.id,\n" +
                        "        supplier_order.type,\n" +
                        "        supplier_order.quantity,\n" +
                        "        supplier_order.unit_price,\n" +
                        "        supplier_order.week_est_delivery,\n" +
                        "        supplier_order.delay,\n" +
                        "        supplier_order.status \n" +
                        "FROM supplier_order\n" +
                        "JOIN supplier ON supplier.id = supplier_order.FK_supplier \n" +
                        "WHERE supplier_order.id = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, filter_id);
            ResultSet sqlReturnValues = stmt.executeQuery();

            sqlReturnValues.next();
            Integer id = sqlReturnValues.getInt(2);
            String name = sqlReturnValues.getString(1);
            String type = sqlReturnValues.getString(3);
            int qty = sqlReturnValues.getInt(4);
            double price = sqlReturnValues.getDouble(5);
            int delivery_week = sqlReturnValues.getInt(6);
            int delay = sqlReturnValues.getInt(7);
            String status = sqlReturnValues.getString(8);

            SupplierOrder returnValue = new SupplierOrder(id, name, type, qty, price, delivery_week, delay, status);

            return returnValue;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public SupplierOrder getPendingSupplierOrderByClientOrder(ClientOrder co){
        String sql =    "SELECT  supplier.name,\n" +
                        "        supplier_order.id,\n" +
                        "        supplier_order.type,\n" +
                        "        supplier_order.quantity,\n" +
                        "        supplier_order.unit_price,\n" +
                        "        supplier_order.week_est_delivery,\n" +
                        "        supplier_order.delay,\n" +
                        "        supplier_order.status \n" +
                        "FROM supplier_order\n" +
                        "JOIN supplier ON supplier.id = supplier_order.FK_supplier \n" +
                        "WHERE  supplier_order.status = ? AND " +
                        "       supplier_order.id IN \n" +
                        "           (SELECT piece.fk_supplier_order \n" +
                        "           FROM piece \n" +
                        "           WHERE piece.FK_client_order = ?);";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, "waiting_confirmation");
            stmt.setInt(2, co.getId());
            ResultSet sqlReturnValues = stmt.executeQuery();

            sqlReturnValues.next();
            Integer id = sqlReturnValues.getInt(2);
            String name = sqlReturnValues.getString(1);
            String type = sqlReturnValues.getString(3);
            int qty = sqlReturnValues.getInt(4);
            double price = sqlReturnValues.getDouble(5);
            int delivery_week = sqlReturnValues.getInt(6);
            int delay = sqlReturnValues.getInt(7);
            String status = sqlReturnValues.getString(8);

            SupplierOrder returnValue = new SupplierOrder(id, name, type, qty, price, delivery_week, delay, status);

            return returnValue;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public ArrayList<SupplierOrder> getSupplierOrdersByName(String supplier_name){
        String sql =    "SELECT  supplier.name,\n" +
                "        supplier_order.id,\n" +
                "        supplier_order.type,\n" +
                "        supplier_order.quantity,\n" +
                "        supplier_order.unit_price,\n" +
                "        supplier_order.week_est_delivery,\n" +
                "        supplier_order.delay,\n" +
                "        supplier_order.status \n" +
                "FROM  supplier_order\n" +
                "JOIN  supplier ON supplier.id = supplier_order.FK_supplier " +
                "WHERE supplier.name = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, supplier_name);
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<SupplierOrder> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(2);
                String name = sqlReturnValues.getString(1);
                String type = sqlReturnValues.getString(3);
                int qty = sqlReturnValues.getInt(4);
                double price = sqlReturnValues.getDouble(5);
                int delivery_week = sqlReturnValues.getInt(6);
                int delay = sqlReturnValues.getInt(7);
                String status = sqlReturnValues.getString(8);

                returnValues.add(new SupplierOrder(id, name, type, qty, price, delivery_week, delay, status) );
            }
            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public ArrayList<SupplierOrder> getConfirmedSupplierOrdersByClientOrder(ClientOrder co){
        String sql =    "SELECT  supplier.name,\n" +
                "        supplier_order.id,\n" +
                "        supplier_order.type,\n" +
                "        supplier_order.quantity,\n" +
                "        supplier_order.unit_price,\n" +
                "        supplier_order.week_est_delivery,\n" +
                "        supplier_order.delay,\n" +
                "        supplier_order.status \n" +
                "FROM supplier_order\n" +
                "JOIN supplier ON supplier.id = supplier_order.FK_supplier \n" +
                "WHERE  supplier_order.status = ? AND " +
                "       supplier_order.id IN \n" +
                "           (SELECT piece.fk_supplier_order \n" +
                "           FROM piece \n" +
                "           WHERE piece.FK_client_order = ?);";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, "confirmed");
            stmt.setInt(2, co.getId());
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<SupplierOrder> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(2);
                String name = sqlReturnValues.getString(1);
                String type = sqlReturnValues.getString(3);
                int qty = sqlReturnValues.getInt(4);
                double price = sqlReturnValues.getDouble(5);
                int delivery_week = sqlReturnValues.getInt(6);
                int delay = sqlReturnValues.getInt(7);
                String status = sqlReturnValues.getString(8);

                returnValues.add(new SupplierOrder(id, name, type, qty, price, delivery_week, delay, status) );
            }
            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
        //BUG
    public boolean updatePendingSupplierOrderStatusByClientOrder(ClientOrder co, String new_status){
        String sql =    "UPDATE supplier_order\n" +
                "SET status = ?\n" +
                "WHERE status = 'waiting_confirmation' AND id IN\n" +
                "   (SELECT piece.fk_supplier_order\n" +
                "   FROM piece\n" +
                "   WHERE piece.FK_client_order = ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, new_status);
            stmt.setInt(2, co.getId());
            stmt.execute();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean cancelPendingSupplierOrder(SupplierOrder so){
        String sql =    "UPDATE supplier_order\n" +
                        "SET    status = ?\n" +
                        "WHERE  supplier_order.id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, "canceled");
            stmt.setInt(2, so.getId() );
            stmt.execute();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }
    public ArrayList<SupplierOrder> getSupplierOrdersByCurrentDelivery(int week){
        String sql =    "SELECT  supplier.name,\n" +
                "        supplier_order.id,\n" +
                "        supplier_order.type,\n" +
                "        supplier_order.quantity,\n" +
                "        supplier_order.unit_price,\n" +
                "        supplier_order.week_est_delivery,\n" +
                "        supplier_order.delay,\n" +
                "        supplier_order.status \n" +
                "FROM  supplier_order\n" +
                "JOIN  supplier ON supplier.id = supplier_order.FK_supplier " +
                "WHERE supplier_order.week_est_delivery + supplier_order.delay = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, week);
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<SupplierOrder> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(2);
                String name = sqlReturnValues.getString(1);
                String type = sqlReturnValues.getString(3);
                int qty = sqlReturnValues.getInt(4);
                double price = sqlReturnValues.getDouble(5);
                int delivery_week = sqlReturnValues.getInt(6);
                int delay = sqlReturnValues.getInt(7);
                String status = sqlReturnValues.getString(8);

                returnValues.add(new SupplierOrder(id, name, type, qty, price, delivery_week, delay, status) );
            }
            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public boolean updateSupplierOrderStatus(SupplierOrder so, String status){
        String sql =    "UPDATE supplier_order\n" +
                        "SET status = ?\n" +
                        "WHERE id = ? \n";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, so.getId());
            stmt.execute();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean updateSupplierOrderDelay(SupplierOrder so, int delay){
        String sql =    "UPDATE supplier_order\n" +
                "SET delay = ?\n" +
                "WHERE id = ? \n";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, delay);
            stmt.setInt(2, so.getId());
            stmt.execute();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }


    //Internal Orders (IO, PO, EO)
    public boolean deleteCanceledInternalOrders(){
        String sql =    "DELETE FROM inbound_order\n" +
                "WHERE status = 'canceled'; \n" +
                "DELETE FROM production_order\n" +
                "WHERE status = 'canceled'; \n" +
                "DELETE FROM expedition_order\n" +
                "WHERE status = 'canceled'; ";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.execute();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }

    //Inbound order methods
    public int createInboundOrder(InboundOrder io, int SO_id){
        int id = -1;
        try {
            PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO inbound_order (week, status, FK_supplier_order)\n" +
                            "VALUES  (?, ?, ?)" +
                            "", Statement.RETURN_GENERATED_KEYS);
            insertStatement.setInt(1, io.getWeek());
            insertStatement.setString(2, io.getStatus());
            insertStatement.setInt(3, SO_id);
            insertStatement.executeUpdate();

            ResultSet rs_id = insertStatement.getGeneratedKeys();
            rs_id.next();
            id = rs_id.getInt(1);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return id;
        }
        return id;
    }
    public boolean updateInboundStatusByClientOrder(ClientOrder co, String new_status){
        String sql =    "UPDATE inbound_order\n" +
                        "SET status = ?\n" +
                        "WHERE id IN\n" +
                        "   (SELECT piece.fk_inbound_order\n" +
                        "   FROM piece\n" +
                        "   WHERE piece.FK_client_order = ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, new_status);
            stmt.setInt(2, co.getId());
            stmt.execute();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }
    public ArrayList<InboundOrder> getInboundOrders(){
        String sql =    "SELECT  id,\n" +
                        "        week,\n" +
                        "        status,\n" +
                        "        FK_supplier_order\n" +
                        "FROM inbound_order\n" +
                        "ORDER BY week ASC ";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<InboundOrder> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(1);
                int week = sqlReturnValues.getInt(2);
                String status = sqlReturnValues.getString(3);
                int SO_id = sqlReturnValues.getInt(4);

                returnValues.add(new InboundOrder(id, week, status, getPiecesByIO(id), getSupplierOrder(SO_id) ) );
            }


            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public InboundOrder getInboundOrderBySupplierOrder(SupplierOrder so){
        String sql =    "SELECT  id,\n" +
                        "        week,\n" +
                        "        status,\n" +
                        "        FK_supplier_order\n" +
                        "FROM inbound_order\n" +
                        "WHERE fk_supplier_order = ?   ";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, so.getId());
            ResultSet sqlReturnValues = stmt.executeQuery();

            sqlReturnValues.next();
            Integer id = sqlReturnValues.getInt(1);
            int week = sqlReturnValues.getInt(2);
            String status = sqlReturnValues.getString(3);
            int SO_id = sqlReturnValues.getInt(4);

            InboundOrder returnValues = new InboundOrder(id, week, status, getPiecesByIO(id), getSupplierOrder(SO_id) );

            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public boolean cancelInboundOrder(InboundOrder io){
        String sql =    "UPDATE inbound_order\n" +
                "SET status = ?\n" +
                "WHERE id = ?\n";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, "canceled");
            stmt.setInt(2, io.getId());
            stmt.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean updateInboundWeekBySupplierOrder(SupplierOrder so){
        String sql =    "UPDATE inbound_order\n" +
                        "SET week = ?\n" +
                        "WHERE fk_supplier_order = ?\n";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, so.getDelivery_week() + so.getDelay());
            stmt.setInt(2, so.getId());
            stmt.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }

    //Production orders methods
    public int getProductionCountByWeek(int week){
        String sql =    "SELECT  COUNT(piece.id)\n" +
                        "FROM    piece\n" +
                        "JOIN    production_order    ON piece.FK_production_order = production_order.id\n" +
                        "WHERE   production_order.week = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, week);
            ResultSet sqlReturnValues = stmt.executeQuery();

            sqlReturnValues.next();

            int count = sqlReturnValues.getInt(1);

            return count;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return -1;
    }
    public int createProductionOrder(ProductionOrder po){
        int id = -1;
        try {
            PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO production_order (week, status)\n" +
                        "VALUES  (?, ?)" +
                        "", Statement.RETURN_GENERATED_KEYS);
            insertStatement.setInt(1, po.getWeek());
            insertStatement.setString(2, "waiting_confirmation");
            insertStatement.executeUpdate();

            ResultSet rs_id = insertStatement.getGeneratedKeys();
            rs_id.next();
            id = rs_id.getInt(1);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return id;
        }
        return id;
    }
    public int createConfirmedProductionOrder(ProductionOrder po){
        int id = -1;
        try {
            PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO production_order (week, status)\n" +
                            "VALUES  (?, ?)" +
                            "", Statement.RETURN_GENERATED_KEYS);
            insertStatement.setInt(1, po.getWeek());
            insertStatement.setString(2, "confirmed");
            insertStatement.executeUpdate();

            ResultSet rs_id = insertStatement.getGeneratedKeys();
            rs_id.next();
            id = rs_id.getInt(1);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return id;
        }
        return id;
    }
    public boolean updateProductionStatusByClientOrder(ClientOrder co, String new_status){
        String sql =    "UPDATE production_order\n" +
                        "SET status = ?\n" +
                        "WHERE id IN\n" +
                        "   (SELECT piece.fk_production_order\n" +
                        "   FROM piece\n" +
                        "   WHERE piece.FK_client_order = ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, new_status);
            stmt.setInt(2, co.getId());
            stmt.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }
    public ArrayList<ProductionOrder> getProductionOrders(){
        String sql =    "SELECT  id,\n" +
                        "        week,\n" +
                        "        status\n" +
                        "FROM production_order\n" +
                        "ORDER BY week ASC";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<ProductionOrder> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(1);
                int week = sqlReturnValues.getInt(2);
                String status = sqlReturnValues.getString(3);

                Piece piece_aux = getPiecesByPO(id).get(0);
                returnValues.add(new ProductionOrder(id, week, status, piece_aux.getType(), piece_aux.getFinal_type(), getPiecesByPO(id) ) );
            }


            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public ArrayList<ProductionOrder> getConfirmedProductionOrdersByCO(ClientOrder co){
        String sql =    "SELECT id, week, status\n" +
                        "FROM production_order\n" +
                        "WHERE status = 'confirmed' AND id IN\n" +
                        "(SELECT fk_production_order\n" +
                        "FROM piece\n" +
                        "WHERE fk_client_order = ?) ";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, co.getId());
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<ProductionOrder> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(1);
                int week = sqlReturnValues.getInt(2);
                String status = sqlReturnValues.getString(3);

                Piece piece_aux = getPiecesByPO(id).get(0);
                returnValues.add(new ProductionOrder(id, week, status, piece_aux.getType(), piece_aux.getFinal_type(), getPiecesByPO(id) ) );
            }


            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public ArrayList<ProductionOrder> getConfirmedProductionOrdersBySO(SupplierOrder so){
        String sql =    "SELECT id, week, status\n" +
                        "FROM production_order\n" +
                        "WHERE status = 'confirmed' AND id IN\n" +
                        "(SELECT fk_production_order\n" +
                        "FROM piece\n" +
                        "WHERE fk_supplier_order = ?) ";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, so.getId());
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<ProductionOrder> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(1);
                int week = sqlReturnValues.getInt(2);
                String status = sqlReturnValues.getString(3);

                Piece piece_aux = getPiecesByPO(id).get(0);
                returnValues.add(new ProductionOrder(id, week, status, piece_aux.getType(), piece_aux.getFinal_type(), getPiecesByPO(id) ) );
            }


            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public boolean cancelProductionOrder(ProductionOrder po){
        String sql =    "UPDATE production_order\n" +
                        "SET status = ?\n" +
                        "WHERE id = ?\n";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, "canceled");
            stmt.setInt(2, po.getId());
            stmt.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }
    public int getFinalWarehouseOccupation(int week){
        String sql =    "SELECT(\n" +
                            "(SELECT COUNT(piece.id)\n" +
                            "FROM piece\n" +
                            "JOIN production_order ON piece.fk_production_order = production_order.id\n" +
                            "WHERE piece.status != 'defective' AND production_order.week <= ? AND production_order.status != 'canceled')\n" +
                            "-\n" +
                            "(SELECT COUNT(piece.id)\n" +
                            "FROM piece\n" +
                            "JOIN expedition_order ON piece.fk_expedition_order = expedition_order.id\n" +
                            "WHERE expedition_order.week <= ? AND expedition_order.status != 'canceled')\n" +
                        ")\n";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, week);
            stmt.setInt(2, week);
            ResultSet sqlReturnValues = stmt.executeQuery();

            sqlReturnValues.next();

            int count = sqlReturnValues.getInt(1);

            return count;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return -1;
    }
    public int getMaterialWarehouseOccupation(int week){
        String sql =    "SELECT(\n" +
                            "(SELECT COUNT(piece.id)\n" +
                            "FROM piece\n" +
                            "JOIN inbound_order ON piece.fk_inbound_order = inbound_order.id\n" +
                            "WHERE inbound_order.week <= ? AND inbound_order.status != 'canceled')\n" +
                            "-\n" +
                            "(SELECT COUNT(piece.id)\n" +
                            "FROM piece\n" +
                            "JOIN production_order ON piece.fk_production_order = production_order.id\n" +
                            "WHERE piece.status != 'defective' AND production_order.week <= ? AND production_order.status != 'canceled')\n" +
                        ")\n";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, week);
            stmt.setInt(2, week-1);
            ResultSet sqlReturnValues = stmt.executeQuery();

            sqlReturnValues.next();

            int count = sqlReturnValues.getInt(1);

            return count;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return -1;
    }


    //Expedition orders methods
    public int createExpeditionOrder(ExpeditionOrder eo, int CO_id){
        int id = -1;
        try {
            PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO expedition_order (week, status, FK_client_order)\n" +
                            "VALUES  (?, ?, ?)" +
                            "", Statement.RETURN_GENERATED_KEYS);
            insertStatement.setInt(1, eo.getWeek());
            insertStatement.setString(2, "waiting_confirmation");
            insertStatement.setInt(3, CO_id);
            insertStatement.executeUpdate();

            ResultSet rs_id = insertStatement.getGeneratedKeys();
            rs_id.next();
            id = rs_id.getInt(1);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return id;
        }
        return id;
    }
    public boolean updateExpeditionStatusByClientOrder(ClientOrder co, String new_status){
        String sql =    "UPDATE expedition_order\n" +
                        "SET status = ?\n" +
                        "WHERE id IN\n" +
                        "   (SELECT piece.fk_expedition_order\n" +
                        "   FROM piece\n" +
                        "   WHERE piece.FK_client_order = ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, new_status);
            stmt.setInt(2, co.getId());
            stmt.execute();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }
    public ArrayList<ExpeditionOrder> getExpeditionOrders(){
        String sql =    "SELECT  id,\n" +
                        "        week,\n" +
                        "        status,\n" +
                        "        FK_client_order\n" +
                        "FROM expedition_order\n" +
                        "ORDER BY week ASC";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<ExpeditionOrder> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(1);
                int week = sqlReturnValues.getInt(2);
                String status = sqlReturnValues.getString(3);
                int CO_id = sqlReturnValues.getInt(4);

                returnValues.add(new ExpeditionOrder(id, week, status, getPiecesByEO(id), getClientOrder(CO_id) ) );
            }


            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public ExpeditionOrder getExpeditionOrderByClientOrder(ClientOrder co){
        String sql =    "SELECT  id,\n" +
                "        week,\n" +
                "        status,\n" +
                "        FK_client_order\n" +
                "FROM expedition_order\n" +
                "WHERE fk_client_order = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, co.getId());
            ResultSet sqlReturnValues = stmt.executeQuery();
            sqlReturnValues.next();

            Integer id = sqlReturnValues.getInt(1);
            int week = sqlReturnValues.getInt(2);
            String status = sqlReturnValues.getString(3);
            int CO_id = sqlReturnValues.getInt(4);

            return new ExpeditionOrder(id, week, status, getPiecesByEO(id), getClientOrder(CO_id) );

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public boolean updateExpeditionWeekByClientOrder(ClientOrder co){
        String sql =    "UPDATE expedition_order\n" +
                "SET week = ?\n" +
                "WHERE fk_client_order = ?\n";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, co.getCurrent_estimation());
            stmt.setInt(2, co.getId());
            stmt.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }

    //Piece methods
    public boolean createPiece(Piece p, int SO_id, int CO_id, int IO_id, int PO_id, int EO_id){
        try {
            PreparedStatement insertStatement = connection.prepareStatement(
            "INSERT INTO piece ( type, status, final_type, " +
                                    "FK_supplier_order, FK_client_order, " +
                                    "FK_inbound_order, FK_production_order, FK_expedition_order)\n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            insertStatement.setString(1, p.getType());
            insertStatement.setString(2, p.getStatus());
            insertStatement.setString(3, p.getFinal_type());
            insertStatement.setInt(4, SO_id);

            if(CO_id != -1) insertStatement.setInt(5, CO_id);
            else            insertStatement.setNull(5, java.sql.Types.INTEGER);

            insertStatement.setInt(6, IO_id);

            if(PO_id != -1) insertStatement.setInt(7, PO_id);
            else            insertStatement.setNull(7, java.sql.Types.INTEGER);

            if(EO_id != -1) insertStatement.setInt(8, EO_id);
            else            insertStatement.setNull(8, java.sql.Types.INTEGER);

            insertStatement.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean updatePiece(Piece p, int CO_id, int PO_id, int EO_id){
        try {
            PreparedStatement insertStatement = connection.prepareStatement(
                            "UPDATE piece\n" +
                                "SET final_type = ?, FK_client_order = ?, FK_production_order = ?, FK_expedition_order = ?\n" +
                                "WHERE id = ? ;");

            insertStatement.setString(1, p.getFinal_type());

            if(CO_id == -1)      insertStatement.setNull(2, java.sql.Types.INTEGER);
            else                 insertStatement.setInt(2, CO_id);

            if(PO_id == -1)      insertStatement.setNull(3, java.sql.Types.INTEGER);
            else                 insertStatement.setInt(3, PO_id);

            if(EO_id == -1)      insertStatement.setNull(4, java.sql.Types.INTEGER);
            else                 insertStatement.setInt(4, EO_id);

            insertStatement.setInt(5, p.getId() );

            insertStatement.executeUpdate();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean updatePiecePO(Piece p, int PO_id){
        try {
            PreparedStatement insertStatement = connection.prepareStatement(
                    "UPDATE piece\n" +
                            "SET final_type = ?, FK_production_order = ?\n" +
                            "WHERE id = ? ;");

            insertStatement.setString(1, p.getFinal_type());

            if(PO_id == -1)      insertStatement.setNull(2, java.sql.Types.INTEGER);
            else                 insertStatement.setInt(2, PO_id);

            insertStatement.setInt(3, p.getId() );

            insertStatement.executeUpdate();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean updatePieceEO(Piece p, int EO_id){
        try {
            PreparedStatement insertStatement = connection.prepareStatement(
                    "UPDATE piece\n" +
                            "SET FK_expedition_order = ?\n" +
                            "WHERE id = ? ;");

            if(EO_id == -1)      insertStatement.setNull(1, java.sql.Types.INTEGER);
            else                 insertStatement.setInt(1, EO_id);

            insertStatement.setInt(2, p.getId() );

            insertStatement.executeUpdate();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean updatePieceCO(Piece p, int CO_id){
        try {
            PreparedStatement insertStatement = connection.prepareStatement(
                    "UPDATE piece\n" +
                            "SET FK_client_order = ?\n" +
                            "WHERE id = ? ;");

            if(CO_id == -1)      insertStatement.setNull(1, java.sql.Types.INTEGER);
            else                 insertStatement.setInt(1, CO_id);

            insertStatement.setInt(2, p.getId() );

            insertStatement.executeUpdate();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean freeDefectivePiecesFromClientOrder(ClientOrder co){
        try {
            PreparedStatement insertStatement = connection.prepareStatement(
                    "UPDATE piece\n" +
                            "SET FK_client_order = ?\n" +
                            "WHERE FK_client_order = ? AND status = 'defective' ;");

            insertStatement.setNull(1, java.sql.Types.INTEGER);
            insertStatement.setInt(2, co.getId() );

            insertStatement.executeUpdate();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean updatePieceFinalType(Piece p, String final_type){
        try {
            PreparedStatement insertStatement = connection.prepareStatement(
                        "UPDATE piece\n" +
                            "SET final_type = ?\n" +
                            "WHERE id = ?");

            insertStatement.setString(1, final_type);
            insertStatement.setInt(2, p.getId() );

            insertStatement.executeUpdate();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }
    public ArrayList<Piece> getPiecesByIO(int IO_id){
        String sql =    "SELECT  id,\n" +
                        "        type,\n" +
                        "        status,\n" +
                        "        final_type,\n" +
                        "        week_arrived,\n" +
                        "        week_produced,\n" +
                        "        duration_production,\n" +
                        "        safety_stock,\n" +
                        "        wh_pos\n" +
                        "FROM piece \n" +
                        "WHERE FK_inbound_order = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, IO_id);
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<Piece> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(1);
                String type = sqlReturnValues.getString(2);
                String status = sqlReturnValues.getString(3);
                String final_type = sqlReturnValues.getString(4);
                Integer week_arrived = sqlReturnValues.getInt(5);
                Integer week_produced = sqlReturnValues.getInt(6);
                Float duration_production = sqlReturnValues.getFloat(7);
                boolean safety_stock = sqlReturnValues.getBoolean(8);
                Integer wh_pos = sqlReturnValues.getInt(9);

                returnValues.add(new Piece(id, type, status, final_type, week_arrived, week_produced, duration_production, safety_stock, wh_pos) );
            }
            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public ArrayList<Piece> getPiecesByPO(int PO_id){
        String sql =    "SELECT  id,\n" +
                "        type,\n" +
                "        status,\n" +
                "        final_type,\n" +
                "        week_arrived,\n" +
                "        week_produced,\n" +
                "        duration_production,\n" +
                "        safety_stock,\n" +
                "        wh_pos\n" +
                "FROM piece \n" +
                "WHERE FK_production_order = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, PO_id);
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<Piece> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(1);
                String type = sqlReturnValues.getString(2);
                String status = sqlReturnValues.getString(3);
                String final_type = sqlReturnValues.getString(4);
                Integer week_arrived = sqlReturnValues.getInt(5);
                Integer week_produced = sqlReturnValues.getInt(6);
                Float duration_production = sqlReturnValues.getFloat(7);
                boolean safety_stock = sqlReturnValues.getBoolean(8);
                Integer wh_pos = sqlReturnValues.getInt(9);

                returnValues.add(new Piece(id, type, status, final_type, week_arrived, week_produced, duration_production, safety_stock, wh_pos) );
            }
            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public ArrayList<Piece> getPiecesByEO(int EO_id){
        String sql =    "SELECT  id,\n" +
                "        type,\n" +
                "        status,\n" +
                "        final_type,\n" +
                "        week_arrived,\n" +
                "        week_produced,\n" +
                "        duration_production,\n" +
                "        safety_stock,\n" +
                "        wh_pos\n" +
                "FROM piece \n" +
                "WHERE FK_expedition_order = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, EO_id);
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<Piece> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(1);
                String type = sqlReturnValues.getString(2);
                String status = sqlReturnValues.getString(3);
                String final_type = sqlReturnValues.getString(4);
                Integer week_arrived = sqlReturnValues.getInt(5);
                Integer week_produced = sqlReturnValues.getInt(6);
                Float duration_production = sqlReturnValues.getFloat(7);
                boolean safety_stock = sqlReturnValues.getBoolean(8);
                Integer wh_pos = sqlReturnValues.getInt(9);

                returnValues.add(new Piece(id, type, status, final_type, week_arrived, week_produced, duration_production, safety_stock, wh_pos) );
            }
            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public ArrayList<Piece> getPiecesByCO(int CO_id){
        String sql =    "SELECT  id,\n" +
                "        type,\n" +
                "        status,\n" +
                "        final_type,\n" +
                "        week_arrived,\n" +
                "        week_produced,\n" +
                "        duration_production,\n" +
                "        safety_stock,\n" +
                "        wh_pos\n" +
                "FROM piece \n" +
                "WHERE FK_client_order = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, CO_id);
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<Piece> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(1);
                String type = sqlReturnValues.getString(2);
                String status = sqlReturnValues.getString(3);
                String final_type = sqlReturnValues.getString(4);
                Integer week_arrived = sqlReturnValues.getInt(5);
                Integer week_produced = sqlReturnValues.getInt(6);
                Float duration_production = sqlReturnValues.getFloat(7);
                boolean safety_stock = sqlReturnValues.getBoolean(8);
                Integer wh_pos = sqlReturnValues.getInt(9);

                returnValues.add(new Piece(id, type, status, final_type, week_arrived, week_produced, duration_production, safety_stock, wh_pos) );
            }
            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public ArrayList<Piece> getPiecesBySOandCO(SupplierOrder so, ClientOrder co){
        String sql =    "SELECT  id,\n" +
                "        type,\n" +
                "        status,\n" +
                "        final_type,\n" +
                "        week_arrived,\n" +
                "        week_produced,\n" +
                "        duration_production,\n" +
                "        safety_stock,\n" +
                "        wh_pos\n" +
                "FROM piece \n" +
                "WHERE FK_supplier_order = ? AND FK_client_order = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, so.getId());
            stmt.setInt(2, co.getId());
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<Piece> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(1);
                String type = sqlReturnValues.getString(2);
                String status = sqlReturnValues.getString(3);
                String final_type = sqlReturnValues.getString(4);
                Integer week_arrived = sqlReturnValues.getInt(5);
                Integer week_produced = sqlReturnValues.getInt(6);
                Float duration_production = sqlReturnValues.getFloat(7);
                boolean safety_stock = sqlReturnValues.getBoolean(8);
                Integer wh_pos = sqlReturnValues.getInt(9);

                returnValues.add(new Piece(id, type, status, final_type, week_arrived, week_produced, duration_production, safety_stock, wh_pos) );
            }
            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public ArrayList<Piece> getAvailableFinalPiecesInWH(String filter_final_type){
        String sql =    "SELECT  id,\n" +
                        "        type,\n" +
                        "        status,\n" +
                        "        final_type,\n" +
                        "        week_arrived,\n" +
                        "        week_produced,\n" +
                        "        duration_production,\n" +
                        "        safety_stock,\n" +
                        "        wh_pos\n" +
                        "FROM piece \n" +
                        "WHERE FK_client_order IS NULL AND status != 'defective' AND type = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, filter_final_type);
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<Piece> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(1);
                String type = sqlReturnValues.getString(2);
                String status = sqlReturnValues.getString(3);
                String final_type = sqlReturnValues.getString(4);
                Integer week_arrived = sqlReturnValues.getInt(5);
                Integer week_produced = sqlReturnValues.getInt(6);
                Float duration_production = sqlReturnValues.getFloat(7);
                boolean safety_stock = sqlReturnValues.getBoolean(8);
                Integer wh_pos = sqlReturnValues.getInt(9);

                returnValues.add(new Piece(id, type, status, final_type, week_arrived, week_produced, duration_production, safety_stock, wh_pos) );
            }
            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public ArrayList<Piece> getAvailablePiecesInWH(String filter_type){
        String sql =    "SELECT  id,\n" +
                        "        type,\n" +
                        "        status,\n" +
                        "        final_type,\n" +
                        "        week_arrived,\n" +
                        "        week_produced,\n" +
                        "        duration_production,\n" +
                        "        safety_stock,\n" +
                        "        wh_pos\n" +
                        "FROM piece \n" +
                        "WHERE FK_client_order IS NULL AND status != 'defective' AND wh_pos IS NOT NULL AND type = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, filter_type);
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<Piece> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(1);
                String type = sqlReturnValues.getString(2);
                String status = sqlReturnValues.getString(3);
                String final_type = sqlReturnValues.getString(4);
                Integer week_arrived = sqlReturnValues.getInt(5);
                Integer week_produced = sqlReturnValues.getInt(6);
                Float duration_production = sqlReturnValues.getFloat(7);
                boolean safety_stock = sqlReturnValues.getBoolean(8);
                Integer wh_pos = sqlReturnValues.getInt(9);

                returnValues.add(new Piece(id, type, status, final_type, week_arrived, week_produced, duration_production, safety_stock, wh_pos) );
            }
            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public ArrayList<Piece> getAvailablePiecesArriving(String filter_type){
        String sql =    "SELECT id,\n" +
                "        type,\n" +
                "        status,\n" +
                "        final_type,\n" +
                "        week_arrived,\n" +
                "        week_produced,\n" +
                "        duration_production,\n" +
                "        safety_stock,\n" +
                "        wh_pos\n" +
                "FROM piece \n" +
                "WHERE type = ? AND FK_client_order IS NULL AND " +
                "      FK_supplier_order IN\n" +
                "      (SELECT id FROM supplier_order WHERE status = 'arriving' OR status = 'confirmed') ";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, filter_type);
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<Piece> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(1);
                String type = sqlReturnValues.getString(2);
                String status = sqlReturnValues.getString(3);
                String final_type = sqlReturnValues.getString(4);
                Integer week_arrived = sqlReturnValues.getInt(5);
                Integer week_produced = sqlReturnValues.getInt(6);
                Float duration_production = sqlReturnValues.getFloat(7);
                boolean safety_stock = sqlReturnValues.getBoolean(8);
                Integer wh_pos = sqlReturnValues.getInt(9);

                returnValues.add(new Piece(id, type, status, final_type, week_arrived, week_produced, duration_production, safety_stock, wh_pos) );
            }
            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
    public double getPieceCost(Piece p){
        String sql =    "SELECT unit_price\n" +
                        "FROM supplier_order\n" +
                        "JOIN piece ON supplier_order.id = piece.fk_supplier_order\n" +
                        "WHERE piece.id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, p.getId());
            ResultSet sqlReturnValues = stmt.executeQuery();

            sqlReturnValues.next();

            double count = sqlReturnValues.getDouble(1);

            return count;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return 0;
    }
    public ArrayList<Piece> getDefectivePiecesByCO(ClientOrder CO){
        String sql =    "SELECT id,\n" +
                        "        type,\n" +
                        "        status,\n" +
                        "        final_type,\n" +
                        "        week_arrived,\n" +
                        "        week_produced,\n" +
                        "        duration_production,\n" +
                        "        safety_stock,\n" +
                        "        wh_pos\n" +
                        "FROM piece\n" +
                        "WHERE fk_client_order = ? AND status = 'defective'";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, CO.getId());
            ResultSet sqlReturnValues = stmt.executeQuery();

            ArrayList<Piece> returnValues = new ArrayList<>();

            while (sqlReturnValues.next()){
                Integer id = sqlReturnValues.getInt(1);
                String type = sqlReturnValues.getString(2);
                String status = sqlReturnValues.getString(3);
                String final_type = sqlReturnValues.getString(4);
                Integer week_arrived = sqlReturnValues.getInt(5);
                Integer week_produced = sqlReturnValues.getInt(6);
                Float duration_production = sqlReturnValues.getFloat(7);
                boolean safety_stock = sqlReturnValues.getBoolean(8);
                Integer wh_pos = sqlReturnValues.getInt(9);

                returnValues.add(new Piece(id, type, status, final_type, week_arrived, week_produced, duration_production, safety_stock, wh_pos) );
            }
            return returnValues;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }



    //MES simulation methods
    public void setInboundRunning(int week){
        String sql =    "UPDATE inbound_order\n" +
                "SET status = ? \n" +
                "WHERE week = ? \n";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, "running");
            stmt.setInt(2, week);
            stmt.execute();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return;
        }
        return;
    }
    public void setProductionRunning(int week){
        String sql =    "UPDATE production_order\n" +
                "SET status = ? \n" +
                "WHERE week = ? \n";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, "running");
            stmt.setInt(2, week);
            stmt.execute();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return;
        }
        return;
    }
    public void setExpeditionRunning(int week){
        String sql =    "UPDATE expedition_order\n" +
                "SET status = ? \n" +
                "WHERE week = ? \n";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, "running");
            stmt.setInt(2, week);
            stmt.execute();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return;
        }
        return;
    }

    public void setInboundCompleted(int week){
        String sql =    "UPDATE inbound_order\n" +
                "SET status = ? \n" +
                "WHERE week = ? \n";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, "completed");
            stmt.setInt(2, week);
            stmt.execute();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return;
        }
        return;
    }
    public void setPiecesInbound(int week){
        String sql =    "UPDATE piece \n" +
                        "SET week_arrived = ?, wh_pos = 11\n" +
                        "WHERE fk_inbound_order IN\n" +
                        "(SELECT id FROM inbound_order WHERE week = ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, week);
            stmt.setInt(2, week);
            stmt.execute();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return;
        }
        return;
    }
    public void setProductionCompleted(int week){
        String sql =    "UPDATE production_order\n" +
                "SET status = ? \n" +
                "WHERE week = ? \n";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, "completed");
            stmt.setInt(2, week);
            stmt.execute();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return;
        }
        return;
    }

    public void setPiecesProduction(int week){
        String sql =    "UPDATE piece \n" +
                "SET type = final_type, week_produced = ?\n" +
                "WHERE fk_production_order IN\n" +
                "(SELECT id FROM production_order WHERE week = ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, week);
            stmt.setInt(2, week);
            stmt.execute();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return;
        }
        return;
    }
    public void setExpeditionCompleted(int week){
        String sql =    "UPDATE expedition_order\n" +
                "SET status = ? \n" +
                "WHERE week = ? \n";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, "completed");
            stmt.setInt(2, week);
            stmt.execute();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return;
        }
        return;
    }
    public void setPiecesExpedition(int week){
        String sql =    "UPDATE piece \n" +
                "SET wh_pos = ?\n" +
                "WHERE fk_expedition_order IN\n" +
                "(SELECT id FROM expedition_order WHERE week = ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setNull(1, java.sql.Types.INTEGER);
            stmt.setInt(2, week);
            stmt.execute();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return;
        }
        return;
    }



}
