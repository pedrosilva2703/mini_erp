package com.example.minierp.model;

public class ClientOrder {
    String client;
    String type;
    int quantity;
    double price;
    int delivery_week;
    int current_estimation;
    String status;

    public ClientOrder(String client, String type, int quantity, double price, int delivery_week, int current_estimation, String status) {
        this.client = client;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.delivery_week = delivery_week;
        this.current_estimation = current_estimation;
        this.status = status;
    }


    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDelivery_week() {
        return delivery_week;
    }

    public void setDelivery_week(int delivery_week) {
        this.delivery_week = delivery_week;
    }

    public int getCurrent_estimation() {
        return current_estimation;
    }

    public void setCurrent_estimation(int current_estimation) {
        this.current_estimation = current_estimation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
