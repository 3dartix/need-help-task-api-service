package ru.pugart.task.api.service.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "category_index")
public class Category {
    @Id
    private String id;

    @Field(type = FieldType.Text, name = "name")
    private String name;

    // главная категория, т.е. к какой катагории принадлежит категория (может быть пустой)
    @Field(type = FieldType.Text, name = "category_main")
    private String categoryMain;

    public static Category example() {
        return Category.builder()
                .id("category_index_example")
                .name("квартиры и помещения")
                .categoryMain("ремонт")
                .build();
    }
}
