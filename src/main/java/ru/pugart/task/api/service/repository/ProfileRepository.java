package ru.pugart.task.api.service.repository;

import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Mono;
import ru.pugart.task.api.service.repository.entity.Profile;

public interface ProfileRepository extends ReactiveElasticsearchRepository<Profile, String> {
    Mono<Profile> findByPhone(String phone);
}
