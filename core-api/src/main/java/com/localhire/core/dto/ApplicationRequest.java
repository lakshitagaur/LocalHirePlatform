package com.localhire.core.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ApplicationRequest(
    @NotNull(message = "Job ID is required")
    UUID jobId,

    String coverNote
) {}