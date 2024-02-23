package com.tradegene.risk_management.commandservice.infrastructure.adapter.messaging.in;

import com.google.gson.Gson;
import com.tradegene.risk_management.commandservice.application.dto.BondExecutionResultRegisterUseCaseInput;
import com.tradegene.risk_management.commandservice.application.ports.in.BondExecutionResultUseCase;
import com.tradegene.risk_management.commandservice.infrastructure.adapter.dto.BondExecutionResultRegisterConsumerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BondExecutionResultConsumer {
	
	private final BondExecutionResultUseCase bondExecutionResultUseCase;

	@KafkaListener(
			id = "bond-execution-result.register",
			topics = "bond-execution-result.register",
			groupId = "risk-management",
			errorHandler = "customErrorHandler",
			autoStartup = "true"
	)
    public void subRegister(String input) {

		BondExecutionResultRegisterConsumerDto bondExecutionResultRegisterConsumerDto = new Gson().fromJson(input, BondExecutionResultRegisterConsumerDto.class);

		/* =================================================================
		 * 1. 거래 등록 useCase 호출
		 * ================================================================= */
		// 1-1. 입력값 조립
		BondExecutionResultRegisterUseCaseInput bondExecutionResultRegisterUseCaseInput = _setRegisterBondExecutionResultUseCaseInput(bondExecutionResultRegisterConsumerDto);

		// 1-1. 호출
		bondExecutionResultUseCase.register(bondExecutionResultRegisterUseCaseInput);
	}

	private BondExecutionResultRegisterUseCaseInput _setRegisterBondExecutionResultUseCaseInput(BondExecutionResultRegisterConsumerDto input) {
		return BondExecutionResultRegisterUseCaseInput.builder()
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
				.build();
	}
}
