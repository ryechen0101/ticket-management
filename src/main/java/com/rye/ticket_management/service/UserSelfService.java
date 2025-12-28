package com.rye.ticket_management.service;

import com.rye.ticket_management.dto.auth.ChangeMyPasswordRequest;
import com.rye.ticket_management.entity.User;
import com.rye.ticket_management.exception.BadRequestException;
import com.rye.ticket_management.exception.ResourceNotFoundException;
import com.rye.ticket_management.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserSelfService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSelfService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private User currentUserEntity() {
        String username = AuthUtil.currentUsername();
        if (username == null) throw new BadRequestException("尚未登入");
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("找不到使用者: " + username));
    }

    public void changeMyPassword(ChangeMyPasswordRequest req) {
        User me = currentUserEntity();

        if (!passwordEncoder.matches(req.oldPassword(), me.getPassword())) {
            throw new BadRequestException("舊密碼不正確");
        }

        if (req.oldPassword().equals(req.newPassword())) {
            throw new BadRequestException("新密碼不可與舊密碼相同");
        }

        me.setPassword(passwordEncoder.encode(req.newPassword()));
        userRepository.save(me);
    }
}
