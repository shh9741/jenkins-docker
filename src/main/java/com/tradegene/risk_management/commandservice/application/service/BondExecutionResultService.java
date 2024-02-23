package com.tradegene.risk_management.commandservice.application.service;

import com.tradegene.app.dto.common.enumeration.SequenceType;
import com.tradegene.app.exception.DomainException;
import com.tradegene.risk_management.commandservice.application.dto.BondExecutionResultRegisterAckNackProducerDto;
import com.tradegene.risk_management.commandservice.application.dto.BondExecutionResultRegisterUseCaseInput;
import com.tradegene.risk_management.commandservice.application.ports.in.BondExecutionResultUseCase;
import com.tradegene.risk_management.commandservice.application.ports.out.BondExecutionResultProducer;
import com.tradegene.risk_management.commandservice.domain.dto.RegisterBondExecutionResultDomainServiceInput;
import com.tradegene.risk_management.commandservice.domain.dto.RegisterBondExecutionResultDomainServiceOutput;
import com.tradegene.risk_management.commandservice.domain.service.BondExecutionResultDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BondExecutionResultService implements BondExecutionResultUseCase {

    private final BondExecutionResultDomainService bondExecutionResultDomainService;
    private final BondExecutionResultProducer bondExecutionResultProducer;

    @Transactional
    @Override
    public void register(BondExecutionResultRegisterUseCaseInput input) {

        /* =================================================================
         * 변수 선언
         * ================================================================= */
        RegisterBondExecutionResultDomainServiceInput domainServiceInput = null;
        RegisterBondExecutionResultDomainServiceOutput domainServiceOutput = null;

        try {

            /* =================================================================
             * 1. 채권체결결과 등록 domain service 호출
             * ================================================================= */
            // 1-1. 입력값 조립
            domainServiceInput = _setRegisterBondExecutionResultDomainServiceInput(input);

            // 1-2. 호출
            domainServiceOutput = bondExecutionResultDomainService.register(domainServiceInput);

        } catch (Exception e) {

            /* =================================================================
             * 2. initial nack topic 발행
             * ================================================================= */
            // 2-1. 입력값 조립
            BondExecutionResultRegisterAckNackProducerDto producerDto = _setBondExecutionResultRegisterAckNackProducerDto(input);
            String message = _setBondExecutionResultRegisterInitialNackMessage(input, e);

            // 2-2. 호출
            bondExecutionResultProducer.pubRegisterInitialNack(producerDto, message);

            throw e;
        }

        /* =================================================================
         * 2. final ack topic 발행
         * ================================================================= */
        // 2-1. 입력값 조립
        BondExecutionResultRegisterAckNackProducerDto producerDto = _setBondExecutionResultRegisterAckNackProducerDto(input);
        String message = _setBondExecutionResultRegisterFinalAckMessage(domainServiceOutput);

        // 2-2. 호출
        bondExecutionResultProducer.pubRegisterFinalAck(producerDto, message);
    }

    private RegisterBondExecutionResultDomainServiceInput _setRegisterBondExecutionResultDomainServiceInput(BondExecutionResultRegisterUseCaseInput input) {
        return RegisterBondExecutionResultDomainServiceInput.builder()
                .id(input.getId())
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
                .build();
    }

    private BondExecutionResultRegisterAckNackProducerDto _setBondExecutionResultRegisterAckNackProducerDto(BondExecutionResultRegisterUseCaseInput input) {
        return BondExecutionResultRegisterAckNackProducerDto.builder()
                .id(input.getId())
                .build();
    }

    private String _setBondExecutionResultRegisterInitialNackMessage(BondExecutionResultRegisterUseCaseInput input, Exception e) {

        String errorInfo = "";

        if (e instanceof DomainException) {
            errorInfo = ((DomainException) e).getErrorInfo().getMessage();
            errorInfo = errorInfo + " - ";
            errorInfo = errorInfo + e.getMessage();
        } else {
            errorInfo = e.getMessage();
        }

        String message = "체결 데이터 등록 실패(" + SequenceType.INITIAL + ") 체결번호: " + input.getId() + ", " + errorInfo;

        return message;
    }

    private String _setBondExecutionResultRegisterFinalAckMessage(RegisterBondExecutionResultDomainServiceOutput domainServiceOutput) {
        
        String message = "체결 데이터 등록 성공(" + SequenceType.FINAL + ") 체결번호: " + domainServiceOutput.getId();

        return message;
    }
}
