package com.tradegene.risk_management.commandservice.infrastructure.adapter.database;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tradegene.risk_management.commandservice.domain.model.ContractTransactionDetail;

public interface ContractTransactionDetailRepository extends JpaRepository<ContractTransactionDetail, Long> {
	
    List<ContractTransactionDetail> findByContractTransactionId(Long contractTransactionId);
}