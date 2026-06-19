package com.localhire.core.controller;

import com.localhire.core.dto.ApplicationRequest;
import com.localhire.core.dto.ApplicationResponse;
import com.localhire.core.enums.ApplicationStatus;
import com.localhire.core.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     * Candidate applies to a job
     */
    @PostMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ApplicationResponse> apply(
        @Valid @RequestBody ApplicationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(applicationService.applyToJob(request));
    }

    /**
     * Candidate views their applications
     */
    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications() {
        return ResponseEntity.ok(applicationService.getMyApplications());
    }

    /**
     * Employer views applications for their job
     */
    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsForJob(
        @PathVariable UUID jobId
    ) {
        return ResponseEntity.ok(applicationService.getApplicationsForJob(jobId));
    }

    @GetMapping("/{applicationId}")
public ResponseEntity<ApplicationResponse> getApplicationById(
    @PathVariable UUID applicationId
) {
    return ResponseEntity.ok(applicationService.getApplicationById(applicationId));
}


    /**
     * Employer updates application status
     */
    @PatchMapping("/{applicationId}/status")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApplicationResponse> updateStatus(
        @PathVariable UUID applicationId,
        @RequestParam ApplicationStatus status
    ) {
        return ResponseEntity.ok(applicationService.updateStatus(applicationId, status));
    }
}