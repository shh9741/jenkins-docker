package com.tradegene.risk_management.commandservice.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class ExecutionTransactionRegisterSuccessUseCaseInput {

	private Long transactionId; // 트렌젝션ID
}    
