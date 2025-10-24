package com.healthinsurancemanagementsystem.CeyMedInsurance.service;

import com.healthinsurancemanagementsystem.CeyMedInsurance.dto.PurchaseRequest;
import com.healthinsurancemanagementsystem.CeyMedInsurance.dto.PurchaseDto;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Policy;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Purchase;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.User;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.PurchaseStatus;
import com.healthinsurancemanagementsystem.CeyMedInsurance.repository.PolicyRepository;
import com.healthinsurancemanagementsystem.CeyMedInsurance.repository.PaymentRepository;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Payment;
import com.healthinsurancemanagementsystem.CeyMedInsurance.repository.PurchaseRepository;
import com.healthinsurancemanagementsystem.CeyMedInsurance.repository.UserRepository;
import com.healthinsurancemanagementsystem.CeyMedInsurance.strategy.PurchaseStatusStrategy;
import com.healthinsurancemanagementsystem.CeyMedInsurance.strategy.PurchaseStatusStrategyFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
public class PurchaseService {
    private static final Logger log = LoggerFactory.getLogger(PurchaseService.class);
    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final PaymentRepository paymentRepository;
    private final PurchaseStatusStrategyFactory strategyFactory;

    public PurchaseService(PurchaseRepository purchaseRepository, UserRepository userRepository, PolicyRepository policyRepository, PaymentRepository paymentRepository, PurchaseStatusStrategyFactory strategyFactory) {
        this.purchaseRepository = purchaseRepository;
        this.userRepository = userRepository;
        this.policyRepository = policyRepository;
        this.paymentRepository = paymentRepository;
        this.strategyFactory = strategyFactory;
    }

    public Purchase purchase(PurchaseRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(user.getEmail())) {
            throw new IllegalArgumentException("Email mismatch for user");
        }
        Policy policy = policyRepository.findById(request.getPolicyId()).orElseThrow(() -> new IllegalArgumentException("Policy not found"));
        BigDecimal monthly = policy.getPremium().multiply(policy.getCoverage());

