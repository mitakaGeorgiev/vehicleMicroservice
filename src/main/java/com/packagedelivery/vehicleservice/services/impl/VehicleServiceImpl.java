package com.packagedelivery.vehicleservice.services.impl;

import com.packagedelivery.globalExceptionHandler.EntityNotFoundException;
import com.packagedelivery.vehicledto.model.dto.vehicles.*;
import com.packagedelivery.vehicleservice.datamodels.Vehicle;
import com.packagedelivery.vehicleservice.logging.annotations.DebugLog;
import com.packagedelivery.vehicleservice.mappers.VehicleMapper;
import com.packagedelivery.vehicleservice.repositories.VehicleRepository;
import com.packagedelivery.vehicleservice.services.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    @Override
    @Cacheable(value = "vehicle_redis")
    @DebugLog("[Vehicle Service] - Retrieve All Vehicles")
    public List<VehicleListResponse> findAll() {

        return vehicleMapper.vehiclesToVehicleListResponse(this.vehicleRepository.findAll());
    }

    @Override
    @Cacheable(value = "vehicle_redis_single", key = "#id")
    @DebugLog("[Vehicle Service] - Retrieve Single Vehicles")
    public VehicleDetailsResponse retrieveSingle(long id) {
        Vehicle vehicle = this.vehicleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Vehicle not found"));
        return vehicleMapper.vehicleToVehicleDetailsResponse(vehicle);
    }

    @Override
    @CacheEvict(value = "vehicle_redis", allEntries = true)
    @DebugLog("[Vehicle Service] - Create Vehicle")
    public VehicleCreateResponse createVehicle(VehicleCreateRequest vehicleCreateRequest) {
        Vehicle vehicle = new Vehicle();

        vehicle.setCapacity(vehicleCreateRequest.getCapacity());

        vehicle.setVehicleType(vehicleCreateRequest.getVehicleType());

        vehicle = this.vehicleRepository.save(vehicle);

        return vehicleMapper.vehicleToVehicleCreateResponse(vehicle);
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "vehicle_redis", allEntries = true), @CacheEvict(value = "vehicle_redis_single", key = "#id")})
    @DebugLog("[Vehicle Service] - Update Vehicle")
    public VehicleUpdateResponse updateVehicle(long id, VehicleUpdateRequest vehicleUpdateRequest) {
        Vehicle vehicle = this.vehicleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Vehicle not found"));

        vehicle.setCapacity(vehicleUpdateRequest.getCapacity());
        vehicle.setVehicleType(vehicleUpdateRequest.getVehicleType());
        this.vehicleRepository.saveAndFlush(vehicle);

        return vehicleMapper.vehicleToVehicleUpdateResponse(vehicle);
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "vehicle_redis", allEntries = true), @CacheEvict(value = "vehicle_redis_single", key = "#id")})
    @DebugLog("[Vehicle Service] - Delete Vehicle")
    public Vehicle deleteVehicle(long id) {
        Vehicle vehicle = this.vehicleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Vehicle not found"));

        vehicleRepository.delete(vehicle);

        return vehicle;
    }
}

