package com.tradegene.risk_management.commandservice.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class RegisterTrxTransactionDomainServiceOutput {
	
    private RegisterTrxTransactionDomainServiceOutput.TransactionDto transactionDto;
    
    @ToString
    @Getter
    @Builder
    public static class TransactionDto {
    	
    	private Long id; // ID
        private Long executionId; // 체결ID
        private String cancelYn; // 취소여부
        private String transactionProcessingDatetime; // 거래처리일시
    }
    
}
