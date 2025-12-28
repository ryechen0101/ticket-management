package com.rye.ticket_management.controller;

import com.rye.ticket_management.dto.auth.*;
import com.rye.ticket_management.entity.User;
import com.rye.ticket_management.exception.BadRequestException;
import com.rye.ticket_management.repository.UserRepository;
import com.rye.ticket_management.service.JwtService;
import com.rye.ticket_management.service.UserSelfService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSelfService userSelfService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserSelfService userSelfService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userSelfService = userSelfService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );

        UserDetails user = (UserDetails) auth.getPrincipal();

        String token = jwtService.generateToken(
                user.getUsername(),
                user.getAuthorities()
        );

        String role = user.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .findFirst()
                .orElse("");

        return new LoginResponse(token, user.getUsername(), role);
    }

    // 自行註冊（只能註冊 USER）
    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest req) {
        String username = req.username().trim();

        if (userRepository.findByUsername(username).isPresent()) {
            throw new BadRequestException("帳號已存在: " + username);
        }

        User u = new User();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(req.password()));
        u.setRole(User.Role.USER);
        u.setEnabled(true);

        userRepository.save(u);
        return "註冊成功，請登入";
    }

    // 登入後：自己改密碼（需要舊密碼）
    @PutMapping("/me/password")
    public String changeMyPassword(@Valid @RequestBody ChangeMyPasswordRequest req) {
        userSelfService.changeMyPassword(req);
        return "密碼已更新";
    }
}
