package com.healthinsurancemanagementsystem.CeyMedInsurance.controller;

import com.healthinsurancemanagementsystem.CeyMedInsurance.dto.PolicyDto;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Policy;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.User;
import com.healthinsurancemanagementsystem.CeyMedInsurance.service.PolicyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/policy")
public class AdminPageController {
    private final PolicyService policyService;

    public AdminPageController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @GetMapping("/admin")
    public String admin(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !Boolean.TRUE.equals(user.getIsAdmin())) {
            return "redirect:/user/login";
        }
        model.addAttribute("policies", policyService.convertToDtoList(policyService.getAllPolicies()));
        model.addAttribute("policyDto", new PolicyDto());
        return "admin";
    }

    @PostMapping("/admin/create")
    public String create(@Valid @ModelAttribute PolicyDto policyDto,
                         BindingResult result,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || !Boolean.TRUE.equals(user.getIsAdmin())) {
            return "redirect:/user/login";
        }
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please check the form for errors.");
            return "redirect:/policy/admin";
        }
        policyService.createPolicy(policyDto);
        redirectAttributes.addFlashAttribute("successMessage", "Policy created successfully!");
        return "redirect:/policy/admin";
    }

    @PostMapping("/admin/update/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute PolicyDto policyDto,
                         BindingResult result,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || !Boolean.TRUE.equals(user.getIsAdmin())) {
            return "redirect:/user/login";
        }
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please check the form for errors.");
            return "redirect:/policy/admin";
        }
        policyService.updatePolicy(id, policyDto);
        redirectAttributes.addFlashAttribute("successMessage", "Policy updated successfully!");
        return "redirect:/policy/admin";
    }

    @PostMapping("/admin/delete/{id}")
    public String delete(@PathVariable Long id,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || !Boolean.TRUE.equals(user.getIsAdmin())) {
            return "redirect:/user/login";
        }
        policyService.deletePolicy(id);
        redirectAttributes.addFlashAttribute("successMessage", "Policy deleted successfully!");
        return "redirect:/policy/admin";
    }

    @RestController
    @RequestMapping("/admin/policies")
    public static class AdminController {
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
}


