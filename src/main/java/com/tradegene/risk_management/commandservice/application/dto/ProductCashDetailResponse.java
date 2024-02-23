package com.tradegene.risk_management.commandservice.application.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCashDetailResponse {

	private Long id;
	private String cancelYn;
	private String validYn;
	private String startDate;
	private String endDate;
	private String currencyCode;
	private String cashYn;
}
