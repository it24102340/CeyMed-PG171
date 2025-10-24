package com.healthinsurancemanagementsystem.CeyMedInsurance.service;

import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Claim;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.ClaimStatus;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Policy;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Purchase;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.User;
import com.healthinsurancemanagementsystem.CeyMedInsurance.repository.ClaimRepository;
import com.healthinsurancemanagementsystem.CeyMedInsurance.repository.PolicyRepository;
import com.healthinsurancemanagementsystem.CeyMedInsurance.repository.PurchaseRepository;
import com.healthinsurancemanagementsystem.CeyMedInsurance.repository.UserRepository;
import com.healthinsurancemanagementsystem.CeyMedInsurance.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClaimService {
    private final ClaimRepository claimRepository;
    private final PolicyRepository policyRepository;
    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final String uploadDir = "uploads/claims/";

    public ClaimService(ClaimRepository claimRepository, PolicyRepository policyRepository, 
                       PurchaseRepository purchaseRepository, UserRepository userRepository,
                       NotificationService notificationService) {
        this.claimRepository = claimRepository;
        this.policyRepository = policyRepository;
        this.purchaseRepository = purchaseRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public Claim submitClaim(String customerId, Long policyId, BigDecimal claimAmount, 
                           String hospitalName, String description, MultipartFile hospitalBill) {
        
        // Find user by customerId
        User user = userRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("User not found with customer ID: " + customerId));
        
        Long userId = user.getId();
        
        // Validate user has purchased this policy
        validateUserPolicy(userId, policyId);
        
        // Validate claim amount against policy limits
        validateClaimAmount(policyId, claimAmount, userId);
        
        // Save uploaded file
        String filePath = saveUploadedFile(hospitalBill);
        
        // Create and save claim
        Claim claim = new Claim(userId, policyId, claimAmount, hospitalName, filePath, description);
        return claimRepository.save(claim);
    }

    public List<Claim> getUserClaims(String customerId) {
        User user = userRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("User not found with customer ID: " + customerId));
        return claimRepository.findByUserIdOrderBySubmissionDateDesc(user.getId());
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    public List<Claim> getClaimsByStatus(ClaimStatus status) {
        return claimRepository.findByStatusOrderBySubmissionDateDesc(status);
    }

    public List<Claim> getDeletionRequests() {
        return claimRepository.findByDeletionRequestedTrueOrderBySubmissionDateDesc();
    }

    public Claim updateClaimStatus(Long claimId, ClaimStatus status, String adminMessage) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found with id: " + claimId));
        
        claim.setStatus(status);
        if (adminMessage != null && !adminMessage.trim().isEmpty()) {
            claim.setAdminMessage(adminMessage);
        }
        
        Claim savedClaim = claimRepository.save(claim);
        
        // Send notification to user about status update
        notificationService.notifyClaimStatusUpdate(savedClaim);
        
        return savedClaim;
    }

    public Claim requestClaimDeletion(Long claimId, String reason) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found with id: " + claimId));
        
        // Only allow deletion requests for pending claims
        if (!claim.getStatus().equals(ClaimStatus.PENDING)) {
            throw new RuntimeException("Cannot request deletion for claims that are already under review or processed. Only pending claims can be deleted.");
        }
        
        claim.setDeletionRequested(true);
        claim.setDeletionReason(reason);
        
        return claimRepository.save(claim);
    }

    public void approveClaimDeletion(Long claimId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found with id: " + claimId));
        
        String claimNumber = claim.getClaimNumber();
        Long userId = claim.getUserId();
        
        claimRepository.delete(claim);
        
        // Send notification to user about deletion approval
        notificationService.notifyClaimDeletionApproved(userId, claimNumber);
    }

    public void rejectClaimDeletion(Long claimId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found with id: " + claimId));
        
        String claimNumber = claim.getClaimNumber();
        Long userId = claim.getUserId();
        
        claim.setDeletionRequested(false);
        claim.setDeletionReason(null);
        
        claimRepository.save(claim);
        
        // Send notification to user about deletion rejection
        notificationService.notifyClaimDeletionRejected(userId, claimNumber);
    }

    public Optional<Claim> getClaimById(Long claimId) {
        return claimRepository.findById(claimId);
    }

    public Optional<Claim> getClaimByNumber(String claimNumber) {
        return claimRepository.findByClaimNumber(claimNumber);
    }

    private void validateUserPolicy(Long userId, Long policyId) {
        List<Purchase> userPurchases = purchaseRepository.findByUserId(userId);
        boolean hasActivePolicy = userPurchases.stream()
                .anyMatch(purchase -> purchase.getPolicy().getPolicyId().equals(policyId) 
                    && purchase.getStatus().name().equals("ACTIVE"));
        
        if (!hasActivePolicy) {
            throw new RuntimeException("User has not purchased this policy or policy is not active");
        }
        
        // Additional validation: Check if policy itself is not expired
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Policy not found with id: " + policyId));
        
        if (policy.getEndDate().isBefore(java.time.LocalDate.now())) {
            throw new RuntimeException("Policy has expired. Cannot submit claims for expired policies.");
        }
    }

    private void validateClaimAmount(Long policyId, BigDecimal claimAmount, Long userId) {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Policy not found with id: " + policyId));
        
        // Check if claim amount exceeds policy coverage
        if (claimAmount.compareTo(policy.getCoverage()) > 0) {
            throw new RuntimeException("Claim amount exceeds policy coverage limit");
        }
        
        // Check total approved claims for this user and policy
        BigDecimal totalApprovedClaims = claimRepository.sumClaimAmountByUserIdAndStatus(userId, ClaimStatus.APPROVED);
        if (totalApprovedClaims == null) totalApprovedClaims = BigDecimal.ZERO;
        
        BigDecimal remainingCoverage = policy.getCoverage().subtract(totalApprovedClaims);
        if (claimAmount.compareTo(remainingCoverage) > 0) {
            throw new RuntimeException("Claim amount exceeds remaining coverage");
        }
    }

    private String saveUploadedFile(MultipartFile file) {
        try {
            // Validate file format
            validateFileFormat(file);
            
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            
            // Save file
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            return uploadDir + uniqueFilename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save uploaded file", e);
        }
    }
    
    private void validateFileFormat(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new RuntimeException("No file selected");
        }
        
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".pdf"};
        
        boolean isValidFormat = false;
        for (String allowedExt : allowedExtensions) {
            if (fileExtension.equals(allowedExt)) {
                isValidFormat = true;
                break;
            }
        }
        
        if (!isValidFormat) {
            throw new RuntimeException("Invalid file format. Only JPG, JPEG, PNG, and PDF files are allowed.");
        }
        
        // Check file size (10MB limit)
        long maxSize = 10 * 1024 * 1024; // 10MB in bytes
        if (file.getSize() > maxSize) {
            throw new RuntimeException("File size exceeds 10MB limit.");
        }
    }

    public byte[] getClaimDocument(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }
}
