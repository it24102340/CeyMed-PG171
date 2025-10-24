package com.healthinsurancemanagementsystem.CeyMedInsurance.strategy;

import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.PurchaseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Factory class for managing purchase status strategies.
 * This follows the Factory Pattern to provide the appropriate strategy
 * based on the purchase status.
 */
@Component
public class PurchaseStatusStrategyFactory {
    
    private final Map<PurchaseStatus, PurchaseStatusStrategy> strategies;
    
    @Autowired
    public PurchaseStatusStrategyFactory(Map<PurchaseStatus, PurchaseStatusStrategy> strategies) {
        this.strategies = strategies;
    }
    
    /**
     * Gets the appropriate strategy for the given purchase status.
     * 
     * @param status the purchase status
     * @return the corresponding strategy
     * @throws IllegalArgumentException if no strategy is found for the status
     */
    public PurchaseStatusStrategy getStrategy(PurchaseStatus status) {
        PurchaseStatusStrategy strategy = strategies.get(status);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for status: " + status);
        }
        return strategy;
    }
}
