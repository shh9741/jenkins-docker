package com.tradegene.risk_management.commandservice.application.ports.out;

import com.tradegene.risk_management.commandservice.domain.model.ContractTransaction;

public interface ContractTransactionStore {
	
    ContractTransaction save(ContractTransaction contractTransaction);
}
