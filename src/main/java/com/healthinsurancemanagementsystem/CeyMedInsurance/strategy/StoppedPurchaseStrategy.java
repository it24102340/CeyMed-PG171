package com.healthinsurancemanagementsystem.CeyMedInsurance.strategy;

import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Purchase;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.PurchaseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Strategy implementation for STOPPED purchase status.
 * Handles operations specific to stopped purchases.
 */
@Component
public class StoppedPurchaseStrategy implements PurchaseStatusStrategy {
    
    private static final Logger log = LoggerFactory.getLogger(StoppedPurchaseStrategy.class);

    @Override
    public Purchase handleStopRequest(Purchase purchase, Long purchaseId) {
        // Already stopped cannot request stop again
        log.warn("LOGIC BLOCKED: requestStop -> status='STOPPED' for purchase ID: {}", purchaseId);
        throw new IllegalArgumentException("Purchase is already stopped. Cannot request to stop again.");
    }

    @Override
    public Purchase handleStopApproval(Purchase purchase, Long purchaseId) {
        // Already stopped cannot be approved again
        log.warn("LOGIC BLOCKED: approveStop -> already 'STOPPED' for purchase ID: {}", purchaseId);
        throw new IllegalArgumentException("Purchase is already stopped.");
    }

    @Override
    public Purchase handleRenewal(Purchase purchase, Long purchaseId) {
        // For STOPPED, bring it back to ACTIVE
        purchase.setStatus(PurchaseStatus.ACTIVE);
        // Reset stop-related fields
        purchase.setStopRequested(false);
        purchase.setStopApproved(false);
        purchase.setStopRequestDate(null);
        purchase.setStopApprovalDate(null);
        // Reset or update other relevant fields as needed
        purchase.setNextPaymentDate(java.time.LocalDateTime.now().plusMonths(1));
        log.info("LOGIC EXECUTED: renew -> status 'STOPPED' to 'ACTIVE' for purchase ID: {}", purchaseId);
        return purchase;
    }
}
