package com.healthinsurancemanagementsystem.CeyMedInsurance.repository;

import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByUserId(Long userId);
    List<Purchase> findByUserEmail(String email);

    @Query("SELECT p FROM Purchase p WHERE p.stopRequested = true AND p.stopApproved = false")
    List<Purchase> findPendingStopRequests();

    @Query("SELECT p FROM Purchase p WHERE p.status = com.healthinsurancemanagementsystem.CeyMedInsurance.entity.PurchaseStatus.ACTIVE")
    List<Purchase> findActivePurchases();

    @Query("SELECT p FROM Purchase p WHERE p.user.id = :userId AND p.status = com.healthinsurancemanagementsystem.CeyMedInsurance.entity.PurchaseStatus.ACTIVE")
    List<Purchase> findActivePurchasesByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Purchase p JOIN FETCH p.user JOIN FETCH p.policy WHERE p.user.id = :userId")
    List<Purchase> findDetailedByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Purchase p JOIN FETCH p.user JOIN FETCH p.policy WHERE p.stopRequested = true AND p.stopApproved = false")
    List<Purchase> findPendingStopRequestsDetailed();
}


