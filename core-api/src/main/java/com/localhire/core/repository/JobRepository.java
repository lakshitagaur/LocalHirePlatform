package com.localhire.core.repository;

import com.localhire.core.entity.Job;
import com.localhire.core.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {

    List<Job> findByStatus(JobStatus status);

    List<Job> findByEmployerId(UUID employerId);

    List<Job> findByCategoryAndStatus(String category, JobStatus status);

    // Haversine geo-distance query — returns jobs within radiusKm
    @Query(value = """
        SELECT * FROM jobs j
        WHERE j.status = 'OPEN'
        AND (6371 * acos(
            cos(radians(:lat)) * cos(radians(j.location_lat)) *
            cos(radians(j.location_lng) - radians(:lng)) +
            sin(radians(:lat)) * sin(radians(j.location_lat))
        )) < :radiusKm
        ORDER BY created_at DESC
        """, nativeQuery = true)
    List<Job> findJobsNearLocation(
        @Param("lat") double lat,
        @Param("lng") double lng,
        @Param("radiusKm") double radiusKm
    );
}