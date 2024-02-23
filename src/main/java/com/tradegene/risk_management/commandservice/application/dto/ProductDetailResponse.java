package com.tradegene.risk_management.commandservice.application.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponse {

	private Long id;
	private String cancelYn;
	private String validYn;
	private String startDate;
	private String endDate;
	private String currencyCode;
	private String cashYn;

	private String governmentBondIssueTypeCode;
	private BigDecimal closePrice;
	private String issueEnglishName;
	private String issuerCode;
	private BigDecimal couponInterestRate;
	private String issueCode;
	private String issueName;

	private String expiryTypeCode; // 만기구분코드 01:1년, 02:2년, 03:3년, 04:5년, 05:10년, 06:20년, 07:30년, 08:50년
}
