package ru.pugart.task.api.service.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Arrays;
import java.util.List;

@Document(indexName = "task_index")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    private String id;

    @Field(type = FieldType.Text, name = "author_phone")
    private String author;

    @Field(type = FieldType.Object, name = "task_details")
    private TaskDetails taskDetails;

    @Field(type = FieldType.Object, name = "category")
    private Category taskCategory;

    // список откликнувшихся исполнителей
    @Field(type = FieldType.Object, name = "performer_orders")
    private List<PerformerOrder> performerOrders;

    public static Task example() {
        return Task.builder()
                .id("example")
                .author("example")
                .taskDetails(TaskDetails.example())
                .taskCategory(Category.example())
                .performerOrders(Arrays.asList(PerformerOrder.example(), PerformerOrder.example()))
                .build();
    }
}
