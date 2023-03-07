/*******************************************************************************
   CREATE Tables (copy to pgadmin)
********************************************************************************/

/** LOOKUP TABLE **/
CREATE TABLE factory (
    setup_status        VARCHAR(50) NOT NULL,
    sim_status          VARCHAR(50) NOT NULL,
    working_mode        VARCHAR(50) NOT NULL,
    warehouse_capacity  INT         NOT NULL,
    weekly_production   INT         NOT NULL
);
INSERT  INTO factory (setup_status, sim_status, working_mode, warehouse_capacity, weekly_production)
        VALUES ('waiting_db_conn','waiting_sim_start','single',0,0);

/*******************************************************************************
   DROP TABLES (copy to pgadmin)
********************************************************************************/
DROP TABLE IF EXISTS factory CASCADE




/*******************************************************************************
   CREATE Tables (embedded in java)
********************************************************************************/
CREATE TABLE material (
    id      INT NOT NULL,
    type    VARCHAR(50) NOT NULL,

    CONSTRAINT PK_material PRIMARY KEY (id)
);

CREATE TABLE supplier_material (
    min_quantity    INT NOT NULL,
    unit_price      FLOAT(6),
    delivery_time   INT NOT NULL,
    FK_supplier     INT NOT NULL,
    FK_material     INT NOT NULL
);

CREATE TABLE supplier (
    id      INT NOT NULL,
    name    VARCHAR(50) NOT NULL,

    CONSTRAINT PK_supplier PRIMARY KEY (id)
);

CREATE TABLE supplier_order (
    id                  INT NOT NULL,
    type                VARCHAR(50) NOT NULL,
    quantity            INT NOT NULL,
    unit_price          FLOAT(6),
    week_est_delivery   INT NOT NULL,
    delay               INT NOT NULL,

    FK_supplier         INT NOT NULL,

    CONSTRAINT PK_supplier_order PRIMARY KEY (id)
);

CREATE TABLE rawpiece (
    id              INT NOT NULL,
    type            VARCHAR(50) NOT NULL,
    week_arrived    INT NOT NULL,
    safety_stock    BOOLEAN,
    wh_pos          INT NOT NULL,

    FK_supplier_order   INT NOT NULL,
    FK_inbound_order    INT NOT NULL,
    FK_production_order INT NOT NULL,
    FK_product          INT NOT NULL,

    CONSTRAINT PK_rawpiece PRIMARY KEY (id)
);

CREATE TABLE inbound_order (
    id      INT NOT NULL,
    week    INT NOT NULL,

    FK_supplier_order   INT NOT NULL,

    CONSTRAINT PK_inbound_order PRIMARY KEY (id)
);

CREATE TABLE production_order (
    id      INT NOT NULL,
    week    INT NOT NULL,

    CONSTRAINT PK_production_order PRIMARY KEY (id)
);

CREATE TABLE expedition_order (
    id      INT NOT NULL,
    week    INT NOT NULL,

    FK_client_order INT NOT NULL,
    CONSTRAINT PK_expedition_order PRIMARY KEY (id)
);

CREATE TABLE product (
    id                  INT NOT NULL,
    type                VARCHAR(50) NOT NULL,
    status              VARCHAR(50) NOT NULL,
    week_produced       INT NOT NULL,
    duration_production FLOAT(6),
    wh_pos              INT NOT NULL,

    FK_client_order     INT NOT NULL,
    FK_production_order INT NOT NULL,
    FK_expedition_order INT NOT NULL,

    CONSTRAINT PK_product PRIMARY KEY (id)
);

CREATE TABLE client_order (
    id                  INT NOT NULL,
    type                VARCHAR(50) NOT NULL,
    type_base           VARCHAR(50) NOT NULL,
    type_lid            VARCHAR(50) NOT NULL,
    price               FLOAT(6),
    week_est_delivery   INT NOT NULL,
    delay               INT NOT NULL,
    status              VARCHAR(50),

    FK_client           INT NOT NULL,

    CONSTRAINT PK_client_order PRIMARY KEY (id)
);

CREATE TABLE client (
    id      INT NOT NULL,
    name    VARCHAR(50),

    CONSTRAINT PK_client PRIMARY KEY (id)
);

/*******************************************************************************
   Create Foreign Keys (embedded in java)
********************************************************************************/

