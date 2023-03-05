package com.packagedelivery.vehicleservice.datamodels;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
public class VehicleReservationId implements Serializable {

    private Long vehicle;
    private LocalDate bookingDate;

    public VehicleReservationId(Long vehicle, LocalDate bookingDate) {
        this.vehicle = vehicle;
        this.bookingDate = bookingDate;
    }

    public VehicleReservationId() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VehicleReservationId bookingId = (VehicleReservationId) o;
        return vehicle.equals(bookingId.vehicle) &&
                bookingDate.equals(bookingId.bookingDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicle, bookingDate);
    }

}
