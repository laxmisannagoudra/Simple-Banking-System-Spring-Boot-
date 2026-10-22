package com.laxmi.banking.controller;

import com.laxmi.banking.dto.Requests;
import com.laxmi.banking.model.Account;
import com.laxmi.banking.model.Transaction;
import com.laxmi.banking.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Account> create(@Valid @RequestBody Requests.CreateAccount req) {
        Account account = service.createAccount(req.holderName(), req.initialDeposit());
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @GetMapping("/{accountNumber}")
    public Account get(@PathVariable String accountNumber) {
        return service.getAccount(accountNumber);
    }

    @PostMapping("/{accountNumber}/deposit")
    public Account deposit(@PathVariable String accountNumber, @Valid @RequestBody Requests.Amount req) {
        return service.deposit(accountNumber, req.amount());
    }

    @PostMapping("/{accountNumber}/withdraw")
    public Account withdraw(@PathVariable String accountNumber, @Valid @RequestBody Requests.Amount req) {
        return service.withdraw(accountNumber, req.amount());
    }

    @PostMapping("/transfer")
    public Account transfer(@Valid @RequestBody Requests.Transfer req) {
        return service.transfer(req.fromAccount(), req.toAccount(), req.amount());
    }

    @GetMapping("/{accountNumber}/transactions")
    public List<Transaction> history(@PathVariable String accountNumber) {
        return service.history(accountNumber);
    }
}
