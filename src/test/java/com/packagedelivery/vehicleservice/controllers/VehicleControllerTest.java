package com.packagedelivery.vehicleservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.packagedelivery.vehicledto.enums.VehicleType;
import com.packagedelivery.vehicledto.model.dto.vehicles.VehicleCreateRequest;
import com.packagedelivery.vehicledto.model.dto.vehicles.VehicleUpdateRequest;
import com.packagedelivery.vehicleservice.datamodels.Vehicle;
import com.packagedelivery.vehicleservice.repositories.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureTestDatabase
@Transactional
class VehicleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VehicleRepository vehicleRepository;

    @BeforeEach
    public void setup() {

    }
    @Test
    void testRequest_WithRandomData_ShouldReturnCorrectResponse() throws Exception {
        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType("application/json")
                        .content(this.objectMapper.writeValueAsString(getVehicleCreateRequest())))
                .andExpect(status().is(201));
    }

    @Test
    void testCreateVehicle_WithRandomData_ShouldReturnCorrectResponse() throws Exception {
        VehicleCreateRequest vehicleCreateRequest = getCreateRequest(32, VehicleType.BUS);

        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType("application/json")
                        .content(this.objectMapper.writeValueAsString(vehicleCreateRequest)))
                .andExpect(status().is(201));

        Vehicle vehicle = vehicleRepository.findAll().stream().findFirst().orElse(null);
        assertEquals(32, vehicle.getCapacity());
    }

    @Test
    void testRetrieveAllVehicles_WithRandomData_ShouldReturnCorrectResponse() throws Exception {
        VehicleCreateRequest vehicleCreateRequest = getCreateRequest(32, VehicleType.BUS);
        VehicleCreateRequest vehicleCreateRequest2 = getCreateRequest(35, VehicleType.TRUCK);

        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType("application/json")
                        .content(this.objectMapper.writeValueAsString(vehicleCreateRequest)))
                .andExpect(status().is(201));
        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType("application/json")
                        .content(this.objectMapper.writeValueAsString(vehicleCreateRequest2)))
                .andExpect(status().is(201));
        mockMvc.perform(get("/api/v1/vehicles"))
                .andExpect(status().isOk());

        Vehicle vehicle = vehicleRepository.findAll().stream().findFirst().orElse(null);
        assertEquals(32, vehicle.getCapacity());
    }

    @Test
    void testGetVehicle_WithRandomData_ShouldReturnCorrectResponse() throws Exception {
        Vehicle vehicle = getVehicle();
        vehicleRepository.save(vehicle);
        Long currentId = vehicle.getId();


        mockMvc.perform(get("/api/v1/vehicles/{id}", Math.toIntExact(currentId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(vehicle)))
                .andExpect(status().is(200))
                .andReturn();
    }


    @Test
    void testDeleteVehicle_WithRandomData_ShouldReturnCorrectResponse() throws Exception {
        Vehicle vehicle = getVehicle();
        vehicleRepository.save(vehicle);
        Long currentId = vehicle.getId();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/vehicles/{id}", Math.toIntExact(currentId))
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateVehicle_WithRandomData_ShouldReturnCorrectResponse() throws Exception {
        Vehicle vehicle = getVehicle();
        vehicleRepository.save(vehicle);
        Long currentId = vehicle.getId();
        VehicleUpdateRequest vehicleUpdateRequest = new VehicleUpdateRequest();
        vehicleUpdateRequest.setVehicleType(VehicleType.TRUCK);
        vehicleUpdateRequest.setCapacity(33);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/vehicles/{id}", Math.toIntExact(currentId))
                        .contentType("application/json")
                        .content(this.objectMapper.writeValueAsString(vehicleUpdateRequest)))
                .andExpect(status().is(200))
                .andReturn();
        Vehicle vehicleFromDb = vehicleRepository.findById(currentId).orElse(null);

        assertEquals(vehicleUpdateRequest.getCapacity(), vehicleFromDb.getCapacity(), "Capacity is not the same");
        assertEquals(vehicleUpdateRequest.getVehicleType().toString(), vehicleFromDb.getVehicleType().toString(), "Vehicle type is not the same");

    }

    private static VehicleCreateRequest getCreateRequest(int capacity, VehicleType bus) {
        VehicleCreateRequest vehicleCreateRequest = new VehicleCreateRequest();
        vehicleCreateRequest.setCapacity(capacity);
        vehicleCreateRequest.setVehicleType(bus);
        return vehicleCreateRequest;
    }

    private VehicleCreateRequest getVehicleCreateRequest() {
        VehicleCreateRequest vehicleCreateRequest = getCreateRequest(12, VehicleType.BUS);
        return vehicleCreateRequest;
    }

    private static Vehicle getVehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setCapacity(60);
        vehicle.setVehicleType(VehicleType.BUS);
        return vehicle;
    }

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
