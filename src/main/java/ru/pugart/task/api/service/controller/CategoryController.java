package ru.pugart.task.api.service.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.pugart.task.api.service.dto.CategoryDto;
import ru.pugart.task.api.service.service.CategoryService;

@RestController
@RequestMapping("/category")
@AllArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping(value = "all")
    public Flux<CategoryDto> getAllCategories() {
        return categoryService.getAllCategories();
    }
}
