package com.tradegene.risk_management.commandservice.application.ports.in;

import com.tradegene.risk_management.commandservice.application.dto.ExecutionTransactionRegisterFailureUseCaseInput;
import com.tradegene.risk_management.commandservice.application.dto.ExecutionTransactionRegisterSuccessUseCaseInput;
import com.tradegene.risk_management.commandservice.application.dto.ExecutionTransactionRegisterUseCaseInput;

public interface ExecutionTransactionUseCase {

	public void register(ExecutionTransactionRegisterUseCaseInput input);
	
	public void registerSuccess(ExecutionTransactionRegisterSuccessUseCaseInput input);
	
	public void registerFailure(ExecutionTransactionRegisterFailureUseCaseInput input);
}
