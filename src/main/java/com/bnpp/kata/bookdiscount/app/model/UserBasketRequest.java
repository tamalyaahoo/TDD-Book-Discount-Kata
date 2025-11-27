package com.bnpp.kata.bookdiscount.app.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UserBasketRequest(

        @NotNull(message = "Items must not be null")
        @Size(min = 1, message = "Must contain at least one book")
        List<BookItem> bookItemList
) {}
