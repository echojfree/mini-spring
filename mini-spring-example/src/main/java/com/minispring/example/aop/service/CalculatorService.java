package com.minispring.example.aop.service;

import com.minispring.beans.factory.annotation.Service;

/**
 * 计算器服务
 * 用于演示 AOP 切面功能
 */
@Service
public class CalculatorService {

    public int add(int a, int b) {
        System.out.println("[Calculator] Calculating: " + a + " + " + b);
        // 模拟耗时操作
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        int result = a + b;
        System.out.println("[Calculator] Result: " + result);
        return result;
    }

    public int subtract(int a, int b) {
        System.out.println("[Calculator] Calculating: " + a + " - " + b);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        int result = a - b;
        System.out.println("[Calculator] Result: " + result);
        return result;
    }

    public int multiply(int a, int b) {
        System.out.println("[Calculator] Calculating: " + a + " * " + b);
        try {
            Thread.sleep(80);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        int result = a * b;
        System.out.println("[Calculator] Result: " + result);
        return result;
    }

    public double divide(int a, int b) {
        System.out.println("[Calculator] Calculating: " + a + " / " + b);
        if (b == 0) {
            throw new IllegalArgumentException("Division by zero");
        }
        try {
            Thread.sleep(60);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        double result = (double) a / b;
        System.out.println("[Calculator] Result: " + result);
        return result;
    }
}
