package com.tradegene.risk_management.commandservice.domain.service;

import com.tradegene.app.exception.DomainException;
import com.tradegene.app.exception.ErrorInfo;
import com.tradegene.app.utils.CommonUtil;
import com.tradegene.risk_management.commandservice.application.ports.out.BondExecutionResultReader;
import com.tradegene.risk_management.commandservice.application.ports.out.BondExecutionResultStore;
import com.tradegene.risk_management.commandservice.domain.code.Code;
import com.tradegene.risk_management.commandservice.domain.dto.RegisterBondExecutionResultDomainServiceInput;
import com.tradegene.risk_management.commandservice.domain.dto.RegisterBondExecutionResultDomainServiceOutput;
import com.tradegene.risk_management.commandservice.domain.model.BondExecutionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BondExecutionResultDomainService {

    private final BondExecutionResultReader bondExecutionResultReader;
    private final BondExecutionResultStore bondExecutionResultStore;

    public RegisterBondExecutionResultDomainServiceOutput register(RegisterBondExecutionResultDomainServiceInput input) {

        /* =========================================W========================
         * 1. 입력값 검증
         * ================================================================= */
        // 1-1. 필수입력항목 검증
        _checkRegisterInput(input);

        /* =================================================================
         * 2. 채권채결결과 저장
         * ================================================================= */
        // 2-1. 입력값 조립
        BondExecutionResult bondExecutionResult = _setBondExecutionResult(input);

        // 2-2. 호출
        bondExecutionResult = bondExecutionResultStore.save(bondExecutionResult);

        /* =================================================================
         * 3. 출력값 조립
         * ================================================================= */
        RegisterBondExecutionResultDomainServiceOutput output = _setRegisterBondExecutionResultDomainServiceOutput(bondExecutionResult);

        return output;
    }

    public void setProcessed(Long id) {

        /* =================================================================
         * 1. 채권채결결과 처리여부 Y 저장
         * ================================================================= */
        // 1-1. 선조회
        BondExecutionResult bondExecutionResult = bondExecutionResultReader.findById(id);

        // 1-2. 처리여부 Y
        bondExecutionResult = bondExecutionResult.toBuilder()
                .processingYn(Code.YES)
                .build();

        // 1-3. 저장
        bondExecutionResultStore.save(bondExecutionResult);
    }

    private void _checkRegisterInput(RegisterBondExecutionResultDomainServiceInput input) {

        if (CommonUtil.isNullOrZero(input.getId())) {

            throw new DomainException(ErrorInfo.ERROR_0001, "ID");
        }
    }

    private BondExecutionResult _setBondExecutionResult(RegisterBondExecutionResultDomainServiceInput input) {
        return BondExecutionResult.builder()
                .id(input.getId())
                .sourceSystemCode(input.getSourceSystemCode())
                .sourceSystemId(input.getSourceSystemId())
                .portfolioId(input.getPortfolioId())
                .productId(input.getProductId())
                .bondOrderId(input.getBondOrderId())
                .bondMarketMakingOrderId(input.getBondMarketMakingOrderId())
                .interfaceLogId(input.getInterfaceLogId())
                .executionResultMqProcessingYn(input.getExecutionResultMqProcessingYn())
                .executionDate(input.getExecutionDate())
                .executionTime(input.getExecutionTime())
                .sellBuyTypeCode(input.getSellBuyTypeCode())
                .sellPatternTypeCode(input.getSellPatternTypeCode())
                .priceCurrencyCode(input.getPriceCurrencyCode())
                .executionQuantity(input.getExecutionQuantity())
                .executionPrice(input.getExecutionPrice())
                .executionYield(input.getExecutionYield())
                .executionAmount(input.getExecutionAmount())
                .accountNo(input.getAccountNo())
                .bondOrderKindTypeCode(input.getBondOrderKindTypeCode())
                .executionNumber(input.getExecutionNumber())
                .marketIdentification(input.getMarketIdentification())
                .boardIdentification(input.getBoardIdentification())
                .memberNumber(input.getMemberNumber())
                .branchNumber(input.getBranchNumber())
                .orderIdentification(input.getOrderIdentification())
                .originalOrderIdentification(input.getOriginalOrderIdentification())
                .orderQuantity(input.getOrderQuantity())
                .orderPrice(input.getOrderPrice())
                .orderYield(input.getOrderYield())
                .trustPrincipalTypeCode(input.getTrustPrincipalTypeCode())
                .trustCompanyIdentification(input.getTrustCompanyIdentification())
                .accountTypeCode(input.getAccountTypeCode())
                .investorTypeCode(input.getInvestorTypeCode())
                .foreignIdentification(input.getForeignIdentification())
                .foreignInvestorTypeCode(input.getForeignInvestorTypeCode())
                .orderMediaTypeCode(input.getOrderMediaTypeCode())
                .traderIdentificationInformation(input.getTraderIdentificationInformation())
                .macAddressInformation(input.getMacAddressInformation())
                .effectStopReopenTypeCode(input.getEffectStopReopenTypeCode())
                .traderNumber(input.getTraderNumber())
                .settlementDate(input.getSettlementDate())
                .marketMakingTypeNumber(input.getMarketMakingTypeNumber())
                .lastSellBuyTypeCode(input.getLastSellBuyTypeCode())
                .processingYn(Code.NO)
                .build();
    }

    private RegisterBondExecutionResultDomainServiceOutput _setRegisterBondExecutionResultDomainServiceOutput(BondExecutionResult bondExecutionResult) {
        return RegisterBondExecutionResultDomainServiceOutput.builder()
                .id(bondExecutionResult.getId())
                .sourceSystemCode(bondExecutionResult.getSourceSystemCode())
                .sourceSystemId(bondExecutionResult.getSourceSystemId())
                .portfolioId(bondExecutionResult.getPortfolioId())
                .productId(bondExecutionResult.getProductId())
                .bondOrderId(bondExecutionResult.getBondOrderId())
                .bondMarketMakingOrderId(bondExecutionResult.getBondMarketMakingOrderId())
                .interfaceLogId(bondExecutionResult.getInterfaceLogId())
                .executionResultMqProcessingYn(bondExecutionResult.getExecutionResultMqProcessingYn())
                .executionDate(bondExecutionResult.getExecutionDate())
                .executionTime(bondExecutionResult.getExecutionTime())
                .sellBuyTypeCode(bondExecutionResult.getSellBuyTypeCode())
                .sellPatternTypeCode(bondExecutionResult.getSellPatternTypeCode())
                .priceCurrencyCode(bondExecutionResult.getPriceCurrencyCode())
                .executionQuantity(bondExecutionResult.getExecutionQuantity())
                .executionPrice(bondExecutionResult.getExecutionPrice())
                .executionYield(bondExecutionResult.getExecutionYield())
                .executionAmount(bondExecutionResult.getExecutionAmount())
                .accountNo(bondExecutionResult.getAccountNo())
                .bondOrderKindTypeCode(bondExecutionResult.getBondOrderKindTypeCode())
                .executionNumber(bondExecutionResult.getExecutionNumber())
                .marketIdentification(bondExecutionResult.getMarketIdentification())
                .boardIdentification(bondExecutionResult.getBoardIdentification())
                .memberNumber(bondExecutionResult.getMemberNumber())
                .branchNumber(bondExecutionResult.getBranchNumber())
                .orderIdentification(bondExecutionResult.getOrderIdentification())
                .originalOrderIdentification(bondExecutionResult.getOriginalOrderIdentification())
                .orderQuantity(bondExecutionResult.getOrderQuantity())
                .orderPrice(bondExecutionResult.getOrderPrice())
                .orderYield(bondExecutionResult.getOrderYield())
                .trustPrincipalTypeCode(bondExecutionResult.getTrustPrincipalTypeCode())
                .trustCompanyIdentification(bondExecutionResult.getTrustCompanyIdentification())
                .accountTypeCode(bondExecutionResult.getAccountTypeCode())
                .investorTypeCode(bondExecutionResult.getInvestorTypeCode())
                .foreignIdentification(bondExecutionResult.getForeignIdentification())
                .foreignInvestorTypeCode(bondExecutionResult.getForeignInvestorTypeCode())
                .orderMediaTypeCode(bondExecutionResult.getOrderMediaTypeCode())
                .traderIdentificationInformation(bondExecutionResult.getTraderIdentificationInformation())
                .macAddressInformation(bondExecutionResult.getMacAddressInformation())
                .effectStopReopenTypeCode(bondExecutionResult.getEffectStopReopenTypeCode())
                .traderNumber(bondExecutionResult.getTraderNumber())
                .settlementDate(bondExecutionResult.getSettlementDate())
                .marketMakingTypeNumber(bondExecutionResult.getMarketMakingTypeNumber())
                .lastSellBuyTypeCode(bondExecutionResult.getLastSellBuyTypeCode())
                .processingYn(bondExecutionResult.getProcessingYn())
                .build();
    }
}
