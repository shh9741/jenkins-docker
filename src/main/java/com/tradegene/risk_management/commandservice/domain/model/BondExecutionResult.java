package com.tradegene.risk_management.commandservice.domain.model;

import com.tradegene.app.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "bond_execution_results")
public class BondExecutionResult extends BaseEntity {

    @Id
    private Long id;
    private String sourceSystemCode;
    private Long sourceSystemId;
    private Long portfolioId;
    private Long productId;
    private Long bondOrderId;
    private Long bondMarketMakingOrderId;
    private Long interfaceLogId;
    private String executionResultMqProcessingYn;
    private String executionDate;
    private String executionTime;
    private String sellBuyTypeCode;
    private String sellPatternTypeCode;
    private String priceCurrencyCode;
    private BigDecimal executionQuantity;
    private BigDecimal executionPrice;
    private BigDecimal executionYield;
    private BigDecimal executionAmount;
    private String accountNo;
    private String bondOrderKindTypeCode;
    private Long executionNumber;
    private String marketIdentification;
    private String boardIdentification;
    private Long memberNumber;
    private Long branchNumber;
    private String orderIdentification;
    private String originalOrderIdentification;
    private BigDecimal orderQuantity;
    private BigDecimal orderPrice;
    private BigDecimal orderYield;
    private String trustPrincipalTypeCode;
    private String trustCompanyIdentification;
    private String accountTypeCode;
    private String investorTypeCode;
    private String foreignIdentification;
    private String foreignInvestorTypeCode;
    private String orderMediaTypeCode;
    private String traderIdentificationInformation;
    private String macAddressInformation;
    private String effectStopReopenTypeCode;
    private Long traderNumber;
    private String settlementDate;
    private Long marketMakingTypeNumber;
    private String lastSellBuyTypeCode;

    private String processingYn;
}
