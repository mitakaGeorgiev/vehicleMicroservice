package com.packagedelivery.vehicleservice.services.feign;

import com.packagedelivery.userdto.model.dto.UserDetailsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="user-service", url="${services.users.getUserUrl}")
public interface UserFeignClient {
    @GetMapping("{id}")
    UserDetailsResponse user(@PathVariable long id);
}
