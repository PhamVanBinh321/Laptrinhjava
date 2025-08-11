package com.example.service;


import com.example.model.User;
import java.util.List;
import java.util.Optional;
import com.example.dto.RegisterRequest;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Integer id);
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String email);
    List<User> searchUsers(String keyword);
    User saveUser(User user);      // Cho cả tạo mới và cập nhật
    void deleteUser(Integer id);
    List<User> getUsersByRole(User.Role role);
    List<User> getUsersByStatus(User.Status status);
    void updateStatus(Integer userId, User.Status status);
        void registerCustomer(RegisterRequest request);

}
