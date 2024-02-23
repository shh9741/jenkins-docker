package com.tradegene.risk_management.commandservice.application.ports.out;

import com.tradegene.risk_management.commandservice.domain.model.BondExecutionResult;

public interface BondExecutionResultReader {

    BondExecutionResult findById(Long id);
}
