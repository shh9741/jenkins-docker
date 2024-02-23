package com.tradegene.risk_management.commandservice.domain.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class ValuePositionDomainServiceInput {

	private Long portfolioId; // 포트폴리오ID

    private Long productId; // 상품ID
    
    private BigDecimal valuationPrice; // 평가가격
}
