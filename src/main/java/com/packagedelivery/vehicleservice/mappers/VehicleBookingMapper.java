package com.packagedelivery.vehicleservice.mappers;

import com.packagedelivery.vehicledto.model.dto.vehicleReservation.VehicleReservationDetailsResponse;
import com.packagedelivery.vehicleservice.datamodels.VehicleReservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VehicleBookingMapper {

    List<VehicleReservationDetailsResponse> vehicleReservationsToVehicleReservationListResponse(List<VehicleReservation> reservations);

    @Mapping(target = "vehicleId", source = "vehicle.id")
    VehicleReservationDetailsResponse deliveryToDeliveriesDetailsResponse(VehicleReservation reservation);
}
