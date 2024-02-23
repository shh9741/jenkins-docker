package com.tradegene.risk_management.commandservice.domain.model;

import java.math.BigDecimal;

import com.tradegene.app.base.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@Table(name = "contract_leg_transactions")
public class ContractLegTransaction extends BaseEntity {
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "contract_leg_id")
	private ContractLeg contractLeg; // 계약레그기본
	
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
	
	@OneToOne(mappedBy = "contractLegTransaction", cascade = CascadeType.ALL)
	@JoinColumn(name = "contract_leg_position_id")
	private ContractLegPosition contractLegPosition; // 계약레그포지션기본
	
	public void setContractLegPosition(ContractLegPosition contractLegPosition) {
		this.contractLegPosition = contractLegPosition;
		contractLegPosition.setContractLegTransaction(this);
	}
}