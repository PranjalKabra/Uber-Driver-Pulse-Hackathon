package com.uber.service;

import com.uber.models.Driver;
import com.uber.models.Shift;
import com.uber.repository.DriverRepository;
import java.time.LocalTime;

public class ShiftService {

    private final DriverRepository driverRepo;

    public ShiftService(DriverRepository driverRepo) {
        this.driverRepo = driverRepo;
    }

    public Shift startShift(Driver driver, LocalTime startTime, LocalTime endTime) {
        if (driver.getCurrentShift() != null && driver.getCurrentShift().isActive()) {
            throw new IllegalStateException("Driver " + driver.getName() + " already has an active shift.");
        }
        Shift shift = new Shift(driver.getId(), startTime, endTime);
        shift.activate();
        driver.setCurrentShift(shift);
        driverRepo.save(driver);
        System.out.println("[ShiftService] Shift started for " + driver.getName()
                + " | " + startTime + " → " + endTime);
        return shift;
    }

    public void endShift(Driver driver) {
        Shift shift = driver.getCurrentShift();
        if (shift == null || !shift.isActive()) {
            throw new IllegalStateException("No active shift found for " + driver.getName());
        }
        shift.end();
        driverRepo.save(driver);
        System.out.printf("[ShiftService] Shift ended for %s | Hours worked: %.2f%n",
                driver.getName(), shift.getHoursWorked());
    }

    public boolean isWithinShift(Driver driver) {
        Shift shift = driver.getCurrentShift();
        if (shift == null || !shift.isActive()) return false;
        LocalTime now = LocalTime.now();
        return !now.isBefore(shift.getStartTime()) && !now.isAfter(shift.getEndTime());
    }

    public double getHoursRemaining(Driver driver) {
        Shift shift = driver.getCurrentShift();
        if (shift == null) return 0.0;
        return shift.getHoursRemaining();
    }
}
