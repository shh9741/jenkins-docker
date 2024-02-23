package com.tradegene.risk_management.commandservice.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class UpdatePortfolioMqProcessingYnDomainServiceInput {
	
	private Long transactionId; // 트렌젝션ID
	
	private String portfolioMqProcessingYn; // 포트폴리오MQ처리여부
}
