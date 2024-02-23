package com.tradegene.risk_management.commandservice.domain.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class SplitExecutionTransactionDomainServiceOutput {

	private List<SplitExecutionTransactionDomainServiceOutput.SplitTransactionDto> splitTransactionDtoList;
    
    @ToString
    @Getter
    @Builder
    public static class SplitTransactionDto {

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
}    
