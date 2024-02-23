package com.tradegene.risk_management.commandservice.infrastructure.adapter.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Getter
@Builder
public class BondExecutionResultRegisterConsumerDto {

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
}
