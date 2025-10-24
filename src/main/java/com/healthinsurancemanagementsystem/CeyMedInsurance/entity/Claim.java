package com.healthinsurancemanagementsystem.CeyMedInsurance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "claim_id")
    private Long claimId;

    @NotBlank
    @Column(name = "claim_number", unique = true, nullable = false)
    private String claimNumber;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Column(name = "policy_id", nullable = false)
    private Long policyId;

    @NotNull
    @Column(name = "claim_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal claimAmount;

    @NotBlank
    @Column(name = "hospital_name", nullable = false)
    private String hospitalName;

    @NotBlank
    @Column(name = "hospital_bill_path", nullable = false)
    private String hospitalBillPath;

    @Column(name = "description", length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClaimStatus status = ClaimStatus.PENDING;

    @Column(name = "submission_date", nullable = false)
    private LocalDateTime submissionDate;

    @Column(name = "admin_message", length = 1000)
    private String adminMessage;

    @Column(name = "admin_response_date")
    private LocalDateTime adminResponseDate;

    @Column(name = "deletion_requested", nullable = false)
    private Boolean deletionRequested = false;

    @Column(name = "deletion_reason", length = 500)
    private String deletionReason;

    // Constructors
    public Claim() {
        this.submissionDate = LocalDateTime.now();
    }

    public Claim(Long userId, Long policyId, BigDecimal claimAmount, String hospitalName, 
                String hospitalBillPath, String description) {
        this();
        this.userId = userId;
        this.policyId = policyId;
        this.claimAmount = claimAmount;
        this.hospitalName = hospitalName;
        this.hospitalBillPath = hospitalBillPath;
        this.description = description;
        this.claimNumber = generateClaimNumber();
    }

    private String generateClaimNumber() {
        return "CLM" + System.currentTimeMillis();
    }

    // Getters and Setters
    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public String getClaimNumber() {
        return claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public BigDecimal getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(BigDecimal claimAmount) {
        this.claimAmount = claimAmount;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getHospitalBillPath() {
        return hospitalBillPath;
    }

    public void setHospitalBillPath(String hospitalBillPath) {
        this.hospitalBillPath = hospitalBillPath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getAdminMessage() {
        return adminMessage;
    }

    public void setAdminMessage(String adminMessage) {
        this.adminMessage = adminMessage;
        this.adminResponseDate = LocalDateTime.now();
    }

    public LocalDateTime getAdminResponseDate() {
        return adminResponseDate;
    }

    public void setAdminResponseDate(LocalDateTime adminResponseDate) {
        this.adminResponseDate = adminResponseDate;
    }

    public Boolean getDeletionRequested() {
        return deletionRequested;
    }

    public void setDeletionRequested(Boolean deletionRequested) {
        this.deletionRequested = deletionRequested;
    }

    public String getDeletionReason() {
        return deletionReason;
    }

    public void setDeletionReason(String deletionReason) {
        this.deletionReason = deletionReason;
    }
}
