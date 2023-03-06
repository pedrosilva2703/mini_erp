package com.example.minierp.model;

public class Factory {
    private static Factory instance;

    private String working_mode;
    private int warehouse_capacity;
    private int weekly_production;
    // Setup status: waiting_db_conn, waiting_factory_params, done
    private String setup_status;
    // Simulation status: waiting_sim_start, ongoing_week, waiting_week_start
    private String sim_status;

    private Factory(){
        this.setup_status = "waiting_db_conn";
        this.sim_status = "waiting_sim_start";
    }

    public static Factory getInstance(){
        if (instance == null) {
            instance = new Factory();
        }
        return instance;
    }

    public String getSetup_status(){    return setup_status;        }
    public String getSim_status(){      return sim_status;          }
    public String getWorking_mode() {   return working_mode;        }
    public int getWarehouse_capacity(){ return warehouse_capacity;  }
    public int getWeekly_production(){  return weekly_production;   }

    public boolean isWaitingForDbConn(){    return this.setup_status.equals("waiting_db_conn");          }
    public boolean isWaitingForParams(){    return this.setup_status.equals("waiting_factory_params");  }
    public boolean isSetupDone(){           return this.setup_status.equals("done");                    }

    public void setDbConnected(){   this.setup_status = "waiting_factory_params";   }
    public void setSetupDone(){     this.setup_status = "done";                     }

    public void setWorking_mode(String mode){           this.working_mode = mode;           }
    public void setWarehouse_capacity(int capacity){    this.warehouse_capacity = capacity; }
    public void setWeekly_production(int prod){         this.weekly_production = prod;      }

}
