package com.uber.service;

import com.uber.enums.RideStatus;
import com.uber.models.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

public class CsvLogger {

    private static final String EARNING_LOG  = "earning_velocity_log.csv";
    private static final String AUDIO_LOG    = "audio_sensor_log.csv";
    private static final String MOTION_LOG   = "motion_sensor_log.csv";
    private static final String RIDE_LOG     = "ride_summary_log.csv";
    private static final String FLAGGED_LOG  = "flagged_moments.csv";

    public CsvLogger() {
        // Write headers fresh on each run
        write(EARNING_LOG, "log_id,driver_id,date,timestamp,current_earnings,elapsed_hours,"
                + "curr_velocity,required_velocity,velocity_delta,trips_completed,forecast_status", false);
        write(AUDIO_LOG,   "log_id,ride_id,driver_id,timestamp,decibels,sustained_seconds,"
                + "audio_score,audio_level,is_flagged", false);
        write(MOTION_LOG,  "log_id,ride_id,driver_id,timestamp,speed,acceleration,latitude,longitude,"
                + "motion_score,motion_level,is_flagged", false);
        write(RIDE_LOG,    "ride_id,driver_id,duration,distance,start_location,end_location,fare,"
                + "motion_flag_count,audio_flag_count,flagged_moment_count,stress_rating,stress_rating_label", false);
        write(FLAGGED_LOG, "flag_id,trip_id,driver_id,timestamp,elapsed_seconds,motion_score,"
                + "motion_rating,audio_score,audio_rating,stress_score,stress_rating,explanation", false);
    }

    public void logEarningVelocity(EarningVelocity ev, Driver driver, Shift shift) {
        String row = String.join(",",
                uid(),
                driver.getId(),
                LocalDate.now().toString(),
                ev.getTimestamp().toString(),
                fmt(driver.getEarningGoal().getCurrentEarned()),    // from EarningGoal
                fmt(shift.getHoursWorked()),                        // from Shift
                fmt(ev.getCurrentVelocity()),
                fmt(ev.getRequiredVelocity()),
                fmt(ev.getVelocityDelta()),
                String.valueOf(driver.getRidesByStatus(RideStatus.COMPLETED).size()), // from Driver
                ev.getPaceStatus().toString()
        );
        write(EARNING_LOG, row, true);
    }

    public void logAudioReading(SensorReading reading, StressSnapshot snapshot, String driverId) {
        AudioData a = reading.getAudioData();
        String row = String.join(",",
                uid(),
                reading.getRideId(),
                driverId,
                reading.getTimestamp().toString(),
                fmt(a.getDecibels()),
                fmt(a.getSustainedSeconds()),
                fmt(snapshot.getAudioScore()),
                snapshot.getAudioLevel().toString(),
                String.valueOf(snapshot.isAudioFlagged())
        );
        write(AUDIO_LOG, row, true);
    }

    public void logMotionReading(SensorReading reading, StressSnapshot snapshot, String driverId) {
        MotionData m = reading.getMotionData();
        String row = String.join(",",
                uid(),
                reading.getRideId(),
                driverId,
                reading.getTimestamp().toString(),
                fmt(m.getSpeed()),
                fmt(m.getAcceleration()),
                fmt(m.getLatitude()),
                fmt(m.getLongitude()),
                fmt(snapshot.getMotionScore()),
                snapshot.getMotionLevel().toString(),
                String.valueOf(snapshot.isMotionFlagged())
        );
        write(MOTION_LOG, row, true);
    }

    public void logRideSummary(Ride ride) {
        RideRequest req = ride.getRequest();
        String row = String.join(",",
                ride.getId(),
                ride.getDriver().getId(),
                String.valueOf(ride.getDuration()),
                fmt(req.getEstimatedDistance()),
                req.getPickupLocation().getLabel(),
                req.getDropLocation().getLabel(),
                fmt(ride.getActualFare()),
                String.valueOf(ride.getMotionFlagCount()),
                String.valueOf(ride.getAudioFlagCount()),
                String.valueOf(ride.getTotalFlagCount()),
                ride.getStressRating() != null ? ride.getStressRating().toString() : "N/A",
                ride.getStressRating() != null ? ride.getStressRating().toString() : "N/A"
        );
        write(RIDE_LOG, row, true);
    }

    public void logFlaggedMoment(StressSnapshot snapshot, SensorReading reading, Ride ride) {
        if (!snapshot.isAudioFlagged() && !snapshot.isMotionFlagged()) return;
        long elapsedSeconds = Duration.between(ride.getStartTime(), reading.getTimestamp()).toSeconds();
        String row = String.join(",",
                uid(),
                ride.getId(),
                ride.getDriver().getId(),
                reading.getTimestamp().toString(),
                String.valueOf(elapsedSeconds),
                fmt(snapshot.getMotionScore()),
                snapshot.getMotionLevel().toString(),
                fmt(snapshot.getAudioScore()),
                snapshot.getAudioLevel().toString(),
                fmt(snapshot.getCombinedScore()),
                snapshot.getCombinedLevel().toString(),
                "\"" + buildExplanation(snapshot, reading) + "\""
        );
        write(FLAGGED_LOG, row, true);
    }

    // ── Private helpers ───────────────────────────────────────────────

    private String buildExplanation(StressSnapshot snapshot, SensorReading reading) {
        MotionData m = reading.getMotionData();
        AudioData  a = reading.getAudioData();
        boolean audioFlagged  = snapshot.isAudioFlagged();
        boolean motionFlagged = snapshot.isMotionFlagged();

        String motionDesc = m.getAcceleration() > 4.0
                ? String.format("Harsh braking/acceleration detected (%.1f m/s²)", m.getAcceleration())
                : String.format("High speed detected (%.1f km/h)", m.getSpeed());

        String audioDesc = String.format("Sustained high audio (%.1f dB) for %.0fs",
                a.getDecibels(), a.getSustainedSeconds());

        if (audioFlagged && motionFlagged) {
            return "Combined signal: " + motionDesc + " + " + audioDesc + ". Strong conflict indicator.";
        } else if (motionFlagged) {
            return motionDesc + ". Motion disturbance detected.";
        } else {
            return audioDesc + ". Audio disturbance detected.";
        }
    }

    private void write(String filename, String line, boolean append) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename, append))) {
            pw.println(line);
        } catch (IOException e) {
            System.err.println("[CsvLogger] Failed to write to " + filename + ": " + e.getMessage());
        }
    }

    private String fmt(double val) {
        return String.format("%.4f", val);
    }

    private String uid() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}