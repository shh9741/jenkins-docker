package com.tradegene.risk_management.commandservice.application.service;

import com.tradegene.app.dto.common.enumeration.SequenceType;
import com.tradegene.app.exception.DomainException;
import com.tradegene.app.exception.ErrorInfo;
import com.tradegene.app.utils.CommonUtil;
import com.tradegene.app.utils.DateUtil;
import com.tradegene.app.utils.WorkContextUtil;
import com.tradegene.risk_management.commandservice.application.dto.*;
import com.tradegene.risk_management.commandservice.application.ports.in.CashTransactionUseCase;
import com.tradegene.risk_management.commandservice.application.ports.out.CashTransactionProducer;
import com.tradegene.risk_management.commandservice.application.ports.out.DataManagementMicroserviceClient;
import com.tradegene.risk_management.commandservice.application.ports.out.PortfolioMicroserviceClient;
import com.tradegene.risk_management.commandservice.application.ports.out.TransactionQueryProducer;
import com.tradegene.risk_management.commandservice.domain.code.Code;
import com.tradegene.risk_management.commandservice.domain.dto.RegisterContractTransactionDomainServiceInput;
import com.tradegene.risk_management.commandservice.domain.dto.RegisterContractTransactionDomainServiceOutput;
import com.tradegene.risk_management.commandservice.domain.dto.RegisterContractTransactionDomainServiceOutput.ContractTransactionDetailDto;
import com.tradegene.risk_management.commandservice.domain.service.TransactionDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CashTransactionService implements CashTransactionUseCase {

	private final TransactionDomainService contractTransactionDomainService;
	
	private final CashTransactionProducer cashTransactionProducer;
	private final TransactionQueryProducer transactionQueryProducer;
	
	private final PortfolioMicroserviceClient portfolioMicroserviceClient;
	private final DataManagementMicroserviceClient dataManagementMicroserviceClient;

	
	@Transactional
	@Override
	public CashTransactionRegisterUseCaseOutput register(CashTransactionRegisterUseCaseInput input) {
		
		/* =================================================================
		 * 변수 선언
		 * ================================================================= */
		PortfolioDetailResponse portfolioDetailResponse = null;
		ProductCashDetailResponse productCashDetailResponse = null;
		
		RegisterContractTransactionDomainServiceInput registerContractTransactionDomainServiceInput = null;
		RegisterContractTransactionDomainServiceOutput registerContractTransactionDomainServiceOutput = null;
		
		try {

			/* =================================================================
			 * 0. 입력값 검증
			 * ================================================================= */
			_checkRegisterInput(input);
			
			/* =================================================================
			 * 1. 포트폴리오 조회
			 * ================================================================= */
			portfolioDetailResponse = portfolioMicroserviceClient.detailPortfolio(WorkContextUtil.getPortfolioId()).getContent();

			/* =================================================================
			 * 2. cash상품 조회
			 * ================================================================= */
			productCashDetailResponse = dataManagementMicroserviceClient.detailCashProduct(portfolioDetailResponse.getPortfolioCurrencyCode()).getContent();

			/* =================================================================
			 * 3. 계약거래 등록 domain service 호출
			 * ================================================================= */
			// 3-1. 입력값 조립
			registerContractTransactionDomainServiceInput = _setRegisterContractTransactionDomainServiceInput(input, productCashDetailResponse);
			
			// 3-2. 호출
			registerContractTransactionDomainServiceOutput = contractTransactionDomainService.registerContractTransaction(registerContractTransactionDomainServiceInput);
		
		} catch (Exception e) {

			/* =================================================================
			 * 4. initial nack topic 발행
			 * ================================================================= */
			// 4-1. 입력값 조립
			String message = _setCashTransactionRegisterInitialNackMessage(e);

			// 4-2. 호출
			cashTransactionProducer.pubRegisterInitialNack(message);
			
			throw e;
		}
		
		/* =================================================================
		 * 4. query MSA 동기화 topic 발행
		 * ================================================================= */
		// 4-1. 입력값 조립
		TransactionQueryProducerDto transactionQueryProducerDto = _setTransactionQueryProducerDto(registerContractTransactionDomainServiceOutput);
		
		// 4-2. 호출
		transactionQueryProducer.pubQuery(transactionQueryProducerDto);

		/* =================================================================
		 * 5. final ack topic 발행
		 * ================================================================= */
		// 5-1. 입력값 조립
		String message = _setCashTransactionRegisterFinalAckMessage(registerContractTransactionDomainServiceOutput);

		// 5-2. 호출
		cashTransactionProducer.pubRegisterFinalAck(message);

		/* =================================================================
		 * 6. 결과값 리턴
		 * ================================================================= */
		// 6-1. 결과값 조립
		CashTransactionRegisterUseCaseOutput cashTransactionRegisterUseCaseOutput = _setCashTransactionRegisterUseCaseOutput(registerContractTransactionDomainServiceOutput);

		// 6-2. 결과값 리턴
		return cashTransactionRegisterUseCaseOutput;
	}

	private void _checkRegisterInput(CashTransactionRegisterUseCaseInput input) {

		// 포트폴리오ID
		if (CommonUtil.isNullOrZero(WorkContextUtil.getPortfolioId())) {

			throw new DomainException(ErrorInfo.ERROR_0001, "포트폴리오ID");
		}
	}

	private RegisterContractTransactionDomainServiceInput _setRegisterContractTransactionDomainServiceInput(CashTransactionRegisterUseCaseInput command, ProductCashDetailResponse productCashDetailResponse) {

		RegisterContractTransactionDomainServiceInput.ProductDto productDto = RegisterContractTransactionDomainServiceInput.ProductDto.builder()
				.id(productCashDetailResponse.getId())
				.cashYn(productCashDetailResponse.getCashYn())
				.currencyCode(productCashDetailResponse.getCurrencyCode())
				.build();

		RegisterContractTransactionDomainServiceInput registerContractTransactionDomainServiceInput = RegisterContractTransactionDomainServiceInput.builder()
				.transactionId(Long.valueOf(0))
				.portfolioId(WorkContextUtil.getPortfolioId())
				.productDto(productDto)
				.executionDate(DateUtil.getCurrentDate())
				.executionTime(DateUtil.getCurrentTime())
				.sellBuyTypeCode(Code.SELL_BUY_TYPE_CODE_BUY)
				.executionQuantity(command.getAmount())
				.executionPrice(BigDecimal.ONE)
				.executionAmount(command.getAmount())
				.settlementDate(DateUtil.getCurrentDate())
				.portfolioMqProcessingTargetYn(Code.NO)
				.build();
		
		return registerContractTransactionDomainServiceInput;
	}

	private String _setCashTransactionRegisterFinalAckMessage(RegisterContractTransactionDomainServiceOutput registerContractTransactionDomainServiceOutput) {

		String message = "현금 거래 등록 성공(" + SequenceType.FINAL + ") 거래번호: " + registerContractTransactionDomainServiceOutput.getContractTransactionDto().getId();

		return message;
	}

	private String _setCashTransactionRegisterInitialNackMessage(Exception e) {

		String errorInfo = "";

		if (e instanceof DomainException) {
			errorInfo = ((DomainException) e).getErrorInfo().getMessage();
			errorInfo = errorInfo + " - ";
			errorInfo = errorInfo + e.getMessage();
		} else {
			errorInfo = e.getMessage();
		}

		String message = "현금 거래 등록 실패(" + SequenceType.INITIAL + ") " + errorInfo;

		return message;
	}

	private CashTransactionRegisterUseCaseOutput _setCashTransactionRegisterUseCaseOutput(RegisterContractTransactionDomainServiceOutput registerContractTransactionDomainServiceOutput) {

		return CashTransactionRegisterUseCaseOutput.builder()
				.contractTransactionId(registerContractTransactionDomainServiceOutput.getContractTransactionDto().getId())
				.build();
	}

	private TransactionQueryProducerDto _setTransactionQueryProducerDto(RegisterContractTransactionDomainServiceOutput contractRegisterTransactionDomainServiceOutput) {
		
		/* =================================================================
		 * 계약
		 * ================================================================= */
		TransactionQueryProducerDto.ContractDto contractDto = TransactionQueryProducerDto.ContractDto.builder()
				.id(contractRegisterTransactionDomainServiceOutput.getContractDto().getId())
				.productId(contractRegisterTransactionDomainServiceOutput.getContractDto().getProductId())
				.transactionCounterpartyEntityId(contractRegisterTransactionDomainServiceOutput.getContractDto().getTransactionCounterpartyEntityId())
				.contractDate(contractRegisterTransactionDomainServiceOutput.getContractDto().getContractDate())
				.validDate(contractRegisterTransactionDomainServiceOutput.getContractDto().getValidDate())
				.expiryDate(contractRegisterTransactionDomainServiceOutput.getContractDto().getExpiryDate())
				.portfolioId(contractRegisterTransactionDomainServiceOutput.getContractDto().getPortfolioId())
				.build();
		
		
		/* =================================================================
		 * 계약거래
		 * ================================================================= */
		// 거래세금금액
		BigDecimal transactionTaxAmount = _getTransactionAmount(contractRegisterTransactionDomainServiceOutput.getContractTransactionDetailDtoList(), Code.AMOUNT_PATTERN_TYPE_CODE_TAX, Code.AMOUNT_PATTERN_TYPE_CODE_TAX);
		// 거래수수료금액
		BigDecimal transactionFeeAmount = _getTransactionAmount(contractRegisterTransactionDomainServiceOutput.getContractTransactionDetailDtoList(), Code.AMOUNT_PATTERN_TYPE_CODE_FEE, Code.AMOUNT_PATTERN_TYPE_CODE_FEE);
		
		TransactionQueryProducerDto.ContractTransactionDto contractTransactionDto = TransactionQueryProducerDto.ContractTransactionDto.builder()
				.id(contractRegisterTransactionDomainServiceOutput.getContractTransactionDto().getId())
				.transactionId(contractRegisterTransactionDomainServiceOutput.getContractTransactionDto().getTransactionId())
				.contractId(contractRegisterTransactionDomainServiceOutput.getContractTransactionDto().getContractId())
				.portfolioMqProcessingYn(contractRegisterTransactionDomainServiceOutput.getContractTransactionDto().getPortfolioMqProcessingYn())
				.transactionDate(contractRegisterTransactionDomainServiceOutput.getContractTransactionDto().getTransactionDate())
				.cancelYn(contractRegisterTransactionDomainServiceOutput.getContractLegTransactionDto().getCancelYn())
				.sellBuyTypeCode(contractRegisterTransactionDomainServiceOutput.getContractLegTransactionDto().getSellBuyTypeCode())
				.transactionCurrencyCode(contractRegisterTransactionDomainServiceOutput.getContractLegTransactionDto().getTransactionCurrencyCode())
				.priceCurrencyCode(contractRegisterTransactionDomainServiceOutput.getContractLegTransactionDto().getPriceCurrencyCode())
				.transactionQuantity(contractRegisterTransactionDomainServiceOutput.getContractLegTransactionDto().getTransactionQuantity())
				.transactionPrice(contractRegisterTransactionDomainServiceOutput.getContractLegTransactionDto().getTransactionPrice())
				.transactionAmount(contractRegisterTransactionDomainServiceOutput.getContractLegTransactionDto().getTransactionAmount())
				.transactionTaxAmount(transactionTaxAmount)
				.transactionFeeAmount(transactionFeeAmount)
				.transactionProfitLossAmount(contractRegisterTransactionDomainServiceOutput.getContractLegTransactionDto().getTransactionProfitLossAmount())
				.settlementDate(contractRegisterTransactionDomainServiceOutput.getContractLegTransactionDto().getSettlementDate())
				.settlementCurrencyCode(contractRegisterTransactionDomainServiceOutput.getContractLegTransactionDto().getSettlementCurrencyCode())
				.settlementAmount(contractRegisterTransactionDomainServiceOutput.getContractLegTransactionDto().getSettlementAmount())
				.settlementExchangeRate(contractRegisterTransactionDomainServiceOutput.getContractLegTransactionDto().getSettlementExchangeRate())
				.settlementYn(contractRegisterTransactionDomainServiceOutput.getContractLegTransactionDto().getSettlementYn())
				.build();
		
		/* =================================================================
		 * 계약포지션
		 * ================================================================= */
		TransactionQueryProducerDto.ContractPositionDto contractPositionDto = TransactionQueryProducerDto.ContractPositionDto.builder()
				.id(contractRegisterTransactionDomainServiceOutput.getContractLegPositionDto().getId())
				.contractTransactionId(contractRegisterTransactionDomainServiceOutput.getContractTransactionDto().getId())
				.cancelYn(contractRegisterTransactionDomainServiceOutput.getContractLegPositionDto().getCancelYn())
				.positionTypeCode(contractRegisterTransactionDomainServiceOutput.getContractLegPositionDto().getPositionTypeCode())
				.positionStartDatetime(contractRegisterTransactionDomainServiceOutput.getContractLegPositionDto().getPositionStartDatetime())
				.positionEndDatetime(contractRegisterTransactionDomainServiceOutput.getContractLegPositionDto().getPositionEndDatetime())
				.currencyCode(contractRegisterTransactionDomainServiceOutput.getContractLegPositionDto().getCurrencyCode())
				.priceCurrencyCode(contractRegisterTransactionDomainServiceOutput.getContractLegPositionDto().getPriceCurrencyCode())
				.positionQuantity(contractRegisterTransactionDomainServiceOutput.getContractLegPositionDto().getPositionQuantity())
				.transactionPrice(contractRegisterTransactionDomainServiceOutput.getContractLegPositionDto().getTransactionPrice())
				.transactionAmount(contractRegisterTransactionDomainServiceOutput.getContractLegPositionDto().getTransactionAmount())
				.build();
		
		/* =================================================================
		 * 이전 계약레그포지션
		 * ================================================================= */
		TransactionQueryProducerDto.UpdateContractPositionDto updateContractPositionDto = null;
		
		if (contractRegisterTransactionDomainServiceOutput.getPreviousContractLegPositionDto() != null) {
			
			updateContractPositionDto = TransactionQueryProducerDto.UpdateContractPositionDto.builder()
					.id(contractRegisterTransactionDomainServiceOutput.getPreviousContractLegPositionDto().getId())
					.positionEndDatetime(contractRegisterTransactionDomainServiceOutput.getPreviousContractLegPositionDto().getPositionEndDatetime())
					.build();
		}
		
		/* =================================================================
		 * grid 조립
		 * ================================================================= */
		List<TransactionQueryProducerDto.Grid> gridList = new ArrayList<>();
		
		TransactionQueryProducerDto.Grid grid = TransactionQueryProducerDto.Grid.builder()
				.contractDto(contractDto)
				.contractTransactionDto(contractTransactionDto)
				.contractPositionDto(contractPositionDto)
				.updateContractPositionDto(updateContractPositionDto)
				.build();
		
		gridList.add(grid);
		
		TransactionQueryProducerDto transactionQueryProducerDto = TransactionQueryProducerDto.builder()
				.gridList(gridList)
				.build();
		
		return transactionQueryProducerDto;
	}
	
	private BigDecimal _getTransactionAmount(List<ContractTransactionDetailDto> contractTransactionDetailDtoList, String amountPatternTypeCode, String detailAmountPatternTypeCode) {

		BigDecimal transactionAmount = BigDecimal.ZERO;
		
		for (ContractTransactionDetailDto contractTransactionDetailDto : contractTransactionDetailDtoList) {
			
			// 금액유형코드, 상세금액유형코드가 같은 경우
			if (	amountPatternTypeCode.equals(contractTransactionDetailDto.getAmountPatternTypeCode())
				&&  detailAmountPatternTypeCode.equals(contractTransactionDetailDto.getDetailAmountPatternTypeCode())) {
				
				transactionAmount = contractTransactionDetailDto.getTransactionAmount();
				
				break;
			}
		}
		
		return transactionAmount;
	}
}
