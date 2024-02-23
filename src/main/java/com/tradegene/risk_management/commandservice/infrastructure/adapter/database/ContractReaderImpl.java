package com.tradegene.risk_management.commandservice.infrastructure.adapter.database;

import org.springframework.stereotype.Component;

import com.tradegene.risk_management.commandservice.application.ports.out.ContractReader;
import com.tradegene.risk_management.commandservice.domain.model.Contract;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ContractReaderImpl implements ContractReader {

    private final ContractRepository contractRepository;

	@Override
	public Contract findFirstByPortfolioIdAndProductId(Long portfolioId, Long productId) {

		return contractRepository.findFirstByPortfolioIdAndProductId(portfolioId, productId);
	}
}
