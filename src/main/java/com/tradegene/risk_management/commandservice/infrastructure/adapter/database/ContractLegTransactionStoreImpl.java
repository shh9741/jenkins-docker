package com.tradegene.risk_management.commandservice.infrastructure.adapter.database;

import org.springframework.stereotype.Component;

import com.tradegene.risk_management.commandservice.application.ports.out.ContractLegTransactionStore;
import com.tradegene.risk_management.commandservice.domain.model.ContractLegTransaction;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ContractLegTransactionStoreImpl implements ContractLegTransactionStore {

    private final ContractLegTransactionRepository contractLegTransactionRepository;

    @Override
    public ContractLegTransaction save(ContractLegTransaction contractLegTransaction) {
    	
        return contractLegTransactionRepository.save(contractLegTransaction);
    }
}
