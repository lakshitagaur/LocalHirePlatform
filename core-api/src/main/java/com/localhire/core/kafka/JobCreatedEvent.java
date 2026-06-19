package com.localhire.core.kafka;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record JobCreatedEvent(
    UUID jobId,
    String title,
    String category,
    String description,
    BigDecimal locationLat,
    BigDecimal locationLng,
    String locationName,
    UUID employerId,
    OffsetDateTime createdAt
) {}