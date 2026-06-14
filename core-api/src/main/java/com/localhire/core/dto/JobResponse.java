package com.localhire.core.dto;

import com.localhire.core.enums.JobStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record JobResponse(
    UUID id,
    String title,
    String category,
    String description,
    BigDecimal locationLat,
    BigDecimal locationLng,
    String locationName,
    JobStatus status,
    String employerName,
    OffsetDateTime createdAt
) {}