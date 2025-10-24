package com.healthinsurancemanagementsystem.CeyMedInsurance.controller;

import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Notification;
import com.healthinsurancemanagementsystem.CeyMedInsurance.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/my-notifications")
    public String getUserNotifications(HttpSession session, Model model) {
        Object sessionUser = session.getAttribute("user");
        if (sessionUser == null) {
            model.addAttribute("error", "Please log in to view notifications");
            return "my-notifications";
        }
        
        // Get user ID from session (assuming User entity has getId method)
        Long userId = null;
        try {
            userId = (Long) sessionUser.getClass().getMethod("getId").invoke(sessionUser);
        } catch (Exception e) {
            model.addAttribute("error", "Unable to get user information");
            return "my-notifications";
        }
        
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        Long unreadCount = notificationService.getUnreadCount(userId);
        
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
        
        return "my-notifications";
    }

    @PostMapping("/mark-read/{notificationId}")
    @ResponseBody
    public ResponseEntity<String> markAsRead(@PathVariable Long notificationId) {
        try {
            notificationService.markAsRead(notificationId);
            return ResponseEntity.ok("Notification marked as read");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to mark notification as read");
        }
    }

    @PostMapping("/mark-all-read")
    @ResponseBody
    public ResponseEntity<String> markAllAsRead(HttpSession session) {
        try {
            Object sessionUser = session.getAttribute("user");
            if (sessionUser == null) {
                return ResponseEntity.badRequest().body("User not logged in");
            }
            
            Long userId = (Long) sessionUser.getClass().getMethod("getId").invoke(sessionUser);
            notificationService.markAllAsRead(userId);
            return ResponseEntity.ok("All notifications marked as read");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to mark all notifications as read");
        }
    }

    @GetMapping("/unread-count")
    @ResponseBody
    public ResponseEntity<Long> getUnreadCount(HttpSession session) {
        try {
            Object sessionUser = session.getAttribute("user");
            if (sessionUser == null) {
                return ResponseEntity.ok(0L);
            }
            
            Long userId = (Long) sessionUser.getClass().getMethod("getId").invoke(sessionUser);
            Long unreadCount = notificationService.getUnreadCount(userId);
            return ResponseEntity.ok(unreadCount);
        } catch (Exception e) {
            return ResponseEntity.ok(0L);
        }
    }
}
