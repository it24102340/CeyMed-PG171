package com.healthinsurancemanagementsystem.CeyMedInsurance.controller;

import com.healthinsurancemanagementsystem.CeyMedInsurance.dto.PurchaseRequest;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.User;
import com.healthinsurancemanagementsystem.CeyMedInsurance.service.PurchaseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.healthinsurancemanagementsystem.CeyMedInsurance.repository.PaymentRepository;

@Controller
@RequestMapping("/purchase")
public class PurchasePageController {
    private final PurchaseService purchaseService;
    private final PaymentRepository paymentRepository;

    public PurchasePageController(PurchaseService purchaseService, PaymentRepository paymentRepository) {
        this.purchaseService = purchaseService;
        this.paymentRepository = paymentRepository;
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
        var dtos = purchaseService.convertToDtoList(purchaseService.history(user.getId()));
        // Show each policy only once: keep the most recent purchase per policy
        java.util.Map<Long, com.healthinsurancemanagementsystem.CeyMedInsurance.dto.PurchaseDto> uniqueByPolicy = new java.util.LinkedHashMap<>();
        for (var dto : dtos) {
            if (!uniqueByPolicy.containsKey(dto.getPolicyId())) {
                uniqueByPolicy.put(dto.getPolicyId(), dto);
            }
        }
        model.addAttribute("purchases", new java.util.ArrayList<>(uniqueByPolicy.values()));
        return "purchase-history";
    }

    @GetMapping("/create")
    public String showCreate(HttpSession session, Model model, @org.springframework.web.bind.annotation.RequestParam(required = false) Long policyId) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/user/login";
        model.addAttribute("userId", user.getCustomerId());
        if (policyId != null) {
            model.addAttribute("policyId", policyId);
        }
        return "purchase-create";
    }

    @PostMapping("/create")
    public String create(Long policyId, String userEmail, java.time.LocalDateTime purchaseDate,
                         String cardHolderName, String cardNumber, String cvv, java.time.LocalDateTime cardExpiry,
                         HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/user/login";
        PurchaseRequest req = new PurchaseRequest();
        req.setPolicyId(policyId);
        req.setUserId(user.getId()); // Use database ID for the actual purchase record
        req.setEmail(userEmail);
        req.setPurchaseDate(purchaseDate);
        req.setCardHolderName(cardHolderName);
        req.setCardNumber(cardNumber);
        req.setCvv(cvv);
        req.setCardExpiry(cardExpiry);
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

    @PostMapping("/pay/{id}")
    public String pay(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/user/login";
        purchaseService.payNextMonth(id);
        redirectAttributes.addFlashAttribute("successMessage", "Payment successful for current month.");
        return "redirect:/purchase/history";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/user/login";
        purchaseService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Purchase removed from history.");
        return "redirect:/purchase/history";
    }

    @GetMapping("/payments/{id}")
    public String payments(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/user/login";
        var userPurchases = purchaseService.history(user.getId());
        var purchaseOpt = userPurchases.stream().filter(p -> p.getPurchaseId().equals(id)).findFirst();
        if (purchaseOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "You do not have access to this purchase.");
            return "redirect:/purchase/history";
        }
        var purchase = purchaseOpt.get();
        var payments = paymentRepository.findByPurchaseId(id);
        // aggregates
        long count = payments.size();
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        for (var p : payments) { total = total.add(p.getAmount()); }
        java.time.LocalDateTime firstPaidAt = payments.isEmpty() ? null : payments.get(payments.size()-1).getPaidAt();
        java.time.LocalDateTime lastPaidAt = payments.isEmpty() ? null : payments.get(0).getPaidAt();

        model.addAttribute("payments", payments);
        model.addAttribute("paymentCount", count);
        model.addAttribute("paymentTotal", total);
        model.addAttribute("firstPaidAt", firstPaidAt);
        model.addAttribute("lastPaidAt", lastPaidAt);
        model.addAttribute("purchase", purchaseService.convertToDto(purchase));
        return "purchase-payment-history";
    }
}


