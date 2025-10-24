package com.healthinsurancemanagementsystem.CeyMedInsurance.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

public class PurchaseRequest {
    @NotNull
    private Long policyId;

    @NotNull
    private Long userId;

    @Email
    private String email;

    private LocalDateTime purchaseDate;

    @NotBlank(message = "Card holder name is required")
    private String cardHolderName;

    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Card number must be 10 digits")
    private String cardNumber;

    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "^\\d{3}$", message = "CVV must be 3 digits")
    private String cvv;

    @NotNull(message = "Card expiry date is required")
    @FutureOrPresent(message = "Card expiry cannot be in the past")
    private LocalDateTime cardExpiry;

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }

    public String getCardHolderName() { return cardHolderName; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    public LocalDateTime getCardExpiry() { return cardExpiry; }
    public void setCardExpiry(LocalDateTime cardExpiry) { this.cardExpiry = cardExpiry; }
}


