package com.example.insurance.controller;

import com.example.insurance.dto.PolicyDto;
import com.example.insurance.entity.User;
import com.example.insurance.service.PolicyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
}


