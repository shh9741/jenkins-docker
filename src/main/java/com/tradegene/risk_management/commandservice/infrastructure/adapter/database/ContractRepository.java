package com.tradegene.risk_management.commandservice.infrastructure.adapter.database;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tradegene.risk_management.commandservice.domain.model.Contract;

public interface ContractRepository extends JpaRepository<Contract, Long> {

	Contract findFirstByPortfolioIdAndProductId(Long portfolioId, Long productId);
}
