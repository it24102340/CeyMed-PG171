package com.healthinsurancemanagementsystem.CeyMedInsurance.entity;

public enum NotificationType {
    CLAIM_STATUS_UPDATE("Claim Status Update"),
    CLAIM_APPROVED("Claim Approved"),
    CLAIM_REJECTED("Claim Rejected"),
    CLAIM_REQUIRES_DOCUMENTS("Additional Documents Required"),
    CLAIM_DELETION_APPROVED("Claim Deletion Approved"),
    CLAIM_DELETION_REJECTED("Claim Deletion Rejected"),
    GENERAL("General Notification");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
