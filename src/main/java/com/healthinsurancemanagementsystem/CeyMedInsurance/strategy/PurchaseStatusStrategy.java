package com.healthinsurancemanagementsystem.CeyMedInsurance.strategy;

import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Purchase;

/**
 * Strategy interface for handling different purchase status operations.
 * This follows the Strategy Pattern to encapsulate different behaviors
 * for each purchase status type.
 */
public interface PurchaseStatusStrategy {
    
    /**
     * Handles stop request for a purchase based on its current status.
     * 
     * @param purchase the purchase to process
     * @param purchaseId the ID of the purchase for logging
     * @return the updated purchase
     * @throws IllegalArgumentException if the operation is not allowed for this status
     * @throws IllegalStateException if the status is unhandled
     */
    Purchase handleStopRequest(Purchase purchase, Long purchaseId);
    
    /**
     * Handles stop approval for a purchase based on its current status.
     * 
     * @param purchase the purchase to process
     * @param purchaseId the ID of the purchase for logging
     * @return the updated purchase
     * @throws IllegalArgumentException if the operation is not allowed for this status
     * @throws IllegalStateException if the status is unhandled
     */
    Purchase handleStopApproval(Purchase purchase, Long purchaseId);
    
    /**
     * Handles renewal for a purchase based on its current status.
     * 
     * @param purchase the purchase to process
     * @param purchaseId the ID of the purchase for logging
     * @return the updated purchase
     * @throws IllegalArgumentException if the operation is not allowed for this status
     * @throws IllegalStateException if the status is unhandled
     */
    Purchase handleRenewal(Purchase purchase, Long purchaseId);
}
