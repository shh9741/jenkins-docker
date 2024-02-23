package com.tradegene.risk_management.commandservice.domain.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class ValuePositionDomainServiceOutput {
	
	private String currencyCode; // 통화코드
	
    private BigDecimal positionQuantity; // 포지션수량
    
    private BigDecimal positionValuationProfitLossAmount; // 포지션평가손익금액
}
