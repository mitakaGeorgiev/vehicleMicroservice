package com.packagedelivery.vehicleservice.controllers;

import com.packagedelivery.vehicledto.model.dto.vehicleReservation.VehicleReservationDetailsResponse;
import com.packagedelivery.vehicledto.model.dto.vehicleReservation.VehicleReservationGenericRequest;
import com.packagedelivery.vehicleservice.datamodels.VehicleReservationId;
import com.packagedelivery.vehicleservice.datamodels.dto.CustomizedVehicleReservationDto;
import com.packagedelivery.vehicleservice.services.VehicleReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class VehicleReservationController {

    final VehicleReservationService bookingService;

    @GetMapping
    public ResponseEntity<Iterable<VehicleReservationDetailsResponse>> retrieveAll() {
        return new ResponseEntity<>(bookingService.findAll(), HttpStatus.OK);
    }

    @PostMapping("/reservation")
    public ResponseEntity<VehicleReservationDetailsResponse> retrieveSingle( CustomizedVehicleReservationDto customizedVehicleReservationDto) {
        return new ResponseEntity<>(bookingService.retrieveSingle(new VehicleReservationId(customizedVehicleReservationDto.getVehicleId(), customizedVehicleReservationDto.getBookingDate())), HttpStatus.OK);
    }

    @GetMapping("/{date}")
    public ResponseEntity<Iterable<VehicleReservationDetailsResponse>> findByBookingDate(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return new ResponseEntity<>(bookingService.findByReservationDate(date), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<VehicleReservationDetailsResponse> createReservation(@Valid @RequestBody VehicleReservationGenericRequest reservation, HttpServletRequest httpRequest) {
        return new ResponseEntity<>(this.bookingService.createReservation(reservation, httpRequest), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<VehicleReservationDetailsResponse> updateReservation(@Valid @RequestBody VehicleReservationGenericRequest reservation, HttpServletRequest httpRequest) {
        return new ResponseEntity<>(this.bookingService.updateReservation(reservation, httpRequest), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteReservation(@RequestBody VehicleReservationId bookingId) {
        return new ResponseEntity<>(this.bookingService.deleteReservation(bookingId), HttpStatus.OK);
    }

}
