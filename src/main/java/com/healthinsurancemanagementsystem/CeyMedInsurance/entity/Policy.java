package com.healthinsurancemanagementsystem.CeyMedInsurance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "policy")
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Long policyId;

    @NotBlank
    @Column(name = "policy_name", nullable = false)
    private String policyName;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "coverage", nullable = false, precision = 10, scale = 2)
    private BigDecimal coverage;

    @NotBlank
    @Column(name = "category", nullable = false)
    private String category;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull
    @Positive
    @Column(name = "duration", nullable = false)
    private Integer duration;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "premium", nullable = false, precision = 10, scale = 2)
    private BigDecimal premium;

    @Column(name = "specification", length = 2000)
    private String specification;

    @Column(name = "notes", length = 2000)
    private String notes;

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Purchase> purchases;

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public BigDecimal getCoverage() {
        return coverage;
    }

    public void setCoverage(BigDecimal coverage) {
        this.coverage = coverage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public String getSpecification() { return specification; }
    public void setSpecification(String specification) { this.specification = specification; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<Purchase> getPurchases() { return purchases; }
    public void setPurchases(List<Purchase> purchases) { this.purchases = purchases; }

    public BigDecimal getMonthlyCost() {
        return premium.multiply(coverage);
    }
}


