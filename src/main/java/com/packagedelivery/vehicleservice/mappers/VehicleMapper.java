package com.packagedelivery.vehicleservice.mappers;


import com.packagedelivery.vehicledto.model.dto.vehicles.VehicleCreateResponse;
import com.packagedelivery.vehicledto.model.dto.vehicles.VehicleDetailsResponse;
import com.packagedelivery.vehicledto.model.dto.vehicles.VehicleListResponse;
import com.packagedelivery.vehicledto.model.dto.vehicles.VehicleUpdateResponse;
import com.packagedelivery.vehicleservice.datamodels.Vehicle;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VehicleMapper {
    VehicleDetailsResponse vehicleToVehicleDetailsResponse(Vehicle vehicle);

    List<VehicleListResponse> vehiclesToVehicleListResponse(List<Vehicle> vehicles);

    VehicleUpdateResponse vehicleToVehicleUpdateResponse(Vehicle vehicle);

    VehicleCreateResponse vehicleToVehicleCreateResponse(Vehicle vehicle);
}
