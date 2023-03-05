package com.packagedelivery.vehicleservice.services.impl;

import com.packagedelivery.globalExceptionHandler.EntityNotFoundException;
import com.packagedelivery.userdto.model.dto.UserDetailsResponse;
import com.packagedelivery.vehicledto.model.dto.vehicleReservation.VehicleReservationDetailsResponse;
import com.packagedelivery.vehicledto.model.dto.vehicleReservation.VehicleReservationGenericRequest;
import com.packagedelivery.vehicleservice.datamodels.VehicleReservation;
import com.packagedelivery.vehicleservice.datamodels.VehicleReservationId;
import com.packagedelivery.vehicleservice.logging.annotations.DebugLog;
import com.packagedelivery.vehicleservice.mappers.VehicleBookingMapper;
import com.packagedelivery.vehicleservice.repositories.VehicleRepository;
import com.packagedelivery.vehicleservice.repositories.VehicleReservationRepository;
import com.packagedelivery.vehicleservice.services.VehicleReservationService;
import com.packagedelivery.vehicleservice.services.feign.UserFeignClient;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleReservationServiceImpl implements VehicleReservationService {

    final VehicleReservationRepository bookingRepository;
    final VehicleRepository vehicleRepository;
    final VehicleBookingMapper vehicleBookingMapper;
    private final UserFeignClient userFeignClient;

    @Override
    @Cacheable(value = "vehicle_reservation_redis")
    @DebugLog("[Vehicle Booking Service] - Retrieve All Vehicle Bookings")
    public List<VehicleReservationDetailsResponse> findAll() {
        return vehicleBookingMapper.vehicleReservationsToVehicleReservationListResponse(this.bookingRepository.findAll());
    }

    @Override
    @Cacheable(value = "vehicle_reservation_redis_single", keyGenerator = "CustomKeyGenerator")
    @DebugLog("[Vehicle Booking Service] - Retrieve Single Vehicle Bookings")
    public VehicleReservationDetailsResponse retrieveSingle(VehicleReservationId vehicleReservationIdId) {
        return vehicleBookingMapper.deliveryToDeliveriesDetailsResponse(this.bookingRepository.findById(vehicleReservationIdId).orElseThrow(() -> new EntityNotFoundException("Booking not found")));

    }

    @Override
    @CacheEvict(value = "vehicle_reservation_redis", allEntries = true)
    @DebugLog("[Vehicle Booking Service] - Create Vehicle Bookings")
    public VehicleReservationDetailsResponse createReservation(VehicleReservationGenericRequest request, HttpServletRequest httpRequest) {
        VehicleReservation booking = new VehicleReservation();
        booking.setBookingDate(request.getBookingDate());

        try {
            UserDetailsResponse user= this.userFeignClient.user(request.getUserId());
        } catch (Exception httpServerErrorException) {
            throw new EntityNotFoundException("User with that userId was not found");
        }

        booking.setUserId(request.getUserId());
        booking.setVehicle(this.vehicleRepository.findById(request.getVehicleId()).orElseThrow(() -> new EntityNotFoundException("Vehicle not found")));
        try {
            this.bookingRepository.save(booking);
        } catch (DataIntegrityViolationException e) {
            Throwable rootCause = e.getCause();
            if (rootCause instanceof ConstraintViolationException) {
                throw new EntityNotFoundException("User already has a vehicle for that date");
            }
        }
        return vehicleBookingMapper.deliveryToDeliveriesDetailsResponse(this.bookingRepository.save(booking));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "vehicle_reservation_redis", allEntries = true),
            @CacheEvict(value = "vehicle_reservation_redis_single", keyGenerator = "CustomKeyGenerator")
    })
    @DebugLog("[Vehicle Booking Service] - Update Vehicle Bookings")
    public VehicleReservationDetailsResponse updateReservation(VehicleReservationGenericRequest request, HttpServletRequest httpRequest) {
        this.vehicleRepository.findById(request.getVehicleId()).orElseThrow(() -> new EntityNotFoundException("No such Vehicle!"));

        VehicleReservationId bookingId = new VehicleReservationId(request.getVehicleId(), request.getBookingDate());
        VehicleReservation booking = this.bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotFoundException("No such Date!"));

        try {
             this.userFeignClient.user(request.getUserId());
        } catch (Exception httpServerErrorException) {
            throw new EntityNotFoundException("User with that userId was not found");
        }

        booking.setUserId(request.getUserId());
        return vehicleBookingMapper.deliveryToDeliveriesDetailsResponse(this.bookingRepository.saveAndFlush(booking));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "vehicle_reservation_redis", allEntries = true),
            @CacheEvict(value = "vehicle_reservation_redis_single", keyGenerator = "CustomKeyGenerator")
    })
    @DebugLog("[Vehicle Booking Service] - Delete Vehicle Bookings")
    public VehicleReservationId deleteReservation(VehicleReservationId vehicleReservationId) {
        this.bookingRepository.findById(vehicleReservationId).orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        this.bookingRepository.deleteById(vehicleReservationId);
        return vehicleReservationId;
    }

    @Override
    @Cacheable(value = "vehicle_reservation_redis")
    @DebugLog("[Vehicle Booking Service] - Retrieve Vehicle Bookings By Date")
    public List<VehicleReservationDetailsResponse> findByReservationDate(LocalDate date) {
        List<VehicleReservation> vehicleReservationList = Streamable.of(this.bookingRepository.findByBookingDate(date)).toList();
        return vehicleBookingMapper.vehicleReservationsToVehicleReservationListResponse(vehicleReservationList);
    }
}
