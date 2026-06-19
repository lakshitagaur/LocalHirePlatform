package com.localhire.core.service;

import com.localhire.core.dto.ApplicationRequest;
import com.localhire.core.dto.ApplicationResponse;
import com.localhire.core.entity.Application;
import com.localhire.core.entity.Job;
import com.localhire.core.entity.User;
import com.localhire.core.enums.ApplicationStatus;
import com.localhire.core.enums.JobStatus;
import com.localhire.core.enums.UserRole;
import com.localhire.core.kafka.ApplicationEvent;
import com.localhire.core.repository.ApplicationRepository;
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
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final KafkaTemplate<String, ApplicationEvent> applicationKafkaTemplate;

    public ApplicationService(ApplicationRepository applicationRepository,
                              JobRepository jobRepository,
                              UserRepository userRepository,
                              SecurityUtil securityUtil,
                              KafkaTemplate<String, ApplicationEvent> applicationKafkaTemplate) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.securityUtil = securityUtil;
        this.applicationKafkaTemplate = applicationKafkaTemplate;
    }

    /**
     * Candidate applies to a job
     */
    public ApplicationResponse applyToJob(ApplicationRequest request) {
        String candidateEmail = securityUtil.getCurrentUserEmail();

        User candidate = userRepository.findByEmail(candidateEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Only candidates can apply
        if (!candidate.getRole().equals(UserRole.CANDIDATE)) {
            throw new RuntimeException("Only candidates can apply to jobs");
        }

        Job job = jobRepository.findById(request.jobId())
            .orElseThrow(() -> new RuntimeException("Job not found"));

        // Job must be open
        if (!job.getStatus().equals(JobStatus.OPEN)) {
            throw new RuntimeException("This job is no longer accepting applications");
        }

        // Prevent duplicate applications
        if (applicationRepository.existsByJobIdAndCandidateId(job.getId(), candidate.getId())) {
            throw new RuntimeException("You have already applied to this job");
        }

        Application application = Application.builder()
            .job(job)
            .candidate(candidate)
            .coverNote(request.coverNote())
            .build();

        Application saved = applicationRepository.save(application);

        // Publish to Kafka
        ApplicationEvent event = new ApplicationEvent(
            saved.getId(),
            job.getId(),
            candidate.getId(),
            job.getTitle(),
            candidate.getFullName(),
            "SUBMITTED",
            "SUBMITTED",
            OffsetDateTime.now()
        );
        applicationKafkaTemplate.send("application.submitted", event);

        return mapToResponse(saved);
    }

    /**
     * Candidate views their own applications
     */
    public List<ApplicationResponse> getMyApplications() {
        String candidateEmail = securityUtil.getCurrentUserEmail();
        User candidate = userRepository.findByEmail(candidateEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return applicationRepository.findByCandidateId(candidate.getId())
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Employer views all applications for their job
     */
    public List<ApplicationResponse> getApplicationsForJob(UUID jobId) {
        String employerEmail = securityUtil.getCurrentUserEmail();

        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found"));

        // Verify employer owns this job
        if (!job.getEmployer().getEmail().equals(employerEmail)) {
            throw new RuntimeException("Unauthorized: you don't own this job");
        }

        return applicationRepository.findByJobId(jobId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }


    public ApplicationResponse getApplicationById(UUID applicationId) {
    String currentUserEmail = securityUtil.getCurrentUserEmail();

    Application application = applicationRepository.findById(applicationId)
        .orElseThrow(() -> new RuntimeException("Application not found"));

    // Only the candidate who applied OR the employer who owns the job can view it
    boolean isCandidate = application.getCandidate().getEmail().equals(currentUserEmail);
    boolean isEmployer = application.getJob().getEmployer().getEmail().equals(currentUserEmail);

    if (!isCandidate && !isEmployer) {
        throw new RuntimeException("Unauthorized: you cannot view this application");
    }

    return mapToResponse(application);
}

    /**
     * Employer updates application status
     */
    public ApplicationResponse updateStatus(UUID applicationId, ApplicationStatus newStatus) {
        String employerEmail = securityUtil.getCurrentUserEmail();

        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));

        // Verify employer owns the job this application is for
        if (!application.getJob().getEmployer().getEmail().equals(employerEmail)) {
            throw new RuntimeException("Unauthorized: you don't own this job");
        }

        // Validate state transition
        validateStatusTransition(application.getStatus(), newStatus);

        application.setStatus(newStatus);
        Application updated = applicationRepository.save(application);

        // Publish status change event to Kafka
        ApplicationEvent event = new ApplicationEvent(
            updated.getId(),
            updated.getJob().getId(),
            updated.getCandidate().getId(),
            updated.getJob().getTitle(),
            updated.getCandidate().getFullName(),
            "STATUS_CHANGED",
            newStatus.name(),
            OffsetDateTime.now()
        );
        applicationKafkaTemplate.send("application.status.changed", event);

        return mapToResponse(updated);
    }

    /**
     * Validate allowed state transitions
     * SUBMITTED → REVIEWED → HIRED or REJECTED
     */
    private void validateStatusTransition(ApplicationStatus current, ApplicationStatus next) {
        boolean valid = switch (current) {
            case SUBMITTED -> next == ApplicationStatus.REVIEWED;
            case REVIEWED -> next == ApplicationStatus.HIRED || next == ApplicationStatus.REJECTED;
            case HIRED, REJECTED -> false; // terminal states
        };

        if (!valid) {
            throw new RuntimeException(
                "Invalid status transition: " + current + " → " + next
            );
        }
    }

    /**
     * Convert Application entity → ApplicationResponse DTO
     */
    private ApplicationResponse mapToResponse(Application application) {
        return new ApplicationResponse(
            application.getId(),
            application.getJob().getId(),
            application.getJob().getTitle(),
            application.getCandidate().getFullName(),
            application.getStatus(),
            application.getCoverNote(),
            application.getAppliedAt()
        );
    }
}