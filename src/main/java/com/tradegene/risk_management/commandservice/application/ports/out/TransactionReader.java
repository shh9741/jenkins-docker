package com.tradegene.risk_management.commandservice.application.ports.out;

import com.tradegene.risk_management.commandservice.domain.model.Transaction;

public interface TransactionReader {
	
	Transaction findById(Long id);

	Boolean existsBySourceSystemCodeAndSourceSystemId(String sourceSystemCode, Long executionId);
}

