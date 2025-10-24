package com.healthinsurancemanagementsystem.CeyMedInsurance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDto {
    private Long paymentId;
    private Long purchaseId;
    private BigDecimal amount;
    private LocalDateTime paidAt;
    private LocalDateTime paidForMonth;

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    public Long getPurchaseId() { return purchaseId; }
    public void setPurchaseId(Long purchaseId) { this.purchaseId = purchaseId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    public LocalDateTime getPaidForMonth() { return paidForMonth; }
    public void setPaidForMonth(LocalDateTime paidForMonth) { this.paidForMonth = paidForMonth; }
}



