package com.tradegene.risk_management.commandservice.infrastructure.adapter.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@ToString
@Getter
@Builder
public class CashRegisterResponse {

    private Long contractTransactionId; // 계약거래ID
}
