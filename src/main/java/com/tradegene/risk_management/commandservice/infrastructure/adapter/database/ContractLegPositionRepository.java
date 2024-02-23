package com.tradegene.risk_management.commandservice.infrastructure.adapter.database;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tradegene.risk_management.commandservice.domain.model.ContractLeg;
import com.tradegene.risk_management.commandservice.domain.model.ContractLegPosition;

public interface ContractLegPositionRepository extends JpaRepository<ContractLegPosition, Long> {
	
	ContractLegPosition findFirstByContractLegOrderByIdDesc(ContractLeg contractLeg);
	
	ContractLegPosition findFirstByContractLegProductIdOrderByIdDesc(Long productId);
}