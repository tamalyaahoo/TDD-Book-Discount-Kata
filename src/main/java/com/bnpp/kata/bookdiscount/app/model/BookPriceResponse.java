package com.bnpp.kata.bookdiscount.app.model;

public record BookPriceResponse(
        UserBasketRequest userRequest,
        double bestOfferedPrice
) {

    public static BookPriceResponse of(UserBasketRequest request, double price){
        return new BookPriceResponse(request, price);
    }

}