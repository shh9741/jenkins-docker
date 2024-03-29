package com.tradegene.risk_management.commandservice.application.ports.in;

import com.tradegene.risk_management.commandservice.application.dto.CashTransactionRegisterUseCaseInput;
import com.tradegene.risk_management.commandservice.application.dto.CashTransactionRegisterUseCaseOutput;

public interface CashTransactionUseCase {

	public CashTransactionRegisterUseCaseOutput register(CashTransactionRegisterUseCaseInput cashTransactionRegisterUseCaseInput);
}
