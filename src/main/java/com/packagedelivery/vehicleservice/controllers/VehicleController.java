package com.packagedelivery.vehicleservice.controllers;

import com.packagedelivery.vehicledto.model.dto.vehicles.*;
import com.packagedelivery.vehicleservice.logging.annotations.InfoLog;
import com.packagedelivery.vehicleservice.services.VehicleService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    @InfoLog("[Vehicle Controller] - Retrieve All Vehicles")
    public ResponseEntity<Iterable<VehicleListResponse>> retrieveAll() {
        return new ResponseEntity<>(vehicleService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @InfoLog("[Vehicle Controller] - Retrieve Single Vehicles")
    public ResponseEntity<VehicleDetailsResponse> retrieveSingle(@PathVariable Long id) {
        return new ResponseEntity<>(vehicleService.retrieveSingle(id), HttpStatus.OK);
    }

    @PostMapping
    @InfoLog("[Vehicle Controller] - Create Vehicle")
    public ResponseEntity<VehicleCreateResponse> createVehicle(@Valid @RequestBody VehicleCreateRequest vehicleCreateRequest) {
        return new ResponseEntity<>(vehicleService.createVehicle(vehicleCreateRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @InfoLog("[Vehicle Controller] - Update Vehicle")
    public ResponseEntity<VehicleUpdateResponse> updateVehicle(@PathVariable Long id, @Valid @RequestBody VehicleUpdateRequest vehicleUpdateRequest) {
        return new ResponseEntity<>(vehicleService.updateVehicle(id, vehicleUpdateRequest), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @InfoLog("[Vehicle Controller] - Delete Vehicle")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
