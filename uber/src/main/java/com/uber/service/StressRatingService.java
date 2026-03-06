package com.uber.service;

import com.uber.enums.StressRating;
import com.uber.models.Ride;
import com.uber.strategy.StressRatingStrategy;

public class StressRatingService {

    private StressRatingStrategy strategy;

    public StressRatingService(StressRatingStrategy strategy) {
        this.strategy = strategy;
    }

    // Rates a completed ride using the current strategy
    public StressRating rateRide(Ride ride) {
        if (ride.getStressSnapshots().isEmpty()) {
            System.out.println("[StressRatingService] No snapshots found for ride " + ride.getId()
                    + " — defaulting to LOW.");
            return StressRating.LOW;
        }
        StressRating rating = strategy.calculate(ride.getStressSnapshots());
        ride.setStressRating(rating);
        System.out.printf("[StressRatingService] Ride %s rated %s using [%s]%n",
                ride.getId(), rating, strategy.getName());
        return rating;
    }

    // Swap strategy at runtime — Strategy Pattern in action
    public void setStrategy(StressRatingStrategy strategy) {
        System.out.println("[StressRatingService] Strategy switched to: " + strategy.getName());
        this.strategy = strategy;
    }

    public StressRatingStrategy getStrategy() {
        return strategy;
    }
}
