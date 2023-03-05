package com.packagedelivery.vehicleservice.controllers.resolvers;

import com.packagedelivery.vehicleservice.datamodels.dto.CustomizedVehicleReservationDto;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDate;

@RequiredArgsConstructor
public class CustomizedVehicleReservationDtoResolver implements HandlerMethodArgumentResolver {


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(CustomizedVehicleReservationDto.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {


        var bf = ((ServletWebRequest) webRequest).getRequest().getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bf.readLine()) != null) {
            sb.append(line);
        }
        JSONObject json = new JSONObject(sb.toString());

        return new CustomizedVehicleReservationDto(json.getLong("vehicleId"), LocalDate.parse(json.getString("bookingDate")));
    }
}
