package com.example.insurance.service;

import com.example.insurance.dto.PolicyDto;
import com.example.insurance.dto.PolicySearchDto;
import com.example.insurance.entity.Policy;
import com.example.insurance.repository.PolicyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PolicyService {
    private final PolicyRepository policyRepository;

    public PolicyService(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    public Policy createPolicy(PolicyDto policyDto) {
        Policy policy = new Policy();
        policy.setPolicyName(policyDto.getPolicyName());
        policy.setCoverage(policyDto.getCoverage());
        policy.setCategory(policyDto.getCategory());
        policy.setStartDate(policyDto.getStartDate());
        policy.setEndDate(policyDto.getEndDate());
        policy.setDuration(policyDto.getDuration());
        policy.setPremium(policyDto.getPremium());
        return policyRepository.save(policy);
    }

    public Policy updatePolicy(Long policyId, PolicyDto policyDto) {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Policy not found with id: " + policyId));
        policy.setPolicyName(policyDto.getPolicyName());
        policy.setCoverage(policyDto.getCoverage());
        policy.setCategory(policyDto.getCategory());
        policy.setStartDate(policyDto.getStartDate());
        policy.setEndDate(policyDto.getEndDate());
        policy.setDuration(policyDto.getDuration());
        policy.setPremium(policyDto.getPremium());
        return policyRepository.save(policy);
    }

    public void deletePolicy(Long policyId) {
        if (!policyRepository.existsById(policyId)) {
            throw new RuntimeException("Policy not found with id: " + policyId);
        }
        policyRepository.deleteById(policyId);
    }

    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    public Policy getPolicyById(Long policyId) {
        return policyRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Policy not found with id: " + policyId));
    }

    public List<Policy> searchPolicies(PolicySearchDto searchDto) {
        if (searchDto.getPolicyId() != null) {
            return policyRepository.findByPolicyId(searchDto.getPolicyId());
        } else if (searchDto.getCategory() != null && !searchDto.getCategory().trim().isEmpty()) {
            return policyRepository.findByCategoryIgnoreCase(searchDto.getCategory());
        } else {
            return policyRepository.findAll();
        }
    }

    public List<Policy> getPoliciesByCategory(String category) {
        return policyRepository.findByCategory(category);
    }

    public List<String> getAllCategories() {
        return policyRepository.findAllCategories();
    }

    public List<Policy> getActivePolicies() {
        return policyRepository.findActivePoliciesOnDate(LocalDate.now());
    }

    public PolicyDto convertToDto(Policy policy) {
        PolicyDto dto = new PolicyDto();
        dto.setPolicyId(policy.getPolicyId());
        dto.setPolicyName(policy.getPolicyName());
        dto.setCoverage(policy.getCoverage());
        dto.setCategory(policy.getCategory());
        dto.setStartDate(policy.getStartDate());
        dto.setEndDate(policy.getEndDate());
        dto.setDuration(policy.getDuration());
        dto.setPremium(policy.getPremium());
        dto.setMonthlyCost(policy.getMonthlyCost());
        return dto;
    }

    public List<PolicyDto> convertToDtoList(List<Policy> policies) {
        return policies.stream().map(this::convertToDto).collect(Collectors.toList());
    }
}


