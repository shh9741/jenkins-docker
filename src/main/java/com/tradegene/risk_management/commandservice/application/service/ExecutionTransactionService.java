package com.tradegene.risk_management.commandservice.application.service;

import com.tradegene.app.dto.common.enumeration.SequenceType;
import com.tradegene.app.exception.DomainException;
import com.tradegene.app.exception.ErrorInfo;
import com.tradegene.app.utils.CommonUtil;
import com.tradegene.risk_management.commandservice.application.dto.*;
import com.tradegene.risk_management.commandservice.application.ports.in.ExecutionTransactionUseCase;
import com.tradegene.risk_management.commandservice.application.ports.out.DataManagementMicroserviceClient;
import com.tradegene.risk_management.commandservice.application.ports.out.ExecutionTransactionProducer;
import com.tradegene.risk_management.commandservice.application.ports.out.PortfolioMicroserviceClient;
import com.tradegene.risk_management.commandservice.application.ports.out.TransactionQueryProducer;
import com.tradegene.risk_management.commandservice.domain.code.Code;
import com.tradegene.risk_management.commandservice.domain.dto.*;
import com.tradegene.risk_management.commandservice.domain.dto.RegisterContractTransactionDomainServiceOutput.ContractTransactionDetailDto;
import com.tradegene.risk_management.commandservice.domain.service.BondExecutionResultDomainService;
import com.tradegene.risk_management.commandservice.domain.service.TransactionDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExecutionTransactionService implements ExecutionTransactionUseCase {
	
	private final TransactionDomainService transactionDomainService;
	private final BondExecutionResultDomainService bondExecutionResultDomainService;

	private final TransactionQueryProducer transactionQueryProducer;
	private final ExecutionTransactionProducer executionTransactionProducer;
	
	private final PortfolioMicroserviceClient portfolioQueryMicroserviceClient;
	private final DataManagementMicroserviceClient dataManagementMicroserviceClient;
	
	@Transactional
	@Override
	public void register(ExecutionTransactionRegisterUseCaseInput input) {
		
		/* =================================================================
		 * 변수 선언
		 * ================================================================= */
		PortfolioDetailResponse portfolioDetailResponse = null;
		ProductDetailResponse productDetailResponse = null;
		ProductCashDetailResponse productCashDetailResponse = null;
		
		RegisterTrxTransactionDomainServiceInput registerTrxTransactionDomainServiceInput = null;
		RegisterTrxTransactionDomainServiceOutput registerTrxTransactionDomainServiceOutput = null;
		
		SplitExecutionTransactionDomainServiceInput splitExecutionTransactionDomainServiceInput = null;
		SplitExecutionTransactionDomainServiceOutput splitExecutionTransactionDomainServiceOutput = null;
		
		RegisterContractTransactionDomainServiceInput registerContractTransactionDomainServiceInput = null;
		RegisterContractTransactionDomainServiceOutput registerContractTransactionDomainServiceOutput = null;
		
		RegisterContractTransactionDomainServiceInput cashRegisterContractTransactionDomainServiceInput = null;
		RegisterContractTransactionDomainServiceOutput cashRegisterContractTransactionDomainServiceOutput = null;
		
		ValuePositionDomainServiceInput valuePositionDomainServiceInput = null;
		ValuePositionDomainServiceOutput valuePositionDomainServiceOutput = null;
		
		List<RegisterContractTransactionDomainServiceOutput> registerContractTransactionDomainServiceOutputList1 = new ArrayList<>();
		List<RegisterContractTransactionDomainServiceOutput> registerContractTransactionDomainServiceOutputList2 = new ArrayList<>();
		
		try {

			/* =================================================================
			 * 0. 입력값 검증
			 * ================================================================= */
			_checkRegisterInput(input);

			/* =================================================================
			 * 1. 포트폴리오 조회
			 * ================================================================= */
			portfolioDetailResponse = portfolioQueryMicroserviceClient.detailPortfolio(input.getPortfolioId()).getContent();

			/* =================================================================
			 * 2. 상품 조회
			 * ================================================================= */
			// 2-1. 거래 상품 조회
			productDetailResponse = dataManagementMicroserviceClient.detailProduct(input.getProductId()).getContent();
			
			// 2-3. 캐쉬 상품 조회
			productCashDetailResponse = dataManagementMicroserviceClient.detailCashProduct(portfolioDetailResponse.getPortfolioCurrencyCode()).getContent();

			/* =================================================================
			 * 3. 트렌젝션거래 등록 domain service 호출
			 * ================================================================= */
			// 3-1. 입력값 조립
			registerTrxTransactionDomainServiceInput = _setRegisterTrxTransactionDomainServiceInput(input);
			
			// 3-2. 호출
			registerTrxTransactionDomainServiceOutput = transactionDomainService.registerTrxTransaction(registerTrxTransactionDomainServiceInput);
			
			/* =================================================================
			 * 4. 계약거래 분리 domain service 호출
			 * ================================================================= */
			// 4-1. 입력값 조립
			splitExecutionTransactionDomainServiceInput = _setSplitExecutionTransactionDomainServiceInput(input);
			
			// 4-2. 호출
			splitExecutionTransactionDomainServiceOutput = transactionDomainService.splitExecutionTransaction(splitExecutionTransactionDomainServiceInput);

			for (SplitExecutionTransactionDomainServiceOutput.SplitTransactionDto splitTransactionDto : splitExecutionTransactionDomainServiceOutput.getSplitTransactionDtoList()) {
				
				/* =================================================================
				 * 5. 거래 상품 - 계약거래 등록 domain service 호출
				 * ================================================================= */
				// 5-1. 입력값 조립
				registerContractTransactionDomainServiceInput = _setRegisterContractTransactionDomainServiceInput(splitTransactionDto, productDetailResponse, registerTrxTransactionDomainServiceOutput.getTransactionDto().getId());
				
				// 5-2. 호출
				registerContractTransactionDomainServiceOutput = transactionDomainService.registerContractTransaction(registerContractTransactionDomainServiceInput);
				registerContractTransactionDomainServiceOutputList1.add(registerContractTransactionDomainServiceOutput);
				registerContractTransactionDomainServiceOutputList2.add(registerContractTransactionDomainServiceOutput);
				
				/* =================================================================
				 * 6. 캐쉬 상품 - 계약거래 등록 domain service 호출
				 * ================================================================= */
				// 6-1. 입력값 조립
				cashRegisterContractTransactionDomainServiceInput = _setCashRegisterContractTransactionDomainServiceInput(splitTransactionDto, registerContractTransactionDomainServiceOutput, productCashDetailResponse);
				
				// 6-2. 호출
				cashRegisterContractTransactionDomainServiceOutput = transactionDomainService.registerContractTransaction(cashRegisterContractTransactionDomainServiceInput);
				registerContractTransactionDomainServiceOutputList2.add(cashRegisterContractTransactionDomainServiceOutput);
			}
			
			/* =================================================================
			 * 7. 포지션 평가 domain service 호출
			 * ================================================================= */
			// 7-1. 입력값 조립
			valuePositionDomainServiceInput = _setValuePositionDomainServiceInput(input);
			
			// 7-2. 호출
			valuePositionDomainServiceOutput = transactionDomainService.valuePosition(valuePositionDomainServiceInput);


			/* =================================================================
			 * 채권채결결과 처리완료 domain service 호출
			 * ================================================================= */
			bondExecutionResultDomainService.setProcessed(input.getExecutionId());
		
		} catch (Exception e) {

			/* =================================================================
			 * 8. initial nack topic 발행
			 * ================================================================= */
			// 8-1. 입력값 조립
			ExecutionTransactionRegisterAckNackProducerDto executionTransactionRegisterAckNackProducerDto = _setExecutionTransactionRegisterAckNackProducerDto(input.getExecutionId());
			String message = _setExecutionTransactionRegisterInitialNackMessage(e);
			
			// 8-2. 호출
			executionTransactionProducer.pubRegisterInitialNack(executionTransactionRegisterAckNackProducerDto, message);
			
			throw e;
		}

		/* =================================================================
		 * 8. mian topic 발행
		 * ================================================================= */
		// 8-1. 입력값 조립
		ExecutionTransactionRegisterProducerDto executionTransactionRegisterProducerDto = _setExecutionTransactionRegisterProducerDto(registerTrxTransactionDomainServiceOutput, registerContractTransactionDomainServiceOutputList1, valuePositionDomainServiceOutput);
		
		// 8-2. 호출
		executionTransactionProducer.pubRegister(executionTransactionRegisterProducerDto);
		
		/* =================================================================
		 * 9. query MSA 동기화 topic 발행
		 * ================================================================= */
		// 9-1. 입력값 조립
		TransactionQueryProducerDto transactionQueryProducerDto = _setTransactionQueryProducerDto(registerContractTransactionDomainServiceOutputList2);
		
		// 9-2. 호출
		transactionQueryProducer.pubQuery(transactionQueryProducerDto);
		
		/* =================================================================
		 * 10. initial ack topic 발행
		 * ================================================================= */
		// 10-1. 입력값 조립
		String message = _setExecutionTransactionRegisterInitialAckMessage(registerTrxTransactionDomainServiceOutput);
		
		// 10-2. 호출
		executionTransactionProducer.pubRegisterInitialAck(message);
	}

	@Transactional
	@Override
	public void registerSuccess(ExecutionTransactionRegisterSuccessUseCaseInput input) {

		/* =================================================================
		 * 1. 포트폴리오MQ처리여부 갱신 domain service 호출
		 * ================================================================= */
		// 1-1. 입력값 조립
		UpdatePortfolioMqProcessingYnDomainServiceInput updatePortfolioMqProcessingYnDomainServiceInput = UpdatePortfolioMqProcessingYnDomainServiceInput.builder()
				.transactionId(input.getTransactionId())
				.portfolioMqProcessingYn(Code.YES)
				.build();
		
		// 1-2. 호출
		UpdatePortfolioMqProcessingYnDomainServiceOutput updatePortfolioMqProcessingYnDomainServiceOutput = transactionDomainService.updatePortfolioMqProcessingYn(updatePortfolioMqProcessingYnDomainServiceInput);
		
		/* =================================================================
		 * 2. query MSA 동기화 topic 발행
		 * ================================================================= */
		// 2-1. 입력값 조립
		TransactionQueryProducerDto transactionQueryProducerDto = _setTransactionQueryProducerDto(updatePortfolioMqProcessingYnDomainServiceOutput);
		
		// 2-2. 호출
		transactionQueryProducer.pubQuery(transactionQueryProducerDto);

		/* =================================================================
		 * 3. final ack topic 발행
		 * ================================================================= */
		// 3-1. 입력값 조립
		String message = _setExecutionTransactionRegisterFinalAckMessage(updatePortfolioMqProcessingYnDomainServiceOutput);
		ExecutionTransactionRegisterAckNackProducerDto executionTransactionRegisterAckNackProducerDto = _setExecutionTransactionRegisterAckNackProducerDto(updatePortfolioMqProcessingYnDomainServiceOutput.getTransactionDto().getExecutionId());
		
		// 3-2. 호출
		executionTransactionProducer.pubRegisterFinalAck(executionTransactionRegisterAckNackProducerDto, message);
	}

	@Transactional
	@Override
	public void registerFailure(ExecutionTransactionRegisterFailureUseCaseInput input) {
		
		/* =================================================================
		 * 1. 포트폴리오MQ처리여부 갱신 domain service 호출
		 * ================================================================= */
		// 1-1. 입력값 조립
		UpdatePortfolioMqProcessingYnDomainServiceInput updatePortfolioMqProcessingYnDomainServiceInput = UpdatePortfolioMqProcessingYnDomainServiceInput.builder()
				.transactionId(input.getTransactionId())
				.portfolioMqProcessingYn(Code.NO)
				.build();
		
		// 1-2. 호출
		UpdatePortfolioMqProcessingYnDomainServiceOutput updatePortfolioMqProcessingYnDomainServiceOutput = transactionDomainService.updatePortfolioMqProcessingYn(updatePortfolioMqProcessingYnDomainServiceInput);

		/* =================================================================
		 * 2. query MSA 동기화 topic 발행
		 * ================================================================= */
		// 2-1. 입력값 조립
		TransactionQueryProducerDto transactionQueryProducerDto = _setTransactionQueryProducerDto(updatePortfolioMqProcessingYnDomainServiceOutput);

		// 2-2. 호출
		transactionQueryProducer.pubQuery(transactionQueryProducerDto);
		
		/* =================================================================
		 * 3. final nack topic 발행
		 * ================================================================= */
		// 3-1. 입력값 조립
		String message = _setExecutionTransactionRegisterFinalNackMessage(updatePortfolioMqProcessingYnDomainServiceOutput);
		ExecutionTransactionRegisterAckNackProducerDto executionTransactionRegisterAckNackProducerDto = _setExecutionTransactionRegisterAckNackProducerDto(updatePortfolioMqProcessingYnDomainServiceOutput.getTransactionDto().getExecutionId());
		
		// 3-2. 호출
		executionTransactionProducer.pubRegisterFinalNack(executionTransactionRegisterAckNackProducerDto, message);
	}

	private void _checkRegisterInput(ExecutionTransactionRegisterUseCaseInput input) {

		// 포트폴리오ID
		if (CommonUtil.isNullOrZero(input.getPortfolioId())) {

			throw new DomainException(ErrorInfo.ERROR_0001, "포트폴리오ID");
		}

		// 상품ID
		if (CommonUtil.isNullOrZero(input.getProductId())) {

			throw new DomainException(ErrorInfo.ERROR_0001, "상품ID");
		}
	}

	private RegisterTrxTransactionDomainServiceInput _setRegisterTrxTransactionDomainServiceInput(
			ExecutionTransactionRegisterUseCaseInput input) {

		RegisterTrxTransactionDomainServiceInput registerTrxTransactionDomainServiceInput = RegisterTrxTransactionDomainServiceInput.builder()
				.executionId(input.getExecutionId())
				.executionDate(input.getExecutionDate())
				.executionTime(input.getExecutionTime())
				.build();
		
		return registerTrxTransactionDomainServiceInput;
	}
	
	private SplitExecutionTransactionDomainServiceInput _setSplitExecutionTransactionDomainServiceInput(
			ExecutionTransactionRegisterUseCaseInput input) {

		SplitExecutionTransactionDomainServiceInput contractTransactionSplitDomainServiceInput = SplitExecutionTransactionDomainServiceInput.builder()
				.portfolioId(input.getPortfolioId())
				.productId(input.getProductId())
				.executionDate(input.getExecutionDate())
				.executionTime(input.getExecutionTime())
				.sellBuyTypeCode(input.getSellBuyTypeCode())
				.executionQuantity(input.getExecutionQuantity())
				.executionPrice(input.getExecutionPrice())
				.executionAmount(input.getExecutionAmount())
				.settlementDate(input.getSettlementDate())
				.build();
		
		return contractTransactionSplitDomainServiceInput;
	}

	private RegisterContractTransactionDomainServiceInput _setRegisterContractTransactionDomainServiceInput(
			SplitExecutionTransactionDomainServiceOutput.SplitTransactionDto input,
			ProductDetailResponse productDetailResponse,
			Long transactionId) {

		RegisterContractTransactionDomainServiceInput.ProductDto productDto = RegisterContractTransactionDomainServiceInput.ProductDto.builder()
				.id(productDetailResponse.getId())
				.cashYn(productDetailResponse.getCashYn())
				.currencyCode(productDetailResponse.getCurrencyCode())
				.build();

		RegisterContractTransactionDomainServiceInput registerContractTransactionDomainServiceInput = RegisterContractTransactionDomainServiceInput.builder()
				.transactionId(transactionId)
				.portfolioId(input.getPortfolioId())
				.productDto(productDto)
				.executionDate(input.getExecutionDate())
				.executionTime(input.getExecutionTime())
				.sellBuyTypeCode(input.getSellBuyTypeCode())
				.executionQuantity(input.getExecutionQuantity())
				.executionPrice(input.getExecutionPrice())
				.executionAmount(input.getExecutionAmount())
				.settlementDate(input.getSettlementDate())
				.build();
		
		return registerContractTransactionDomainServiceInput;
	}
	
	private RegisterContractTransactionDomainServiceInput _setCashRegisterContractTransactionDomainServiceInput(
			SplitExecutionTransactionDomainServiceOutput.SplitTransactionDto input,
			RegisterContractTransactionDomainServiceOutput contractRegisterTransactionDomainServiceOutput,
			ProductCashDetailResponse productCashDetailResponse) {

		// 매도매입구분코드
		String sellBuyTypeCode = contractRegisterTransactionDomainServiceOutput.getContractLegTransactionDto().getSellBuyTypeCode() == "1" ? "2" : "1";
		
		// 세금금액
		BigDecimal taxAmount = _getTransactionAmount(contractRegisterTransactionDomainServiceOutput.getContractTransactionDetailDtoList(), "01", "01");
		// 수수료금액
		BigDecimal feeAmount = _getTransactionAmount(contractRegisterTransactionDomainServiceOutput.getContractTransactionDetailDtoList(), "02", "02");
		// 거래금액
		BigDecimal transactionAmont = contractRegisterTransactionDomainServiceOutput.getContractLegTransactionDto().getTransactionAmount();
		
		// 총거래금액 = 거래금액 + 세금금액 + 거래금액
		BigDecimal totalTransactionAmont = transactionAmont.add(taxAmount).add(feeAmount);

		RegisterContractTransactionDomainServiceInput.ProductDto productDto = RegisterContractTransactionDomainServiceInput.ProductDto.builder()
				.id(productCashDetailResponse.getId())
				.cashYn(productCashDetailResponse.getCashYn())
				.currencyCode(productCashDetailResponse.getCurrencyCode())
				.build();
		
		
		RegisterContractTransactionDomainServiceInput registerContractTransactionDomainServiceInput = RegisterContractTransactionDomainServiceInput.builder()
				.transactionId(contractRegisterTransactionDomainServiceOutput.getContractTransactionDto().getTransactionId())
				.portfolioId(contractRegisterTransactionDomainServiceOutput.getContractDto().getPortfolioId())
				.productDto(productDto)
				.executionDate(contractRegisterTransactionDomainServiceOutput.getContractTransactionDto().getTransactionDate())
				.executionTime(input.getExecutionTime())
				.sellBuyTypeCode(sellBuyTypeCode)
				.executionQuantity(totalTransactionAmont)
				.executionPrice(BigDecimal.ONE)
				.executionAmount(totalTransactionAmont)
				.settlementDate(input.getSettlementDate())
				.build();
		
		return registerContractTransactionDomainServiceInput;
	}

	private ExecutionTransactionRegisterProducerDto _setExecutionTransactionRegisterProducerDto(
			RegisterTrxTransactionDomainServiceOutput registerTrxTransactionDomainServiceOutput, 
			List<RegisterContractTransactionDomainServiceOutput> registerContractTransactionDomainServiceOutputList1,
			ValuePositionDomainServiceOutput valuePositionDomainServiceOutput) {
		
		Long portfolioId = null;
		Long productId = null;
		String transactionDate = "";
		String currencyCode = "";
		
		if (registerContractTransactionDomainServiceOutputList1.size() > 0) {
			
			portfolioId = registerContractTransactionDomainServiceOutputList1.get(0).getContractDto().getPortfolioId();
			productId = registerContractTransactionDomainServiceOutputList1.get(0).getContractDto().getProductId();
			transactionDate = registerContractTransactionDomainServiceOutputList1.get(0).getContractTransactionDto().getTransactionDate();
			currencyCode = registerContractTransactionDomainServiceOutputList1.get(0).getContractLegTransactionDto().getPriceCurrencyCode();
		}

		BigDecimal transactionProfitLossAmount = BigDecimal.ZERO;
		
		for (RegisterContractTransactionDomainServiceOutput temp : registerContractTransactionDomainServiceOutputList1) {
		
			transactionProfitLossAmount = transactionProfitLossAmount.add(temp.getContractLegTransactionDto().getTransactionProfitLossAmount());
		}
		
		ExecutionTransactionRegisterProducerDto executionTransactionRegisterProducerDto = ExecutionTransactionRegisterProducerDto.builder()
				.transactionId(registerTrxTransactionDomainServiceOutput.getTransactionDto().getId())
				.portfolioId(portfolioId)
				.productId(productId)
				.transactionDate(transactionDate)
				.currencyCode(currencyCode)
				.transactionProfitLossAmount(transactionProfitLossAmount)
				.positionQuantity(valuePositionDomainServiceOutput.getPositionQuantity())
				.valueProfitLossAmount(valuePositionDomainServiceOutput.getPositionValuationProfitLossAmount())
				.build();
		
		return executionTransactionRegisterProducerDto;
	}

	private String _setExecutionTransactionRegisterInitialAckMessage(RegisterTrxTransactionDomainServiceOutput registerTrxTransactionDomainServiceOutput) {

		String message = "체결 거래 등록 성공(" + SequenceType.INITIAL + ") 거래번호: " + registerTrxTransactionDomainServiceOutput.getTransactionDto().getId();

		return message;
	}

	private String _setExecutionTransactionRegisterFinalAckMessage(UpdatePortfolioMqProcessingYnDomainServiceOutput updatePortfolioMqProcessingYnDomainServiceOutput) {

		String message = "체결 거래 등록 성공(" + SequenceType.FINAL + ") 거래번호: " + updatePortfolioMqProcessingYnDomainServiceOutput.getTransactionDto().getId();

		return message;
	}

	private String _setExecutionTransactionRegisterInitialNackMessage(Exception e) {

		String errorInfo = "";

		if (e instanceof DomainException) {
			errorInfo = ((DomainException) e).getErrorInfo().getMessage();
			errorInfo = errorInfo + " - ";
			errorInfo = errorInfo + e.getMessage();
		} else {
			errorInfo = e.getMessage();
		}

		String message = "체결 거래 등록 실패(" + SequenceType.INITIAL + ") " + errorInfo;

		return message;
	}

	private String _setExecutionTransactionRegisterFinalNackMessage(UpdatePortfolioMqProcessingYnDomainServiceOutput updatePortfolioMqProcessingYnDomainServiceOutput) {

		String message = "체결 거래 등록 실패(" + SequenceType.FINAL + ") 거래번호: " + updatePortfolioMqProcessingYnDomainServiceOutput.getTransactionDto().getId();

		return message;
	}

	private ExecutionTransactionRegisterAckNackProducerDto _setExecutionTransactionRegisterAckNackProducerDto(Long executionId) {

		return ExecutionTransactionRegisterAckNackProducerDto.builder()
				.executionId(executionId)
				.build();
	}

	private ValuePositionDomainServiceInput _setValuePositionDomainServiceInput(ExecutionTransactionRegisterUseCaseInput input) {

		ValuePositionDomainServiceInput valuePositionDomainServiceInput = ValuePositionDomainServiceInput.builder()
				.portfolioId(input.getPortfolioId())
				.productId(input.getProductId())
				.valuationPrice(input.getExecutionPrice())
				.build();
		
		return valuePositionDomainServiceInput;
	}

	private TransactionQueryProducerDto _setTransactionQueryProducerDto(
			List<RegisterContractTransactionDomainServiceOutput> contractRegisterTransactionDomainServiceOutputList) {

		List<TransactionQueryProducerDto.Grid> gridList = new ArrayList<>();
		
		for (RegisterContractTransactionDomainServiceOutput contractRegisterTransactionDomainServiceOutput : contractRegisterTransactionDomainServiceOutputList) {
			
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
			TransactionQueryProducerDto.Grid grid = TransactionQueryProducerDto.Grid.builder()
					.contractDto(contractDto)
					.contractTransactionDto(contractTransactionDto)
					.contractPositionDto(contractPositionDto)
					.updateContractPositionDto(updateContractPositionDto)
					.build();
			
			gridList.add(grid);
		}
		
		TransactionQueryProducerDto transactionQueryProducerDto = TransactionQueryProducerDto.builder()
				.gridList(gridList)
				.build();
		
		return transactionQueryProducerDto;
	}

	private TransactionQueryProducerDto _setTransactionQueryProducerDto(UpdatePortfolioMqProcessingYnDomainServiceOutput updatePortfolioMqProcessingYnDomainServiceOutput) {

		List<TransactionQueryProducerDto.Grid> gridList = new ArrayList<>();

		for (UpdatePortfolioMqProcessingYnDomainServiceOutput.ContractTransactionDto temp : updatePortfolioMqProcessingYnDomainServiceOutput.getContractTransactionDto()) {

			/* =================================================================
			 * 계약거래
			 * ================================================================= */
			TransactionQueryProducerDto.UpdateContractTransactionDto updateContractTransactionDto = TransactionQueryProducerDto.UpdateContractTransactionDto.builder()
					.id(temp.getId())
					.portfolioMqProcessingYn(temp.getPortfolioMqProcessingYn())
					.build();

			/* =================================================================
			 * grid 조립
			 * ================================================================= */
			TransactionQueryProducerDto.Grid grid = TransactionQueryProducerDto.Grid.builder()
					.updateContractTransactionDto(updateContractTransactionDto)
					.build();

			gridList.add(grid);
		}

		TransactionQueryProducerDto transactionQueryProducerDto = TransactionQueryProducerDto.builder()
				.gridList(gridList)
				.build();

		return transactionQueryProducerDto;

	}

	private BigDecimal _getTransactionAmount(List<ContractTransactionDetailDto> contractTransactionDetailDtoList, 
			String amountPatternTypeCode, 
			String detailAmountPatternTypeCode) {

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
