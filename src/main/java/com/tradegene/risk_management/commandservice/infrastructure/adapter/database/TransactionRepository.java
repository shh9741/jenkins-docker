package com.tradegene.risk_management.commandservice.infrastructure.adapter.database;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tradegene.risk_management.commandservice.domain.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Boolean existsBySourceSystemCodeAndSourceSystemId(String sourceSystemCode, Long executionId);
}
