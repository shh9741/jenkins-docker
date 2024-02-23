package com.tradegene.risk_management.commandservice.domain.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class RegisterContractTransactionDomainServiceOutput {
	
	private RegisterContractTransactionDomainServiceOutput.ContractDto contractDto;
	private RegisterContractTransactionDomainServiceOutput.ContractTransactionDto contractTransactionDto;
	private List<RegisterContractTransactionDomainServiceOutput.ContractTransactionDetailDto> contractTransactionDetailDtoList;
	
	private RegisterContractTransactionDomainServiceOutput.ContractLegDto contractLegDto;
	private RegisterContractTransactionDomainServiceOutput.ContractLegTransactionDto contractLegTransactionDto;
	private RegisterContractTransactionDomainServiceOutput.ContractLegPositionDto contractLegPositionDto;
	private RegisterContractTransactionDomainServiceOutput.PreviousContractLegPositionDto previousContractLegPositionDto;
	
    @ToString
    @Getter
    @Builder
    public static class ContractDto {
    	
        private Long id; // ID
        private Long productId; // 상품ID
        private Long transactionCounterpartyEntityId; // 거래상대방엔티티ID
        private String contractDate; // 계약일자
        private String validDate; // 유효일자
        private String expiryDate; // 만기일자
        private Long portfolioId; // 포트폴리오ID
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
    
    @ToString
    @Getter
    @Builder
    public static class ContractTransactionDetailDto {
    	
    	private Long id; // ID
        private Long contractTransactionId; // 계약거래ID
        private String amountPatternTypeCode; // 금액유형구분코드
        private String detailAmountPatternTypeCode; // 상세금액유형구분코드
        private String transactionCurrencyCode; // 거래통화코드
        private String priceCurrencyCode; // 가격통화코드
        private BigDecimal transactionAmount; // 거래금액
        private BigDecimal transactionProfitLossAmount; // 거래손익금액
        private String settlementDate; // 결제일자
        private String settlementCurrencyCode; // 결제통화코드
        private BigDecimal settlementAmount; // 결제금액
        private BigDecimal settlementExchangeRate; // 결제환율
        private String settlementYn; // 결제여부
    }
    
    @ToString
    @Getter
    @Builder
    public static class ContractLegDto {
    	
    	private Long id; // ID
        private Long contractId; // 계약ID
        private String legTransactionDate; // 레그거래일자
        private String legValidDate; // 레그유효일자
        private String legExpiryDate; // 레그만기일자
        private Long productId; // 상품ID
    }
    
    @ToString
    @Getter
    @Builder
    public static class ContractLegTransactionDto {
    	
    	private Long id; // ID
    	private Long contractLegId; // 계약레그ID
    	private Long contractTransactionId; // 계약거래ID
    	private String cancelYn; // 취소여부
    	private String sellBuyTypeCode; // 매도매수구분코드
    	private String transactionCurrencyCode; // 거래통화코드
    	private String priceCurrencyCode; // 가격통화코드
    	private BigDecimal transactionQuantity; // 거래수량
    	private BigDecimal transactionPrice; // 거래가격
    	private BigDecimal transactionAmount; // 거래금액
    	private BigDecimal transactionProfitLossAmount; // 거래손익금액
    	private String settlementDate; // 결제일자
    	private String settlementCurrencyCode; // 결제통화코드
    	private BigDecimal settlementAmount; // 결제금액
    	private BigDecimal settlementExchangeRate; // 결제환율
    	private String settlementYn; // 결제여부
    }
    
    @ToString
    @Getter
    @Builder
    public static class ContractLegPositionDto {
    	
    	private Long id;
        private Long contractLegId; // 계약레그ID
        private Long contractLegTransactionId; // 계약레그거래ID
        private String cancelYn; // 취소여부
        private String positionTypeCode; // 포지션구분코드
        private String positionStartDatetime; // 포지션시작일시
        private String positionEndDatetime; // 포지션종료일시
        private String currencyCode; // 통화코드
        private String priceCurrencyCode; // 가격통화코드
        private BigDecimal positionQuantity; // 포지션수량
        private BigDecimal transactionPrice; // 거래가격
        private BigDecimal transactionAmount; // 거래금액
    }
    
    @ToString
    @Getter
    @Builder
    public static class PreviousContractLegPositionDto {
    	
    	private Long id;
    	private String positionEndDatetime; // 포지션종료일시
    }
}
