package com.example.insurance.config;

import com.example.insurance.entity.Policy;
import com.example.insurance.entity.User;
import com.example.insurance.repository.PolicyRepository;
import com.example.insurance.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           PolicyRepository policyRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.policyRepository = policyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        createSamplePolicies();
        createAdminUser();
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
            userRepository.save(admin);
        }
    }
}


