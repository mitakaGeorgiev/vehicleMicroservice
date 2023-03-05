package com.packagedelivery.vehicleservice.configuration;


import com.packagedelivery.vehicleservice.controllers.resolvers.CustomizedVehicleReservationDtoResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VehicleReservationArgumentResolverConfiguration implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(
            List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CustomizedVehicleReservationDtoResolver());
    }

}