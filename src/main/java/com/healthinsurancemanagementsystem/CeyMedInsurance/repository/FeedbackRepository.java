package com.healthinsurancemanagementsystem.CeyMedInsurance.repository;

import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByPolicyIdOrderByCreatedAtDesc(Long policyId);
    List<Feedback> findByUserIdOrderByCreatedAtDesc(Long userId);
    boolean existsByUserIdAndPolicyId(Long userId, Long policyId);
    Optional<Feedback> findByUserIdAndPolicyId(Long userId, Long policyId);
}
