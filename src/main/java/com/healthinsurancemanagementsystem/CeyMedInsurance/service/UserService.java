package com.healthinsurancemanagementsystem.CeyMedInsurance.service;

import com.healthinsurancemanagementsystem.CeyMedInsurance.dto.LoginDto;
import com.healthinsurancemanagementsystem.CeyMedInsurance.dto.UserRegistrationDto;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.User;
import com.healthinsurancemanagementsystem.CeyMedInsurance.repository.UserRepository;
import com.healthinsurancemanagementsystem.CeyMedInsurance.repository.PurchaseRepository;
import com.healthinsurancemanagementsystem.CeyMedInsurance.repository.PaymentRepository;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Purchase;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Payment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PurchaseRepository purchaseRepository;
    private final PaymentRepository paymentRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                      PurchaseRepository purchaseRepository, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.purchaseRepository = purchaseRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public User registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("User with email " + registrationDto.getEmail() + " already exists");
        }
        
        // Generate appropriate ID based on role (if provided) or default to customer
        String role = registrationDto.getRole();
        String userId;
        
        if (role != null && !role.isEmpty()) {
            // Admin page registration with role selection
            if ("admin".equals(role)) {
                userId = generateNextAdminId();
            } else if ("staff".equals(role)) {
                userId = generateNextStaffId();
            } else {
                userId = generateNextCustomerId();
            }
        } else {
            // Signup page registration - always customer
            userId = generateNextCustomerId();
            role = "customer";
        }
        
        User user = new User();
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setCustomerId(userId);
        
        // Set role based on selection or default to customer
        if ("admin".equals(role)) {
            user.setIsAdmin(true);
            user.setIsStaff(false);
        } else if ("staff".equals(role)) {
            user.setIsAdmin(false);
            user.setIsStaff(true);
        } else {
            user.setIsAdmin(false);
            user.setIsStaff(false);
        }
        user.setDateOfBirth(registrationDto.getDateOfBirth());
        user.setAddress(registrationDto.getAddress());
        user.setOccupation(registrationDto.getOccupation());
        user.setMonthlySalary(registrationDto.getMonthlySalary());
        user.setMaritalStatus(registrationDto.getMaritalStatus());
        user.setGender(registrationDto.getGender());
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User authenticateUser(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        if (!passwordMatches(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public User authenticateAdmin(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid admin credentials"));
        if (!Boolean.TRUE.equals(user.getIsAdmin())) {
            throw new RuntimeException("User is not admin");
        }
        if (!passwordMatches(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid admin credentials");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public User authenticateStaff(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid staff credentials"));
        if (!Boolean.TRUE.equals(user.getIsStaff())) {
            throw new RuntimeException("User is not staff");
        }
        if (!passwordMatches(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid staff credentials");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public List<User> getAllRegularUsers() { return userRepository.findAllRegularUsers(); }

    @Transactional
    public User updateUser(Long id, UserRegistrationDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if email is being changed and if it's already taken
        if (!user.getEmail().equals(userDto.getEmail()) && userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Check if customer ID is being changed and if it's already taken
        if (!user.getCustomerId().equals(userDto.getCustomerId()) && userRepository.existsByCustomerId(userDto.getCustomerId())) {
            throw new RuntimeException("Customer ID already exists");
        }
        
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setCustomerId(userDto.getCustomerId());
        user.setDateOfBirth(userDto.getDateOfBirth());
        user.setAddress(userDto.getAddress());
        user.setOccupation(userDto.getOccupation());
        user.setMonthlySalary(userDto.getMonthlySalary());
        user.setMaritalStatus(userDto.getMaritalStatus());
        user.setGender(userDto.getGender());
        
        // Only update password if provided
        if (userDto.getPassword() != null && !userDto.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Prevent admin from deleting themselves
        if (Boolean.TRUE.equals(user.getIsAdmin())) {
            throw new RuntimeException("Cannot delete admin user");
        }
        
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean verifyCurrentPassword(User user, String currentPassword) {
        return passwordMatches(currentPassword, user.getPassword());
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserByCustomerId(String customerId) {
        return userRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        return rawPassword.equals(storedPassword);
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public List<Purchase> getCustomerPurchases(Long userId) {
        try {
            return purchaseRepository.findDetailedByUserId(userId);
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Payment> getCustomerPayments(Long userId) {
        try {
            List<Purchase> purchases = purchaseRepository.findByUserId(userId);
            if (purchases == null || purchases.isEmpty()) {
                return List.of();
            }
            return purchases.stream()
                    .flatMap(purchase -> {
                        try {
                            return paymentRepository.findByPurchaseId(purchase.getPurchaseId()).stream();
                        } catch (Exception e) {
                            return java.util.stream.Stream.empty();
                        }
                    })
                    .toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Generates the next available customer ID in the format C001, C002, C003, etc.
     * @return the next customer ID
     */
    private String generateNextCustomerId() {
        try {
            List<String> existingIds = userRepository.findCustomerIdsStartingWithC();
            int nextNumber = 1;
            
            if (!existingIds.isEmpty()) {
                // Get the highest number from existing IDs
                String highestId = existingIds.get(0);
                if (highestId != null && highestId.startsWith("C") && highestId.length() > 1) {
                    try {
                        String numberPart = highestId.substring(1);
                        nextNumber = Integer.parseInt(numberPart) + 1;
                    } catch (NumberFormatException e) {
                        // If parsing fails, start from 1
                        nextNumber = 1;
                    }
                }
            }
            
            return String.format("C%03d", nextNumber);
        } catch (Exception e) {
            // Fallback to C001 if there's any error
            return "C001";
        }
    }

    /**
     * Generates the next available staff ID in the format S001, S002, S003, etc.
     * @return the next staff ID
     */
    private String generateNextStaffId() {
        try {
            List<String> existingIds = userRepository.findStaffIdsStartingWithS();
            int nextNumber = 1;
            
            if (!existingIds.isEmpty()) {
                // Get the highest number from existing IDs
                String highestId = existingIds.get(0);
                if (highestId != null && highestId.startsWith("S") && highestId.length() > 1) {
                    try {
                        String numberPart = highestId.substring(1);
                        nextNumber = Integer.parseInt(numberPart) + 1;
                    } catch (NumberFormatException e) {
                        // If parsing fails, start from 1
                        nextNumber = 1;
                    }
                }
            }
            
            return String.format("S%03d", nextNumber);
        } catch (Exception e) {
            // Fallback to S001 if there's any error
            return "S001";
        }
    }

    /**
     * Generates the next available admin ID in the format A001, A002, A003, etc.
     * @return the next admin ID
     */
    private String generateNextAdminId() {
        try {
            List<String> existingIds = userRepository.findAdminIdsStartingWithA();
            int nextNumber = 1;
            
            if (!existingIds.isEmpty()) {
                // Get the highest number from existing IDs
                String highestId = existingIds.get(0);
                if (highestId != null && highestId.startsWith("A") && highestId.length() > 1) {
                    try {
                        String numberPart = highestId.substring(1);
                        nextNumber = Integer.parseInt(numberPart) + 1;
                    } catch (NumberFormatException e) {
                        // If parsing fails, start from 1
                        nextNumber = 1;
                    }
                }
            }
            
            return String.format("A%03d", nextNumber);
        } catch (Exception e) {
            // Fallback to A001 if there's any error
            return "A001";
        }
    }
}


