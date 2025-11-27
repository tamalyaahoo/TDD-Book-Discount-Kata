package com.bnpp.kata.bookdiscount.app.controller;

import com.bnpp.kata.bookdiscount.app.model.BookPriceResponse;
import com.bnpp.kata.bookdiscount.app.service.BookPriceService;
import com.bnpp.kata.bookdiscount.app.model.UserBasketRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/price")
@Validated
@Tag(name = "Book-Price", description = "API to calculate book basket price with discounts")
public class BookPriceController {

    private final BookPriceService priceService;

    @Autowired
    public BookPriceController(BookPriceService priceService){
        this.priceService = priceService;
    }

    @Operation(
            summary = "Calculate total price for a basket of books",
            description = "Takes a map of book titles to quantities and returns the total price with discounts applied"
    )
    @PostMapping("/calculate")
    public ResponseEntity<BookPriceResponse> calculatePrice(@Valid @RequestBody UserBasketRequest request) {
        System.out.println("Controller = "+request.bookItemList());
        double totalPrice = priceService.calculatePrice(request.bookItemList());
        return ResponseEntity.ok(BookPriceResponse.of(request, totalPrice));
    }
}
