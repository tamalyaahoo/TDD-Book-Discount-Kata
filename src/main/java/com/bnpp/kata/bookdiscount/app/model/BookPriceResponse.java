package com.bnpp.kata.bookdiscount.app.model;

public class BookPriceResponse {

    private double totalPrice;

    public BookPriceResponse() { }

    public BookPriceResponse(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}