package com.pos.model;

public class SalesDetail {
    private String transactionId;
    private String itemCode;
    private int quantity;
    private double purchasePrice;
    private double sellingPrice;
    private String date; // Adding date here for easier stats calculation without joining

    public SalesDetail(String transactionId, String itemCode, int quantity, double purchasePrice, double sellingPrice,
            String date) {
        this.transactionId = transactionId;
        this.itemCode = itemCode;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.date = date;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public String getDate() {
        return date;
    }
}
