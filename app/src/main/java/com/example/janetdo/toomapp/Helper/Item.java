package com.example.janetdo.toomapp.Helper;

/**
 * Created by janetdo on 26.12.17.
 */

public class Item {
    private String id;
    private String name;
    private String description;
    private String quantity;
    private String category;
    private double price;
    private double salePrice;
    private int aisle;

    public Item(String id, String name, String description, String quantity, String category,  double price, double salePrice,int aisle) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.category = category;
        this.salePrice = salePrice;
        this.price = price;
        this.aisle = aisle;
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

    public double getSalePrice() {
        return salePrice;
    }

    public double getPrice() {
        return price;
    }

    public int getAisle() {
        return aisle;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", quantity='" + quantity + '\'' +
                ", category='" + category + '\'' +
                ", salePrice=" + salePrice +
                ", price=" + price +
                ", aisle=" + aisle +
                '}';
    }
}
