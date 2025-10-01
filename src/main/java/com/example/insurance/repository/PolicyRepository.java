package com.example.insurance.repository;

import com.example.insurance.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    List<Policy> findByCategory(String category);

    @Query("SELECT p FROM Policy p WHERE p.policyId = :policyId")
    List<Policy> findByPolicyId(@Param("policyId") Long policyId);

    @Query("SELECT p FROM Policy p WHERE LOWER(p.category) = LOWER(:category)")
    List<Policy> findByCategoryIgnoreCase(@Param("category") String category);

    @Query("SELECT p FROM Policy p WHERE p.policyName LIKE %:name%")
    List<Policy> findByPolicyNameContaining(@Param("name") String name);

    @Query("SELECT p FROM Policy p WHERE p.startDate <= :date AND p.endDate >= :date")
    List<Policy> findActivePoliciesOnDate(@Param("date") LocalDate date);

    @Query("SELECT DISTINCT p.category FROM Policy p")
    List<String> findAllCategories();
}


