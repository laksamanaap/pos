package com.pos.model;

public class Item {
    private String code;
    private String name;
    private double purchasePrice;
    private double sellingPrice;
    private double profitMargin;
    private int stock;

    public Item(String code, String name, double purchasePrice, double sellingPrice, int stock) {
        this.code = code;
        this.name = name;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.profitMargin = 20.0; // Default 20% - akan di-override oleh SettingsManager
        this.stock = stock;
    }

    public Item(String code, String name, double purchasePrice, double sellingPrice, double profitMargin, int stock) {
        this.code = code;
        this.name = name;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.profitMargin = profitMargin;
        this.stock = stock;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public double getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(double profitMargin) {
        this.profitMargin = profitMargin;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    // Calculate selling price based on profit margin percentage
    public double calculateSellingPrice(double purchasePrice, double profitMargin) {
        return purchasePrice + (purchasePrice * profitMargin / 100.0);
    }

    @Override
    public String toString() {
        return name; // Useful for dropdowns
    }
}
