package com.uber.enums;

public enum AudioRating {
    QUIET(0.00, 0.25),
    CONVERSATIONAL(0.25, 0.50),
    ARGUMENT(0.50, 0.75),
    VERY_LOUD(0.75, 1.00);

    private final double min;
    private final double max;

    AudioRating(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public static AudioRating from(double score) {
        for (AudioRating level : values()) {
            if (score < level.max) return level;
        }
        return VERY_LOUD; // clamp for score == 1.0
    }
}
