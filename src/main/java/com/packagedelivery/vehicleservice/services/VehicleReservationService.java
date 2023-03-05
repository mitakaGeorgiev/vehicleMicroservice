package com.packagedelivery.vehicleservice.services;

import com.packagedelivery.vehicledto.model.dto.vehicleReservation.VehicleReservationDetailsResponse;
import com.packagedelivery.vehicledto.model.dto.vehicleReservation.VehicleReservationGenericRequest;
import com.packagedelivery.vehicleservice.datamodels.VehicleReservationId;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

public interface VehicleReservationService {

    List<VehicleReservationDetailsResponse> findAll();

    VehicleReservationDetailsResponse retrieveSingle(VehicleReservationId bookingId);

    VehicleReservationDetailsResponse createReservation(VehicleReservationGenericRequest booking, HttpServletRequest httpRequest);

    VehicleReservationDetailsResponse updateReservation(VehicleReservationGenericRequest booking,HttpServletRequest httpRequest);

    VehicleReservationId deleteReservation(VehicleReservationId bookingId);

    List<VehicleReservationDetailsResponse> findByReservationDate(LocalDate date);

}