package ru.pugart.task.api.service.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.pugart.task.api.service.repository.CategoriesRepository;
import ru.pugart.task.api.service.repository.TaskRepository;
import ru.pugart.task.api.service.repository.entity.Task;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class TaskService implements TaskApi {

    private final TaskRepository taskRepository;
    private final CategoriesRepository categoriesRepository;
    private final ProfileService profileService;

    private static final Float SCALE_STEP = 100.0f;

    @Override
    public Mono<Task> createOrUpdate(Mono<Task> task) {
        return task
                .log()
                .filterWhen(t -> profileService.findProfileByPhone(t.getAuthor()).hasElement())
                .switchIfEmpty(Mono.error(new RuntimeException("forbidden, profile with phone not found")))
                .flatMap(entity -> categoriesRepository.findByNameAndCategoryMain(entity.getTaskCategory().getName(), entity.getTaskCategory().getCategoryMain())
                        .filter(Objects::nonNull)
                        .switchIfEmpty(Mono.error(new RuntimeException("error, unknown category")))
                        .flatMap(category -> {
                            entity.setTaskCategory(category);
                            return taskRepository.save(entity);
                        }))
                .switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<Task> getTaskById(Mono<String> taskId) {
        return taskId
                .log()
                .flatMap(taskRepository::findById)
                .switchIfEmpty(Mono.empty());
    }

    @Override
    public Flux<Task> getAllTasks(Float lat, Float lon, Float scale) {

        float delta = SCALE_STEP * scale;

        Float latGte1 = lat - delta;
        Float latLte1 = lat + delta;
        Float latGte2 = lon - delta;
        Float latLte2 = lon + delta;

        log.info("search within [lat: {} - {}, lon: {} - {}]", latGte1, latLte1, latGte2, latLte2);

        return taskRepository
                .findAllWithinCoordinates(latGte1, latLte1, latGte2, latLte2)
                .log()
                .switchIfEmpty(Flux.empty());
    }

    public Mono<Task> addImages(Mono<String> taskId, List<String> images) {
        return getTaskById(taskId)
                .log()
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.error(new RuntimeException("error: task with id: {} not found")))
                .flatMap(task -> {
                    //todo check duplicate
                    task.getTaskDetails().getImages().addAll(images);
                    return taskRepository.save(task);
                })
                .switchIfEmpty(Mono.empty());
    }

    public Mono<Task> deletedImages(Mono<String> taskId, List<String> images) {
        return getTaskById(taskId)
                .log()
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.error(new RuntimeException("error: task with id: {} not found")))
                .flatMap(task -> {
                    task.getTaskDetails().getImages().removeAll(images);
                    return taskRepository.save(task);
                })
                .switchIfEmpty(Mono.empty());
    }
}
