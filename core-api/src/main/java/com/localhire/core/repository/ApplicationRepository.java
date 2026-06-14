package com.localhire.core.repository;

import com.localhire.core.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    List<Application> findByCandidateId(UUID candidateId);
    List<Application> findByJobId(UUID jobId);
    Optional<Application> findByJobIdAndCandidateId(UUID jobId, UUID candidateId);
    boolean existsByJobIdAndCandidateId(UUID jobId, UUID candidateId);
}