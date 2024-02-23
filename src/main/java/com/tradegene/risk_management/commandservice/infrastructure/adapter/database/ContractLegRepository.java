package com.tradegene.risk_management.commandservice.infrastructure.adapter.database;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tradegene.risk_management.commandservice.domain.model.ContractLeg;

public interface ContractLegRepository extends JpaRepository<ContractLeg, Long> {

    ContractLeg findFirstByContractIdAndProductId(Long contractId, Long productId);
}