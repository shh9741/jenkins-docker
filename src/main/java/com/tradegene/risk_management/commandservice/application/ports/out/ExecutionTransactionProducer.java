package com.tradegene.risk_management.commandservice.application.ports.out;

import com.tradegene.risk_management.commandservice.application.dto.ExecutionTransactionRegisterAckNackProducerDto;
import com.tradegene.risk_management.commandservice.application.dto.ExecutionTransactionRegisterProducerDto;

public interface ExecutionTransactionProducer {

	public void pubRegister(ExecutionTransactionRegisterProducerDto input);
	
	public void pubRegisterInitialAck(String message);

	public void pubRegisterInitialNack(ExecutionTransactionRegisterAckNackProducerDto input, String message);

	public void pubRegisterFinalAck(ExecutionTransactionRegisterAckNackProducerDto input, String message);

	public void pubRegisterFinalNack(ExecutionTransactionRegisterAckNackProducerDto input, String message);
}
