package com.tradegene.risk_management.commandservice.application.ports.out;

import com.tradegene.risk_management.commandservice.application.dto.BondExecutionResultRegisterAckNackProducerDto;

public interface BondExecutionResultProducer {

	public void pubRegisterInitialNack(BondExecutionResultRegisterAckNackProducerDto input, String message);

	public void pubRegisterFinalAck(BondExecutionResultRegisterAckNackProducerDto input, String message);
}
