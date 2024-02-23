package com.tradegene.risk_management.commandservice.application.ports.out;

public interface CashTransactionProducer {
	
	public void pubRegisterInitialNack(String message);

	public void pubRegisterFinalAck(String message);
}
