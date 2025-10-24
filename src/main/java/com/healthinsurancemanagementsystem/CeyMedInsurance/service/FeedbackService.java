package com.healthinsurancemanagementsystem.CeyMedInsurance.service;

import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Feedback;
import com.healthinsurancemanagementsystem.CeyMedInsurance.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    // Create new feedback
    public Feedback createFeedback(Long userId, Long policyId, Integer rating, String comments) {
        // Check if user already gave feedback for this policy
        if (feedbackRepository.existsByUserIdAndPolicyId(userId, policyId)) {
            throw new IllegalArgumentException("You have already provided feedback for this policy!");
        }

        Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        feedback.setPolicyId(policyId);
        feedback.setRating(rating);
        feedback.setComments(comments);
        feedback.setCreatedAt(LocalDateTime.now());
        feedback.setUpdatedAt(LocalDateTime.now());

        return feedbackRepository.save(feedback);
    }

    // Update existing feedback
    public Feedback updateFeedback(Long feedbackId, Long userId, Integer rating, String comments) {
        Optional<Feedback> feedbackOpt = feedbackRepository.findById(feedbackId);
        if (feedbackOpt.isEmpty()) {
            throw new IllegalArgumentException("Feedback not found!");
        }

        Feedback feedback = feedbackOpt.get();
        
        // Check if the feedback belongs to the current user
        if (!feedback.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only edit your own feedback!");
        }

        feedback.setRating(rating);
        feedback.setComments(comments);
        feedback.setUpdatedAt(LocalDateTime.now());

        return feedbackRepository.save(feedback);
    }

    // Delete feedback
    public void deleteFeedback(Long feedbackId, Long userId) {
        Optional<Feedback> feedbackOpt = feedbackRepository.findById(feedbackId);
        if (feedbackOpt.isEmpty()) {
            throw new IllegalArgumentException("Feedback not found!");
        }

        Feedback feedback = feedbackOpt.get();
        
        // Check if the feedback belongs to the current user
        if (!feedback.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only delete your own feedback!");
        }

        feedbackRepository.delete(feedback);
    }

    // Admin delete any feedback
    public void adminDeleteFeedback(Long feedbackId) {
        Optional<Feedback> feedbackOpt = feedbackRepository.findById(feedbackId);
        if (feedbackOpt.isEmpty()) {
            throw new IllegalArgumentException("Feedback not found!");
        }
        feedbackRepository.deleteById(feedbackId);
    }

    // Get all feedback for a policy
    public List<Feedback> getFeedbackByPolicy(Long policyId) {
        return feedbackRepository.findByPolicyIdOrderByCreatedAtDesc(policyId);
    }

    // Get all feedback from a user
    public List<Feedback> getFeedbackByUser(Long userId) {
        return feedbackRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Get all feedback
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    // Check if user has feedback for a policy
    public boolean userHasFeedbackForPolicy(Long userId, Long policyId) {
        return feedbackRepository.existsByUserIdAndPolicyId(userId, policyId);
    }

    // Get user's feedback for a specific policy
    public Optional<Feedback> getUserFeedbackForPolicy(Long userId, Long policyId) {
        return feedbackRepository.findByUserIdAndPolicyId(userId, policyId);
    }

    // Get feedback by ID
    public Optional<Feedback> getFeedbackById(Long feedbackId) {
        return feedbackRepository.findById(feedbackId);
    }
}

