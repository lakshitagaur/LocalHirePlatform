package com.localhire.core.entity;

import com.localhire.core.enums.JobStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private User employer;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "location_lat", nullable = false, precision = 9, scale = 6)
    private BigDecimal locationLat;

    @Column(name = "location_lng", nullable = false, precision = 9, scale = 6)
    private BigDecimal locationLng;

    @Column(name = "location_name")
    private String locationName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status = JobStatus.OPEN;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public Job() {}

    // Getters
    public UUID getId() { return id; }
    public User getEmployer() { return employer; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public BigDecimal getLocationLat() { return locationLat; }
    public BigDecimal getLocationLng() { return locationLng; }
    public String getLocationName() { return locationName; }
    public JobStatus getStatus() { return status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setEmployer(User employer) { this.employer = employer; }
    public void setTitle(String title) { this.title = title; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setLocationLat(BigDecimal locationLat) { this.locationLat = locationLat; }
    public void setLocationLng(BigDecimal locationLng) { this.locationLng = locationLng; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public void setStatus(JobStatus status) { this.status = status; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private User employer;
        private String title;
        private String category;
        private String description;
        private BigDecimal locationLat;
        private BigDecimal locationLng;
        private String locationName;

        public Builder employer(User employer) { this.employer = employer; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder locationLat(BigDecimal locationLat) { this.locationLat = locationLat; return this; }
        public Builder locationLng(BigDecimal locationLng) { this.locationLng = locationLng; return this; }
        public Builder locationName(String locationName) { this.locationName = locationName; return this; }

        public Job build() {
            Job job = new Job();
            job.setEmployer(this.employer);
            job.setTitle(this.title);
            job.setCategory(this.category);
            job.setDescription(this.description);
            job.setLocationLat(this.locationLat);
            job.setLocationLng(this.locationLng);
            job.setLocationName(this.locationName);
            return job;
        }
    }
}