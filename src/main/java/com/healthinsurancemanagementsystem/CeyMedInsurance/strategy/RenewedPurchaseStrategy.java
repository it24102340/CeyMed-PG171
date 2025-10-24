package com.healthinsurancemanagementsystem.CeyMedInsurance.strategy;

import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Purchase;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.PurchaseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Strategy implementation for RENEWED purchase status.
 * Handles operations specific to renewed purchases.
 */
@Component
public class RenewedPurchaseStrategy implements PurchaseStatusStrategy {
    
    private static final Logger log = LoggerFactory.getLogger(RenewedPurchaseStrategy.class);

    @Override
    public Purchase handleStopRequest(Purchase purchase, Long purchaseId) {
        // Treat RENEWED similar to ACTIVE for stop requests (project choice)
        purchase.setStopRequested(true);
        purchase.setStopRequestDate(LocalDateTime.now());
        log.info("LOGIC EXECUTED: requestStop -> status='RENEWED' treated as ACTIVE for purchase ID: {}", purchaseId);
        return purchase;
    }

    @Override
    public Purchase handleStopApproval(Purchase purchase, Long purchaseId) {
        // Stop can be approved only if it was requested first
        if (Boolean.TRUE.equals(purchase.getStopRequested())) {
            purchase.setStopApproved(true);
            purchase.setStopApprovalDate(LocalDateTime.now());
            purchase.setStatus(PurchaseStatus.STOPPED);
            log.info("LOGIC EXECUTED: approveStop -> status='RENEWED' transitioned to 'STOPPED' for purchase ID: {}", purchaseId);
        } else {
            log.warn("LOGIC BLOCKED: approveStop -> stop not requested for purchase ID: {}", purchaseId);
            throw new IllegalArgumentException("Stop was not requested for this purchase.");
        }
        return purchase;
    }

    @Override
    public Purchase handleRenewal(Purchase purchase, Long purchaseId) {
        // Already renewed, disallow duplicate renew
        log.warn("LOGIC BLOCKED: renew -> already 'RENEWED' for purchase ID: {}", purchaseId);
        throw new IllegalArgumentException("Purchase has already been renewed.");
    }
}
