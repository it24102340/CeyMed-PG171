package com.healthinsurancemanagementsystem.CeyMedInsurance.repository;

import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("select p from Payment p where p.purchase.purchaseId = :purchaseId order by p.paidForMonth desc")
    List<Payment> findByPurchaseId(@Param("purchaseId") Long purchaseId);

    @Query("select count(p) from Payment p where p.purchase.purchaseId = :purchaseId")
    long countByPurchaseId(@Param("purchaseId") Long purchaseId);
    
    @Query("select p from Payment p join fetch p.purchase pu join fetch pu.user u order by p.paidAt desc")
    List<Payment> findAllWithCustomerInfo();
}



