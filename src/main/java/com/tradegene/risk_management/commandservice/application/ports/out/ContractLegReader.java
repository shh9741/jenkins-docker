package com.tradegene.risk_management.commandservice.application.ports.out;

import com.tradegene.risk_management.commandservice.domain.model.ContractLeg;

public interface ContractLegReader {
	
    ContractLeg findFirstByContractIdAndProductId(Long contractId, Long productId);
//    ContractLeg findByProductId(Long productId);
}
