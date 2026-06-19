package com.localhire.core.controller;

import com.localhire.core.dto.JobRequest;
import com.localhire.core.dto.JobResponse;
import com.localhire.core.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * Create a new job (employer only)
     */
    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody JobRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(jobService.createJob(request));
    }

    /**
     * Get all open jobs with optional filters
     * Query params: category, latitude, longitude, radiusKm
     */
    @GetMapping
    public ResponseEntity<List<JobResponse>> getAllJobs(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Double latitude,
        @RequestParam(required = false) Double longitude,
        @RequestParam(required = false) Double radiusKm
    ) {
        return ResponseEntity.ok(jobService.getAllJobs(category, latitude, longitude, radiusKm));
    }

    /**
     * Get single job by ID
     */
    @GetMapping("/{jobId}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable UUID jobId) {
        return ResponseEntity.ok(jobService.getJobById(jobId));
    }

    /**
     * Update job (employer only, must own the job)
     */
    @PutMapping("/{jobId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobResponse> updateJob(
        @PathVariable UUID jobId,
        @Valid @RequestBody JobRequest request
    ) {
        return ResponseEntity.ok(jobService.updateJob(jobId, request));
    }

    /**
     * Close a job (employer only)
     */
    @DeleteMapping("/{jobId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> closeJob(@PathVariable UUID jobId) {
        jobService.closeJob(jobId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get my posted jobs (employer only)
     */
    @GetMapping("/my-jobs")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<List<JobResponse>> getMyJobs() {
        return ResponseEntity.ok(jobService.getMyJobs());
    }
}