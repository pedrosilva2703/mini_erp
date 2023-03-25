package com.example.minierp.model;

import java.util.ArrayList;

public class ProductionOrder {
    Integer id;
    int week;
    String status;
    int quantity;
    String initial_type;
    String final_type;
    ArrayList<Piece> pieces;

    public ProductionOrder(Integer id, int week, String status, String initial_type, String final_type, ArrayList<Piece> pieces) {
        this.id = id;
        this.week = week;
        this.status = status;
        this.initial_type = initial_type;
        this.final_type = final_type;
        this.pieces = pieces;


        this.quantity = pieces.size();
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

    public String getStatus() {
        return status;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getInitial_type() {
        return initial_type;
    }

    public String getFinal_type() {
        return final_type;
    }
}
