package com.example.minierp.model;

import java.util.ArrayList;

public class ExpeditionOrder {
    Integer id;
    int week;
    int quantity;
    String client;
    String status;
    String type;
    ArrayList<Piece> pieces;
    ClientOrder co;

    public ExpeditionOrder(Integer id, int week, String status, ArrayList<Piece> pieces, ClientOrder co) {
        this.id = id;
        this.week = week;
        this.pieces = pieces;
        this.co = co;
        this.status = status;

        this.client = co.getClient();
        this.quantity = pieces.size();
        this.type = co.getType();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public ArrayList<Piece> getPieces() {
        return pieces;
    }

    public void setPieces(ArrayList<Piece> pieces) {
        this.pieces = pieces;
    }

    public void addPiece(Piece p){
        this.pieces.add(p);
    }

    public int getQuantity() {
        return quantity;
    }

    public String getClient() {
        return client;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }
}
