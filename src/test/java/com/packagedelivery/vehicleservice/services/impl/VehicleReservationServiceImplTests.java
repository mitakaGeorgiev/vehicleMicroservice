package com.packagedelivery.vehicleservice.services.impl;

import com.packagedelivery.globalExceptionHandler.EntityNotFoundException;
import com.packagedelivery.userdto.model.dto.UserDetailsResponse;
import com.packagedelivery.vehicledto.enums.VehicleType;
import com.packagedelivery.vehicledto.model.dto.vehicleReservation.VehicleReservationDetailsResponse;
import com.packagedelivery.vehicledto.model.dto.vehicleReservation.VehicleReservationGenericRequest;
import com.packagedelivery.vehicleservice.datamodels.Vehicle;
import com.packagedelivery.vehicleservice.datamodels.VehicleReservation;
import com.packagedelivery.vehicleservice.datamodels.VehicleReservationId;
import com.packagedelivery.vehicleservice.mappers.VehicleBookingMapper;
import com.packagedelivery.vehicleservice.repositories.VehicleRepository;
import com.packagedelivery.vehicleservice.repositories.VehicleReservationRepository;
import com.packagedelivery.vehicleservice.services.feign.UserFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class VehicleReservationServiceImplTests {

    @InjectMocks
    private VehicleReservationServiceImpl vehicleReservationService;
    @Mock
    private VehicleReservationRepository bookingRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Spy
    private VehicleBookingMapper vehicleBookingMapper = Mappers.getMapper(VehicleBookingMapper.class);
    @Mock
    private UserFeignClient userFeignClient;
    @Mock
    private HttpServletRequest httpRequest;
    private VehicleReservation vehicleReservation;

    @BeforeEach
    public void setup() {
        this.bookingRepository = Mockito.mock(VehicleReservationRepository.class);
        this.vehicleReservationService = new VehicleReservationServiceImpl(this.bookingRepository, vehicleRepository, vehicleBookingMapper, userFeignClient);
    }

    @Test
    void testRetrieveAllBookingsSize_givenBookingList_shouldReturnCorrectBookingListSize() {
        VehicleReservation vehicleReservation1 = createBooking();
        VehicleReservation vehicleReservation2 = createBooking2();
        Mockito.when(this.bookingRepository.findAll()).thenReturn(List.of(vehicleReservation1, vehicleReservation2));

        List<VehicleReservationDetailsResponse> vehicleReservationDetailsResponseList = vehicleReservationService.findAll();

        assertThat(vehicleReservationDetailsResponseList).isNotNull();
        assertEquals(2, vehicleReservationDetailsResponseList.size());
    }

    @Test
    void testCreateReservation_withNotExistentVehicle_shouldThrowEntityNotFoundException() throws URISyntaxException {
        VehicleReservation vehicleReservation1 = createBooking();
        Mockito.when(this.vehicleRepository.findById(vehicleReservation1.getVehicle().getId())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> vehicleReservationService.createReservation(getVehicleReservationGenericRequest(), httpRequest));
    }

    @Test
    void testRetrieveBookings_withNoBookings_shouldCallRepositoryFindAllMethod() {
        this.bookingRepository.findAll();

        Mockito.verify(this.bookingRepository, Mockito.times(1)).findAll();
    }

    @Test
    void testCreateBooking_withPreviouslyCreatedBookingRequest_shouldCallRepositorySaveMethod() throws URISyntaxException {
        Mockito.when(this.bookingRepository.save(any())).thenReturn(createBooking());
        Mockito.when(userFeignClient.user(getVehicleReservationGenericRequest().getUserId())).thenReturn(getUserResponse());
        Mockito.when(vehicleRepository.findById(1L)).thenReturn(Optional.of(createVehicle()));

        VehicleReservationDetailsResponse actualResponse = this.vehicleReservationService.createReservation(this.getVehicleReservationGenericRequest(), httpRequest);

        assertEquals(getVehicleReservationGenericRequest().getVehicleId(), actualResponse.getVehicleId(), "Vehicle Id is not the same");
        assertEquals(getVehicleReservationGenericRequest().getBookingDate(), actualResponse.getBookingDate(), "Booking Id is not the same");
        assertEquals(getVehicleReservationGenericRequest().getUserId(), actualResponse.getUserId(), "User Id is not the same");
    }

    @Test
    void testRetrieveSingleBooking_withPreviouslyCreatedBooking_shouldThrowCorrectData() {
        VehicleReservation vehicleReservation = createBooking();
        Mockito.when(this.bookingRepository.findById(getVehicleReservationId())).thenReturn(Optional.ofNullable(vehicleReservation));

        VehicleReservationDetailsResponse actualResponse = this.vehicleReservationService.retrieveSingle(getVehicleReservationId());

        assertEquals(vehicleReservation.getVehicle().getId(), actualResponse.getVehicleId(), "Vehicle Id is not the same");
        assertEquals(vehicleReservation.getBookingDate(), actualResponse.getBookingDate(), "Booking date is not the same");
        assertEquals(vehicleReservation.getUserId(), actualResponse.getUserId(), "User Id type is not the same");
        Mockito.verify(this.bookingRepository, Mockito.times(1)).findById(getVehicleReservationId());
    }

    @Test
    void testRetrieveBookingsByDate_withPreviouslyCreatedBookings_shouldThrowCorrectData() {
        LocalDate localDate = LocalDate.of(2020, 1, 8);
        VehicleReservation vehicleReservation1 = createBooking();
        Mockito.when(this.bookingRepository.findByBookingDate(localDate)).thenReturn(List.of(vehicleReservation1));

        List<VehicleReservationDetailsResponse> actualResponse = this.vehicleReservationService.findByReservationDate(localDate);

        assertThat(actualResponse).isNotNull();
        assertEquals(1, actualResponse.size());
    }

    @Test
    void testRetrieveDeletedBooking_withPreviouslyCreatedBooking_shouldThrowCorrectData() {
        VehicleReservation vehicleReservation1 = createBooking();
        VehicleReservationId vehicleReservationId = getVehicleReservationId();
        Mockito.when(this.bookingRepository.findById(vehicleReservationId)).thenReturn(Optional.ofNullable(vehicleReservation1));
        Mockito.when(this.bookingRepository.saveAndFlush(vehicleReservation1)).thenReturn(vehicleReservation1);

        this.vehicleReservationService.deleteReservation(vehicleReservationId);

        assertEquals(vehicleReservation1, bookingRepository.saveAndFlush(vehicleReservation1));
        Mockito.verify(this.bookingRepository, Mockito.times(1)).saveAndFlush(vehicleReservation1);
    }

    @Test
    void testGetSingleBooking_withNonExistentBooking_shouldThrowEntityNotFoundException() {
        VehicleReservationId vehicleReservationId = getVehicleReservationId();
        Mockito.when(this.bookingRepository.findById(vehicleReservationId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vehicleReservationService.retrieveSingle(vehicleReservationId));
    }

    @Test
    void testUserFeignClient_withNonExistentUser_shouldThrowEntityNotFoundException() throws URISyntaxException {
        Mockito.when(userFeignClient.user(999L)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> vehicleReservationService.createReservation(this.getVehicleReservationGenericRequest(), httpRequest));
    }


    @Test
    void testUpdateBooking_withPreviouslyCreatedBookingRequest_shouldReturnCorrectData() throws URISyntaxException {
        VehicleReservationId vehicleReservationId = getVehicleReservationId();
        VehicleReservation vehicleReservation1 = createBooking();
        Mockito.when(this.bookingRepository.findById(vehicleReservationId)).thenReturn(Optional.of(vehicleReservation1));
        Mockito.when(this.bookingRepository.saveAndFlush(vehicleReservation1)).thenReturn(createBooking3());
        Mockito.when(this.vehicleRepository.findById(getVehicleReservationGenericRequest2().getVehicleId())).thenReturn(Optional.ofNullable(createVehicle()));

        VehicleReservationDetailsResponse actualResponse = this.vehicleReservationService.updateReservation(this.getVehicleReservationGenericRequest2(), httpRequest);

        assertEquals(getVehicleReservationGenericRequest2().getVehicleId(), actualResponse.getVehicleId(), "Vehicle Id is not the same");
        assertEquals(getVehicleReservationGenericRequest2().getBookingDate(), actualResponse.getBookingDate(), "Booking Id is not the same");
        assertEquals(getVehicleReservationGenericRequest2().getUserId(), actualResponse.getUserId(), "User Id is not the same");
    }

    private static VehicleReservation createBooking() {
        VehicleReservation vehicleReservation = new VehicleReservation();

        vehicleReservation.setUserId(1L);
        LocalDate localDate = LocalDate.of(2020, 1, 8);
        vehicleReservation.setBookingDate(localDate);
        vehicleReservation.setVehicle(createVehicle());

        return vehicleReservation;
    }

    private static VehicleReservation createBooking2() {
        VehicleReservation vehicleReservation2 = new VehicleReservation();

        vehicleReservation2.setUserId(2L);
        LocalDate localDate = LocalDate.of(2002, 1, 8);
        vehicleReservation2.setBookingDate(localDate);
        vehicleReservation2.setVehicle(createVehicle2());

        return vehicleReservation2;
    }

    private static VehicleReservation createBooking3() {
        VehicleReservation vehicleReservation = new VehicleReservation();

        vehicleReservation.setUserId(3L);
        LocalDate localDate = LocalDate.of(2020, 1, 8);
        vehicleReservation.setBookingDate(localDate);
        vehicleReservation.setVehicle(createVehicle());

        return vehicleReservation;
    }

    private static Vehicle createVehicle() {
        Vehicle vehicle = new Vehicle();

        vehicle.setId(1L);
        vehicle.setCapacity(12);
        vehicle.setVehicleType(VehicleType.BUS);

        return vehicle;
    }

    private static Vehicle createVehicle2() {
        Vehicle vehicle2 = new Vehicle();

        vehicle2.setId(2L);
        vehicle2.setCapacity(24);
        vehicle2.setVehicleType(VehicleType.TRUCK);

        return vehicle2;
    }

    private VehicleReservationGenericRequest getVehicleReservationGenericRequest() {
        VehicleReservationGenericRequest vehicleReservationGenericRequest = new VehicleReservationGenericRequest();

        vehicleReservationGenericRequest.setUserId(1L);
        LocalDate localDate = LocalDate.of(2020, 1, 8);
        vehicleReservationGenericRequest.setBookingDate(localDate);
        vehicleReservationGenericRequest.setVehicleId(1L);

        return vehicleReservationGenericRequest;
    }

    private VehicleReservationGenericRequest getVehicleReservationGenericRequest2() {
        VehicleReservationGenericRequest vehicleReservationGenericRequest = new VehicleReservationGenericRequest();

        vehicleReservationGenericRequest.setUserId(3L);
        LocalDate localDate = LocalDate.of(2020, 1, 8);
        vehicleReservationGenericRequest.setBookingDate(localDate);
        vehicleReservationGenericRequest.setVehicleId(1L);

        return vehicleReservationGenericRequest;
    }

    private UserDetailsResponse getUserResponse() {
        UserDetailsResponse response = new UserDetailsResponse();

        response.setId(1L);

        return response;
    }

    private VehicleReservationId getVehicleReservationId() {
        VehicleReservationId vehicleReservationId = new VehicleReservationId();
        vehicleReservationId.setVehicle(1L);
        LocalDate localDate = LocalDate.of(2020, 1, 8);
        vehicleReservationId.setBookingDate(localDate);
        return vehicleReservationId;
    }

}
