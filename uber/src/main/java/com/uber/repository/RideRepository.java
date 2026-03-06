package com.uber.repository;

import com.uber.enums.RideStatus;
import com.uber.models.Ride;
import java.util.*;
import java.util.stream.Collectors;

public class RideRepository {

    // Primary store: rideId → Ride
    private final Map<String, Ride>        store    = new HashMap<>();
    // Secondary index: driverId → List of rides (fast lookup)
    private final Map<String, List<Ride>>  byDriver = new HashMap<>();

    public void save(Ride ride) {
        store.put(ride.getId(), ride);
        byDriver.computeIfAbsent(ride.getDriver().getId(), k -> new ArrayList<>()).add(ride);
    }

    public Ride findById(String id) {
        return store.get(id);
    }

    public List<Ride> findByDriver(String driverId) {
        return byDriver.getOrDefault(driverId, Collections.emptyList());
    }

    public List<Ride> findByStatus(String driverId, RideStatus status) {
        return findByDriver(driverId).stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Ride> findAll() {
        return new ArrayList<>(store.values());
    }
}
