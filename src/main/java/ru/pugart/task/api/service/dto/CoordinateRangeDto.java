package ru.pugart.task.api.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CoordinateRangeDto {
    private Float lat;
    private Float lon;
}
