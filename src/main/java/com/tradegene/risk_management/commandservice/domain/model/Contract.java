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
@Table(name = "contracts")
public class Contract extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID

    private Long productId; // 상품ID

    private Long transactionCounterpartyEntityId; // 거래상대방엔티티ID

    private String contractDate; // 계약일자

    private String validDate; // 유효일자

    private String expiryDate; // 만기일자
    
    private Long portfolioId; // 포트폴리오ID
}
