/*******************************************************************************
   CREATE Tables
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
   DROP TABLES
********************************************************************************/
DROP TABLE IF EXISTS factory CASCADE