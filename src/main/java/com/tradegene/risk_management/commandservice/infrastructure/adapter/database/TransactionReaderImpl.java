package com.tradegene.risk_management.commandservice.infrastructure.adapter.database;

import com.tradegene.risk_management.commandservice.application.ports.out.TransactionReader;
import com.tradegene.risk_management.commandservice.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionReaderImpl implements TransactionReader {

    private final TransactionRepository transactionRepository;

	@Override
	public Transaction findById(Long id) {

		return transactionRepository.findById(id).orElse(null);
	}

	@Override
	public Boolean existsBySourceSystemCodeAndSourceSystemId(String sourceSystemCode, Long executionId) {

		return transactionRepository.existsBySourceSystemCodeAndSourceSystemId(sourceSystemCode, executionId);
	}
}
