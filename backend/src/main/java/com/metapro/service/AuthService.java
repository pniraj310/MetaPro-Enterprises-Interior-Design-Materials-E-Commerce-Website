package com.metapro.service;

import com.metapro.model.AdminUser;
import com.metapro.repository.AdminUserRepository;
import com.metapro.security.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Value("${app.admin.initial-user:admin}")
    private String initialUser;

    @Value("${app.admin.initial-email:pdheeraj351@gmail.com}")
    private String initialEmail;

    @Value("${app.admin.initial-password:}")
    private String initialPassword;

    public AuthService(AdminUserRepository adminUserRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public void ensureDefaultAdmin() {
        if (adminUserRepository.count() == 0) {
            AdminUser defaultAdmin = new AdminUser(
                    initialUser,
                    initialEmail,
                    passwordEncoder.encode(initialPassword),
                    "ADMIN"
            );
            adminUserRepository.save(defaultAdmin);
        }
    }

    public Optional<Map<String, Object>> authenticate(String usernameOrEmail, String rawPassword) {
        ensureDefaultAdmin();

        Optional<AdminUser> userOpt = adminUserRepository.findByUsernameIgnoreCase(usernameOrEmail);
        if (userOpt.isEmpty()) {
            userOpt = adminUserRepository.findByEmailIgnoreCase(usernameOrEmail);
        }

        if (userOpt.isPresent()) {
            AdminUser user = userOpt.get();
            if (passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
                String token = jwtUtils.generateToken(user.getUsername(), user.getRole());
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("username", user.getUsername());
                response.put("email", user.getEmail());
                return Optional.of(response);
            }
        }

        return Optional.empty();
    }
}
