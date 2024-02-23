package com.tradegene.risk_management.commandservice.infrastructure.adapter.database;

import java.util.List;

import org.springframework.stereotype.Component;

import com.tradegene.risk_management.commandservice.application.ports.out.ContractTransactionReader;
import com.tradegene.risk_management.commandservice.domain.model.ContractTransaction;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ContractTransactionReaderImpl implements ContractTransactionReader {

	private final ContractTransactionRepository contractTransactionRepository;

	@Override
	public List<ContractTransaction> findByTransactionId(Long transactionId) {

		return contractTransactionRepository.findByTransactionId(transactionId);
	}
}
