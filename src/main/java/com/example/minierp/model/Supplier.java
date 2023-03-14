package com.example.minierp.model;

public class Supplier {
    Integer id;
    String name;
    String material_type;
    int unit_price;
    int min_quantity;
    int delivery_time;

    public Supplier(Integer id, String name, String material_type, int unit_price, int min_quantity, int delivery_time) {
        this.id = id;
        this.name = name;
        this.material_type = material_type;
        this.unit_price = unit_price;
        this.min_quantity = min_quantity;
        this.delivery_time = delivery_time;
    }

    public int getId(){ return id;}

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

    public int getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(int unit_price) {
        this.unit_price = unit_price;
    }

    public int getMin_quantity() {
        return min_quantity;
    }

    public void setMin_quantity(int min_quantity) {
        this.min_quantity = min_quantity;
    }

    public int getDelivery_time() {
        return delivery_time;
    }

    public void setDelivery_time(int delivery_time) {
        this.delivery_time = delivery_time;
    }

}
