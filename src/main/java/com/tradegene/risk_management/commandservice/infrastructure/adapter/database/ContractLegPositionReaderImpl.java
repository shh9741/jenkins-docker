package com.tradegene.risk_management.commandservice.infrastructure.adapter.database;

import org.springframework.stereotype.Component;

import com.tradegene.risk_management.commandservice.application.ports.out.ContractLegPositionReader;
import com.tradegene.risk_management.commandservice.domain.model.ContractLeg;
import com.tradegene.risk_management.commandservice.domain.model.ContractLegPosition;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ContractLegPositionReaderImpl implements ContractLegPositionReader {

    private final ContractLegPositionRepository contractLegPositionRepository;

    @Override
	public ContractLegPosition findFirstByContractLegOrderByIdDesc(ContractLeg contractLeg) {

    	return contractLegPositionRepository.findFirstByContractLegOrderByIdDesc(contractLeg);
	}

	@Override
	public ContractLegPosition findFirstByContractLegProductIdOrderByIdDesc(Long productId) {

		return contractLegPositionRepository.findFirstByContractLegProductIdOrderByIdDesc(productId);
	}
}
