package com.nutritionisthacksdaily.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateDto {

    private Long id;
    private String title;
    private String issuer;
    private Integer year;
    private String imageFilename;
    private Integer displayOrder;
}
