package com.example.service.impl;

import com.example.dto.RegisterRequest;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;   // ✅ thống nhất tên biến
    private final PasswordEncoder passwordEncoder; // ✅ được tiêm qua constructor của Lombok

    @Override
    @Transactional
    public void registerCustomer(RegisterRequest req) {
        if (!req.getPassword().equals(req.getRePassword())) {
            throw new IllegalArgumentException("Mật khẩu nhập lại không khớp");
        }
        if (userRepository.existsByUsernameIgnoreCase(req.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }
        if (userRepository.existsByEmailIgnoreCase(req.getEmail())) {
            throw new IllegalArgumentException("Email đã được dùng");
        }

        User u = new User();
        u.setFullName(req.getFullName().trim());
        u.setEmail(req.getEmail().trim());
        u.setUsername(req.getUsername().trim());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setRole(User.Role.CUSTOMER);   // enum lồng trong User
        u.setStatus(User.Status.ACTIVE); // enum lồng trong User

        userRepository.save(u);
    }

    @Override
    @Transactional
    public void updateStatus(Integer userId, User.Status status) {
        userRepository.findById(userId).ifPresent(u -> {
            u.setStatus(status);
            userRepository.save(u);
        });
    }

    // Các hàm CRUD/tra cứu khác (nếu interface bạn đang có)
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> searchUsers(String keyword) {
        return userRepository.findByFullNameContainingIgnoreCase(keyword);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public List<User> getUsersByStatus(User.Status status) {
        return userRepository.findByStatus(status);
    }
}
