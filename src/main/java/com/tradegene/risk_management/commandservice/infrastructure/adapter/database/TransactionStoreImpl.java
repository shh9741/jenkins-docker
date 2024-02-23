package com.tradegene.risk_management.commandservice.infrastructure.adapter.database;

import org.springframework.stereotype.Component;

import com.tradegene.risk_management.commandservice.application.ports.out.TransactionStore;
import com.tradegene.risk_management.commandservice.domain.model.Transaction;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransactionStoreImpl implements TransactionStore {
	
    private final TransactionRepository transactionRepository;
    
    @Override
    public Transaction save(Transaction transaction) {
    	
        return transactionRepository.save(transaction);
    }
}
