package com.bnpp.kata.bookdiscount.app.service;

import com.bnpp.kata.bookdiscount.app.exception.InvalidBasketException;
import com.bnpp.kata.bookdiscount.app.model.BookItem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.Objects;
import java.util.Comparator;
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

    /**
     * Main method orchestrating the entire price calculation:
     * 1. Validate user input
     * 2. Normalize + merge titles (case-insensitive)
     * 3. Prepare sorted quantity list
     * 4. Compute optimal discounted price.
     */
    public double calculatePrice(List<BookItem> items) {
        validateBasket(items);
        Map<String, Integer> merged = mergeDuplicateTitles(items);
        List<Integer> sortedCounts = extractSortedCounts(merged);
        if (sortedCounts.isEmpty()) {
            throw new InvalidBasketException("Basket must contain at least one book with quantity > 0");
        }
        return computeOptimalPrice(sortedCounts, new HashMap<>());
    }

    // =======================================================================
    //                            VALIDATION
    // =======================================================================

    /**
     * Validates user input for:
     * - Null list
     * - Empty list
     * - Invalid book title (null/blank)
     * - Invalid quantity (null/negative)
     * - Ensures at least one positive quantity
     */
    private void validateBasket(List<BookItem> items) {
        requireNonNullList(items);
        requireNonEmptyList(items);
        validateEachBookItem(items);
        ensureAtLeastOnePositiveQuantity(items);
    }

    private void requireNonNullList(List<BookItem> items) {
        Optional.ofNullable(items)
                .orElseThrow(() -> new InvalidBasketException("Basket must not be null"));
    }

    private void requireNonEmptyList(List<BookItem> items) {
        Optional.of(items)
                .filter(bookList -> !bookList.isEmpty())
                .orElseThrow(() -> new InvalidBasketException("Basket must contain at least one entry"));
    }

    private void validateEachBookItem(List<BookItem> items) {
        items.forEach(item -> {
            String title = Optional.ofNullable(item.title())
                    .map(String::trim)
                    .filter(bookTitle -> !bookTitle.isEmpty())
                    .orElseThrow(() ->
                            new InvalidBasketException("Book title must not be null or empty"));

            Integer qty = Optional.ofNullable(item.quantity())
                    .orElseThrow(() ->
                            new InvalidBasketException("Quantity for book '%s' must not be null".formatted(title)));

            Optional.of(qty)
                    .filter(quantity -> quantity >= 0)
                    .orElseThrow(() ->
                            new InvalidBasketException("Quantity for book '%s' must not be negative".formatted(title)));
        });
    }

    private void ensureAtLeastOnePositiveQuantity(List<BookItem> items) {
        items.stream()
                .map(BookItem::quantity)
                .filter(Objects::nonNull)
                .filter( count-> count > 0)
                .findFirst()
                .orElseThrow(() ->
                        new InvalidBasketException("Basket must contain at least one book with quantity > 0"));
    }

    // =======================================================================
    //                    MERGING + NORMALIZATION LAYER
    // =======================================================================

    /**
     * Merges duplicate titles ignoring case,
     * producing a Map<title(lowercase), totalQuantity>.
     */
    private Map<String, Integer> mergeDuplicateTitles(List<BookItem> items) {
        return items.stream()
                .collect(Collectors.toMap(
                        item -> normalizeTitle(item.title()),
                        BookItem::quantity,
                        Integer::sum
                ));
    }
    /*
     * Convert all input name as lower-case to avoid duplicate entry.
     */
    private String normalizeTitle(String title) {
        return title.trim().toLowerCase();
    }

    /**
     * Extracts quantities > 0 and sorts them descending.
     */
    private List<Integer> extractSortedCounts(Map<String, Integer> merged) {
        return merged.values().stream()
                .filter(quantity -> quantity != null && quantity > 0)
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    // =======================================================================
    //                          DYNAMIC PROGRAMMING (DP)
    // =======================================================================

    /**
     * Recursively computes the minimum possible total price by considering:
     * - All possible groups of distinct books (1–5 titles)
     * - Associated discounts
     * - Remaining book counts after forming each group
     * Memoization avoids re-solving duplicate subproblems.
     */
    private double computeOptimalPrice(List<Integer> bookCounts, Map<String, Double> cache) {
        List<Integer> normalized = normalizeCounts(bookCounts);
        if (normalized.isEmpty()) {
            return 0.0;
        }
        String key = normalized.toString();
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        double bestPrice = tryAllGroupSizes(normalized, cache);
        cache.put(key, bestPrice);
        return bestPrice;
    }

    private List<Integer> normalizeCounts(List<Integer> counts) {
        return counts.stream()
                .filter(count -> count > 0)
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    /**
     * Tries group sizes from 1 to N distinct titles and
     * returns the cheapest combination.
     */
    private double tryAllGroupSizes(List<Integer> state, Map<String, Double> cache) {
        int maxGroupSize = state.size();
        return IntStream.rangeClosed(1, maxGroupSize)
                .mapToDouble(size -> computeCostForGroup(size, state, cache))
                .min()
                .orElse(Double.MAX_VALUE);
    }

    /**
     * Computes:
     * - New state after forming a group of size 'groupSize'
     * - Cost of that group based on discount rules
     * - Total cost = group cost + recursive cost of remaining books
     */
    private double computeCostForGroup(int groupSize, List<Integer> state, Map<String, Double> cache) {
        List<Integer> newState = applyGroupSelection(state, groupSize);
        double discount = DISCOUNTS.getOrDefault(groupSize, 0.0);
        double groupCost = groupSize * BOOK_PRICE * (1 - discount);
        double recursiveCost = computeOptimalPrice(newState, cache);
        return groupCost + recursiveCost;
    }

    /**
     * Reduces count of the first 'groupSize' titles by 1,
     * producing the next DP state.
     */
    private List<Integer> applyGroupSelection(List<Integer> state, int groupSize) {
        return IntStream.range(0, state.size())
                .map(i -> i < groupSize ? state.get(i) - 1 : state.get(i))
                .boxed()
                .toList();
    }
}

