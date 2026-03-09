package com.uber.strategy;

import com.uber.enums.StressRating;
import com.uber.models.StressSnapshot;
import java.util.List;

public interface StressRatingStrategy {
    StressRating calculate(List<StressSnapshot> snapshots);
    String       getName();
}
