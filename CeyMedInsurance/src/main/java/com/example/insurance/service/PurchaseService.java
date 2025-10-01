package com.example.insurance.service;

import com.example.insurance.dto.PurchaseRequest;
import com.example.insurance.dto.PurchaseDto;
import com.example.insurance.entity.Policy;
import com.example.insurance.entity.Purchase;
import com.example.insurance.entity.User;
import com.example.insurance.entity.PurchaseStatus;
import com.example.insurance.repository.PolicyRepository;
import com.example.insurance.repository.PurchaseRepository;
import com.example.insurance.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;

    public PurchaseService(PurchaseRepository purchaseRepository, UserRepository userRepository, PolicyRepository policyRepository) {
        this.purchaseRepository = purchaseRepository;
        this.userRepository = userRepository;
        this.policyRepository = policyRepository;
    }

    public Purchase purchase(PurchaseRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(user.getEmail())) {
            throw new IllegalArgumentException("Email mismatch for user");
        }
        Policy policy = policyRepository.findById(request.getPolicyId()).orElseThrow(() -> new IllegalArgumentException("Policy not found"));
        BigDecimal monthly = policy.getPremium().multiply(policy.getCoverage());

        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setPolicy(policy);
        purchase.setMonthlyCost(monthly);
        if (request.getPurchaseDate() != null) {
            purchase.setPurchaseDate(request.getPurchaseDate());
        }
        return purchaseRepository.save(purchase);
    }

    public List<Purchase> history(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return purchaseRepository.findDetailedByUserId(userId);
    }

    public Purchase requestStop(Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId).orElseThrow(() -> new IllegalArgumentException("Purchase not found"));
        purchase.setStopRequested(true);
        purchase.setStopRequestDate(LocalDateTime.now());
        // here you could notify admin via email/queue. For now it's stored and visible on admin page.
        return purchaseRepository.save(purchase);
    }

    public Purchase approveStop(Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId).orElseThrow(() -> new IllegalArgumentException("Purchase not found"));
        if (Boolean.TRUE.equals(purchase.getStopRequested())) {
            purchase.setStopApproved(true);
            purchase.setStopApprovalDate(LocalDateTime.now());
            purchase.setStatus(PurchaseStatus.STOPPED);
        }
        return purchaseRepository.save(purchase);
    }

    public Purchase renew(Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId).orElseThrow(() -> new IllegalArgumentException("Purchase not found"));
        purchase.setStatus(PurchaseStatus.ACTIVE);
        purchase.setStopRequested(false);
        purchase.setStopApproved(false);
        purchase.setStopRequestDate(null);
        purchase.setStopApprovalDate(null);
        return purchaseRepository.save(purchase);
    }

    public List<Purchase> getPendingStopRequests() {
        return purchaseRepository.findPendingStopRequestsDetailed();
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
        return dto;
    }
}


