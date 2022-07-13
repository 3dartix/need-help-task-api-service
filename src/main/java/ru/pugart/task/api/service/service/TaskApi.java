package ru.pugart.task.api.service.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.pugart.task.api.service.repository.entity.Task;

public interface TaskApi {
    Mono<Task> createOrUpdate(Mono<Task> task);
    Mono<Task> getTaskById(Mono<String> taskId);
    Flux<Task> getAllTasks(Float lat, Float lon, Float scale);
}
