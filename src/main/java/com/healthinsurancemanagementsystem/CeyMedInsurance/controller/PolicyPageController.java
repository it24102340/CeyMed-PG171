package com.healthinsurancemanagementsystem.CeyMedInsurance.controller;

import com.healthinsurancemanagementsystem.CeyMedInsurance.dto.PolicySearchDto;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.User;
import com.healthinsurancemanagementsystem.CeyMedInsurance.service.PolicyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping("/policy")
public class PolicyPageController {
    private final PolicyService policyService;

    public PolicyPageController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @GetMapping("/policies")
    public String showPolicies(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("policies", policyService.convertToDtoList(policyService.getAllPolicies()));
        model.addAttribute("searchDto", new PolicySearchDto());
        model.addAttribute("categories", policyService.getAllCategories());
        return "policies";
    }

    @PostMapping("/search")
    public String search(@ModelAttribute PolicySearchDto searchDto, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("policies", policyService.convertToDtoList(policyService.searchPolicies(searchDto)));
        model.addAttribute("searchDto", searchDto);
        model.addAttribute("categories", policyService.getAllCategories());
        return "policies";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable Long id, Model model) {
        var policy = policyService.getPolicyById(id);
        model.addAttribute("policy", policyService.convertToDto(policy));
        return "policy-details";
    }
}


