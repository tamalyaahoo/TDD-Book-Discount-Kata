package com.bnpp.kata.bookdiscount.app.controller;

import com.bnpp.kata.bookdiscount.app.model.BookPriceResponse;
import com.bnpp.kata.bookdiscount.app.model.UserBasketRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/price")
public class BookPriceController {

    @PostMapping("/calculate")
    public ResponseEntity<BookPriceResponse> calculatePrice(@Valid @RequestBody UserBasketRequest request) {
       return ResponseEntity.ok(null);
    }
}
