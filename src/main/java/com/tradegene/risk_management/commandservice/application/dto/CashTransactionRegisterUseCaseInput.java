package com.tradegene.risk_management.commandservice.application.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class CashTransactionRegisterUseCaseInput {

    private BigDecimal amount; // 금액
}    
