package ru.pugart.task.api.service.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.pugart.task.api.service.repository.entity.Profile;
import ru.pugart.task.api.service.repository.entity.Task;
import ru.pugart.task.api.service.service.TaskApi;
import ru.pugart.task.api.service.service.TaskService;

@RestController
@RequestMapping("/task")
@AllArgsConstructor
@Slf4j
public class TaskController implements TaskApi {

    private final TaskService taskService;

    @Override
    @PostMapping(value = "create-or-update")
    public Mono<Task> createOrUpdate(@RequestBody Mono<Task> task) {
        return taskService.createOrUpdate(task);
    }

    @Override
    @GetMapping(value = "assign/{author}/{taskId}")
    public Mono<Profile> assignTask(@PathVariable String author, @PathVariable String taskId) {
        return taskService.assignTask(author, taskId);
    }

    @Override
    @GetMapping(value = "get/{taskId}")
    public Mono<Task> getTaskById(@PathVariable String taskId) {
        return taskService.getTaskById(taskId);
    }

    @Override
    @GetMapping(value = "get/all")
    public Flux<Task> getAllTasks(@RequestParam(required = false) Float lat,
                                  @RequestParam(required = false) Float lon,
                                  @RequestParam(required = false) Float scale) {
        return taskService.getAllTasks(lat, lon, scale);
    }

    @Override
    @GetMapping(value = "get/example")
    public Mono<Task> getExample() {
        return taskService.getExample();
    }
}
