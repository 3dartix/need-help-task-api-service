package ru.pugart.task.api.service.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PerformerOrder {

    // автор
    @Field(type = FieldType.Text, name = "phone")
    private String phone;

    // заявленная цена
    @Field(type = FieldType.Double, name = "final_amount")
    private BigDecimal amount;

    // срок начала исполнения
    @Field(type = FieldType.Date, name = "date_start")
    private Instant dateStart;

    // срок завершения работ
    @Field(type = FieldType.Date, name = "date_end")
    private Instant dateEnd;

    public static PerformerOrder example() {
        return PerformerOrder.builder()
                .phone("performer_example")
                .amount(BigDecimal.valueOf(20234))
                .dateStart(Instant.now())
                .dateEnd(Instant.now().plusSeconds(20000))
                .build();
    }
}
