package ru.pugart.task.api.service.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.pugart.task.api.service.repository.entity.Profile;

public interface ProfileApi {
    Mono<Profile> createOrUpdate(Mono<Profile> profile);
    Mono<Profile> blockProfile(String authorQuery, String profile);
    Mono<Profile> getProfile(String authorQuery);
//    Mono<Profile> findProfileByPhone(String phone);
    Flux<Profile> getAllProfiles(String authorQuery);
}
