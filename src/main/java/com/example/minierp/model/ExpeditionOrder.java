package com.example.minierp.model;

import java.util.ArrayList;

public class ExpeditionOrder {
    Integer id;
    int week;
    ArrayList<Piece> pieces;
    ClientOrder co;

    public ExpeditionOrder(Integer id, int week, ArrayList<Piece> pieces, ClientOrder co) {
        this.id = id;
        this.week = week;
        this.pieces = pieces;
        this.co = co;
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

}
