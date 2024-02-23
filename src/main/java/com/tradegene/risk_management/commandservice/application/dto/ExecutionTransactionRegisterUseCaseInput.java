package com.tradegene.risk_management.commandservice.application.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class ExecutionTransactionRegisterUseCaseInput {

	private Long executionId; // 체결ID
	private Long portfolioId; // 포트폴리오ID
    private Long productId; // 상품ID
    
    private String executionDate; // 체결일자
    private String executionTime; // 체결시각
    
    private String sellBuyTypeCode; // 매수매도구분코드
    
    private BigDecimal executionQuantity; // 체결수량
    private BigDecimal executionPrice; // 체결가격
    private BigDecimal executionAmount; // 체결금액
    
    private String settlementDate; // 결제일자
}    
