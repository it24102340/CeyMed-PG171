package com.healthinsurancemanagementsystem.CeyMedInsurance.service;

import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Claim;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.ClaimStatus;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Notification;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.NotificationType;
import com.healthinsurancemanagementsystem.CeyMedInsurance.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(Long userId, String title, String message, NotificationType type) {
        Notification notification = new Notification(userId, title, message, type);
        return notificationRepository.save(notification);
    }

    public void notifyClaimStatusUpdate(Claim claim) {
        String title = "Claim Status Update";
        String message = buildStatusUpdateMessage(claim);
        NotificationType type = getNotificationTypeForStatus(claim.getStatus());
        
        createNotification(claim.getUserId(), title, message, type);
    }

    public void notifyClaimDeletionApproved(Long userId, String claimNumber) {
        String title = "Claim Deletion Approved";
        String message = "Your deletion request for claim " + claimNumber + " has been approved. The claim has been removed from your records.";
        
        createNotification(userId, title, message, NotificationType.CLAIM_DELETION_APPROVED);
    }

    public void notifyClaimDeletionRejected(Long userId, String claimNumber) {
        String title = "Claim Deletion Rejected";
        String message = "Your deletion request for claim " + claimNumber + " has been rejected. The claim will continue to be processed.";
        
        createNotification(userId, title, message, NotificationType.CLAIM_DELETION_REJECTED);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedDateDesc(userId);
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedDateDesc(userId);
    }

    public Long getUnreadCount(Long userId) {
        return notificationRepository.countUnreadNotificationsByUserId(userId);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = getUnreadNotifications(userId);
        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
        }
        notificationRepository.saveAll(unreadNotifications);
    }

    private String buildStatusUpdateMessage(Claim claim) {
        String statusMessage = claim.getStatus().getDisplayName();
        String baseMessage = "Your claim " + claim.getClaimNumber() + " status has been updated to: " + statusMessage;
        
        if (claim.getAdminMessage() != null && !claim.getAdminMessage().trim().isEmpty()) {
            baseMessage += "\n\nAdmin Message: " + claim.getAdminMessage();
        }
        
        return baseMessage;
    }

    private NotificationType getNotificationTypeForStatus(ClaimStatus status) {
        switch (status) {
            case APPROVED:
                return NotificationType.CLAIM_APPROVED;
            case REJECTED:
                return NotificationType.CLAIM_REJECTED;
            case REQUIRES_DOCUMENTS:
                return NotificationType.CLAIM_REQUIRES_DOCUMENTS;
            default:
                return NotificationType.CLAIM_STATUS_UPDATE;
        }
    }
}
