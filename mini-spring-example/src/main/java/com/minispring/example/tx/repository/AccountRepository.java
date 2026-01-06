package com.minispring.example.tx.repository;

import com.minispring.beans.factory.annotation.Repository;
import com.minispring.example.tx.domain.Account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 账户数据访问层
 * 模拟数据库操作
 */
@Repository
public class AccountRepository {

    private final Map<Long, Account> accounts = new HashMap<>();

    public AccountRepository() {
        // 初始化测试数据
        accounts.put(1L, new Account(1L, "ACC001", "Alice", new BigDecimal("1000.00")));
        accounts.put(2L, new Account(2L, "ACC002", "Bob", new BigDecimal("500.00")));
    }

    public Account findById(Long id) {
        System.out.println("[Repository] Finding account by id: " + id);
        return accounts.get(id);
    }

    public Account findByAccountNumber(String accountNumber) {
        System.out.println("[Repository] Finding account by number: " + accountNumber);
        return accounts.values().stream()
                .filter(a -> a.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElse(null);
    }

    public void updateBalance(Long id, BigDecimal newBalance) {
        System.out.println("[Repository] Updating balance for account " + id + " to " + newBalance);
        Account account = accounts.get(id);
        if (account != null) {
            account.setBalance(newBalance);
        }
    }

    public List<Account> findAll() {
        return new ArrayList<>(accounts.values());
    }
}
