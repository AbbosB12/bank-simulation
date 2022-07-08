package com.cydeo.banksimulation.repository;

import com.cydeo.banksimulation.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = "SELECT * FROM transactions ORDER BY creation_date ASC LIMIT 10", nativeQuery = true)
    List<Transaction> findLastTenTransactions();

    @Query("SELECT t FROM Transaction t WHERE t.sender.id = ?1 OR t.receiver.id = ?1")
    List<Transaction> findTransactionListById(Long id);

}
