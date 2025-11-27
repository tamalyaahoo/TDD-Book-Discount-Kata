package com.bnpp.kata.bookdiscount.app.service;

import com.bnpp.kata.bookdiscount.app.exception.InvalidBasketException;
import org.springframework.stereotype.Service;

import java.util.*;

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
    public double calculatePrice(Map<String, Integer> basket) {
        //Validate the Book Basket.
        this.validateBasket(basket);

        // Convert counts to a sorted list of positive integers
        List<Integer> counts = basket.values().stream()
                .filter(q -> q != null && q > 0)
                .sorted(Comparator.reverseOrder())
                .toList();

        if (counts.isEmpty()) {
            throw new InvalidBasketException("Basket must contain at least one book with quantity > 0");
        }
        Map<String, Double> cache = new HashMap<>();
        return this.calculateOptimalPrice(counts, cache);
    }

    private void validateBasket(Map<String, Integer> basket){
        if(basket== null || basket.isEmpty()){
            throw new InvalidBasketException("Backet must have some item(s)");
        }
        //Validate the Book quantities.
        basket.forEach((title, qty) -> {
            if (title == null || title.isBlank()) {
                throw new InvalidBasketException("Book title must not be null or empty");
            }
            if (qty == null) {
                throw new InvalidBasketException("Quantity for book '" + title + "' must not be null");
            }
            if (qty < 0) {
                throw new InvalidBasketException("Quantity for book '" + title + "' must not be negative");
            }
        });
    }

    /*
     *
     */
    private double calculateOptimalPrice(List<Integer> counts, Map<String, Double> cache){
            //Remove books with zero copies (no need to track them).
            List<Integer> nonZero = counts.stream()
                    .filter(c -> c > 0)
                    .sorted(Comparator.reverseOrder())
                    .toList();
            //If we have no books left, the price = 0.
            if (nonZero.isEmpty()) {
                return 0.0;
            }
            //If solved this exact combination of remaining books → return stored answer.
            String key = nonZero.toString();
            if (cache.containsKey(key)) {
                return cache.get(key);
            }

            int listSize = nonZero.size();
            double best = Double.MAX_VALUE;
            //Form a group of exactly groupSize of distinct books.
            for (int groupSize = 1; groupSize <= listSize; groupSize++) {
                //Create new state after picking groupSize distinct books
                List<Integer> newState = new ArrayList<>(nonZero);
                for (int i = 0; i < groupSize; i++) {
                    newState.set(i, newState.get(i) - 1);
                }
                //Compute price of the selected group
                double discount = DISCOUNTS.getOrDefault(groupSize, 0.0);
                double groupPrice = groupSize * BOOK_PRICE * (1 - discount);
                //Recurring for remaining books
                double totalPrice = groupPrice + calculateOptimalPrice(newState, cache);
                //Track minimum price
                if (totalPrice < best) {
                    best = totalPrice;
                }
            }
            //Store into cache + return, Caches results to avoid recomputation.
            cache.put(key, best);
            return best;
        }
}
