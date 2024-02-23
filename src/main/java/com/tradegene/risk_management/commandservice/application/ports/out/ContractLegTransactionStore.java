package com.tradegene.risk_management.commandservice.application.ports.out;

import com.tradegene.risk_management.commandservice.domain.model.ContractLegTransaction;

public interface ContractLegTransactionStore {
    
	ContractLegTransaction save(ContractLegTransaction contractLegTransaction);
}
