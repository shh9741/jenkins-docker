package com.tradegene.risk_management.commandservice.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Getter
@Builder
public class CashTransactionRegisterUseCaseOutput {

	private Long contractTransactionId; // 계약거래ID
}    
