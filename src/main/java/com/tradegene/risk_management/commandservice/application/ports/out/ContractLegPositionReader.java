package com.tradegene.risk_management.commandservice.application.ports.out;

import com.tradegene.risk_management.commandservice.domain.model.ContractLeg;
import com.tradegene.risk_management.commandservice.domain.model.ContractLegPosition;

public interface ContractLegPositionReader {

	ContractLegPosition findFirstByContractLegOrderByIdDesc(ContractLeg contractLeg);
	
	ContractLegPosition findFirstByContractLegProductIdOrderByIdDesc(Long productId);
}
