package com.packagedelivery.vehicleservice.datamodels.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class CustomizedVehicleReservationDto {
    private Long vehicleId;
    private LocalDate bookingDate;

}
