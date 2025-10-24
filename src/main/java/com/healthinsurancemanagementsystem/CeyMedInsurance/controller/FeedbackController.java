package com.healthinsurancemanagementsystem.CeyMedInsurance.controller;

import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Feedback;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.User;
import com.healthinsurancemanagementsystem.CeyMedInsurance.service.FeedbackService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/create/{policyId}")
    public String showFeedbackForm(@PathVariable Long policyId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }

            // Check if user already has feedback for this policy
            Optional<Feedback> existingFeedback = feedbackService.getUserFeedbackForPolicy(user.getId(), policyId);
            
            if (existingFeedback.isPresent()) {
                // User already has feedback, redirect to edit mode
                return "redirect:/feedback/edit/" + existingFeedback.get().getId();
            }

        model.addAttribute("policyId", policyId);
        model.addAttribute("isEdit", false);
        return "feedback-form";
    }

    @GetMapping("/edit/{feedbackId}")
    public String showEditFeedbackForm(@PathVariable Long feedbackId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }

            try {
                Optional<Feedback> feedbackOpt = feedbackService.getFeedbackById(feedbackId);
                if (feedbackOpt.isEmpty()) {
                    return "redirect:/policy/policies";
                }

                Feedback feedback = feedbackOpt.get();
                
                // Check if the feedback belongs to the current user
                if (!feedback.getUserId().equals(user.getId())) {
                    return "redirect:/policy/policies";
                }

                model.addAttribute("feedback", feedback);
                model.addAttribute("policyId", feedback.getPolicyId());
                model.addAttribute("isEdit", true);
                return "feedback-form";
            } catch (Exception e) {
                return "redirect:/policy/policies";
            }
    }

    @PostMapping("/create")
    public String createFeedback(@RequestParam Long policyId,
                                @RequestParam Integer rating,
                                @RequestParam String comments,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }

            try {
                Feedback feedback = feedbackService.createFeedback(user.getId(), policyId, rating, comments);
                redirectAttributes.addFlashAttribute("successMessage", "Feedback submitted successfully!");
                
            } catch (IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error submitting feedback. Please try again later.");
            }
        
        return "redirect:/feedback/policy/" + policyId;
    }

    @PostMapping("/update")
    public String updateFeedback(@RequestParam Long feedbackId,
                                @RequestParam Integer rating,
                                @RequestParam String comments,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }

            try {
                Feedback feedback = feedbackService.updateFeedback(feedbackId, user.getId(), rating, comments);
                redirectAttributes.addFlashAttribute("successMessage", "Feedback updated successfully!");
                
            } catch (IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error updating feedback. Please try again later.");
            }
            
            return "redirect:/feedback/policy/" + feedbackService.getFeedbackById(feedbackId).get().getPolicyId();
    }

    @PostMapping("/delete/{feedbackId}")
    public String deleteFeedback(@PathVariable Long feedbackId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }

            try {
                Feedback feedback = feedbackService.getFeedbackById(feedbackId).get();
                Long policyId = feedback.getPolicyId();
                
                feedbackService.deleteFeedback(feedbackId, user.getId());
                redirectAttributes.addFlashAttribute("successMessage", "Feedback deleted successfully!");
                
                return "redirect:/feedback/policy/" + policyId;
                
            } catch (IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error deleting feedback. Please try again later.");
            }
            
            return "redirect:/policy/policies";
    }

    @GetMapping("/policy/{policyId}")
    public String showPolicyFeedbacks(@PathVariable Long policyId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }

            try {
                List<Feedback> feedbacks = feedbackService.getFeedbackByPolicy(policyId);
                model.addAttribute("feedbacks", feedbacks);
                model.addAttribute("policyId", policyId);
                model.addAttribute("userHasFeedback", feedbackService.userHasFeedbackForPolicy(user.getId(), policyId));
                model.addAttribute("currentUserId", user.getId());
            } catch (Exception e) {
                // If there's an error (like table doesn't exist), show empty list
                model.addAttribute("feedbacks", new java.util.ArrayList<>());
                model.addAttribute("policyId", policyId);
                model.addAttribute("userHasFeedback", false);
                model.addAttribute("currentUserId", user.getId());
                model.addAttribute("errorMessage", "No feedback available yet. Be the first to review this policy!");
            }

        return "policy-feedbacks";
    }
}
