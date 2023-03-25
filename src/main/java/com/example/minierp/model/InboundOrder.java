package com.example.minierp.model;

import java.util.ArrayList;

public class InboundOrder {
    Integer id;
    int week;
    int quantity;
    String supplier;
    String status;
    String type;
    ArrayList<Piece> pieces;
    SupplierOrder so;

    public InboundOrder(Integer id, int week, String status, ArrayList<Piece> pieces, SupplierOrder so) {
        this.id = id;
        this.week = week;
        this.pieces = pieces;
        this.so = so;
        this.status = status;

        this.supplier = so.getName();
        this.quantity = pieces.size();
        this.type = so.getMaterial_type();
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
        this.quantity++;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getSupplier() {
        return supplier;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }
}
