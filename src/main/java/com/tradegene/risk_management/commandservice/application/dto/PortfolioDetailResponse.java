package com.tradegene.risk_management.commandservice.application.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDetailResponse {

	private Long portfolioId;
	private String cancelYn; //취소여부
	private String validYn; //유효여부
	private String portfolioDefinedField; //포트폴리오정의ID
	private String portfolioName; //포트폴리오명
	private String portfolioContent; //포트폴리오내용
	private Long entityId; //엔티티ID
	private String riskLevelTypeCode; //리스크레벨구분코드
	private String portfolioCurrencyCode; //포트폴리오통화코드
	private BigDecimal portfolioManageAmount; //포트폴리오운용금액
	private String singleTransactionUseYn; //단일거래사용여부
	private String multiMarketmakingTransactionUseYn; //복수MM거래사용여부
	private String productMultiTransactionUseYn; //상품복수거래사용여부
	private String productMultiMarketmakingTransactionYn; //상품복수MM거래여부
	private String ruleTransactionUseYn; //규칙거래사용여부
	private String pythonRuleTransactionUseYn; //python규칙거래사용여부
	private List<PortfolioProductDto> portfolioProductDtos;

	@Getter
	@ToString
	@Builder
	public static class PortfolioProductDto {
		private Long portfolioProductId;
		private String cancelYn; //취소여부
		private String validYn; //유효여부
		private Long portfolioId; //포트폴리오ID
		private Long productId; //상품ID
		private String startDatetime; //시작일시
		private String endDatetime; //종료일시
	}
}
