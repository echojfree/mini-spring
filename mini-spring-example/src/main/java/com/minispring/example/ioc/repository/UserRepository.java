package com.minispring.example.ioc.repository;

import com.minispring.beans.factory.annotation.Repository;
import com.minispring.example.ioc.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用户数据访问层
 */
@Repository
public class UserRepository {

    private final List<User> users = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        users.add(user);
        System.out.println("[Repository] User saved: " + user);
        return user;
    }

    public User findById(Long id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    public void deleteById(Long id) {
        users.removeIf(u -> u.getId().equals(id));
        System.out.println("[Repository] User deleted: " + id);
    }
}
