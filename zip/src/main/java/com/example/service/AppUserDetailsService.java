package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User u = userRepo.findByUsernameIgnoreCaseOrEmailIgnoreCase(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản"));

        boolean enabled = u.getStatus() == User.Status.ACTIVE; // ✅ enum lồng trong User
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole().name()));

        return new org.springframework.security.core.userdetails.User(
                u.getUsername(),
                u.getPassword(),
                enabled, true, true, enabled,
                authorities
        );
    }
}
