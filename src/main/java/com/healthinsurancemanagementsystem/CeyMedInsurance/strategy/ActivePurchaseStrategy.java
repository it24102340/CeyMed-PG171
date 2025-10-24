package com.healthinsurancemanagementsystem.CeyMedInsurance.strategy;

import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Purchase;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.PurchaseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Strategy implementation for ACTIVE purchase status.
 * Handles operations specific to active purchases.
 */
@Component
public class ActivePurchaseStrategy implements PurchaseStatusStrategy {
    
    private static final Logger log = LoggerFactory.getLogger(ActivePurchaseStrategy.class);

    @Override
    public Purchase handleStopRequest(Purchase purchase, Long purchaseId) {
        // For ACTIVE purchases, allow stop request
        purchase.setStopRequested(true);
        purchase.setStopRequestDate(LocalDateTime.now());
        log.info("LOGIC EXECUTED: requestStop -> status='ACTIVE' for purchase ID: {}", purchaseId);
        return purchase;
    }

    @Override
    public Purchase handleStopApproval(Purchase purchase, Long purchaseId) {
        // Stop can be approved only if it was requested first
        if (Boolean.TRUE.equals(purchase.getStopRequested())) {
            purchase.setStopApproved(true);
            purchase.setStopApprovalDate(LocalDateTime.now());
            purchase.setStatus(PurchaseStatus.STOPPED);
            log.info("LOGIC EXECUTED: approveStop -> status='ACTIVE' transitioned to 'STOPPED' for purchase ID: {}", purchaseId);
        } else {
            log.warn("LOGIC BLOCKED: approveStop -> stop not requested for purchase ID: {}", purchaseId);
            throw new IllegalArgumentException("Stop was not requested for this purchase.");
        }
        return purchase;
    }

    @Override
    public Purchase handleRenewal(Purchase purchase, Long purchaseId) {
        // Already active — renewing does not apply
        log.warn("LOGIC BLOCKED: renew -> already 'ACTIVE' for purchase ID: {}", purchaseId);
        throw new IllegalArgumentException("Purchase is already active. No need to renew.");
    }
}
