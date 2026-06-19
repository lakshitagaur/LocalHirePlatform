package com.localhire.core.kafka;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ApplicationEvent(
    UUID applicationId,
    UUID jobId,
    UUID candidateId,
    String jobTitle,
    String candidateName,
    String eventType,  // "SUBMITTED" or "STATUS_CHANGED"
    String newStatus,
    OffsetDateTime timestamp
) {}