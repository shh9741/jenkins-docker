package com.tradegene.risk_management.commandservice.application.ports.out;

import java.util.List;

import com.tradegene.risk_management.commandservice.domain.model.ContractTransaction;

public interface ContractTransactionReader {

	List<ContractTransaction> findByTransactionId(Long transactionId);
}
