package com.example.repository;


import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;


public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRole(User.Role role);
    List<User> findByStatus(User.Status status);
    List<User> findByFullNameContainingIgnoreCase(String keyword);
    // Thêm các hàm search/filter khác khi cần
    Optional<User> findById(Integer id);
}
