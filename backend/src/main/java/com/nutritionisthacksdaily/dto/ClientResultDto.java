package com.nutritionisthacksdaily.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResultDto {

    private Long id;
    private String clientName;
    private Integer age;
    private String diagnosis;
    private BigDecimal weightLossKg;
    private Integer durationMonths;
    private String description;
    private String photoFilename;
    private Integer displayOrder;
    private LocalDateTime createdAt;
}
