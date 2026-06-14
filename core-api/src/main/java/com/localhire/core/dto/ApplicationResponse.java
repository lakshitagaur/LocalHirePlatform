package com.localhire.core.dto;

import com.localhire.core.enums.ApplicationStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ApplicationResponse(
    UUID id,
    UUID jobId,
    String jobTitle,
    String candidateName,
    ApplicationStatus status,
    String coverNote,
    OffsetDateTime appliedAt
) {}