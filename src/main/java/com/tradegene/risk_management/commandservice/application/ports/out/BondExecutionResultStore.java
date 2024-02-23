package com.tradegene.risk_management.commandservice.application.ports.out;

import com.tradegene.risk_management.commandservice.domain.model.BondExecutionResult;

public interface BondExecutionResultStore {

    BondExecutionResult save(BondExecutionResult input);
}
