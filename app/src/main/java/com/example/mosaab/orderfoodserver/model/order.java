package com.example.mosaab.orderfoodserver.model;

public class order {

    private String ProudctId;
    private String ProductName;
    private String Quantity;
    private String Price;
    private String Discount;


    public order()
    {

    }

    public order(String proudctId, String productName, String quantity, String price, String discount) {
        ProudctId = proudctId;
        ProductName = productName;
        Quantity = quantity;
        Price = price;
        Discount = discount;
    }

    public String getProudctId() {
        return ProudctId;
    }

    public void setProudctId(String proudctId) {
        ProudctId = proudctId;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }
}
