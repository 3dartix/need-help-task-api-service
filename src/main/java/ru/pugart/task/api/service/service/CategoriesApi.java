package ru.pugart.task.api.service.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.pugart.task.api.service.dto.CategoryDto;
import ru.pugart.task.api.service.repository.entity.Category;

import java.util.List;

public interface CategoriesApi {
    void store(List<Category> categories);
    Mono<Category> isSubCategoryExits(String nameSubCategory, String categoryMain);
    Mono<Category> isCategoryExits(String nameCategory);
    Flux<CategoryDto> getAllCategories();
}
