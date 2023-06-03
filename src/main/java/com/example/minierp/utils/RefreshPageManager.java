package com.example.minierp.utils;

public class RefreshPageManager {
    private static RefreshPageManager instance;

    private boolean refreshCI = true;
    private boolean refreshII = true;
    private boolean refreshSI = true;

    private RefreshPageManager() {}

    public static RefreshPageManager getInstance() {
        if (instance == null) {
            instance = new RefreshPageManager();
        }
        return instance;
    }

    public void sendRefreshRequest(){
        this.refreshCI = false;
        this.refreshII = false;
        this.refreshSI = false;
    }

    public boolean isRefreshedCI() {return refreshCI;}
    public void setCiRefreshed(){ this.refreshCI = true;}

    public boolean isRefreshedII() {return refreshII;}
    public void setIiRefreshed(){ this.refreshII = true;}

    public boolean isRefreshedSI() {return refreshSI;}
    public void setSiRefreshed(){ this.refreshSI = true;}
}