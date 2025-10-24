package com.healthinsurancemanagementsystem.CeyMedInsurance.controller;

import com.healthinsurancemanagementsystem.CeyMedInsurance.dto.LoginDto;
import com.healthinsurancemanagementsystem.CeyMedInsurance.dto.LoginRequest;
import com.healthinsurancemanagementsystem.CeyMedInsurance.dto.ProfileUpdateDto;
import com.healthinsurancemanagementsystem.CeyMedInsurance.dto.UserRegistrationDto;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.User;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Purchase;
import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Payment;
import com.healthinsurancemanagementsystem.CeyMedInsurance.service.UserService;
import com.healthinsurancemanagementsystem.CeyMedInsurance.service.PolicyService;
import com.healthinsurancemanagementsystem.CeyMedInsurance.repository.PaymentRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final PolicyService policyService;
    private final PaymentRepository paymentRepository;

    public UserController(UserService userService, PolicyService policyService, PaymentRepository paymentRepository) {
        this.userService = userService;
        this.policyService = policyService;
        this.paymentRepository = paymentRepository;
    }

    @PostMapping("/login")
    public String doLogin(@Valid @ModelAttribute("loginDto") LoginRequest login,
                          BindingResult result,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "login";
        }
        try {
            var user = userService.authenticateUser(new LoginDto() {{
                setEmail(login.getEmail());
                setPassword(login.getPassword());
            }});
            session.setAttribute("user", user);
            session.setAttribute("isAdmin", user.getIsAdmin());
            return "redirect:/policy/policies";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/user/login";
        }
    }

    @PostMapping("/admin-login")
    public String doAdminLogin(@Valid @ModelAttribute("loginDto") LoginRequest login,
                               BindingResult result,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "login";
        }
        try {
            var admin = userService.authenticateAdmin(new LoginDto() {{
                setEmail(login.getEmail());
                setPassword(login.getPassword());
            }});
            session.setAttribute("user", admin);
            session.setAttribute("isAdmin", true);
            return "redirect:/policy/admin";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/user/login";
        }
    }

    @PostMapping("/staff-login")
    public String doStaffLogin(@Valid @ModelAttribute("loginDto") LoginRequest login,
                               BindingResult result,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "login";
        }
        try {
            var staff = userService.authenticateStaff(new LoginDto() {{
                setEmail(login.getEmail());
                setPassword(login.getPassword());
            }});
            session.setAttribute("user", staff);
            session.setAttribute("isStaff", true);
            return "redirect:/user/staff/dashboard";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/user/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        // Pre-populate the form with current user data
        ProfileUpdateDto profileDto = new ProfileUpdateDto();
        profileDto.setFirstName(currentUser.getFirstName());
        profileDto.setLastName(currentUser.getLastName());
        profileDto.setEmail(currentUser.getEmail());
        
        model.addAttribute("user", currentUser);
        model.addAttribute("profileUpdateDto", profileDto);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("profileUpdateDto") ProfileUpdateDto profileDto,
                               BindingResult result,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        // Custom validation for password change
        if (profileDto.getNewPassword() != null && !profileDto.getNewPassword().trim().isEmpty()) {
            if (profileDto.getCurrentPassword() == null || profileDto.getCurrentPassword().trim().isEmpty()) {
                result.rejectValue("currentPassword", "error.currentPassword", "Current password is required to change password");
            } else if (profileDto.getConfirmPassword() == null || profileDto.getConfirmPassword().trim().isEmpty()) {
                result.rejectValue("confirmPassword", "error.confirmPassword", "Please confirm your new password");
            } else if (!profileDto.getNewPassword().equals(profileDto.getConfirmPassword())) {
                result.rejectValue("confirmPassword", "error.confirmPassword", "New passwords do not match");
            } else if (!userService.verifyCurrentPassword(currentUser, profileDto.getCurrentPassword())) {
                result.rejectValue("currentPassword", "error.currentPassword", "Current password is incorrect");
            }
        }
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please check the form for errors.");
            return "redirect:/user/profile";
        }
        
        try {
            // Update only the basic fields that customers should be able to modify
            currentUser.setFirstName(profileDto.getFirstName());
            currentUser.setLastName(profileDto.getLastName());
            currentUser.setEmail(profileDto.getEmail());
            
            // Update password if provided and verified
            if (profileDto.getNewPassword() != null && !profileDto.getNewPassword().trim().isEmpty()) {
                currentUser.setPassword(userService.encodePassword(profileDto.getNewPassword()));
            }
            
            // Save the updated user
            userService.saveUser(currentUser);
            
            // Update session with new user data
            session.setAttribute("user", currentUser);
            
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        
        return "redirect:/user/profile";
    }
    @GetMapping("/login")
    public String showLogin(Model model) {
        model.addAttribute("loginDto", new LoginRequest());
        return "login";
    }

    @GetMapping("/signup")
    public String showSignup(Model model) {
        model.addAttribute("userRegistrationDto", new UserRegistrationDto());
        return "signup";
    }

    @PostMapping("/signup")
    public String doSignup(@Valid @ModelAttribute("userRegistrationDto") UserRegistrationDto signup,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "signup";
        }
        try {
            User registeredUser = userService.registerUser(signup);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Registration successful! Your Customer ID is: " + registeredUser.getCustomerId() + 
                ". Please login with your credentials.");
            return "redirect:/user/login";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/user/signup";
        }
    }

    @GetMapping("/admin/users")
    public String adminUsers(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !Boolean.TRUE.equals(user.getIsAdmin())) {
            return "redirect:/user/login";
        }
        model.addAttribute("users", userService.getAllRegularUsers());
        model.addAttribute("userRegistrationDto", new UserRegistrationDto());
        return "admin-users";
    }

    @PostMapping("/admin/users/create")
    public String createUser(@Valid @ModelAttribute("userRegistrationDto") UserRegistrationDto userDto,
                           @RequestParam(value = "userRole", defaultValue = "customer") String userRole,
                           BindingResult result,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || !Boolean.TRUE.equals(user.getIsAdmin())) {
            return "redirect:/user/login";
        }
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please check the form for errors.");
            return "redirect:/user/admin/users";
        }
        try {
            User createdUser = userService.registerUser(userDto);
            
            // Set role based on selection
            if ("admin".equals(userRole)) {
                createdUser.setIsAdmin(true);
                createdUser.setIsStaff(false);
            } else if ("staff".equals(userRole)) {
                createdUser.setIsAdmin(false);
                createdUser.setIsStaff(true);
            } else {
                createdUser.setIsAdmin(false);
                createdUser.setIsStaff(false);
            }
            
            userService.updateUser(createdUser.getId(), userDto);
            redirectAttributes.addFlashAttribute("successMessage", 
                "User created successfully as " + userRole + "! Customer ID: " + createdUser.getCustomerId());
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/user/admin/users";
    }

    @PostMapping("/admin/users/update/{id}")
    public String updateUser(@PathVariable Long id,
                           @Valid @ModelAttribute("userRegistrationDto") UserRegistrationDto userDto,
                           @RequestParam(value = "role", defaultValue = "customer") String role,
                           BindingResult result,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || !Boolean.TRUE.equals(user.getIsAdmin())) {
            return "redirect:/user/login";
        }
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please check the form for errors.");
            return "redirect:/user/admin/users";
        }
        try {
            userService.updateUser(id, userDto);
            
            // Update role
            User updatedUser = userService.getUserById(id);
            if ("admin".equals(role)) {
                updatedUser.setIsAdmin(true);
                updatedUser.setIsStaff(false);
            } else if ("staff".equals(role)) {
                updatedUser.setIsAdmin(false);
                updatedUser.setIsStaff(true);
            } else {
                updatedUser.setIsAdmin(false);
                updatedUser.setIsStaff(false);
            }
            userService.updateUser(id, userDto);
            
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/user/admin/users";
    }

    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Long id,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || !Boolean.TRUE.equals(user.getIsAdmin())) {
            return "redirect:/user/login";
        }
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/user/admin/users";
    }

    @GetMapping("/staff/dashboard")
    public String staffDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !Boolean.TRUE.equals(user.getIsStaff())) {
            return "redirect:/user/login";
        }
        return "staff-dashboard";
    }

    @GetMapping("/staff/customer/{customerId}")
    public String viewCustomer(@PathVariable String customerId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !Boolean.TRUE.equals(user.getIsStaff())) {
            return "redirect:/user/login";
        }
        try {
            User customer = userService.getUserByCustomerId(customerId);
            model.addAttribute("customer", customer);
            return "customer-details";
        } catch (RuntimeException ex) {
            return "redirect:/user/staff/dashboard";
        }
    }

    @GetMapping("/staff/search-customer")
    public String searchCustomer(@RequestParam String customerId, 
                               HttpSession session, 
                               Model model,
                               RedirectAttributes redirectAttributes) {
        User staffUser = (User) session.getAttribute("user");
        if (staffUser == null || !Boolean.TRUE.equals(staffUser.getIsStaff())) {
            return "redirect:/user/login";
        }
        
        try {
            User customer = userService.getUserByCustomerId(customerId);
            if (customer == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Customer with ID '" + customerId + "' not found.");
                return "redirect:/user/staff/dashboard";
            }
            
            model.addAttribute("customer", customer);
            model.addAttribute("staffUser", staffUser);
            
            // Get customer's purchases and payments
            List<Purchase> purchases = userService.getCustomerPurchases(customer.getId());
            List<Payment> payments = userService.getCustomerPayments(customer.getId());
            
            // Ensure lists are not null
            if (purchases == null) {
                purchases = List.of();
            }
            if (payments == null) {
                payments = List.of();
            }
            
            model.addAttribute("purchases", purchases);
            model.addAttribute("payments", payments);
            
            return "customer-details";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error searching customer: " + ex.getMessage());
            return "redirect:/user/staff/dashboard";
        }
    }

    @GetMapping("/staff/profile")
    public String staffProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !Boolean.TRUE.equals(user.getIsStaff())) {
            return "redirect:/user/login";
        }
        model.addAttribute("user", user);
        return "staff-profile";
    }

    @GetMapping("/staff/payments")
    public String staffViewAllPayments(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !Boolean.TRUE.equals(user.getIsStaff())) {
            return "redirect:/user/login";
        }
        
        // Get all payments with customer information loaded
        List<Payment> allPayments = paymentRepository.findAllWithCustomerInfo();
        
        model.addAttribute("payments", allPayments);
        model.addAttribute("staffUser", user);
        return "staff-all-payments";
    }

    @GetMapping("/staff/policies")
    public String staffViewAllPolicies(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !Boolean.TRUE.equals(user.getIsStaff())) {
            return "redirect:/user/login";
        }
        
        // Get all policies for staff to view
        model.addAttribute("policies", policyService.convertToDtoList(policyService.getAllPolicies()));
        model.addAttribute("categories", policyService.getAllCategories());
        model.addAttribute("staffUser", user);
        return "staff-policies";
    }

    // Test endpoint for customer search without authentication (for testing only)
    @GetMapping("/test/search-customer")
    public String testSearchCustomer(@RequestParam String customerId, Model model) {
        try {
            // First, try to find the customer
            User customer = userService.getUserByCustomerId(customerId);
            model.addAttribute("customer", customer);
            
            // Get customer's purchases and payments
            List<Purchase> purchases = userService.getCustomerPurchases(customer.getId());
            List<Payment> payments = userService.getCustomerPayments(customer.getId());
            
            // Ensure lists are not null
            if (purchases == null) {
                purchases = List.of();
            }
            if (payments == null) {
                payments = List.of();
            }
            
            model.addAttribute("purchases", purchases);
            model.addAttribute("payments", payments);
            
            return "customer-details";
        } catch (Exception ex) {
            // If customer not found, create a test customer with sample data
            if (ex.getMessage().contains("Customer not found")) {
                try {
                    // Create a test customer
                    UserRegistrationDto testCustomer = new UserRegistrationDto();
                    testCustomer.setFirstName("John");
                    testCustomer.setLastName("Doe");
                    testCustomer.setEmail("customer@test.com");
                    testCustomer.setPassword("test123");
                    testCustomer.setCustomerId(customerId);
                    testCustomer.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
                    testCustomer.setAddress("123 Main Street, City");
                    testCustomer.setOccupation("Software Engineer");
                    testCustomer.setMonthlySalary(50000.0);
                    testCustomer.setMaritalStatus("Single");
                    testCustomer.setGender("Male");
                    testCustomer.setRole("customer");
                    
                    User customer = userService.registerUser(testCustomer);
                    model.addAttribute("customer", customer);
                    
                    // Get the created data
                    List<Purchase> purchases = userService.getCustomerPurchases(customer.getId());
                    List<Payment> payments = userService.getCustomerPayments(customer.getId());
                    
                    model.addAttribute("purchases", purchases != null ? purchases : List.of());
                    model.addAttribute("payments", payments != null ? payments : List.of());
                    
                    return "customer-details";
                } catch (Exception createEx) {
                    model.addAttribute("errorMessage", "Could not create test customer: " + createEx.getMessage());
                    return "error";
                }
            } else {
                model.addAttribute("errorMessage", "Error: " + ex.getMessage());
                return "error";
            }
        }
    }
}


