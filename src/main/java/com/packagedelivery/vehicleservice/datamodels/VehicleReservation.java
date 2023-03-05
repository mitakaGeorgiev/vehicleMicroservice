package com.packagedelivery.vehicleservice.datamodels;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "bookings", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "userId", "bookingDate" })
})
@IdClass(VehicleReservationId.class)
public class VehicleReservation implements Serializable {

    @Id
    @ManyToOne
    private Vehicle vehicle;
    @Id
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate bookingDate;
    private Long userId;

}
