package com.localhire.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record JobRequest(
    @NotBlank(message = "Title is required")
    String title,

    @NotBlank(message = "Category is required")
    String category,

    @NotBlank(message = "Description is required")
    String description,

    @NotNull(message = "Latitude is required")
    BigDecimal locationLat,

    @NotNull(message = "Longitude is required")
    BigDecimal locationLng,

    String locationName
) {}