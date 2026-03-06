package com.uber.strategy;

import com.uber.enums.StressRating;
import com.uber.models.StressSnapshot;
import java.util.List;

public class PeakStressStrategy implements StressRatingStrategy {

    @Override
    public StressRating calculate(List<StressSnapshot> snapshots) {
        if (snapshots == null || snapshots.isEmpty()) return StressRating.LOW;
        double peak = snapshots.stream()
                .mapToDouble(StressSnapshot::getCombinedScore)
                .max()
                .orElse(0.0);
        return StressRating.fromScore(peak);
    }

    @Override
    public String getName() { return "Peak Stress Strategy"; }
}
