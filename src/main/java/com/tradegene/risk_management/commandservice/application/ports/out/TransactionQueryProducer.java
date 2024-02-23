package com.tradegene.risk_management.commandservice.application.ports.out;

import com.tradegene.risk_management.commandservice.application.dto.TransactionQueryProducerDto;

public interface TransactionQueryProducer {

	public void pubQuery(TransactionQueryProducerDto transactionQueryProducerDto);
}
