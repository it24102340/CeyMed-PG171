package com.example.insurance.controller;

import com.example.insurance.dto.PolicyFilterRequest;
import com.example.insurance.entity.Policy;
import com.example.insurance.service.PolicyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/policies")
public class PolicyController {
    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @GetMapping
    public ResponseEntity<List<Policy>> list(@RequestParam(required = false) Long policyId,
                                             @RequestParam(required = false) String category) {
        if (policyId != null) {
            return ResponseEntity.ok(List.of(policyService.getPolicyById(policyId)));
        }
        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(policyService.getPoliciesByCategory(category));
        }
        return ResponseEntity.ok(policyService.getAllPolicies());
    }
}


