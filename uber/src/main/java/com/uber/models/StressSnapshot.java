package com.uber.models;

import java.time.LocalDateTime;

public class StressSnapshot {

    private final LocalDateTime timestamp;
    private final double        audioScore;   // 0–1
    private final double        motionScore;  // 0–1
    private final double        combinedScore;// 0–1

    public StressSnapshot(LocalDateTime timestamp,
                          double audioScore,
                          double motionScore,
                          double combinedScore) {
        this.timestamp     = timestamp;
        this.audioScore    = audioScore;
        this.motionScore   = motionScore;
        this.combinedScore = combinedScore;
    }

    public String getLabel() {
        if (combinedScore <= 0.30)  return "Low";
        if (combinedScore <= 0.60)  return "Medium";
        if (combinedScore <= 0.85)  return "High";
        return "Critical";
    }

    public LocalDateTime getTimestamp()     { return timestamp; }
    public double        getAudioScore()    { return audioScore; }
    public double        getMotionScore()   { return motionScore; }
    public double        getCombinedScore() { return combinedScore; }

    @Override
    public String toString() {
        return String.format("StressSnapshot[time=%s, audio=%.2f, motion=%.2f, combined=%.2f (%s)]",
                timestamp, audioScore, motionScore, combinedScore, getLabel());
    }
}
