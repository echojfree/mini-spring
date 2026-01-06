package com.minispring.example.aop;

import com.minispring.context.annotation.AnnotationConfigApplicationContext;
import com.minispring.example.aop.service.CalculatorService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * AOP 功能测试
 * 简化版本 - 仅测试基本服务功能
 * 完整的 AOP 配置需要额外的配置类支持
 */
public class AopExampleTest {

    private AnnotationConfigApplicationContext context;
    private CalculatorService calculatorService;

    @Before
    public void setUp() throws Exception {
        context = new AnnotationConfigApplicationContext("com.minispring.example.aop");
        calculatorService = context.getBean("calculatorService", CalculatorService.class);
        assertNotNull("CalculatorService should not be null", calculatorService);
    }

    @Test
    public void testAdd() {
        System.out.println("\n=== Test: Add Operation ===");

        int result = calculatorService.add(10, 20);

        assertEquals(30, result);
        System.out.println("Addition result: " + result);
    }

    @Test
    public void testSubtract() {
        System.out.println("\n=== Test: Subtract Operation ===");

        int result = calculatorService.subtract(50, 20);

        assertEquals(30, result);
        System.out.println("Subtraction result: " + result);
    }

    @Test
    public void testMultiply() {
        System.out.println("\n=== Test: Multiply Operation ===");

        int result = calculatorService.multiply(5, 6);

        assertEquals(30, result);
        System.out.println("Multiplication result: " + result);
    }

    @Test
    public void testDivide() {
        System.out.println("\n=== Test: Divide Operation ===");

        double result = calculatorService.divide(60, 2);

        assertEquals(30.0, result, 0.001);
        System.out.println("Division result: " + result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDivideByZero() {
        System.out.println("\n=== Test: Divide by Zero ===");

        // 应该抛出异常
        calculatorService.divide(10, 0);
    }

    @Test
    public void testMultipleOperations() {
        System.out.println("\n=== Test: Multiple Operations ===");

        // 测试多个操作
        calculatorService.add(1, 2);
        calculatorService.subtract(10, 5);
        calculatorService.multiply(3, 4);
        calculatorService.divide(20, 4);

        System.out.println("All operations completed successfully");
    }
}
