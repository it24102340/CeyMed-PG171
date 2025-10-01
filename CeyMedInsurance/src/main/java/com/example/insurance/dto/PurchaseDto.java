package com.example.insurance.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PurchaseDto {
    private Long purchaseId;
    @NotNull
    private Long userId;
    @Email
    private String userEmail;
    @NotNull
    private Long policyId;
    private String policyName;
    private BigDecimal monthlyCost;
    private LocalDateTime purchaseDate;
    private String status;
    private Boolean stopRequested;
    private LocalDateTime stopRequestDate;
    private Boolean stopApproved;
    private LocalDateTime stopApprovalDate;

    public Long getPurchaseId() { return purchaseId; }
    public void setPurchaseId(Long purchaseId) { this.purchaseId = purchaseId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public Long getPolicyId() { return policyId; }
    public void setPolicyId(Long policyId) { this.policyId = policyId; }
    public String getPolicyName() { return policyName; }
    public void setPolicyName(String policyName) { this.policyName = policyName; }
    public BigDecimal getMonthlyCost() { return monthlyCost; }
    public void setMonthlyCost(BigDecimal monthlyCost) { this.monthlyCost = monthlyCost; }
    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Boolean getStopRequested() { return stopRequested; }
    public void setStopRequested(Boolean stopRequested) { this.stopRequested = stopRequested; }
    public LocalDateTime getStopRequestDate() { return stopRequestDate; }
    public void setStopRequestDate(LocalDateTime stopRequestDate) { this.stopRequestDate = stopRequestDate; }
    public Boolean getStopApproved() { return stopApproved; }
    public void setStopApproved(Boolean stopApproved) { this.stopApproved = stopApproved; }
    public LocalDateTime getStopApprovalDate() { return stopApprovalDate; }
    public void setStopApprovalDate(LocalDateTime stopApprovalDate) { this.stopApprovalDate = stopApprovalDate; }
}


