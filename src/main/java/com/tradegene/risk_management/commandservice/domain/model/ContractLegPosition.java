package com.tradegene.risk_management.commandservice.domain.model;

import java.math.BigDecimal;

import com.tradegene.app.base.BaseEntity;

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
@Table(name = "contract_leg_positions")
public class ContractLegPosition extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "contract_leg_id")
    private ContractLeg contractLeg; // 계약레그기본
    
    @OneToOne
  	@JoinColumn(name = "contract_leg_transaction_id")
    private ContractLegTransaction contractLegTransaction; // 계약레그거래기본

    private String cancelYn; // 취소여부

    private String positionTypeCode; // 포지션구분코드

    private String positionStartDatetime; // 포지션시작일시

    private String positionEndDatetime; // 포지션종료일시

    private String currencyCode; // 통화코드

    private String priceCurrencyCode; // 가격통화코드

    private BigDecimal positionQuantity; // 포지션수량

    private BigDecimal transactionPrice; // 거래가격

    private BigDecimal transactionAmount; // 거래금액
    
    public void setContractLegTransaction(ContractLegTransaction contractLegTransaction) {
    	this.contractLegTransaction = contractLegTransaction;
    }
}
