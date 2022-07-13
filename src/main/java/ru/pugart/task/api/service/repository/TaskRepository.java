package ru.pugart.task.api.service.repository;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;
import ru.pugart.task.api.service.repository.entity.Task;

public interface TaskRepository extends ReactiveElasticsearchRepository<Task, String> {
    @Query("{\"bool\":{\"must\":[{\"range\":{\"task_details.lat\":{\"gte\":?0,\"lte\":?1 }}},{\"range\":{\"task_details.lon\":{\"gte\":?2,\"lte\":?3 }}}]}}")
    Flux<Task> findAllWithinCoordinates(Float latGte1, Float latLte1, Float latGte2, Float latLte2);
}