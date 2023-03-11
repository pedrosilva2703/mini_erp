package com.example.minierp.model;

public class Client {
    Integer id;
    String name;
    int orders;

    public Client(Integer id, String name, int orders){
        this.id = id;
        this.name = name;
        this.orders = orders;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrders() {
        return orders;
    }

    public void setOrders(int orders) {
        this.orders = orders;
    }
}
