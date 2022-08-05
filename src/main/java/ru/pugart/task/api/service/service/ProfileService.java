package ru.pugart.task.api.service.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.pugart.task.api.service.api.ExtUiApiClient;
import ru.pugart.task.api.service.config.AppConfig;
import ru.pugart.task.api.service.repository.ProfileRepository;
import ru.pugart.task.api.service.repository.entity.Profile;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class ProfileService implements ProfileApi {

    private final ProfileRepository profileRepository;
    private final AppConfig appConfig;
    private final ExtUiApiClient extUiApiClient;

    private static List<String> ADMIN_ROLES = new ArrayList<>();
    private static String TOKEN_FORMAT = "Bearer %s";

    @PostConstruct
    private void init(){
        log.debug("getting admin roles from keycloak...");
        ADMIN_ROLES = extUiApiClient.getToken(appConfig.getCredentials())
                .filter(Objects::nonNull)
                .log()
                .flatMap(token ->
                        extUiApiClient.getRolesByGroup(appConfig.getUserGroups().getAdminGroup(), String.format(TOKEN_FORMAT, token.getToken()))
                                .log()
                                .collectList())
                .block();
        log.debug("found next roles: {}", ADMIN_ROLES);
    }

    @Override
    public Mono<Profile> createOrUpdate(Mono<Profile> profile) {
        return profile
                .log()
                .flatMap(profileRepository::save)
                .switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<Profile> blockProfile(String authorQuery, String profile) {
        return findProfileByPhone(authorQuery)
                .log()
                .filter(authorProfile -> checkAuthority(authorProfile.getRoles()))
                .flatMap(authorProfile -> findProfileByPhone(profile))
                .log()
                .flatMap(blockProfile -> {
                    blockProfile.setIsBlocked(true);
                    return profileRepository.save(blockProfile);
                })
                .log()
                .switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<Profile> getProfile(String authorQuery) {
        return findProfileByPhone(authorQuery);
    }

    public Mono<Profile> findProfileByPhone(String phone) {
        return profileRepository.findByPhone(phone)
                .log()
                .switchIfEmpty(Mono.error(new RuntimeException(String.format("error, user with id: %s not found", phone))));
    }

    @Override
    public Flux<Profile> getAllProfiles(String authorQuery) {
        return findProfileByPhone(authorQuery)
                .log()
                .filter(profile -> checkAuthority(profile.getRoles()))
                .switchIfEmpty(Mono.error(new RuntimeException("forbidden, profile with phone not found")))
                .flux()
                .flatMap(profile -> profileRepository.findAll())
                .switchIfEmpty(Flux.empty());
    }

    private boolean checkAuthority(List<String> profileRoles){
        if(profileRoles == null || profileRoles.isEmpty() || profileRoles.stream().noneMatch(profileRole -> ADMIN_ROLES.contains(profileRole))){
            log.warn("forbidden: profile roles: {}, admin roles: {}", profileRoles, ADMIN_ROLES);
            return false;
        }
        return true;
    }
}
