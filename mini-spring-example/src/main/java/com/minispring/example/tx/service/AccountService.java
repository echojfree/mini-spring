package com.minispring.example.tx.service;

import com.minispring.beans.factory.annotation.Autowired;
import com.minispring.beans.factory.annotation.Service;
import com.minispring.example.tx.domain.Account;
import com.minispring.example.tx.repository.AccountRepository;
import com.minispring.tx.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 账户服务 - 演示事务管理
 * 使用 @Transactional 注解来管理事务
 */
@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    /**
     * 转账操作 - 演示事务回滚
     * 如果转账过程中出现异常，整个操作会回滚
     */
    @Transactional
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        System.out.println("\n=== Starting Transfer Transaction ===");
        System.out.println("From Account: " + fromAccountId);
        System.out.println("To Account: " + toAccountId);
        System.out.println("Amount: " + amount);

        // 验证金额
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        // 获取源账户
        Account fromAccount = accountRepository.findById(fromAccountId);
        if (fromAccount == null) {
            throw new IllegalArgumentException("Source account not found: " + fromAccountId);
        }

        // 获取目标账户
        Account toAccount = accountRepository.findById(toAccountId);
        if (toAccount == null) {
            throw new IllegalArgumentException("Target account not found: " + toAccountId);
        }

        // 检查余额
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance in account: " + fromAccountId);
        }

        // 执行转账
        System.out.println("[Service] Deducting " + amount + " from account " + fromAccountId);
        BigDecimal newFromBalance = fromAccount.getBalance().subtract(amount);
        accountRepository.updateBalance(fromAccountId, newFromBalance);

        System.out.println("[Service] Adding " + amount + " to account " + toAccountId);
        BigDecimal newToBalance = toAccount.getBalance().add(amount);
        accountRepository.updateBalance(toAccountId, newToBalance);

        System.out.println("=== Transfer Transaction Completed ===\n");
    }

    /**
     * 转账操作（会失败的版本） - 演示事务回滚
     * 故意在转账中途抛出异常，验证事务回滚机制
     */
    @Transactional
    public void transferWithError(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        System.out.println("\n=== Starting Transfer Transaction (Will Fail) ===");

        Account fromAccount = accountRepository.findById(fromAccountId);
        Account toAccount = accountRepository.findById(toAccountId);

        if (fromAccount == null || toAccount == null) {
            throw new IllegalArgumentException("Account not found");
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        // 扣款
        System.out.println("[Service] Deducting " + amount + " from account " + fromAccountId);
        BigDecimal newFromBalance = fromAccount.getBalance().subtract(amount);
        accountRepository.updateBalance(fromAccountId, newFromBalance);

        // 模拟异常 - 这将导致事务回滚
        System.out.println("[Service] Simulating error before completing transfer...");
        throw new RuntimeException("Simulated transfer error - transaction should rollback");
    }

    public Account getAccount(Long id) {
        return accountRepository.findById(id);
    }

    public BigDecimal getBalance(Long accountId) {
        Account account = accountRepository.findById(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }
        return account.getBalance();
    }
}
