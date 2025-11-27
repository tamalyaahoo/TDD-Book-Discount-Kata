package com.bnpp.kata.bookdiscount.app.service;

import com.bnpp.kata.bookdiscount.app.exception.InvalidBasketException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookPriceServiceTest {

    private BookPriceService service;

    @BeforeEach
    void setup() {
        service = new BookPriceService();
    }

    // ----------------------------------------------------------------------
    //  BASIC POSITIVE TESTS
    // ----------------------------------------------------------------------

    @Test
    void testSingleBook_noDiscount() {
        Map<String, Integer> basket = Map.of("Clean Code", 1);
        assertEquals(50.0, service.calculatePrice(basket), 0.01);
    }

    @Test
    void testTwoDifferentBooks_5PercentDiscount() {
        Map<String, Integer> basket = Map.of(
                "Clean Code", 1,
                "The Clean Coder", 1
        );
        assertEquals(95.0, service.calculatePrice(basket), 0.01);
    }

    @Test
    void testThreeDifferentBooks_10PercentDiscount() {
        Map<String, Integer> basket = Map.of(
                "Clean Code", 1,
                "The Clean Coder", 1,
                "Clean Architecture", 1
        );
        assertEquals(135.0, service.calculatePrice(basket), 0.01);
    }

    @Test
    void testFourDifferentBooks_20PercentDiscount() {
        Map<String, Integer> basket = Map.of(
                "Clean Code", 1,
                "The Clean Coder", 1,
                "Clean Architecture", 1,
                "TDD", 1
        );

        assertEquals(160.0, service.calculatePrice(basket), 0.01);
    }

    @Test
    void testAllFiveBooks_25PercentDiscount() {
        Map<String, Integer> basket = Map.of(
                "Clean Code", 1,
                "The Clean Coder", 1,
                "Clean Architecture", 1,
                "TDD", 1,
                "Legacy Code", 1
        );

        assertEquals(187.50, service.calculatePrice(basket), 0.01);
    }

    // ----------------------------------------------------------------------
    //  COMPLEX PRICE-KATA SCENARIO
    // ----------------------------------------------------------------------

    @Test
    void testKataExample_multipleSets_Optimal320() {
        Map<String, Integer> basket = Map.of(
                "Clean Code", 2,
                "The Clean Coder", 2,
                "Clean Architecture", 2,
                "TDD", 1,
                "Legacy Code", 1
        );

        assertEquals(320.0, service.calculatePrice(basket), 0.01);
    }

    @Test
    void testSameTitleMultipleCopies_noDiscount() {
        Map<String, Integer> basket = Map.of("Clean Code", 3);
        assertEquals(150.0, service.calculatePrice(basket), 0.01);
    }

    @Test
    void testThreeTitles_multipleCopies_mixedOptimal() {
        Map<String, Integer> basket = Map.of(
                "Clean Code", 2,
                "Clean Architecture", 1,
                "The Clean Coder", 2
        );

        // Best grouping: (3 books × 10%) + (2 books × 5%) = 135 + 95 = 230
        assertEquals(230.0, service.calculatePrice(basket), 0.01);
    }

    // ----------------------------------------------------------------------
    //  VALIDATION TESTS
    // ----------------------------------------------------------------------

    @Test
    void testNullBasket_throwsException() {
        assertThrows(InvalidBasketException.class, () -> service.calculatePrice(null));
    }

    @Test
    void testEmptyBasket_throwsException() {
        Map<String, Integer> basket = new HashMap<>();
        assertThrows(InvalidBasketException.class, () -> service.calculatePrice(basket));
    }

    @Test
    void testNullTitle_throwsException() {
        Map<String, Integer> basket = new HashMap<>();
        basket.put(null, 1);
        assertThrows(InvalidBasketException.class, () -> service.calculatePrice(basket));
    }

    @Test
    void testEmptyTitle_throwsException() {
        Map<String, Integer> basket = Map.of("  ", 1);
        assertThrows(InvalidBasketException.class, () -> service.calculatePrice(basket));
    }

    @Test
    void testNegativeQuantity_throwsException() {
        Map<String, Integer> basket = Map.of("Clean Code", -1);
        assertThrows(InvalidBasketException.class, () -> service.calculatePrice(basket));
    }

    @Test
    void testZeroCopiesOnly_throwsException() {
        Map<String, Integer> basket = Map.of(
                "Clean Code", 0,
                "The Clean Coder", 0
        );
        assertThrows(InvalidBasketException.class, () -> service.calculatePrice(basket));
    }

    @Test
    void testNullQuantity_throwsException() {
        Map<String, Integer> basket = new HashMap<>();
        basket.put("Clean Code", null);
        assertThrows(InvalidBasketException.class, () -> service.calculatePrice(basket));
    }

    // ----------------------------------------------------------------------
    //  EDGE CASE TESTS
    // ----------------------------------------------------------------------

    @Test
    void testLargeQuantitiesStillOptimal() {
        Map<String, Integer> basket = Map.of(
                "Clean Code", 5,
                "The Clean Coder", 5,
                "Clean Architecture", 5,
                "TDD", 5,
                "Legacy Code", 5
        );

        // 5 sets of 5 distinct: 5 × (50×5×0.75) = 5 × 187.5 = 937.5
        assertEquals(937.5, service.calculatePrice(basket), 0.01);
    }

    @Test
    void testOneBookZeroOnePositive() {
        Map<String, Integer> basket = Map.of(
                "Clean Code", 0,
                "TDD", 1
        );
        assertEquals(50.0, service.calculatePrice(basket), 0.01);
    }

    @Test
    void testSingleBookLargeCopies_noDiscount() {
        Map<String, Integer> basket = Map.of("Clean Code", 10);
        assertEquals(500.0, service.calculatePrice(basket), 0.01);
    }
}
