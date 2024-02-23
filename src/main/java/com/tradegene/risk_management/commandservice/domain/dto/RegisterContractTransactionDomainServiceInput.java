package com.tradegene.risk_management.commandservice.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@ToString
@Getter
@SuperBuilder
public class RegisterContractTransactionDomainServiceInput {

	private Long transactionId; // 트랜잭션ID
	
	private Long portfolioId; // 포트폴리오ID

    private ProductDto productDto; // 상품dto
    
	private String executionDate; // 체결일자
    private String executionTime; // 체결시각
    
    private String sellBuyTypeCode; // 매수매도구분코드
    
    private BigDecimal executionQuantity; // 체결수량
    private BigDecimal executionPrice; // 체결가격
    private BigDecimal executionAmount; // 체결금액
    
    private String settlementDate; // 결제일자

    private String portfolioMqProcessingTargetYn; // 포트폴리오MQ처리대상여부

    @ToString
    @Getter
    @Builder
    public static class ProductDto {

        private Long id;
        private String cashYn;
        private String currencyCode;
    }
}    
