package com.minispring.example.ioc.service;

import com.minispring.beans.factory.annotation.Autowired;
import com.minispring.beans.factory.annotation.Service;
import com.minispring.example.ioc.domain.User;
import com.minispring.example.ioc.repository.UserRepository;

import java.util.List;

/**
 * 用户业务逻辑层
 * 演示 @Service 注解和 @Autowired 依赖注入
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(String name, String email) {
        System.out.println("[Service] Creating user: " + name);

        // 业务逻辑验证
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be empty");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        User user = new User(null, name, email);
        return userRepository.save(user);
    }

    public User getUser(Long id) {
        System.out.println("[Service] Getting user by id: " + id);
        User user = userRepository.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + id);
        }
        return user;
    }

    public List<User> getAllUsers() {
        System.out.println("[Service] Getting all users");
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        System.out.println("[Service] Deleting user: " + id);
        userRepository.deleteById(id);
    }
}
