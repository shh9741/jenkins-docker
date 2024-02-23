package com.tradegene.risk_management.commandservice.domain.model;

import com.tradegene.app.base.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "transactions")
public class Transaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID

    private String sourceSystemCode; // 원천시스템코드

    private Long sourceSystemId; // 원천시스템ID

    private Long executionId; // 체결ID

    private String cancelYn; // 취소여부

    private String transactionProcessingDatetime; // 거래처리일시
}
