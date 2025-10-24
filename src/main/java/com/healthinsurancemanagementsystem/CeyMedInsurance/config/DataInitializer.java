package com.healthinsurancemanagementsystem.CeyMedInsurance.config;

import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Payment;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Policy;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Purchase;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.PurchaseStatus;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.User;
import com.healthinsurancemanagementsystem.CeyMedInsurance.repository.PaymentRepository;
import com.healthinsurancemanagementsystem.CeyMedInsurance.repository.PolicyRepository;
import com.healthinsurancemanagementsystem.CeyMedInsurance.repository.PurchaseRepository;
import com.healthinsurancemanagementsystem.CeyMedInsurance.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final PurchaseRepository purchaseRepository;
    private final PaymentRepository paymentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           PolicyRepository policyRepository,
                           PurchaseRepository purchaseRepository,
                           PaymentRepository paymentRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.policyRepository = policyRepository;
        this.purchaseRepository = purchaseRepository;
        this.paymentRepository = paymentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        createSamplePolicies();
        createAdminUser();
        createTestUser();
        createSamplePurchases();
    }

    private void createSamplePolicies() {
        if (policyRepository.count() == 0) {
            Policy premiumHealth = new Policy();
            premiumHealth.setPolicyName("Premium Health Coverage");
            premiumHealth.setCoverage(new BigDecimal("1.5"));
            premiumHealth.setCategory("Premium");
            premiumHealth.setStartDate(LocalDate.now());
            premiumHealth.setEndDate(LocalDate.now().plusYears(1));
            premiumHealth.setDuration(12);
            premiumHealth.setPremium(new BigDecimal("200.00"));
            policyRepository.save(premiumHealth);

            Policy premiumLife = new Policy();
            premiumLife.setPolicyName("Premium Life Insurance");
            premiumLife.setCoverage(new BigDecimal("2.0"));
            premiumLife.setCategory("Premium");
            premiumLife.setStartDate(LocalDate.now());
            premiumLife.setEndDate(LocalDate.now().plusYears(2));
            premiumLife.setDuration(24);
            premiumLife.setPremium(new BigDecimal("150.00"));
            policyRepository.save(premiumLife);

            Policy goldHealth = new Policy();
            goldHealth.setPolicyName("Gold Health Plan");
            goldHealth.setCoverage(new BigDecimal("1.2"));
            goldHealth.setCategory("Gold");
            goldHealth.setStartDate(LocalDate.now());
            goldHealth.setEndDate(LocalDate.now().plusYears(1));
            goldHealth.setDuration(12);
            goldHealth.setPremium(new BigDecimal("120.00"));
            policyRepository.save(goldHealth);

            Policy goldAccident = new Policy();
            goldAccident.setPolicyName("Gold Accident Coverage");
            goldAccident.setCoverage(new BigDecimal("1.0"));
            goldAccident.setCategory("Gold");
            goldAccident.setStartDate(LocalDate.now());
            goldAccident.setEndDate(LocalDate.now().plusMonths(6));
            goldAccident.setDuration(6);
            goldAccident.setPremium(new BigDecimal("80.00"));
            policyRepository.save(goldAccident);

            Policy silverBasic = new Policy();
            silverBasic.setPolicyName("Silver Basic Coverage");
            silverBasic.setCoverage(new BigDecimal("0.8"));
            silverBasic.setCategory("Silver");
            silverBasic.setStartDate(LocalDate.now());
            silverBasic.setEndDate(LocalDate.now().plusYears(1));
            silverBasic.setDuration(12);
            silverBasic.setPremium(new BigDecimal("60.00"));
            policyRepository.save(silverBasic);

            Policy silverDental = new Policy();
            silverDental.setPolicyName("Silver Dental Care");
            silverDental.setCoverage(new BigDecimal("0.5"));
            silverDental.setCategory("Silver");
            silverDental.setStartDate(LocalDate.now());
            silverDental.setEndDate(LocalDate.now().plusMonths(12));
            silverDental.setDuration(12);
            silverDental.setPremium(new BigDecimal("40.00"));
            policyRepository.save(silverDental);
        }
    }

    private void createAdminUser() {
        if (!userRepository.existsByEmail("admin@ceymed.com")) {
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@ceymed.com");
            admin.setPassword(passwordEncoder.encode("1234"));
            admin.setIsAdmin(true);
            admin.setIsStaff(false);
            admin.setCustomerId("A001"); // Set initial admin ID
            userRepository.save(admin);
        }
    }

    private void createTestUser() {
        if (!userRepository.existsByEmail("test@example.com")) {
            User testUser = new User();
            testUser.setFirstName("Test");
            testUser.setLastName("User");
            testUser.setEmail("test@example.com");
            testUser.setPassword(passwordEncoder.encode("password"));
            testUser.setIsAdmin(false);
            testUser.setIsStaff(false);
            testUser.setCustomerId("C001"); // Set initial customer ID
            userRepository.save(testUser);
        }
    }

    private void createSamplePurchases() {
        if (purchaseRepository.count() == 0) {
            User testUser = userRepository.findByEmail("test@example.com").orElse(null);
            if (testUser != null) {
                // Create an active purchase
                Policy premiumHealth = policyRepository.findAll().stream()
                        .filter(p -> "Premium Health Coverage".equals(p.getPolicyName()))
                        .findFirst().orElse(null);
                
                if (premiumHealth != null) {
                    Purchase activePurchase = new Purchase();
                    activePurchase.setUser(testUser);
                    activePurchase.setPolicy(premiumHealth);
                    BigDecimal monthlyCost = premiumHealth.getPremium().multiply(premiumHealth.getCoverage());
                    activePurchase.setMonthlyCost(monthlyCost);
                    activePurchase.setPurchaseDate(LocalDateTime.now().minusDays(10));
                    activePurchase.setNextPaymentDate(LocalDateTime.now().plusMonths(1));
                    activePurchase.setStatus(PurchaseStatus.ACTIVE);
                    Purchase savedActivePurchase = purchaseRepository.save(activePurchase);
                    
                    // Create initial payment for active purchase
                    Payment initialPayment = new Payment();
                    initialPayment.setPurchase(savedActivePurchase);
                    initialPayment.setAmount(monthlyCost);
                    initialPayment.setPaidForMonth(LocalDateTime.now().minusDays(10).withDayOfMonth(1));
                    paymentRepository.save(initialPayment);
                }

                // Create a stopped purchase (for testing remove button)
                Policy goldHealth = policyRepository.findAll().stream()
                        .filter(p -> "Gold Health Plan".equals(p.getPolicyName()))
                        .findFirst().orElse(null);
                
                if (goldHealth != null) {
                    Purchase stoppedPurchase = new Purchase();
                    stoppedPurchase.setUser(testUser);
                    stoppedPurchase.setPolicy(goldHealth);
                    BigDecimal goldMonthlyCost = goldHealth.getPremium().multiply(goldHealth.getCoverage());
                    stoppedPurchase.setMonthlyCost(goldMonthlyCost);
                    stoppedPurchase.setPurchaseDate(LocalDateTime.now().minusDays(30));
                    stoppedPurchase.setNextPaymentDate(LocalDateTime.now().minusDays(5));
                    stoppedPurchase.setStatus(PurchaseStatus.STOPPED);
                    stoppedPurchase.setStopRequested(true);
                    stoppedPurchase.setStopApproved(true);
                    stoppedPurchase.setStopRequestDate(LocalDateTime.now().minusDays(7));
                    stoppedPurchase.setStopApprovalDate(LocalDateTime.now().minusDays(5));
                    Purchase savedStoppedPurchase = purchaseRepository.save(stoppedPurchase);
                    
                    // Create multiple payments for stopped purchase to show payment history
                    Payment payment1 = new Payment();
                    payment1.setPurchase(savedStoppedPurchase);
                    payment1.setAmount(goldMonthlyCost);
                    payment1.setPaidForMonth(LocalDateTime.now().minusDays(30).withDayOfMonth(1));
                    paymentRepository.save(payment1);
                    
                    Payment payment2 = new Payment();
                    payment2.setPurchase(savedStoppedPurchase);
                    payment2.setAmount(goldMonthlyCost);
                    payment2.setPaidForMonth(LocalDateTime.now().minusDays(15).withDayOfMonth(1));
                    paymentRepository.save(payment2);
                    
                    Payment payment3 = new Payment();
                    payment3.setPurchase(savedStoppedPurchase);
                    payment3.setAmount(goldMonthlyCost);
                    payment3.setPaidForMonth(LocalDateTime.now().minusDays(5).withDayOfMonth(1));
                    paymentRepository.save(payment3);
                }
            }
        }
    }
}


