package com.healthinsurancemanagementsystem.CeyMedInsurance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_id")
    private Long purchaseId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "monthly_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyCost;

    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate = LocalDateTime.now();

    // Mirror column to satisfy existing DB schema
    @Column(name = "purchased_at", nullable = false)
    private LocalDateTime purchasedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PurchaseStatus status = PurchaseStatus.ACTIVE;

    @Column(name = "stop_requested")
    private Boolean stopRequested = false;

    @Column(name = "stop_request_date")
    private LocalDateTime stopRequestDate;

    @Column(name = "stop_approved")
    private Boolean stopApproved = false;

    @Column(name = "stop_approval_date")
    private LocalDateTime stopApprovalDate;

    @Column(name = "next_payment_date")
    private LocalDateTime nextPaymentDate;

    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy)
    {
        this.policy = policy;
    }

    public BigDecimal getMonthlyCost() { return monthlyCost; }
    public void setMonthlyCost(BigDecimal monthlyCost) { this.monthlyCost = monthlyCost; }
    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; this.purchasedAt = purchaseDate; }
    public LocalDateTime getPurchasedAt() { return purchasedAt; }
    public void setPurchasedAt(LocalDateTime purchasedAt) { this.purchasedAt = purchasedAt; this.purchaseDate = purchasedAt; }
    public PurchaseStatus getStatus() { return status; }
    public void setStatus(PurchaseStatus status) { this.status = status; }
    public Boolean getStopRequested() { return stopRequested; }
    public void setStopRequested(Boolean stopRequested) { this.stopRequested = stopRequested; }
    public LocalDateTime getStopRequestDate() { return stopRequestDate; }
    public void setStopRequestDate(LocalDateTime stopRequestDate) { this.stopRequestDate = stopRequestDate; }
    public Boolean getStopApproved() { return stopApproved; }
    public void setStopApproved(Boolean stopApproved) { this.stopApproved = stopApproved; }
    public LocalDateTime getStopApprovalDate() { return stopApprovalDate; }
    public void setStopApprovalDate(LocalDateTime stopApprovalDate) { this.stopApprovalDate = stopApprovalDate; }
    public LocalDateTime getNextPaymentDate() { return nextPaymentDate; }
    public void setNextPaymentDate(LocalDateTime nextPaymentDate) { this.nextPaymentDate = nextPaymentDate; }
}


