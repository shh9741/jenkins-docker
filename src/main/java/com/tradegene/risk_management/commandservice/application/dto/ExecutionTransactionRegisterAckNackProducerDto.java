package com.tradegene.risk_management.commandservice.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class ExecutionTransactionRegisterAckNackProducerDto {
	
	private Long executionId; // 체결ID
}
