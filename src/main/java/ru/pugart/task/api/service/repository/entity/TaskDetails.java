package ru.pugart.task.api.service.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDetails {

    @Field(type = FieldType.Text, name = "title")
    private String title;

    @Field(type = FieldType.Text, name = "short_description")
    private String shortDescription;

    @Field(type = FieldType.Text, name = "full_description")
    private String fullDescription;

    // первоначальный гонорар установленный автором
    @Field(type = FieldType.Double, name = "start_amount")
    private BigDecimal startAmount;

    // актуально с
    @Field(type = FieldType.Date, name = "actual_from")
    private Instant actualFrom;

    // актуально до
    @Field(type = FieldType.Date, name = "actual_to")
    private Instant actualTo;

    // итоговый гонорар за работу
    @Field(type = FieldType.Double, name = "final_amount")
    private BigDecimal finalAmount;

    @Field(type = FieldType.Text, name = "images")
    private List<String> images;

    /**
     * Широта и долгота
     */

    @Field(type = FieldType.Float, name = "lat")
    private Float lat;

    @Field(type = FieldType.Float, name = "lon")
    private Float lon;

    /**
     * Данные об исполнении (от автора для исполнителя)
     */

    // исполнено или нет
    @Field(type = FieldType.Boolean, name = "performed")
    private Boolean performed;

    // оценка исполителю
    @Field(type = FieldType.Integer, name = "rating_performer")
    private Integer ratingPerformer;

    // исполнитель (указывается телефон)
    @Field(type = FieldType.Text, name = "performer")
    private String performer;

    // какой-то комментарий от автора
    @Field(type = FieldType.Text, name = "comment_author")
    private String commentAuthor;

    /**
     * Данные об исполнении (от исполнителя автору)
     */

    // какой-то комментарий от исполнителя
    @Field(type = FieldType.Text, name = "comment_performer")
    private String commentPerformer;

    // оценка автору
    @Field(type = FieldType.Integer, name = "rating_author")
    private Integer ratingAuthor;

    // полученный гонорар за работу
    @Field(type = FieldType.Double, name = "received_amount")
    private BigDecimal receivedAmount;

    public static TaskDetails example(){
        return TaskDetails.builder()
                .title("Требуется ремонт ванной")
                .shortDescription("ремонт краткое описание")
                .fullDescription("Ремонт под ключ. Размеры помомещения: 5*5*2.4 кв.м. Постройть трап. Демонтировать старый ремонт")
                .startAmount(BigDecimal.valueOf(15000))
                .actualFrom(Instant.now())
                .actualTo(Instant.now().plusSeconds(20000))
                .finalAmount(BigDecimal.valueOf(20000))
                .images(Arrays.asList("1.jpg", "2.jpg", "3.jpg"))
                .lat(RandomUtils.nextFloat(54.720180205299116F, 54.722180205299116F))
                .lon(RandomUtils.nextFloat(20.524978546018656F, 20.526978546018656F))
                .performed(RandomUtils.nextBoolean())
                .ratingPerformer(RandomUtils.nextInt(1, 11))
                .performer("performer_example_phone")
                .commentAuthor("any_comment_from_author_about_performer")
                .commentPerformer("any_comment_from_performer_about_author")
                .ratingAuthor(RandomUtils.nextInt(1, 11))
                .receivedAmount(BigDecimal.valueOf(20000))
                .build();
    }
}
