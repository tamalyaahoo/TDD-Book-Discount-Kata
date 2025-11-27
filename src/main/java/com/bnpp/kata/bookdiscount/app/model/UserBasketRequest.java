package com.bnpp.kata.bookdiscount.app.model;

import java.util.Map;

public class UserBasketRequest {

    private Map<String, Integer> items;

    public UserBasketRequest() {
    }

    public UserBasketRequest(Map<String, Integer> items) {
        this.items = items;
    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public void setItems(Map<String, Integer> items) {
        this.items = items;
    }
}
