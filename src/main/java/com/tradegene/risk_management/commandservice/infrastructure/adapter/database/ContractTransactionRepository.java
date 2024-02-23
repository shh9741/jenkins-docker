package com.tradegene.risk_management.commandservice.infrastructure.adapter.database;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tradegene.risk_management.commandservice.domain.model.ContractTransaction;

public interface ContractTransactionRepository extends JpaRepository<ContractTransaction, Long> {
	
	List<ContractTransaction> findByTransactionId(Long transactionId);
}
