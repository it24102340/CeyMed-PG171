package com.example.insurance.service;

import com.example.insurance.dto.LoginDto;
import com.example.insurance.dto.UserRegistrationDto;
import com.example.insurance.entity.User;
import com.example.insurance.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("User with email " + registrationDto.getEmail() + " already exists");
        }
        User user = new User();
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setIsAdmin(false);
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
    public List<User> getAllRegularUsers() { return userRepository.findAllRegularUsers(); }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        return rawPassword.equals(storedPassword);
    }
}