/*      supplier_material                           */
ALTER TABLE supplier_material ADD CONSTRAINT FK_supplier_material_id_material
    FOREIGN KEY (FK_material) REFERENCES material (id) ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE supplier_material ADD CONSTRAINT FK_supplier_material_id_supplier
    FOREIGN KEY (FK_supplier) REFERENCES supplier (id) ON DELETE CASCADE ON UPDATE NO ACTION;

/*      supplier(1) -- (*)supplier_order      */
ALTER TABLE supplier_order ADD CONSTRAINT FK_supplier_order_id_supplier
    FOREIGN KEY (FK_supplier) REFERENCES supplier (id) ON DELETE CASCADE ON UPDATE NO ACTION;

/*      supplier_order(1) -- (1)inbound_order      */
ALTER TABLE inbound_order ADD CONSTRAINT FK_inbound_order_id_supplier_order
    FOREIGN KEY (FK_supplier_order) REFERENCES supplier_order (id) ON DELETE CASCADE ON UPDATE NO ACTION;

/*      supplier_order(1) -- (*)rawpiece      */
ALTER TABLE rawpiece ADD CONSTRAINT FK_rawpiece_id_supplier_order
    FOREIGN KEY (FK_supplier_order) REFERENCES supplier_order (id) ON DELETE CASCADE ON UPDATE NO ACTION;

/*      inbound_order(1) -- (*)rawpiece      */
ALTER TABLE rawpiece ADD CONSTRAINT FK_rawpiece_id_inbound_order
    FOREIGN KEY (FK_inbound_order) REFERENCES inbound_order (id) ON DELETE CASCADE ON UPDATE NO ACTION;

/*      production_order(1) -- (*)rawpiece      */
ALTER TABLE rawpiece ADD CONSTRAINT FK_rawpiece_id_production_order
    FOREIGN KEY (FK_production_order) REFERENCES production_order (id) ON DELETE CASCADE ON UPDATE NO ACTION;

/*      product(1) -- (*1..2)rawpiece      */
ALTER TABLE rawpiece ADD CONSTRAINT FK_rawpiece_id_product
    FOREIGN KEY (FK_product) REFERENCES product (id) ON DELETE CASCADE ON UPDATE NO ACTION;

/*      production_order(1) -- (*)product      */
ALTER TABLE product ADD CONSTRAINT FK_product_id_production_order
    FOREIGN KEY (FK_production_order) REFERENCES production_order (id) ON DELETE CASCADE ON UPDATE NO ACTION;

/*      expedition_order(1) -- (*)product      */
ALTER TABLE product ADD CONSTRAINT FK_product_id_expedition_order
    FOREIGN KEY (FK_expedition_order) REFERENCES expedition_order (id) ON DELETE CASCADE ON UPDATE NO ACTION;

/*      client_order(1) -- (*)product      */
ALTER TABLE product ADD CONSTRAINT FK_product_id_client_order
    FOREIGN KEY (FK_client_order) REFERENCES client_order (id) ON DELETE CASCADE ON UPDATE NO ACTION;

/*      expedition_order(1) -- (1)client_order      */
ALTER TABLE expedition_order ADD CONSTRAINT FK_expedition_order_id_client_order
    FOREIGN KEY (FK_client_order) REFERENCES client_order (id) ON DELETE CASCADE ON UPDATE NO ACTION;

/*      client(1) -- (*)client_order      */
ALTER TABLE client_order ADD CONSTRAINT FK_client_order_id_client
    FOREIGN KEY (FK_client) REFERENCES client (id) ON DELETE CASCADE ON UPDATE NO ACTION;


/*******************************************************************************
   DROP TABLES (embedded in java)
********************************************************************************/
DROP TABLE IF EXISTS material CASCADE;
DROP TABLE IF EXISTS supplier_material CASCADE;
DROP TABLE IF EXISTS supplier CASCADE;
DROP TABLE IF EXISTS supplier_order CASCADE;
DROP TABLE IF EXISTS inbound_order CASCADE;
DROP TABLE IF EXISTS rawpiece CASCADE;
DROP TABLE IF EXISTS production_order CASCADE;
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS expedition_order CASCADE;
DROP TABLE IF EXISTS client_order CASCADE;
DROP TABLE IF EXISTS client CASCADE;








