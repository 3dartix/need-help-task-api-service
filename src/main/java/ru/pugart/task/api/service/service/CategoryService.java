package ru.pugart.task.api.service.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.pugart.task.api.service.dto.CategoryDto;
import ru.pugart.task.api.service.repository.CategoriesRepository;
import ru.pugart.task.api.service.repository.entity.Category;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class CategoryService implements CategoriesApi{

    private final CategoriesRepository categoriesRepository;

    @Override
    public void store(List<Category> categories) {
        categoriesRepository.saveAll(Flux.fromIterable(categories)).subscribe();
    }

    @Override
    public Mono<Category> isSubCategoryExits(String nameSubCategory, String categoryMain) {
        return categoriesRepository.findByNameAndCategoryMain(nameSubCategory, categoryMain);
    }

    @Override
    public Mono<Category> isCategoryExits(String categoryMain) {
        return categoriesRepository.findByName(categoryMain);
    }

    @Override
    public Flux<CategoryDto> getAllCategories() {
        return categoriesRepository
                .findAll()
                .collectList()
                .flatMapMany(categories -> Flux.fromIterable(mapToDto(categories)));
    }

    private List<CategoryDto> mapToDto(List<Category> categories){
        Map<String, CategoryDto> result = new HashMap<>();
        categories.forEach(category -> {
            if(category.getCategoryMain() != null) {
                CategoryDto categoryDto = result.get(category.getCategoryMain());
                if(categoryDto == null) {
                    result.put(category.getCategoryMain(), CategoryDto.builder()
                            .categoryMain(category.getCategoryMain())
                            .subCategories(new LinkedList<>(Arrays.asList(category.getName())))
                            .build());
                } else {
                    categoryDto.getSubCategories().add(category.getName());
                }
            }
        });
        return new ArrayList<>(result.values());
    }
}
