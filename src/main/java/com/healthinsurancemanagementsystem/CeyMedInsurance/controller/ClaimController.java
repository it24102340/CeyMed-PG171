package com.healthinsurancemanagementsystem.CeyMedInsurance.controller;

import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Claim;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.ClaimStatus;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.User;
import com.healthinsurancemanagementsystem.CeyMedInsurance.service.ClaimService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/claims")
public class ClaimController {
    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping("/submit")
    public String showClaimSubmissionForm(Model model) {
        return "claim-submit";
    }

    @PostMapping("/submit")
    public String submitClaim(@RequestParam(required = false) String userId,
                            @RequestParam Long policyId,
                            @RequestParam BigDecimal claimAmount,
                            @RequestParam String hospitalName,
                            @RequestParam String description,
                            @RequestParam MultipartFile hospitalBill,
                            HttpSession session,
                            Model model) {
        try {
            // Get userId from parameter or session
            String customerId = userId;
            if (customerId == null || customerId.isEmpty()) {
                User sessionUser = (User) session.getAttribute("user");
                if (sessionUser != null) {
                    customerId = sessionUser.getCustomerId();
                } else {
                    model.addAttribute("error", "Please log in to submit a claim");
                    return "claim-submit";
                }
            }
            
            Claim claim = claimService.submitClaim(customerId, policyId, claimAmount, 
                                                 hospitalName, description, hospitalBill);
            model.addAttribute("success", "Claim submitted successfully! Claim ID: " + claim.getClaimNumber());
            return "claim-submit";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "claim-submit";
        }
    }

    @GetMapping("/my-claims")
    public String getUserClaims(@RequestParam(required = false) String userId, 
                               HttpSession session, Model model) {
        // Get userId from parameter or session
        String customerId = userId;
        if (customerId == null || customerId.isEmpty()) {
            // Try to get from session
            User sessionUser = (User) session.getAttribute("user");
            if (sessionUser != null) {
                customerId = sessionUser.getCustomerId();
            } else {
                model.addAttribute("error", "Please log in to view your claims");
                return "my-claims";
            }
        }
        
        try {
            List<Claim> claims = claimService.getUserClaims(customerId);
            model.addAttribute("claims", claims);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "my-claims";
    }

    @GetMapping("/admin/all")
    public String getAllClaims(Model model) {
        List<Claim> claims = claimService.getAllClaims();
        model.addAttribute("claims", claims);
        return "admin-claims";
    }

    @GetMapping("/admin/pending")
    public String getPendingClaims(Model model) {
        List<Claim> claims = claimService.getClaimsByStatus(ClaimStatus.PENDING);
        model.addAttribute("claims", claims);
        return "admin-claims";
    }

    @GetMapping("/admin/deletion-requests")
    public String getDeletionRequests(Model model) {
        List<Claim> claims = claimService.getDeletionRequests();
        model.addAttribute("claims", claims);
        return "admin-deletion-requests";
    }

    @PostMapping("/admin/update-status")
    public String updateClaimStatus(@RequestParam Long claimId,
                                  @RequestParam ClaimStatus status,
                                  @RequestParam(required = false) String adminMessage,
                                  Model model) {
        try {
            claimService.updateClaimStatus(claimId, status, adminMessage);
            model.addAttribute("success", "Claim status updated successfully");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/claims/admin/all";
    }

    @PostMapping("/request-deletion")
    public String requestClaimDeletion(@RequestParam Long claimId,
                                     @RequestParam String reason,
                                     @RequestParam String userId,
                                     Model model) {
        try {
            claimService.requestClaimDeletion(claimId, reason);
            model.addAttribute("success", "Deletion request submitted successfully");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/claims/my-claims?userId=" + userId;
    }

    @PostMapping("/admin/approve-deletion")
    public String approveClaimDeletion(@RequestParam Long claimId, Model model) {
        try {
            claimService.approveClaimDeletion(claimId);
            model.addAttribute("success", "Claim deletion approved");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/claims/admin/deletion-requests";
    }

    @PostMapping("/admin/reject-deletion")
    public String rejectClaimDeletion(@RequestParam Long claimId, Model model) {
        try {
            claimService.rejectClaimDeletion(claimId);
            model.addAttribute("success", "Claim deletion rejected");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/claims/admin/deletion-requests";
    }

    @GetMapping("/document/{claimId}")
    public ResponseEntity<byte[]> getClaimDocument(@PathVariable Long claimId) {
        try {
            Optional<Claim> claim = claimService.getClaimById(claimId);
            if (claim.isPresent()) {
                byte[] document = claimService.getClaimDocument(claim.get().getHospitalBillPath());
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", "hospital_bill.pdf");
                
                return new ResponseEntity<>(document, headers, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/details/{claimId}")
    public String getClaimDetails(@PathVariable Long claimId, Model model) {
        Optional<Claim> claim = claimService.getClaimById(claimId);
        if (claim.isPresent()) {
            model.addAttribute("claim", claim.get());
            return "claim-details";
        } else {
            model.addAttribute("error", "Claim not found");
            return "error";
        }
    }
}
