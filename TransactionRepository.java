package com.laxmi.banking.repository;

import com.laxmi.banking.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountNumberOrderByCreatedAtDesc(String accountNumber);
}
