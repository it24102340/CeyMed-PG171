package com.healthinsurancemanagementsystem.CeyMedInsurance.repository;

import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByCustomerId(String customerId);
    boolean existsByEmail(String email);
    boolean existsByCustomerId(String customerId);

    @Query("SELECT u FROM User u WHERE u.isAdmin = true")
    List<User> findAllAdmins();

    @Query("SELECT u FROM User u WHERE u.isAdmin = false")
    List<User> findAllRegularUsers();

    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:name% OR u.lastName LIKE %:name%")
    List<User> findByNameContaining(@Param("name") String name);

    @Query("SELECT u.customerId FROM User u WHERE u.customerId LIKE 'C%' ORDER BY CAST(SUBSTRING(u.customerId, 2) AS INTEGER) DESC")
    List<String> findCustomerIdsStartingWithC();

    @Query("SELECT u.customerId FROM User u WHERE u.customerId LIKE 'S%' ORDER BY CAST(SUBSTRING(u.customerId, 2) AS INTEGER) DESC")
    List<String> findStaffIdsStartingWithS();

    @Query("SELECT u.customerId FROM User u WHERE u.customerId LIKE 'A%' ORDER BY CAST(SUBSTRING(u.customerId, 2) AS INTEGER) DESC")
    List<String> findAdminIdsStartingWithA();
}


