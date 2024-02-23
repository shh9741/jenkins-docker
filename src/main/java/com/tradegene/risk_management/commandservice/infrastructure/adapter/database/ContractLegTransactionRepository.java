package com.tradegene.risk_management.commandservice.infrastructure.adapter.database;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tradegene.risk_management.commandservice.domain.model.ContractLegTransaction;

public interface ContractLegTransactionRepository extends JpaRepository<ContractLegTransaction, Long> {
	
    ContractLegTransaction findFirstByContractTransactionIdAndCancelYn(Long contractTransactionId, String cancelYn);
}