package com.rye.ticket_management.service;

import com.rye.ticket_management.dto.admin.*;
import com.rye.ticket_management.entity.User;
import com.rye.ticket_management.exception.BadRequestException;
import com.rye.ticket_management.exception.ResourceNotFoundException;
import com.rye.ticket_management.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到使用者: " + id));
        return UserResponse.from(u);
    }

    public UserResponse createUser(CreateUserRequest req) {
        String username = req.username().trim();

        if (userRepository.findByUsername(username).isPresent()) {
            throw new BadRequestException("帳號已存在: " + username);
        }

        User u = new User();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(req.password()));
        u.setRole(User.Role.valueOf(req.role()));
        u.setEnabled(true);

        return UserResponse.from(userRepository.save(u));
    }

    public UserResponse updateRole(Long id, UpdateRoleRequest req) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到使用者: " + id));

        u.setRole(User.Role.valueOf(req.role()));
        return UserResponse.from(userRepository.save(u));
    }

    public void updatePassword(Long id, UpdatePasswordRequest req) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到使用者: " + id));

        u.setPassword(passwordEncoder.encode(req.password()));
        userRepository.save(u);
    }

    public UserResponse setEnabled(Long id, boolean enabled) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到使用者: " + id));

        u.setEnabled(enabled);
        return UserResponse.from(userRepository.save(u));
    }

    public void disableUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("找不到使用者: " + id);
        }
        setEnabled(id, false);
    }
}
