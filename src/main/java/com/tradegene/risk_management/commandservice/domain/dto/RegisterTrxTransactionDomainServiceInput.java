package com.tradegene.risk_management.commandservice.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class RegisterTrxTransactionDomainServiceInput {

	private Long executionId; // 체결ID
	private String executionDate; // 체결일자
    private String executionTime; // 체결시각
}    
