package com.bnpp.kata.bookdiscount.app.service;

import com.bnpp.kata.bookdiscount.app.exception.InvalidBasketException;
import com.bnpp.kata.bookdiscount.app.model.BookItem;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class BookPriceService {

    private static final Map<Integer, Double> DISCOUNTS = Map.of(
            1, 0.00,
            2, 0.05,
            3, 0.10,
            4, 0.20,
            5, 0.25
    );
    private static final double BOOK_PRICE = 50.0;

    /*
     * Calculate Book Price as per basket.
     */
    public double calculatePrice(List<BookItem> bookItemList) {
        // Step 1: Validate raw basket
        this.validateBasket(bookItemList);

        // Step 2: Normalize titles (case-insensitive) + merge duplicates
        Map<String, Integer> mergedBookQuantity = this.mergeDuplicates(bookItemList);

        // Step 3: Extract sorted quantities of positive integers
        List<Integer> sortedCounts = this.getSortedList(mergedBookQuantity);

        if (sortedCounts.isEmpty()) {
            throw new InvalidBasketException("Basket must contain at least one book with quantity > 0");
        }

        Map<String, Double> cacheResult = new HashMap<>();
        return this.calculateOptimalPrice(sortedCounts, cacheResult);
    }

    /*
     * Validate the user input params.
     */
    private void validateBasket(List<BookItem> bookItemList){

        // Validate basket not null
        Optional.ofNullable(bookItemList)
                .orElseThrow(() -> new InvalidBasketException("Basket must not be null"));

        // Validate basket has at least one entry
        Optional.of(bookItemList)
                .filter( items -> !items.isEmpty())
                .orElseThrow(() -> new InvalidBasketException("Basket must contain at least one entry"));

        // Validate each BookItem object
        bookItemList.stream()
                .forEach(item -> {
                    // ---- Validate Title ----
                    String title = Optional.ofNullable(item.title())
                            .map(String::trim)
                            .filter(bookTitle -> !bookTitle.isEmpty())
                            .orElseThrow(() ->
                                    new InvalidBasketException("Book title must not be null or empty")
                            );
                    // ---- Validate Quantity Exists ----
                    Integer qty = Optional.ofNullable(item.quantity())
                            .orElseThrow(() ->
                                    new InvalidBasketException("Quantity for book '%s' must not be null".formatted(title))
                            );
                    // ---- Validate Quantity Non-negative ----
                    Optional.of(qty)
                            .filter(quantity -> quantity >= 0)
                            .orElseThrow(() ->
                                    new InvalidBasketException("Quantity for book '%s' must not be negative"
                                            .formatted(title))
                            );
                });

        // ---- Validate: at least one quantity > 0 ----
        bookItemList.stream()
                .map(BookItem::quantity)
                .filter(Objects::nonNull)
                .filter(q -> q > 0)
                .findAny()
                .orElseThrow(() ->
                        new InvalidBasketException("Basket must contain at least one book with quantity > 0")
                );
    }

    private Map<String, Integer> mergeDuplicates(List<BookItem> bookItemList){
        return bookItemList.stream()
                .collect(Collectors.toMap(
                        bookItemObj -> bookItemObj.title().trim().toLowerCase(),   // normalized title
                        BookItem::quantity,
                        Integer::sum         // merge duplicates items.
                ));
    }

    private List<Integer> getSortedList(Map<String, Integer> mergedBookQuantity){
        return mergedBookQuantity.values().stream()
                .filter(quantity -> quantity != null && quantity > 0)
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    /*
     * Algo developed for calculation discount price.
     */
    private double calculateOptimalPrice(List<Integer> bookCounts, Map<String, Double> cacheResult) {
        // ------------------------------------------------------------
        // Step 1: Normalize the state → remove zero-count books and sort descending
        // ------------------------------------------------------------
        var normalizedState = bookCounts.stream()
                .filter(count -> count > 0)
                .sorted(Comparator.reverseOrder())
                .toList();

        // If no books left → price is zero
        if (normalizedState.isEmpty()) {
            return 0.0;
        }
        // ------------------------------------------------------------
        // Step 2: If this exact combination of book counts was already
        //         solved, return the cached result.
        // ------------------------------------------------------------
        var cacheKey = normalizedState.toString();

        var cachedResult = Optional.ofNullable(cacheResult.get(cacheKey));
        if (cachedResult.isPresent()) {
            return cachedResult.get();
        }
        // ------------------------------------------------------------
        // Step 3: Compute the optimal (minimum) price by trying all
        //         possible distinct-group sizes.
        // ------------------------------------------------------------
        int distinctTitleCount = normalizedState.size();
        // Try forming groups of size 1 to N (N = number of distinct titles)
        var bestPrice = IntStream.rangeClosed(1, distinctTitleCount)
                .mapToDouble(groupSize -> {
                    // -- Step 3a: Compute new state after taking 1 copy
                    //  from the 'groupSize' most abundant books.
                    var updatedState = IntStream.range(0, distinctTitleCount)
                            .map(idx -> idx < groupSize
                                    ? normalizedState.get(idx) - 1
                                    : normalizedState.get(idx))
                            .boxed()
                            .toList();

                    // -- Step 3b: Compute group cost using predefined discount rules
                    var discount = DISCOUNTS.getOrDefault(groupSize, 0.0);
                    var groupCost = groupSize * BOOK_PRICE * (1 - discount);

                    // -- Step 3c: Recursively compute remaining price
                    var remainingCost = calculateOptimalPrice(updatedState, cacheResult);

                    return groupCost + remainingCost;
                })
                // Step 4: find the minimum total price across all group sizes
                .min()
                .orElse(Double.MAX_VALUE);
        // ------------------------------------------------------------
        // Step 5: Cache and return result
        // ------------------------------------------------------------
        cacheResult.put(cacheKey, bestPrice);
        return bestPrice;
    }
}
