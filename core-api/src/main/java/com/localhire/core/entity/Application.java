package com.localhire.core.entity;

import com.localhire.core.enums.ApplicationStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "applications",
    uniqueConstraints = @UniqueConstraint(columnNames = {"job_id", "candidate_id"}))
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private User candidate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.SUBMITTED;

    @Column(name = "cover_note", columnDefinition = "TEXT")
    private String coverNote;

    @CreationTimestamp
    @Column(name = "applied_at", updatable = false)
    private OffsetDateTime appliedAt;

    public Application() {}

    // Getters
    public UUID getId() { return id; }
    public Job getJob() { return job; }
    public User getCandidate() { return candidate; }
    public ApplicationStatus getStatus() { return status; }
    public String getCoverNote() { return coverNote; }
    public OffsetDateTime getAppliedAt() { return appliedAt; }

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setJob(Job job) { this.job = job; }
    public void setCandidate(User candidate) { this.candidate = candidate; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public void setCoverNote(String coverNote) { this.coverNote = coverNote; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Job job;
        private User candidate;
        private String coverNote;
        private ApplicationStatus status = ApplicationStatus.SUBMITTED;

        public Builder job(Job job) { this.job = job; return this; }
        public Builder candidate(User candidate) { this.candidate = candidate; return this; }
        public Builder coverNote(String coverNote) { this.coverNote = coverNote; return this; }
        public Builder status(ApplicationStatus status) { this.status = status; return this; }

        public Application build() {
            Application app = new Application();
            app.setJob(this.job);
            app.setCandidate(this.candidate);
            app.setCoverNote(this.coverNote);
            app.setStatus(this.status);
            return app;
        }
    }
}