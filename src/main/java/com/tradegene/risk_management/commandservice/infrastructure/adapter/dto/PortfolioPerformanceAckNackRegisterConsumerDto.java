package com.tradegene.risk_management.commandservice.infrastructure.adapter.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class PortfolioPerformanceAckNackRegisterConsumerDto {

	private Long transactionId; // 트렌젝션ID
}
