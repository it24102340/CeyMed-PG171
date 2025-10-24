package com.healthinsurancemanagementsystem.CeyMedInsurance.controller;

import com.healthinsurancemanagementsystem.CeyMedInsurance.dto.PurchaseRequest;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Purchase;
import com.healthinsurancemanagementsystem.CeyMedInsurance.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase")
public class PurchaseController {
    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping
    public ResponseEntity<Purchase> purchase(@Valid @RequestBody PurchaseRequest request) {
        return ResponseEntity.ok(purchaseService.purchase(request));
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<Purchase>> history(@PathVariable Long userId) {
        return ResponseEntity.ok(purchaseService.history(userId));
    }

    @PostMapping("/stop/{id}")
    public ResponseEntity<Purchase> requestStop(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.requestStop(id));
    }

    @PostMapping("/renew/{id}")
    public ResponseEntity<Purchase> renew(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.renew(id));
    }

    @PostMapping("/pay/{id}")
    public ResponseEntity<Purchase> payNext(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.payNextMonth(id));
    }
}


