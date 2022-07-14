package ru.pugart.task.api.service.service;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.pugart.task.api.service.repository.entity.Task;

public interface ImageApi {
    Mono<Task> store(String author, String taskId, Flux<FilePart> files);
    Mono<Task> remove(String author, String taskId, Flux<String> images);
}
