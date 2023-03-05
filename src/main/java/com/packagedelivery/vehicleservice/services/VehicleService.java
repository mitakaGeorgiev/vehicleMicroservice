package com.packagedelivery.vehicleservice.services;


import com.packagedelivery.vehicledto.model.dto.vehicles.*;
import com.packagedelivery.vehicleservice.datamodels.Vehicle;
import java.util.List;

public interface VehicleService {
    List<VehicleListResponse> findAll();

    VehicleDetailsResponse retrieveSingle(long id);

    VehicleCreateResponse createVehicle(VehicleCreateRequest vehicleCreateRequest);

    VehicleUpdateResponse updateVehicle(long id, VehicleUpdateRequest vehicleUpdateRequest);

    Vehicle deleteVehicle(long id);
}
