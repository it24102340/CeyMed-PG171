package com.example.insurance.controller;

import com.example.insurance.dto.PurchaseDto;
import com.example.insurance.dto.PurchaseRequest;
import com.example.insurance.entity.User;
import com.example.insurance.service.PurchaseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/purchase")
public class PurchasePageController {
    private final PurchaseService purchaseService;

    public PurchasePageController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping("/admin/stop-requests")
    public String stopRequests(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !Boolean.TRUE.equals(user.getIsAdmin())) {
            return "redirect:/user/login";
        }
        model.addAttribute("stopRequests", purchaseService.convertToDtoList(purchaseService.getPendingStopRequests()));
        return "admin-stop-requests";
    }

    @PostMapping("/admin/approve-stop/{id}")
    public String approveStop(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || !Boolean.TRUE.equals(user.getIsAdmin())) {
            return "redirect:/user/login";
        }
        purchaseService.approveStop(id);
        redirectAttributes.addFlashAttribute("successMessage", "Stop request approved successfully!");
        return "redirect:/purchase/admin/stop-requests";
    }
    @GetMapping("/history")
    public String history(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/user/login";
        model.addAttribute("purchases", purchaseService.convertToDtoList(purchaseService.history(user.getId())));
        return "purchase-history";
    }

    @GetMapping("/create")
    public String showCreate(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/user/login";
        model.addAttribute("userId", user.getId());
        return "purchase-create";
    }

    @PostMapping("/create")
    public String create(Long policyId, String userEmail, Long userId, java.time.LocalDateTime purchaseDate, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/user/login";
        PurchaseRequest req = new PurchaseRequest();
        req.setPolicyId(policyId);
        req.setUserId(user.getId());
        req.setEmail(userEmail);
        req.setPurchaseDate(purchaseDate);
        var purchase = purchaseService.purchase(req);
        redirectAttributes.addFlashAttribute("successMessage", "Policy purchased. Monthly cost: " + purchase.getMonthlyCost());
        return "redirect:/purchase/history";
    }

    @PostMapping("/request-stop/{id}")
    public String requestStop(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/user/login";
        purchaseService.requestStop(id);
        redirectAttributes.addFlashAttribute("successMessage", "Stop request submitted successfully!");
        return "redirect:/purchase/history";
    }

    @PostMapping("/renew/{id}")
    public String renew(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/user/login";
        purchaseService.renew(id);
        redirectAttributes.addFlashAttribute("successMessage", "Policy renewed successfully!");
        return "redirect:/purchase/history";
    }
}


