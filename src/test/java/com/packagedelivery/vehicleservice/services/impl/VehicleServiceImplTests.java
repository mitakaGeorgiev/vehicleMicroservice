package com.packagedelivery.vehicleservice.services.impl;

import com.packagedelivery.globalExceptionHandler.EntityNotFoundException;
import com.packagedelivery.vehicledto.enums.VehicleType;
import com.packagedelivery.vehicledto.model.dto.vehicles.*;
import com.packagedelivery.vehicleservice.datamodels.Vehicle;
import com.packagedelivery.vehicleservice.mappers.VehicleMapper;
import com.packagedelivery.vehicleservice.repositories.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
 class VehicleServiceImplTests {
    @InjectMocks
    private VehicleServiceImpl vehicleService;
    @Mock
    private VehicleRepository vehicleRepository;
    @Spy
    private VehicleMapper vehicleMapper = Mappers.getMapper(VehicleMapper.class);

    @BeforeEach
    public void setup() {
        this.vehicleRepository = Mockito.mock(VehicleRepository.class);
        this.vehicleService = new VehicleServiceImpl(this.vehicleRepository, vehicleMapper);
    }

    @Test
     void testRetrieveAllVehiclesSize_givenVehicleList_shouldReturnCorrectVehicleListSize() {
        Vehicle vehicle1 = createVehicle();
        Vehicle vehicle2 = createVehicle2();
        Mockito.when(this.vehicleRepository.findAll()).thenReturn(List.of(vehicle1, vehicle2));

        List<VehicleListResponse> vehicleResponseList = vehicleService.findAll();

        assertThat(vehicleResponseList).isNotNull();
        assertEquals(2, vehicleResponseList.size());
    }

    @Test
     void testRetrieveVehicles_withNoVehicles_shouldCallRepositoryFindAllMethod() {
        this.vehicleRepository.findAll();

        Mockito.verify(this.vehicleRepository, Mockito.times(1)).findAll();
    }

    @Test
     void testCreateVehicle_withPreviouslyCreatedVehicleCreateRequest_shouldCallRepositorySaveMethod() {
        // Arrange
        Mockito.when(this.vehicleRepository.save(any())).thenReturn(new Vehicle());

        // Act
        this.vehicleService.createVehicle(this.getRandomVehicleCreateRequest());

        // Assert
        Mockito.verify(this.vehicleRepository, Mockito.times(1)).save(any());
    }

    @Test
     void testCreateVehicle_withPreviouslyCreatedData_shouldCorrectlyMapDataFromRepository() {
        VehicleCreateRequest dummyRequest = this.getRandomVehicleCreateRequest();
        Vehicle vehicle = createVehicle(1L);
        Vehicle vehicle2 = createVehicle();
        Mockito.when(this.vehicleRepository.save(vehicle2)).thenReturn(vehicle);
        VehicleCreateResponse expectedResponse = createVehicleCreateResponse();

        VehicleCreateResponse actualResponse = this.vehicleService.createVehicle(dummyRequest);

        assertEquals(expectedResponse.getId(), actualResponse.getId(), "Id is not the same");
        assertEquals(expectedResponse.getCapacity(), actualResponse.getCapacity(), "Capacity is not the same");
        assertEquals(expectedResponse.getVehicleType().toString(), actualResponse.getVehicleType().toString(), "Vehicle type is not the same");
    }

    @Test
     void testRetrieveVehicle_withCreatedId_shouldThrowEntityNotFoundException() {
        Long testId = 1L;

        Mockito.when(this.vehicleRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vehicleService.retrieveSingle(testId));
    }

    @Test
    void testRetrieveDeletedVehicle_withPreviouslyCreatedVehicle_shouldThrowCorrectData() {
        Vehicle vehicle = createVehicle();
        Mockito.when(this.vehicleRepository.findById(1L)).thenReturn(Optional.ofNullable(vehicle));
        Mockito.when(this.vehicleRepository.saveAndFlush(vehicle)).thenReturn(vehicle);

        this.vehicleService.deleteVehicle(1L);

        assertEquals(vehicle,vehicleRepository.saveAndFlush(vehicle));
        Mockito.verify(this.vehicleRepository, Mockito.times(1)).saveAndFlush(vehicle);

    }
    @Test
     void testRetrieveSingleVehicle_withPreviouslyCreatedVehicle_shouldThrowCorrectData(){
        Long id=1L;
        Vehicle vehicle=createVehicle(1L);
        Mockito.when(this.vehicleRepository.findById(id)).thenReturn(Optional.ofNullable(vehicle));

        VehicleDetailsResponse actualDetailsResponse =this.vehicleService.retrieveSingle(1L);

        assertEquals(vehicle.getId(), actualDetailsResponse.getId(), "Id is not the same");
        assertEquals(vehicle.getCapacity(), actualDetailsResponse.getCapacity(), "Capacity is not the same");
        assertEquals(vehicle.getVehicleType().toString(), actualDetailsResponse.getVehicleType().toString(), "Vehicle type is not the same");
        Mockito.verify(this.vehicleRepository, Mockito.times(1)).findById(id);
    }

    @Test
    void testUpdateVehicle_withPreviouslyCreatedVehicle_shouldThrowCorrectData(){
        Long id=1L;
        Vehicle vehicle=createVehicle(id);
        VehicleUpdateRequest vehicleUpdateRequest = getVehicleUpdateRequest();
        Vehicle vehicle2=createVehicle(id);
        vehicle2.setCapacity(vehicleUpdateRequest.getCapacity());
        vehicle2.setVehicleType(vehicleUpdateRequest.getVehicleType());
        Mockito.when(this.vehicleRepository.findById(id)).thenReturn(Optional.ofNullable(vehicle2));

        VehicleUpdateResponse actualVehicleResponse=vehicleService.updateVehicle(id,vehicleUpdateRequest);

        assertEquals(actualVehicleResponse.getId(), id, "Id is not the same");
        assertEquals(actualVehicleResponse.getCapacity(), vehicleUpdateRequest.getCapacity(), "Capacity is not the same");
        assertEquals(actualVehicleResponse.getVehicleType().toString(), vehicleUpdateRequest.getVehicleType().toString(), "Vehicle type is not the same");
    }

    private static VehicleUpdateRequest getVehicleUpdateRequest() {
        VehicleUpdateRequest vehicleUpdateRequest=new VehicleUpdateRequest();
        vehicleUpdateRequest.setCapacity(13);
        vehicleUpdateRequest.setVehicleType(VehicleType.BUS);
        return vehicleUpdateRequest;
    }


     private static VehicleCreateResponse createVehicleCreateResponse() {
        VehicleCreateResponse expectedResponse = new VehicleCreateResponse();
        expectedResponse.setId(1L);
        expectedResponse.setCapacity(12);
        expectedResponse.setVehicleType("BUS");
        return expectedResponse;
    }

    private static Vehicle createVehicle() {
        Vehicle vehicle2 = new Vehicle();
        vehicle2.setCapacity(12);
        vehicle2.setVehicleType(VehicleType.BUS);
        return vehicle2;
    }

    private static Vehicle createVehicle2() {
        Vehicle vehicle2 = new Vehicle();
        vehicle2.setCapacity(24);
        vehicle2.setVehicleType(VehicleType.TRUCK);
        return vehicle2;
    }

    private static Vehicle createVehicle(Long id) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(id);
        vehicle.setCapacity(12);
        vehicle.setVehicleType(VehicleType.BUS);
        return vehicle;
    }

    private VehicleCreateRequest getRandomVehicleCreateRequest() {
        VehicleCreateRequest vehicleCreateRequest = new VehicleCreateRequest();
        vehicleCreateRequest.setCapacity(12);
        vehicleCreateRequest.setVehicleType(VehicleType.BUS);
        return vehicleCreateRequest;
    }
}
