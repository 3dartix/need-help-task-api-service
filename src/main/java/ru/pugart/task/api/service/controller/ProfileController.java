package ru.pugart.task.api.service.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.pugart.task.api.service.repository.entity.Profile;
import ru.pugart.task.api.service.service.ProfileApi;
import ru.pugart.task.api.service.service.ProfileService;

@RestController
@RequestMapping("/profile")
@AllArgsConstructor
public class ProfileController implements ProfileApi {

    private final ProfileService profileService;

    @Override
    @PostMapping(value = "create-or-update")
    public Mono<Profile> createOrUpdate(@RequestBody Mono<Profile> profile) {
        return profileService.createOrUpdate(profile);
    }

    @Override
    @GetMapping(value = "block-profile")
    public Mono<Profile> blockProfile(@RequestParam String authorQuery, @RequestParam String profile) {
        return profileService.blockProfile(authorQuery, profile);
    }

    @Override
    @GetMapping(value = "find-by-phone")
    public Mono<Profile> findProfileByPhone(@RequestParam String phone) {
        return profileService.findProfileByPhone(phone);
    }

    @Override
    @GetMapping(value = "find-all-profiles")
    public Flux<Profile> getAllProfiles(@RequestParam String authorQuery) {
        return profileService.getAllProfiles(authorQuery);
    }
}
