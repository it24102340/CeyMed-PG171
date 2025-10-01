package com.example.insurance.controller;

import com.example.insurance.dto.PolicyDto;
import com.example.insurance.entity.Policy;
import com.example.insurance.service.PolicyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/policies")
public class AdminController {
    private final PolicyService policyService;

    public AdminController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PostMapping
    public ResponseEntity<Policy> create(@Valid @RequestBody PolicyDto policyDto) {
        return ResponseEntity.ok(policyService.createPolicy(policyDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Policy> update(@PathVariable Long id, @Valid @RequestBody PolicyDto policyDto) {
        return ResponseEntity.ok(policyService.updatePolicy(id, policyDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        policyService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }
}


