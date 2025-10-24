package com.healthinsurancemanagementsystem.CeyMedInsurance.entity;

public enum ClaimStatus {
    PENDING("Pending Review"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    UNDER_REVIEW("Under Review"),
    REQUIRES_DOCUMENTS("Requires Additional Documents");

    private final String displayName;

    ClaimStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
