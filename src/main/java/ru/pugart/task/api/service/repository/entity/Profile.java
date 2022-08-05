package ru.pugart.task.api.service.repository.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Document(indexName = "profile_index")
@Data
public class Profile {

    @Id
    private String phone;

    @Field(type = FieldType.Text, name = "email")
    private String email;

    @Field(type = FieldType.Text, name = "telegram")
    private String telegram;

    // созданные задачи
    @Field(type = FieldType.Text, name = "created_tasks")
    private List<String> tasksAuthor;

    // взятые в работу
    @Field(type = FieldType.Text, name = "tasks_performer")
    private List<String> tasksPerformer;

    // проверен
    @Field(type = FieldType.Boolean, name = "verified")
    private Boolean verified;

    // рейтинг профиля
    @Field(type = FieldType.Integer, name = "rating")
    private Integer rating;

    @Field(type = FieldType.Boolean, name = "is_blocked")
    private Boolean isBlocked;

    @Field(type = FieldType.Text, name = "roles")
    private List<String> roles;
}
