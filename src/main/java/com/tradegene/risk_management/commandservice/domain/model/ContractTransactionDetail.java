package com.tradegene.risk_management.commandservice.domain.model;

import java.math.BigDecimal;

import com.tradegene.app.base.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "contract_transaction_details")
public class ContractTransactionDetail extends BaseEntity {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID
    
    @ManyToOne
    @JoinColumn(name = "contract_transaction_id")
    private ContractTransaction contractTransaction; // 계약거래상세

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
    
    public void setContractTransaction(ContractTransaction contractTransaction) {
    	this.contractTransaction = contractTransaction;
    }
}
