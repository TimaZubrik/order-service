package by.timaz.orderservice.feign;

import by.timaz.orderservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "USER-SERVICE",
        url  = "${wiremock.server.url:http://wiremock:8080}"
)
public interface UserServiceInterface {
    @GetMapping("/user/")
    ResponseEntity<UserDto> getUserByEmail(@RequestParam String email);
}
