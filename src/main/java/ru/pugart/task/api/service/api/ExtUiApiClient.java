package ru.pugart.task.api.service.api;

import org.springframework.web.bind.annotation.*;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.pugart.task.api.service.dto.JwtDto;
import ru.pugart.task.api.service.dto.UserDto;

@ReactiveFeignClient(value = "extUi", url = "${app.services.ext-ui}")
public interface ExtUiApiClient {
    @GetMapping("api/config/get-roles/{group}")
    Flux<String> getRolesByGroup(@PathVariable String group, @RequestHeader("Authorization") String token);
    @PostMapping("user/auth")
    Mono<JwtDto> getToken(@RequestBody UserDto userDto);
}
