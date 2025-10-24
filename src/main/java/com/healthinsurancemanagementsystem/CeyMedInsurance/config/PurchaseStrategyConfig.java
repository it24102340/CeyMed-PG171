package com.healthinsurancemanagementsystem.CeyMedInsurance.config;

import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.PurchaseStatus;
import com.healthinsurancemanagementsystem.CeyMedInsurance.strategy.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Configuration class for mapping purchase status strategies.
 * This configuration creates a map of PurchaseStatus to their corresponding
 * strategy implementations.
 */
@Configuration
public class PurchaseStrategyConfig {
    
    @Bean
    public Map<PurchaseStatus, com.healthinsurancemanagementsystem.CeyMedInsurance.strategy.PurchaseStatusStrategy> purchaseStrategies(
            ActivePurchaseStrategy activeStrategy,
            RenewedPurchaseStrategy renewedStrategy,
            StoppedPurchaseStrategy stoppedStrategy,
            ExpiredPurchaseStrategy expiredStrategy) {
        
        return Map.of(
            PurchaseStatus.ACTIVE, activeStrategy,
            PurchaseStatus.RENEWED, renewedStrategy,
            PurchaseStatus.STOPPED, stoppedStrategy,
            PurchaseStatus.EXPIRED, expiredStrategy
        );
    }
}
