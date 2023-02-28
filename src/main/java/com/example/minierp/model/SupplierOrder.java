package com.example.minierp.model;

public class SupplierOrder {
    String name;
    String material_type;
    int quantity;
    double price;
    int delivery_week;
    int delay;
    String status;

    public SupplierOrder(String name, String material_type, int quantity, double price, int delivery_week, int delay, String status) {
        this.name = name;
        this.material_type = material_type;
        this.quantity = quantity;
        this.price = price;
        this.delivery_week = delivery_week;
        this.delay = delay;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaterial_type() {
        return material_type;
    }

    public void setMaterial_type(String material_type) {
        this.material_type = material_type;
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

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
