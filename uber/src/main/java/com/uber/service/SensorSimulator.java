package com.uber.service;

import com.uber.models.*;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class SensorSimulator {

    private final Random random = new Random();

    private static final double BASE_LAT = 28.6139;
    private static final double BASE_LNG = 77.2090;

    public SensorReading generateReading(String rideId) {
        return new SensorReading(rideId, generateAudio(), generateMotion());
    }

    public List<SensorReading> simulateFullRide(Ride ride, int minutes) {
        List<SensorReading> readings = new ArrayList<>();
        for (int i = 0; i < minutes; i += 2) {
            readings.add(generateReading(ride.getId()));
        }
        return readings;
    }

    private AudioData generateAudio() {
        double decibels         = 40 + random.nextDouble() * 70;
        double sustainedSeconds = random.nextDouble() * 20;
        return new AudioData(decibels, sustainedSeconds);
    }

    private MotionData generateMotion() {
        double speed = 10 + random.nextDouble() * 100;
        double acc_x = random.nextDouble() * 8;
        double acc_y = random.nextDouble() * 8;
        double acc_z = random.nextDouble() * 8;
        double lat   = BASE_LAT + (random.nextDouble() - 0.5) * 0.1;
        double lng   = BASE_LNG + (random.nextDouble() - 0.5) * 0.1;
        return new MotionData(speed, acc_x, acc_y, acc_z, lat, lng);
    }
}