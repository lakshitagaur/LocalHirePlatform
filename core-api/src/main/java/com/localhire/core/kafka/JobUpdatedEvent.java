package com.localhire.core.kafka;

import java.time.OffsetDateTime;
import java.util.UUID;

public record JobUpdatedEvent(
    UUID jobId,
    String changes,  // JSON string describing what changed
    OffsetDateTime updatedAt
) {}