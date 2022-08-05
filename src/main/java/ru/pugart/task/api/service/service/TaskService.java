package ru.pugart.task.api.service.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.pugart.task.api.service.repository.CategoriesRepository;
import ru.pugart.task.api.service.repository.TaskRepository;
import ru.pugart.task.api.service.repository.entity.Profile;
import ru.pugart.task.api.service.repository.entity.Task;

import java.util.ArrayList;
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
                            log.debug("save task");
                            return taskRepository.save(entity)
                                    .log()
                                    .flatMap(entityTask -> {
                                        log.debug("save id task to profile");
                                        return profileService.findProfileByPhone(entityTask.getAuthor())
                                                .log()
                                                .flatMap(entityProfile -> {
                                                    if(entityProfile.getTasksAuthor() == null) {
                                                        entityProfile.setTasksAuthor(new ArrayList<>());
                                                    }
                                                    entityProfile.getTasksAuthor().add(entityTask.getId());
                                                    return profileService.createOrUpdate(Mono.just(entityProfile));
                                                })
                                                .flatMap(entityProfile -> Mono.just(entityTask));
                                    });
                        }))
                .switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<Profile> assignTask(String author, String taskId) {
        return profileService.findProfileByPhone(author)
                .switchIfEmpty(Mono.error(new RuntimeException("forbidden, profile with phone not found")))
                .flatMap(profile ->
                        taskRepository.findById(taskId)
                                .switchIfEmpty(Mono.error(new RuntimeException(String.format("task with id %s not found", taskId))))
                                .flatMap(task -> {
                                    if(profile.getTasksPerformer() == null){
                                        profile.setTasksPerformer(new ArrayList<>());
                                    }
                                    profile.getTasksPerformer().add(taskId);
                                    return profileService.createOrUpdate(Mono.just(profile));
                                }));
    }

    @Override
    public Mono<Task> getTaskById(String taskId) {
        return taskRepository.findById(taskId)
                .log()
                .switchIfEmpty(Mono.error(new RuntimeException(String.format("task with id %s not found", taskId))));
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

    @Override
    public Mono<Task> getExample() {
        return Mono.just(Task.example());
    }

    public Mono<Task> addImages(String taskId, List<String> images) {
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

    public Mono<Task> deletedImages(String taskId, List<String> images) {
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
