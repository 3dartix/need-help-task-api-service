package ru.pugart.task.api.service.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.pugart.task.api.service.repository.entity.Profile;
import ru.pugart.task.api.service.repository.entity.Task;

public interface TaskApi {
    Mono<Task> createOrUpdate(Mono<Task> task);
    Mono<Profile> assignTask(String author, String taskId); // TODO refactoring
    Mono<Task> getTaskById(String taskId);
    Flux<Task> getAllTasks(Float lat, Float lon, Float scale);
    Mono<Task> getExample();
}
