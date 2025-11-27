package com.bnpp.kata.bookdiscount.app.service;

import com.bnpp.kata.bookdiscount.app.exception.InvalidBasketException;
import com.bnpp.kata.bookdiscount.app.model.BookItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

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
    @DisplayName("Calculate price for a single book with no discount")
    void testSingleBook_noDiscount() {
        List<BookItem> items = List.of(
                new BookItem("Clean Code", 1)
        );
        assertEquals(50.0, service.calculatePrice(items), 0.01);
    }

    @Test
    @DisplayName("Apply 5% discount for two different books")
    void testTwoDifferentBooks_5PercentDiscount() {
        List<BookItem> items = List.of(
                new BookItem("Clean Code", 1),
                new BookItem("The Clean Coder", 1)
        );
        assertEquals(95.0, service.calculatePrice(items), 0.01);
    }

    @Test
    @DisplayName("Apply 10% discount for three different books")
    void testThreeDifferentBooks_10PercentDiscount() {
        List<BookItem> items = List.of(
                new BookItem("Clean Code", 1),
                new BookItem("The Clean Coder", 1),
                new BookItem("Clean Architecture", 1)
        );
        assertEquals(135.0, service.calculatePrice(items), 0.01);
    }

    @Test
    @DisplayName("Apply 20% discount for four different books")
    void testFourDifferentBooks_20PercentDiscount() {
        List<BookItem> items = List.of(
                new BookItem("Clean Code", 1),
                new BookItem("The Clean Coder", 1),
                new BookItem("Clean Architecture", 1),
                new BookItem("TDD", 1)
        );

        assertEquals(160.0, service.calculatePrice(items), 0.01);
    }

    @Test
    @DisplayName("Apply 25% discount for all five different books")
    void testAllFiveBooks_25PercentDiscount() {
        List<BookItem> items = List.of(
                new BookItem("Clean Code", 1),
                new BookItem("The Clean Coder", 1),
                new BookItem("Clean Architecture", 1),
                new BookItem("TDD", 1),
                new BookItem("Legacy Code", 1)
        );

        assertEquals(187.50, service.calculatePrice(items), 0.01);
    }

    // ----------------------------------------------------------------------
    //  COMPLEX PRICE-KATA SCENARIO
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Merge duplicate book titles ignoring case and sum their quantities")
    void testCaseInsensitiveMerging() {
        List<BookItem> items = List.of(
                new BookItem("clean code", 1),
                new BookItem("Clean Code", 2),
                new BookItem("CLEAN CODE", 3)
        );
        // Total = 1 + 2 + 3 = 6 copies of 1 book
        double price = service.calculatePrice(items);

        assertEquals(6 * 50.0, price, 0.01); // No discount because only 1 distinct title
    }

    @Test
    @DisplayName("Calculate optimal pricing for multi-set scenario resulting in 320 EUR")
    void testKataExample_multipleSets_Optimal320() {
        List<BookItem> items = List.of(
                new BookItem("Clean Code", 2),
                new BookItem("The Clean Coder", 2),
                new BookItem("Clean Architecture", 2),
                new BookItem("TDD", 1),
                new BookItem("Legacy Code", 1)
        );

        assertEquals(320.0, service.calculatePrice(items), 0.01);
    }

    @Test
    @DisplayName("Calculate price when multiple copies of a single title are purchased with no discount")
    void testSameTitleMultipleCopies_noDiscount() {
        List<BookItem> items = List.of(
                new BookItem("Clean Code", 3)
        );
        assertEquals(150.0, service.calculatePrice(items), 0.01);
    }

    @Test
    @DisplayName("Choose optimal grouping when some titles have multiple copies")
    void testThreeTitles_multipleCopies_mixedOptimal() {
        List<BookItem> items = List.of(
                new BookItem("Clean Code", 2),
                new BookItem("Clean Architecture", 1),
                new BookItem("The Clean Coder", 2)
        );
        assertEquals(230.0, service.calculatePrice(items), 0.01);
    }

    // ----------------------------------------------------------------------
    //  VALIDATION TESTS
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Throw exception when basket is null")
    void testNullBasket_throwsException() {
        assertThrows(InvalidBasketException.class, () -> service.calculatePrice(null));
    }

    @Test
    @DisplayName("Throw exception when basket has no entries")
    void testEmptyBasket_throwsException() {
        List<BookItem> items = List.of();
        assertThrows(InvalidBasketException.class, () -> service.calculatePrice(items));
    }

    @Test
    @DisplayName("Throw exception when book title is null")
    void testNullTitle_throwsException() {
        List<BookItem> items = List.of(
                new BookItem(null, 1)
        );
        assertThrows(InvalidBasketException.class, () -> service.calculatePrice(items));
    }

    @Test
    @DisplayName("Throw exception when book title is empty or blank")
    void testEmptyTitle_throwsException() {
        List<BookItem> items = List.of(
                new BookItem("   ", 1)
        );
        assertThrows(InvalidBasketException.class, () -> service.calculatePrice(items));
    }

    @Test
    @DisplayName("Throw exception when quantity is negative")
    void testNegativeQuantity_throwsException() {
        List<BookItem> items = List.of(
                new BookItem("Clean Code", -1)
        );
        assertThrows(InvalidBasketException.class, () -> service.calculatePrice(items));
    }

    @Test
    @DisplayName("Throw exception when all quantities are zero")
    void testZeroCopiesOnly_throwsException() {
        List<BookItem> items = List.of(
                new BookItem("Clean Code", 0),
                new BookItem("The Clean Coder", 0)
        );
        assertThrows(InvalidBasketException.class, () -> service.calculatePrice(items));
    }

    @Test
    @DisplayName("Throw exception when quantity is null")
    void testNullQuantity_throwsException() {
        List<BookItem> items = List.of(
                new BookItem("Clean Code", null)
        );
        assertThrows(InvalidBasketException.class, () -> service.calculatePrice(items));
    }

    // ----------------------------------------------------------------------
    //  EDGE CASE TESTS
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Calculate optimal price even for very large quantities")
    void testLargeQuantitiesStillOptimal() {
        List<BookItem> items = List.of(
                new BookItem("Clean Code", 5),
                new BookItem("The Clean Coder", 5),
                new BookItem("Clean Architecture", 5),
                new BookItem("TDD", 5),
                new BookItem("Legacy Code", 5)
        );
        assertEquals(937.5, service.calculatePrice(items), 0.01);
    }

    @Test
    @DisplayName("Handle case where one title has zero quantity but another has positive")
    void testOneBookZeroOnePositive() {
        List<BookItem> items = List.of(
                new BookItem("Clean Code", 0),
                new BookItem("TDD", 1)
        );
        assertEquals(50.0, service.calculatePrice(items), 0.01);
    }

    @Test
    @DisplayName("Calculate price for large quantity of a single title with no discount")
    void testSingleBookLargeCopies_noDiscount() {
        List<BookItem> items = List.of(
                new BookItem("Clean Code", 10)
        );
        assertEquals(500.0, service.calculatePrice(items), 0.01);
    }
}
