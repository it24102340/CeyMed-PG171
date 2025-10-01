package com.example.insurance.controller;

import com.example.insurance.dto.LoginRequest;
import com.example.insurance.dto.UserRegistrationDto;
import com.example.insurance.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
            var user = userService.authenticateUser(new com.example.insurance.dto.LoginDto() {{
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
            var admin = userService.authenticateAdmin(new com.example.insurance.dto.LoginDto() {{
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

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("user", user);
        return "profile";
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
            userService.registerUser(signup);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please login with your credentials.");
            return "redirect:/user/login";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/user/signup";
        }
    }
}


