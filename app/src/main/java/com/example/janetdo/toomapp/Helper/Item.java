package com.example.janetdo.toomapp.Helper;

import java.io.Serializable;

/**
 * Created by janetdo on 26.12.17.
 */

public class Item implements Serializable {
    private String id;
    private String name;
    private String description;
    private String quantity;
    private String category;
    private double price;
    private double salesPrice;
    private int aisle;
    private String scanCode;

    public Item(String id, String name, String description, String quantity, String category, double price, double salesPrice, int aisle, String scanCode) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.category = category;
        this.salesPrice = salesPrice;
        this.price = price;
        this.aisle = aisle;
        this.scanCode = scanCode;
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getQuantity() {
        return quantity;
    }

    public double getSalesPrice() {
        return salesPrice;
    }

    public double getPrice() {
        return price;
    }

    public int getAisle() {
        return aisle;
    }

    public String getScanCode() {
        return scanCode;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", quantity='" + quantity + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", salesPrice=" + salesPrice +
                ", aisle=" + aisle +
                ", scanCode='" + scanCode + '\'' +
                '}';
    }
}
