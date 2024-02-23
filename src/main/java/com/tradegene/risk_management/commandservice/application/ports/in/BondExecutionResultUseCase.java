package com.tradegene.risk_management.commandservice.application.ports.in;

import com.tradegene.risk_management.commandservice.application.dto.BondExecutionResultRegisterUseCaseInput;

public interface BondExecutionResultUseCase {

    public void register(BondExecutionResultRegisterUseCaseInput input);
}
