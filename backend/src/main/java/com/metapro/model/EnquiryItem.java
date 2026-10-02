package com.metapro.model;

public class EnquiryItem {
    private String productId;
    private String name;
    private int quantity;
    private String unit;
    private Double price;

    public EnquiryItem() {
    }

    public EnquiryItem(String productId, String name, int quantity, String unit, Double price) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
