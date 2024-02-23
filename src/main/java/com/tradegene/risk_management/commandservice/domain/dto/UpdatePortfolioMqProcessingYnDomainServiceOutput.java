package com.tradegene.risk_management.commandservice.domain.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class UpdatePortfolioMqProcessingYnDomainServiceOutput {
	
	private TransactionDto transactionDto;
	private List<UpdatePortfolioMqProcessingYnDomainServiceOutput.ContractTransactionDto> contractTransactionDto;
	
	@ToString
    @Getter
    @Builder
    public static class TransactionDto {
    	
        private Long id; // ID
        private Long executionId; // 체결ID
        private String cancelYn; // 취소여부
        private String transactionProcessingDatetime; // 거래처리일시
    }
	
	@ToString
    @Getter
    @Builder
    public static class ContractTransactionDto {
    	
        private Long id; // ID
        private Long transactionId; // 트렌젝션ID
        private Long contractId; // 계약ID
        private String portfolioMqProcessingYn; // 포트폴리오MQ처리여부
        private String transactionDate; // 거래일자
        private String sellBuyTypeCode; // 매도매수구분코드
    }
}
