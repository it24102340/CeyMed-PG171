package com.healthinsurancemanagementsystem.CeyMedInsurance.controller;

import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Policy;
import com.healthinsurancemanagementsystem.CeyMedInsurance.service.PolicyService;
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