        // Basic payment validation (simulated processing)
        if (request.getCardHolderName() == null || request.getCardHolderName().isBlank()) {
            throw new IllegalArgumentException("Card holder name is required");
        }
        if (request.getCardNumber() == null || !request.getCardNumber().matches("^\\d{10}$")) {
            throw new IllegalArgumentException("Card number must be 10 digits");
        }
        if (request.getCvv() == null || !request.getCvv().matches("^\\d{3}$")) {
            throw new IllegalArgumentException("CVV must be 3 digits");
        }
        if (request.getCardExpiry() == null || request.getCardExpiry().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException("Card expiry cannot be in the past");
        }

        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setPolicy(policy);
        purchase.setMonthlyCost(monthly);
        if (request.getPurchaseDate() != null) {
            purchase.setPurchaseDate(request.getPurchaseDate());
        }
        // Set next payment date as one month after purchaseDate
        java.time.LocalDateTime baseDate = purchase.getPurchaseDate() != null ? purchase.getPurchaseDate() : java.time.LocalDateTime.now();
        purchase.setNextPaymentDate(baseDate.plusMonths(1));
        Purchase saved = purchaseRepository.save(purchase);
        // Record first month payment at purchase time
        Payment initial = new Payment();
        initial.setPurchase(saved);
        initial.setAmount(monthly);
        initial.setPaidForMonth(baseDate.withDayOfMonth(1));
        paymentRepository.save(initial);
        return saved;
    }

    public List<Purchase> history(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return purchaseRepository.findDetailedByUserId(userId);
    }

    public Purchase requestStop(Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId).orElseThrow(() -> new IllegalArgumentException("Purchase not found"));
        
        // Use strategy pattern to handle stop request based on current status
        PurchaseStatusStrategy strategy = strategyFactory.getStrategy(purchase.getStatus());
        Purchase updatedPurchase = strategy.handleStopRequest(purchase, purchaseId);
        
        // here you could notify admin via email/queue. For now it's stored and visible on admin page.
        return purchaseRepository.save(updatedPurchase);
    }

    public Purchase approveStop(Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId).orElseThrow(() -> new IllegalArgumentException("Purchase not found"));
        
        // Use strategy pattern to handle stop approval based on current status
        PurchaseStatusStrategy strategy = strategyFactory.getStrategy(purchase.getStatus());
        Purchase updatedPurchase = strategy.handleStopApproval(purchase, purchaseId);
        
        return purchaseRepository.save(updatedPurchase);
    }
    public Purchase renew(Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId).orElseThrow(() -> new IllegalArgumentException("Purchase not found"));
        
        // Use strategy pattern to handle renewal based on current status
        PurchaseStatusStrategy strategy = strategyFactory.getStrategy(purchase.getStatus());
        Purchase updatedPurchase = strategy.handleRenewal(purchase, purchaseId);
        
        return purchaseRepository.save(updatedPurchase);
    }

    public List<Purchase> getPendingStopRequests() {
        return purchaseRepository.findPendingStopRequestsDetailed();
    }

    @Transactional
    public void delete(Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("Purchase not found"));
        
        // First delete all associated payments to avoid foreign key constraint violation
        List<Payment> payments = paymentRepository.findByPurchaseId(purchaseId);
        if (!payments.isEmpty()) {
            paymentRepository.deleteAll(payments);
        }
        
        // Now delete the purchase
        purchaseRepository.delete(purchase);
    }

    // DTO helpers for UI rendering
    public List<PurchaseDto> convertToDtoList(List<Purchase> purchases) {
        return purchases.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public PurchaseDto convertToDto(Purchase purchase) {
        PurchaseDto dto = new PurchaseDto();
        dto.setPurchaseId(purchase.getPurchaseId());
        dto.setUserId(purchase.getUser().getId());
        dto.setUserEmail(purchase.getUser().getEmail());
        dto.setPolicyId(purchase.getPolicy().getPolicyId());
        dto.setPolicyName(purchase.getPolicy().getPolicyName());
        dto.setMonthlyCost(purchase.getMonthlyCost());
        dto.setPurchaseDate(purchase.getPurchaseDate());
        dto.setStatus(purchase.getStatus().name());
        dto.setStopRequested(purchase.getStopRequested());
        dto.setStopRequestDate(purchase.getStopRequestDate());
        dto.setStopApproved(purchase.getStopApproved());
        dto.setStopApprovalDate(purchase.getStopApprovalDate());
        dto.setNextPaymentDate(purchase.getNextPaymentDate());
        dto.setDuration(purchase.getPolicy().getDuration());
        
        // Calculate actual policy end date based on purchase date + duration
        if (purchase.getPurchaseDate() != null && purchase.getPolicy().getDuration() != null) {
            int duration = purchase.getPolicy().getDuration();
            java.time.LocalDate purchaseStartDate = purchase.getPurchaseDate().toLocalDate();
            java.time.LocalDate actualEndDate = purchaseStartDate.plusMonths(duration);
            dto.setPolicyEndDate(actualEndDate);
            
            // Remaining months: compute using policy duration minus months elapsed since purchase date
            java.time.LocalDate start = purchaseStartDate.withDayOfMonth(1);
            java.time.LocalDate nowRef = (purchase.getNextPaymentDate() != null ? purchase.getNextPaymentDate() : java.time.LocalDateTime.now()).toLocalDate().withDayOfMonth(1);
            int elapsed = (int) java.time.temporal.ChronoUnit.MONTHS.between(start, nowRef);
            int remaining = Math.max(0, duration - elapsed);
            dto.setRemainingMonths(remaining);
        } else {
            // Fallback to policy's general end date if purchase date or duration is null
            dto.setPolicyEndDate(purchase.getPolicy().getEndDate());
        }
        // Payment information for display
        try {
            List<Payment> payments = paymentRepository.findByPurchaseId(purchase.getPurchaseId());
            dto.setPaymentCount((long) payments.size());
            
            // Calculate total paid amount
            BigDecimal totalPaid = payments.stream()
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setTotalPaid(totalPaid);
            
            // Set first and last payment dates
            if (!payments.isEmpty()) {
                // Payments are ordered by paidForMonth desc, so first is latest, last is earliest
                dto.setLastPaymentDate(payments.get(0).getPaidAt());
                dto.setFirstPaymentDate(payments.get(payments.size() - 1).getPaidAt());
            }
        } catch (Exception ignored) { 
            dto.setPaymentCount(0L);
            dto.setTotalPaid(BigDecimal.ZERO);
        }
        return dto;
    }

    public Purchase payNextMonth(Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId).orElseThrow(() -> new IllegalArgumentException("Purchase not found"));
        // Ensure active
        if (purchase.getStatus() != PurchaseStatus.ACTIVE) {
            throw new IllegalArgumentException("Purchase is not active");
        }
        // Advance nextPaymentDate to next month; if null, set from now. Also record payment row
        java.time.LocalDateTime base = purchase.getNextPaymentDate() != null ? purchase.getNextPaymentDate() : java.time.LocalDateTime.now();
        Payment pay = new Payment();
        pay.setPurchase(purchase);
        pay.setAmount(purchase.getMonthlyCost());
        pay.setPaidForMonth(base.withDayOfMonth(1));
        paymentRepository.save(pay);
        purchase.setNextPaymentDate(base.plusMonths(1));
        return purchaseRepository.save(purchase);
    }
}


