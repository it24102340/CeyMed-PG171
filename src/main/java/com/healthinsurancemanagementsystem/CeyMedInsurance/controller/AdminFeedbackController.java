package com.healthinsurancemanagementsystem.CeyMedInsurance.controller;

import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Feedback;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.User;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Policy;
import com.healthinsurancemanagementsystem.CeyMedInsurance.service.FeedbackService;
import com.healthinsurancemanagementsystem.CeyMedInsurance.repository.UserRepository;
import com.healthinsurancemanagementsystem.CeyMedInsurance.repository.PolicyRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/feedback/admin")
public class AdminFeedbackController {

    private final FeedbackService feedbackService;
    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;

    public AdminFeedbackController(FeedbackService feedbackService, 
                                 UserRepository userRepository,
                                 PolicyRepository policyRepository) {
        this.feedbackService = feedbackService;
        this.userRepository = userRepository;
        this.policyRepository = policyRepository;
    }

    @GetMapping("/all")
    public String viewAllFeedback(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getIsAdmin()) {
            return "redirect:/user/login";
        }

        try {
            List<Feedback> allFeedbacks = feedbackService.getAllFeedback();
            model.addAttribute("feedbacks", allFeedbacks);
            model.addAttribute("totalCount", allFeedbacks.size());
        } catch (Exception e) {
            model.addAttribute("feedbacks", new java.util.ArrayList<>());
            model.addAttribute("totalCount", 0);
            model.addAttribute("errorMessage", "No feedback available yet.");
        }

        return "admin/admin-feedback-all";
    }

    @GetMapping("/by-policy")
    public String viewFeedbackByPolicy(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getIsAdmin()) {
            return "redirect:/user/login";
        }

        try {
            List<Policy> policies = policyRepository.findAll();
            model.addAttribute("policies", policies);
        } catch (Exception e) {
            model.addAttribute("policies", new java.util.ArrayList<>());
            model.addAttribute("errorMessage", "No policies available.");
        }

        return "admin/admin-feedback-by-policy";
    }

    @GetMapping("/by-user")
    public String viewFeedbackByUser(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getIsAdmin()) {
            return "redirect:/user/login";
        }

        try {
            List<User> users = userRepository.findAll();
            model.addAttribute("users", users);
        } catch (Exception e) {
            model.addAttribute("users", new java.util.ArrayList<>());
            model.addAttribute("errorMessage", "No users available.");
        }

        return "admin/admin-feedback-by-user";
    }

    @GetMapping("/policy/{policyId}")
    public String viewPolicyFeedbacks(@PathVariable Long policyId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getIsAdmin()) {
            return "redirect:/user/login";
        }

        try {
            List<Feedback> feedbacks = feedbackService.getFeedbackByPolicy(policyId);
            Optional<Policy> policyOpt = policyRepository.findById(policyId);
            
            model.addAttribute("feedbacks", feedbacks);
            model.addAttribute("policy", policyOpt.orElse(null));
            model.addAttribute("policyId", policyId);
        } catch (Exception e) {
            model.addAttribute("feedbacks", new java.util.ArrayList<>());
            model.addAttribute("policyId", policyId);
            model.addAttribute("errorMessage", "No feedback available for this policy.");
        }

        return "admin/admin-policy-feedbacks";
    }

    @GetMapping("/user/{userId}")
    public String viewUserFeedbacks(@PathVariable Long userId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getIsAdmin()) {
            return "redirect:/user/login";
        }

        try {
            List<Feedback> feedbacks = feedbackService.getFeedbackByUser(userId);
            Optional<User> userOpt = userRepository.findById(userId);
            
            model.addAttribute("feedbacks", feedbacks);
            model.addAttribute("targetUser", userOpt.orElse(null));
            model.addAttribute("userId", userId);
        } catch (Exception e) {
            model.addAttribute("feedbacks", new java.util.ArrayList<>());
            model.addAttribute("userId", userId);
            model.addAttribute("errorMessage", "No feedback available for this user.");
        }

        return "admin/admin-user-feedbacks";
    }

    @PostMapping("/delete/{feedbackId}")
    public String deleteFeedback(@PathVariable Long feedbackId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getIsAdmin()) {
            return "redirect:/user/login";
        }

        try {
            feedbackService.adminDeleteFeedback(feedbackId);
            redirectAttributes.addFlashAttribute("successMessage", "Feedback deleted successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting feedback: " + e.getMessage());
        }
        
        return "redirect:/feedback/admin/all";
    }
}
