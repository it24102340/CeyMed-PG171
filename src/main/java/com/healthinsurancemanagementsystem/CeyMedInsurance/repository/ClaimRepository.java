package com.healthinsurancemanagementsystem.CeyMedInsurance.repository;

import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Claim;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByUserIdOrderBySubmissionDateDesc(Long userId);

    List<Claim> findByStatusOrderBySubmissionDateDesc(ClaimStatus status);

    List<Claim> findByDeletionRequestedTrueOrderBySubmissionDateDesc();

    @Query("SELECT c FROM Claim c WHERE c.userId = :userId AND c.status = :status ORDER BY c.submissionDate DESC")
    List<Claim> findByUserIdAndStatusOrderBySubmissionDateDesc(@Param("userId") Long userId, @Param("status") ClaimStatus status);

    @Query("SELECT c FROM Claim c WHERE c.policyId = :policyId ORDER BY c.submissionDate DESC")
    List<Claim> findByPolicyIdOrderBySubmissionDateDesc(@Param("policyId") Long policyId);

    Optional<Claim> findByClaimNumber(String claimNumber);

    @Query("SELECT COUNT(c) FROM Claim c WHERE c.userId = :userId AND c.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ClaimStatus status);

    @Query("SELECT SUM(c.claimAmount) FROM Claim c WHERE c.userId = :userId AND c.status = :status")
    java.math.BigDecimal sumClaimAmountByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ClaimStatus status);
}
