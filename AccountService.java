package com.laxmi.banking.service;

import com.laxmi.banking.exception.AccountNotFoundException;
import com.laxmi.banking.exception.InsufficientBalanceException;
import com.laxmi.banking.model.Account;
import com.laxmi.banking.model.Transaction;
import com.laxmi.banking.repository.AccountRepository;
import com.laxmi.banking.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AccountService {

    private final AccountRepository accounts;
    private final TransactionRepository transactions;

    public AccountService(AccountRepository accounts, TransactionRepository transactions) {
        this.accounts = accounts;
        this.transactions = transactions;
    }

    public Account createAccount(String holderName, BigDecimal initialDeposit) {
        String number;
        do {
            number = String.valueOf(ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L));
        } while (accounts.existsByAccountNumber(number));

        Account account = accounts.save(new Account(number, holderName, initialDeposit));
        if (initialDeposit.compareTo(BigDecimal.ZERO) > 0) {
            record(number, "DEPOSIT", initialDeposit, initialDeposit);
        }
        return account;
    }

    public Account getAccount(String accountNumber) {
        return accounts.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    @Transactional
    public Account deposit(String accountNumber, BigDecimal amount) {
        Account account = getAccount(accountNumber);
        account.setBalance(account.getBalance().add(amount));
        accounts.save(account);
        record(accountNumber, "DEPOSIT", amount, account.getBalance());
        return account;
    }

    @Transactional
    public Account withdraw(String accountNumber, BigDecimal amount) {
        Account account = getAccount(accountNumber);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
        account.setBalance(account.getBalance().subtract(amount));
        accounts.save(account);
        record(accountNumber, "WITHDRAW", amount, account.getBalance());
        return account;
    }

    // @Transactional: if anything fails halfway, both updates are rolled back
    @Transactional
    public Account transfer(String fromNumber, String toNumber, BigDecimal amount) {
        if (fromNumber.equals(toNumber)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        Account from = getAccount(fromNumber);
        Account to = getAccount(toNumber);
        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        accounts.save(from);
        accounts.save(to);
        record(fromNumber, "TRANSFER_OUT", amount, from.getBalance());
        record(toNumber, "TRANSFER_IN", amount, to.getBalance());
        return from;
    }

    public List<Transaction> history(String accountNumber) {
        getAccount(accountNumber); // throws 404 if the account does not exist
        return transactions.findByAccountNumberOrderByCreatedAtDesc(accountNumber);
    }

    private void record(String accountNumber, String type, BigDecimal amount, BigDecimal balanceAfter) {
        transactions.save(new Transaction(accountNumber, type, amount, balanceAfter));
    }
}
