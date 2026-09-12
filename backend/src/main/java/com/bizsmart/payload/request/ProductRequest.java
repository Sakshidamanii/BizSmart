package com.bizsmart.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {
    @NotBlank
    private String sku;

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private BigDecimal price;

    @NotNull
    private BigDecimal costPrice;

    @NotNull
    private Integer stockQuantity;

    private Integer safetyStock = 10;
    private Integer reorderQuantity = 50;

    private Long categoryId;
}
