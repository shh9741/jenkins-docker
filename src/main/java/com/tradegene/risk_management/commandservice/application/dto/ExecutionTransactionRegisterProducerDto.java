package com.tradegene.risk_management.commandservice.application.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class ExecutionTransactionRegisterProducerDto {
	
	private Long transactionId; // 트렌젝션ID
	
	private Long portfolioId; // 포트폴리오ID

    private Long productId; // 상품ID
    
    private String transactionDate; // 거래일자 (체결 거래일자)
    
    private String currencyCode; // 통화코드
    
    private BigDecimal transactionProfitLossAmount; // 거래손익금액 (체결거래로 발생된 손익)
    
    private BigDecimal positionQuantity; // 포지션수량 (체결거래 후 포지션수량)
    
    private BigDecimal valueProfitLossAmount; // 평가손익금액 (체결거래 후 포지션수량의 평가손익)
}
