package com.packagedelivery.vehicleservice.repositories;

import com.packagedelivery.vehicleservice.datamodels.VehicleReservation;
import com.packagedelivery.vehicleservice.datamodels.VehicleReservationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface VehicleReservationRepository extends JpaRepository<VehicleReservation, VehicleReservationId> {
    Iterable<VehicleReservation> findByBookingDate(LocalDate date);
}
