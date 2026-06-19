package com.localhire.core.service;

import com.localhire.core.dto.JobRequest;
import com.localhire.core.dto.JobResponse;
import com.localhire.core.entity.Job;
import com.localhire.core.entity.User;
import com.localhire.core.enums.JobStatus;
import com.localhire.core.kafka.JobCreatedEvent;
import com.localhire.core.kafka.JobUpdatedEvent;
import com.localhire.core.repository.JobRepository;
import com.localhire.core.repository.UserRepository;
import com.localhire.core.security.SecurityUtil;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final KafkaTemplate<String, JobCreatedEvent> jobCreatedKafkaTemplate;
    private final KafkaTemplate<String, JobUpdatedEvent> jobUpdatedKafkaTemplate;

    public JobService(JobRepository jobRepository,
                      UserRepository userRepository,
                      SecurityUtil securityUtil,
                      KafkaTemplate<String, JobCreatedEvent> jobCreatedKafkaTemplate,
                      KafkaTemplate<String, JobUpdatedEvent> jobUpdatedKafkaTemplate) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.securityUtil = securityUtil;
        this.jobCreatedKafkaTemplate = jobCreatedKafkaTemplate;
        this.jobUpdatedKafkaTemplate = jobUpdatedKafkaTemplate;
    }

    /**
     * Employer creates a new job
     */
    public JobResponse createJob(JobRequest request) {
        String employerEmail = securityUtil.getCurrentUserEmail();
        User employer = userRepository.findByEmail(employerEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = Job.builder()
            .employer(employer)
            .title(request.title())
            .category(request.category())
            .description(request.description())
            .locationLat(request.locationLat())
            .locationLng(request.locationLng())
            .locationName(request.locationName())
            .build();

        Job savedJob = jobRepository.save(job);

        // Publish to Kafka
        JobCreatedEvent event = new JobCreatedEvent(
            savedJob.getId(),
            savedJob.getTitle(),
            savedJob.getCategory(),
            savedJob.getDescription(),
            savedJob.getLocationLat(),
            savedJob.getLocationLng(),
            savedJob.getLocationName(),
            savedJob.getEmployer().getId(),
            savedJob.getCreatedAt()
        );
        jobCreatedKafkaTemplate.send("job.created", event);

        return mapToResponse(savedJob);
    }

    /**
     * Get all open jobs (with optional filters)
     */
    public List<JobResponse> getAllJobs(String category, Double latitude, Double longitude, Double radiusKm) {
        List<Job> jobs;

        if (latitude != null && longitude != null && radiusKm != null) {
            // Geo-distance search
            jobs = jobRepository.findJobsNearLocation(latitude, longitude, radiusKm);
        } else if (category != null) {
            // Category filter
            jobs = jobRepository.findByCategoryAndStatus(category, JobStatus.OPEN);
        } else {
            // All open jobs
            jobs = jobRepository.findByStatus(JobStatus.OPEN);
        }

        return jobs.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get job by ID
     */
    public JobResponse getJobById(UUID jobId) {
        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found"));
        return mapToResponse(job);
    }

    /**
     * Employer updates their job
     */
    public JobResponse updateJob(UUID jobId, JobRequest request) {
        String employerEmail = securityUtil.getCurrentUserEmail();

        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found"));

        // Verify employer owns this job
        if (!job.getEmployer().getEmail().equals(employerEmail)) {
            throw new RuntimeException("Unauthorized: you don't own this job");
        }

        // Update fields
        job.setTitle(request.title());
        job.setCategory(request.category());
        job.setDescription(request.description());
        job.setLocationLat(request.locationLat());
        job.setLocationLng(request.locationLng());
        job.setLocationName(request.locationName());

        Job updated = jobRepository.save(job);

        // Publish update event to Kafka
        JobUpdatedEvent event = new JobUpdatedEvent(updated.getId(),"Job details updated",updated.getUpdatedAt());
        jobUpdatedKafkaTemplate.send("job.updated", event);

        return mapToResponse(updated);
    }

    /**
     * Employer closes a job
     */
    public void closeJob(UUID jobId) {
        String employerEmail = securityUtil.getCurrentUserEmail();

        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getEmployer().getEmail().equals(employerEmail)) {
            throw new RuntimeException("Unauthorized: you don't own this job");
        }

        job.setStatus(JobStatus.CLOSED);
        jobRepository.save(job);

        // Publish event
        JobUpdatedEvent event = new JobUpdatedEvent(
            job.getId(),
            "Job closed",
            OffsetDateTime.now()
        );
        jobUpdatedKafkaTemplate.send("job.updated", event);
    }

    /**
     * Get jobs posted by current employer
     */
    public List<JobResponse> getMyJobs() {
        String employerEmail = securityUtil.getCurrentUserEmail();
        User employer = userRepository.findByEmail(employerEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        List<Job> jobs = jobRepository.findByEmployerId(employer.getId());
        return jobs.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Convert Job entity to JobResponse DTO
     */
    private JobResponse mapToResponse(Job job) {
        return new JobResponse(
            job.getId(),
            job.getTitle(),
            job.getCategory(),
            job.getDescription(),
            job.getLocationLat(),
            job.getLocationLng(),
            job.getLocationName(),
            job.getStatus(),
            job.getEmployer().getFullName(),
            job.getCreatedAt()
        );
    }
}