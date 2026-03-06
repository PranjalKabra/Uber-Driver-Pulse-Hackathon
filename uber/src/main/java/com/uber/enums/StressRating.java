package com.uber.enums;

public enum StressRating {
    LOW(0, 30),
    MEDIUM(31, 60),
    HIGH(61, 85),
    CRITICAL(86, 100);

    private final int minScore;
    private final int maxScore;

    StressRating(int minScore, int maxScore) {
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    public static StressRating fromScore(double score) {
        for (StressRating rating : values()) {
            if (score >= rating.minScore && score <= rating.maxScore) {
                return rating;
            }
        }
        return CRITICAL;
    }

    public int getMinScore() { return minScore; }
    public int getMaxScore() { return maxScore; }
}
