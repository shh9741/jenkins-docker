package com.tradegene.risk_management.commandservice.domain.model;

import com.tradegene.app.base.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "contract_legs")
public class ContractLeg extends BaseEntity {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID

    private Long contractId; // 계약ID

    private String legTransactionDate; // 레그거래일자

    private String legValidDate; // 레그유효일자

    private String legExpiryDate; // 레그만기일자

    private Long productId; // 상품ID
}
