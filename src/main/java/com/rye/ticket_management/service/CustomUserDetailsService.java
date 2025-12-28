package com.rye.ticket_management.service;

import com.rye.ticket_management.entity.User;
import com.rye.ticket_management.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User u = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("找不到使用者：" + username));

        String role = u.getRole().name(); // ADMIN / AGENT / USER
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

        boolean enabled = Boolean.TRUE.equals(u.getEnabled());

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!enabled)
                .build();
    }
}
