package com.tradegene.risk_management.commandservice.domain.model;

import java.util.List;

import com.tradegene.app.base.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "contract_transactions")
public class ContractTransaction extends BaseEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID

    private Long transactionId; // 트렌젝션ID
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contract_id")
    private Contract contract; // 계약기본

    private String portfolioMqProcessingYn; // 포트폴리오MQ처리여부
    
    private String transactionDate; // 거래일자

    private String sellBuyTypeCode; // 매도매수구분코드

    @OneToMany(mappedBy = "contractTransaction", cascade = CascadeType.ALL)
    private List<ContractTransactionDetail> contractTransactionDetails; // 계약거래상세
    
    public void addContractTransactionDetail(ContractTransactionDetail contractTransactionDetail) {
    	this.contractTransactionDetails.add(contractTransactionDetail);
    	contractTransactionDetail.setContractTransaction(this);
    }
}