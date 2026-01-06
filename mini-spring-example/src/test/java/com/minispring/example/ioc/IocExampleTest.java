package com.minispring.example.ioc;

import com.minispring.context.annotation.AnnotationConfigApplicationContext;
import com.minispring.example.ioc.domain.User;
import com.minispring.example.ioc.service.UserService;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * IoC 容器功能测试
 */
public class IocExampleTest {

    private AnnotationConfigApplicationContext context;
    private UserService userService;

    @Before
    public void setUp() throws Exception {
        // 创建应用上下文，使用包扫描
        context = new AnnotationConfigApplicationContext("com.minispring.example.ioc");
        // 获取 UserService bean
        userService = context.getBean("userService", UserService.class);
        assertNotNull("UserService should be autowired", userService);
    }

    @Test
    public void testCreateUser() {
        System.out.println("\n=== Test: Create User ===");

        // 创建用户
        User user = userService.createUser("Alice", "alice@example.com");

        // 验证
        assertNotNull("User should not be null", user);
        assertNotNull("User ID should be generated", user.getId());
        assertEquals("Alice", user.getName());
        assertEquals("alice@example.com", user.getEmail());

        System.out.println("User created successfully: " + user);
    }

    @Test
    public void testGetUser() {
        System.out.println("\n=== Test: Get User ===");

        // 创建用户
        User createdUser = userService.createUser("Bob", "bob@example.com");

        // 获取用户
        User retrievedUser = userService.getUser(createdUser.getId());

        // 验证
        assertNotNull("Retrieved user should not be null", retrievedUser);
        assertEquals(createdUser.getId(), retrievedUser.getId());
        assertEquals("Bob", retrievedUser.getName());
        assertEquals("bob@example.com", retrievedUser.getEmail());

        System.out.println("User retrieved successfully: " + retrievedUser);
    }

    @Test
    public void testGetAllUsers() {
        System.out.println("\n=== Test: Get All Users ===");

        // 创建多个用户
        userService.createUser("User1", "user1@example.com");
        userService.createUser("User2", "user2@example.com");
        userService.createUser("User3", "user3@example.com");

        // 获取所有用户
        List<User> users = userService.getAllUsers();

        // 验证
        assertNotNull("Users list should not be null", users);
        assertTrue("Should have at least 3 users", users.size() >= 3);

        System.out.println("Total users: " + users.size());
        users.forEach(System.out::println);
    }

    @Test
    public void testDeleteUser() {
        System.out.println("\n=== Test: Delete User ===");

        // 创建用户
        User user = userService.createUser("Charlie", "charlie@example.com");
        Long userId = user.getId();

        // 删除用户
        userService.deleteUser(userId);

        // 验证 - 获取已删除的用户应该抛出异常
        try {
            userService.getUser(userId);
            fail("Should throw exception for deleted user");
        } catch (IllegalArgumentException e) {
            assertEquals("User not found: " + userId, e.getMessage());
            System.out.println("User deleted successfully, exception as expected: " + e.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserWithInvalidName() {
        System.out.println("\n=== Test: Create User with Invalid Name ===");
        userService.createUser("", "test@example.com");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserWithInvalidEmail() {
        System.out.println("\n=== Test: Create User with Invalid Email ===");
        userService.createUser("Test", "invalid-email");
    }
}
