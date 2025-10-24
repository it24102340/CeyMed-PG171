package com.healthinsurancemanagementsystem.CeyMedInsurance.strategy;

import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Purchase;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.PurchaseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Strategy implementation for EXPIRED purchase status.
 * Handles operations specific to expired purchases.
 */
@Component
public class ExpiredPurchaseStrategy implements PurchaseStatusStrategy {
    
    private static final Logger log = LoggerFactory.getLogger(ExpiredPurchaseStrategy.class);

    @Override
    public Purchase handleStopRequest(Purchase purchase, Long purchaseId) {
        // Expired purchases cannot be stopped
        log.warn("LOGIC BLOCKED: requestStop -> status='EXPIRED' for purchase ID: {}", purchaseId);
        throw new IllegalArgumentException("Purchase is expired. Cannot request to stop.");
    }

    @Override
    public Purchase handleStopApproval(Purchase purchase, Long purchaseId) {
        // Expired cannot approve stop
        log.warn("LOGIC BLOCKED: approveStop -> status='EXPIRED' for purchase ID: {}", purchaseId);
        throw new IllegalArgumentException("Purchase is expired. Cannot approve stop.");
    }

    @Override
    public Purchase handleRenewal(Purchase purchase, Long purchaseId) {
        // For EXPIRED, bring it back to ACTIVE
        purchase.setStatus(PurchaseStatus.ACTIVE);
        // Reset stop-related fields
        purchase.setStopRequested(false);
        purchase.setStopApproved(false);
        purchase.setStopRequestDate(null);
        purchase.setStopApprovalDate(null);
        // Reset or update other relevant fields as needed
        purchase.setNextPaymentDate(java.time.LocalDateTime.now().plusMonths(1));
        log.info("LOGIC EXECUTED: renew -> status 'EXPIRED' to 'ACTIVE' for purchase ID: {}", purchaseId);
        return purchase;
    }
}
