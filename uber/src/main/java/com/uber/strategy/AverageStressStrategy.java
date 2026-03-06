package com.uber.strategy;

import com.uber.enums.StressRating;
import com.uber.models.StressSnapshot;
import java.util.List;

public class AverageStressStrategy implements StressRatingStrategy {

    @Override
    public StressRating calculate(List<StressSnapshot> snapshots) {
        if (snapshots == null || snapshots.isEmpty()) return StressRating.LOW;
        double avg = snapshots.stream()
                .mapToDouble(StressSnapshot::getCombinedScore)
                .average()
                .orElse(0.0);
        return StressRating.fromScore(avg);
    }

    @Override
    public String getName() { return "Average Stress Strategy"; }
}
