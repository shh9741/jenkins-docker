package com.tradegene.risk_management.commandservice.infrastructure.adapter.database;

import org.springframework.stereotype.Component;

import com.tradegene.risk_management.commandservice.application.ports.out.ContractTransactionStore;
import com.tradegene.risk_management.commandservice.domain.model.ContractTransaction;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ContractTransactionStoreImpl implements ContractTransactionStore {

    private final ContractTransactionRepository contractTransactionRepository;
    
    @Override
    public ContractTransaction save(ContractTransaction contractTransaction) {
    	
        return contractTransactionRepository.save(contractTransaction);
    }
}
