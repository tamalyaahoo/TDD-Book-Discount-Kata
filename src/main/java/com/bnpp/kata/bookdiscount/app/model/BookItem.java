package com.bnpp.kata.bookdiscount.app.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookItem(

        @NotBlank(message = "Book title must not be blank")
        String title,

        @NotNull(message = "Quantity must not be null")
        Integer quantity
) {}