package ru.pugart.task.api.service.repository;

import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Mono;
import ru.pugart.task.api.service.repository.entity.Category;

public interface CategoriesRepository extends ReactiveElasticsearchRepository<Category, String> {
    Mono<Category> findByName(String name);
    Mono<Category> findByNameAndCategoryMain(String name, String categoryMain);
}
