package com.tradegene.risk_management.commandservice.application.ports.out;

import com.tradegene.risk_management.commandservice.domain.model.Transaction;

public interface TransactionStore {
	
    Transaction save(Transaction transaction);
}
