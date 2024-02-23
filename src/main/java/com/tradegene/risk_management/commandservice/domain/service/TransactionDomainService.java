package com.tradegene.risk_management.commandservice.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.tradegene.app.utils.WorkContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.tradegene.app.exception.DomainException;
import com.tradegene.app.exception.ErrorInfo;
import com.tradegene.app.utils.CommonUtil;
import com.tradegene.app.utils.DateUtil;
import com.tradegene.risk_management.commandservice.application.ports.out.ContractLegPositionReader;
import com.tradegene.risk_management.commandservice.application.ports.out.ContractLegReader;
import com.tradegene.risk_management.commandservice.application.ports.out.ContractLegTransactionStore;
import com.tradegene.risk_management.commandservice.application.ports.out.ContractReader;
import com.tradegene.risk_management.commandservice.application.ports.out.ContractTransactionReader;
import com.tradegene.risk_management.commandservice.application.ports.out.ContractTransactionStore;
import com.tradegene.risk_management.commandservice.application.ports.out.TransactionReader;
import com.tradegene.risk_management.commandservice.application.ports.out.TransactionStore;
import com.tradegene.risk_management.commandservice.domain.code.Code;
import com.tradegene.risk_management.commandservice.domain.dto.RegisterContractTransactionDomainServiceInput;
import com.tradegene.risk_management.commandservice.domain.dto.RegisterContractTransactionDomainServiceOutput;
import com.tradegene.risk_management.commandservice.domain.dto.RegisterTrxTransactionDomainServiceInput;
import com.tradegene.risk_management.commandservice.domain.dto.RegisterTrxTransactionDomainServiceOutput;
import com.tradegene.risk_management.commandservice.domain.dto.SplitExecutionTransactionDomainServiceInput;
import com.tradegene.risk_management.commandservice.domain.dto.SplitExecutionTransactionDomainServiceOutput;
import com.tradegene.risk_management.commandservice.domain.dto.UpdatePortfolioMqProcessingYnDomainServiceInput;
import com.tradegene.risk_management.commandservice.domain.dto.UpdatePortfolioMqProcessingYnDomainServiceOutput;
import com.tradegene.risk_management.commandservice.domain.dto.ValuePositionDomainServiceInput;
import com.tradegene.risk_management.commandservice.domain.dto.ValuePositionDomainServiceOutput;
import com.tradegene.risk_management.commandservice.domain.model.Contract;
import com.tradegene.risk_management.commandservice.domain.model.ContractLeg;
import com.tradegene.risk_management.commandservice.domain.model.ContractLegPosition;
import com.tradegene.risk_management.commandservice.domain.model.ContractLegTransaction;
import com.tradegene.risk_management.commandservice.domain.model.ContractTransaction;
import com.tradegene.risk_management.commandservice.domain.model.ContractTransactionDetail;
import com.tradegene.risk_management.commandservice.domain.model.Transaction;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionDomainService {
	
	private final TransactionStore transactionStore;
	
    private final ContractTransactionStore contractTransactionStore;
    private final ContractLegTransactionStore contractLegTransactionStore;
    
    private final TransactionReader transactionReader;
    private final ContractReader contractReader;
    private final ContractTransactionReader contractTransactionReader;
    private final ContractLegReader contractLegReader;
    private final ContractLegPositionReader contractLegPositionReader;
    
    public RegisterTrxTransactionDomainServiceOutput registerTrxTransaction(RegisterTrxTransactionDomainServiceInput input) {
    	
    	/* =================================================================
	     * 1. 입력값 검증
	     * ================================================================= */
    	// 1-1. 필수입력항목 검증
	    _checkReigsterTrxTransactionInput(input);
	
	    // 1-2. 거래정합성 검증
	    _validReigsterTrxTransactionInput(input);
	    
	
	    /* =================================================================
	     * 2. 트랜젝션 save
	     * ================================================================= */
	    // 2-1. 입력값 조립
	    Transaction transaction = _setTransaction(input);
	
	    // 2-2. 호출
	    transactionStore.save(transaction);
	    
	
	    /* =================================================================
	     * 3. 출력값 조립
	     * ================================================================= */
	    RegisterTrxTransactionDomainServiceOutput output = _setRegisterTrxTransactionDomainServiceOutput(transaction);
	
	    return output;
	}
    
    public RegisterContractTransactionDomainServiceOutput registerContractTransaction(RegisterContractTransactionDomainServiceInput input) {
    	
    	/* =================================================================
	     * 1. 입력값 검증
	     * ================================================================= */
	    // 1-1. 필수입력항목 검증
	    _checkReigsterContTransactionInput(input);
	
	    // 1-2. 거래정합성 검증
	    _validReigsterContTransactionInput(input);
	    
	    
	    /* =================================================================
	     * 2. 계약거래 save
	     * ================================================================= */
    	// 2-1. 계약 조회
    	Contract contract = contractReader.findFirstByPortfolioIdAndProductId(input.getPortfolioId(), input.getProductDto().getId());
    	
	    // 2-2. 입력값 조립 - 계약
	    if (contract == null) {
	    	
	    	contract = _setContract(input);
	    }
	
	    // 2-3. 입력값 조립 - 계약거래상세List
	    List<ContractTransactionDetail> contractTransactionDetails = _setContractTransactionDetails(input);
	
	    // 2-4. 입력값 조립 - 계약거래
	    ContractTransaction contractTransaction = _setContractTransaction(input, contract, contractTransactionDetails);
	
	    // 2-5. 호출
	    contractTransactionStore.save(contractTransaction);
	    
	
	    /* =================================================================
	     * 3. 계약레그거래 save
	     * ================================================================= */
	    // 포지션시작일시
	    String positionStartTime = DateUtil.toDateTime(input.getExecutionDate(), input.getExecutionTime());
	    // 이전 계약레그포지션
	    ContractLegPosition previousContractLegPosition = null;
	    
	    // 3-1. 계약레그 조회
	    ContractLeg contractLeg = contractLegReader.findFirstByContractIdAndProductId(contract.getId(), input.getProductDto().getId());

	    
	    if (contractLeg == null) {
	    	
	    	// 3-2. 입력값 조립 - 계약레그기
	    	contractLeg = _setContractLeg(input, contract.getId());
	   
	    } else {
	    	
	    	// 3-2. 이전 계약레그포지션 조회
	    	previousContractLegPosition = contractLegPositionReader.findFirstByContractLegOrderByIdDesc(contractLeg);
	    	previousContractLegPosition = _setPreviousContractLegPosition(previousContractLegPosition, positionStartTime);
	    }
	    
	    // 3-3. 입력값 조립 - 계약레그포지션
	    ContractLegPosition contractLegPosition = _setContractLegPosition(input, contractLeg, previousContractLegPosition, positionStartTime);
	    
	    // 3-4. 입력값 조립 -  계약레그거래
	    ContractLegTransaction contractLegTransaction = _setContractLegTransaction(input, contractTransaction, contractLeg, contractLegPosition, previousContractLegPosition);
	  
	    // 3-5. 호출
	    contractLegTransactionStore.save(contractLegTransaction);
	    
	
	    /* =================================================================
	     * 4. 출력값 조립
	     * ================================================================= */
	    RegisterContractTransactionDomainServiceOutput output = _setRegisterContractTransactionDomainServiceOutput(contract, contractTransaction, contractTransactionDetails, contractLeg, contractLegTransaction, contractLegPosition, previousContractLegPosition);
	
	    return output;
	}
    
    public SplitExecutionTransactionDomainServiceOutput  splitExecutionTransaction(SplitExecutionTransactionDomainServiceInput input) {
		
    	/* =================================================================
	     * 1. 입력값 검증
	     * ================================================================= */
    	// 1-1. 필수입력항목 검증

	    
    	/* =================================================================
	     * 2. 포지션수량 조회
	     * ================================================================= */
	    // 포지션수량
	 	BigDecimal positionQuantity = BigDecimal.ZERO;
	 	
	    ContractLegPosition contractLegPosition = _getContractLegPosition(input.getPortfolioId(), input.getProductId());
	    
	    if (contractLegPosition != null) {
			
			positionQuantity = contractLegPosition.getPositionQuantity();
		}
    	
    	
    	/* =================================================================
	     * 3. 분할여부 조립
	     * ================================================================= */
    	String splitYn = _setSplitYn(input, positionQuantity);
    	
    	
    	/* =================================================================
	     * 4. 출력값 조립
	     * ================================================================= */
    	SplitExecutionTransactionDomainServiceOutput splitExecutionTransactionDomainServiceOutput = _setSplitExecutionTransactionDomainServiceOutput(input, positionQuantity, splitYn);
    	
    	return splitExecutionTransactionDomainServiceOutput;
    }
    
    public ValuePositionDomainServiceOutput valuePosition(ValuePositionDomainServiceInput input) {
		
    	/* =================================================================
	     * 1. 입력값 검증
	     * ================================================================= */
    	// 1-1. 필수입력항목 검증
	    _checkValuePositionTransactionInput(input);
	
	    // 1-2. 거래정합성 검증
	    _validValuePositionTransactionInput(input);
	    
	    
    	/* =================================================================
	     * 2. 포지션수량, 포지션금액 조회
	     * ================================================================= */
    	// 포지션수량
 	 	BigDecimal positionQuantity = BigDecimal.ZERO;
 	 	// 포지션금액
 	 	BigDecimal positionAmount = BigDecimal.ZERO;
 	 	
 	    ContractLegPosition contractLegPosition = _getContractLegPosition(input.getPortfolioId(), input.getProductId());
 	    
 	    if (contractLegPosition != null) {
 			
 			positionQuantity = contractLegPosition.getPositionQuantity();
 			positionAmount = contractLegPosition.getTransactionAmount();
 		}
    	
    	
    	/* =================================================================
	     * 3. 포지션평가금액, 포지션평가손익금액 계산
	     * ================================================================= */
    	// 포지션평가금액 = 포지션수량 * 평가가격
    	BigDecimal positionValuationAmount = positionQuantity.multiply(input.getValuationPrice());
    	
    	// 포지션평가손익금액 = 포지션평가금액 - 포지션금액
    	BigDecimal positionValuationProfitLossAmount = positionValuationAmount.subtract(positionAmount);
    	
    	
    	/* =================================================================
	     * 4. 출력값 조립
	     * ================================================================= */
    	ValuePositionDomainServiceOutput valuePositionDomainServiceOutput = _setValuePositionDomainServiceOutput(contractLegPosition, positionValuationProfitLossAmount);
    	
    	return valuePositionDomainServiceOutput;
    }

	public UpdatePortfolioMqProcessingYnDomainServiceOutput updatePortfolioMqProcessingYn(UpdatePortfolioMqProcessingYnDomainServiceInput input) {
    	
    	/* =================================================================
	     * 1. 입력값 검증
	     * ================================================================= */
    	// 1-1. 필수입력항목 검증
	    _checkUpdatePortfolioMqProcessingYnInput(input);
	
	    // 1-2. 트렌젝션 선조회
	    Transaction transaction = transactionReader.findById(input.getTransactionId());
	    
	    // 1-3. 거래정합성 검증
	    _validUpdatePortfolioMqProcessingYnInput(input, transaction);
	    
	    
	    /* =================================================================
	     * 2. 포트폴리오MQ처리여부 save
	     * ================================================================= */
	    // 2-1. 계약거래List 조회
	    List<ContractTransaction> previousContractTransactions  = contractTransactionReader.findByTransactionId(transaction.getId());
	    
	    List<ContractTransaction> contractTransactions = new ArrayList<>();
	    
	    for (ContractTransaction contractTransaction : previousContractTransactions) {
	    	
	    	// 2-2. 입력값 조립
	    	ContractTransaction updateContractTransaction = contractTransaction.toBuilder()
	    			.portfolioMqProcessingYn(input.getPortfolioMqProcessingYn())
	    			.build();
	    	
	    	// 2-3. 호출
	    	contractTransactionStore.save(updateContractTransaction);
	    	
	    	contractTransactions.add(updateContractTransaction);
	    }
	    
	    
	    /* =================================================================
	     * 3. 출력값 조립
	     * ================================================================= */
	    UpdatePortfolioMqProcessingYnDomainServiceOutput updatePortfolioMqProcessingYnDomainServiceOutput = _setUpdatePortfolioMqProcessingYnDomainServiceOutput(transaction, contractTransactions);
	    
    	return updatePortfolioMqProcessingYnDomainServiceOutput;
    }

	private void _checkReigsterTrxTransactionInput(RegisterTrxTransactionDomainServiceInput input) {
		
		// 체결ID
		if (CommonUtil.isNullOrZero(input.getExecutionId())) {
			
			throw new DomainException(ErrorInfo.ERROR_0001, "체결ID");
		}
		
		// 체결일자
		if (CommonUtil.isNullOrBlank(input.getExecutionDate())) {
			
			throw new DomainException(ErrorInfo.ERROR_0001, "체결일자");
		}
		
		// 체결시각
		if (CommonUtil.isNullOrBlank(input.getExecutionTime())) {
			
			throw new DomainException(ErrorInfo.ERROR_0001, "체결시각");
		}
	}

	private void _checkReigsterContTransactionInput(RegisterContractTransactionDomainServiceInput input) {
			
		// 트랜잭션ID
    	if (input.getTransactionId() == null) {
    		
    		throw new DomainException(ErrorInfo.ERROR_0001, "트랜잭션ID");
    	}
    	
    	// 포트폴리오ID
    	if (CommonUtil.isNullOrZero(input.getPortfolioId())) {
    		
    		throw new DomainException(ErrorInfo.ERROR_0001, "포트폴리오ID");
    	}
    	
    	// 상품
    	if (input.getProductDto() == null) {
    		
    		throw new DomainException(ErrorInfo.ERROR_0001, "상품");
    	}
    	
    	// 체결일자
    	if (CommonUtil.isNullOrBlank(input.getExecutionDate())) {
    		
    		throw new DomainException(ErrorInfo.ERROR_0001, "체결일자");
    	}
    	
    	// 체결시각
    	if (CommonUtil.isNullOrBlank(input.getExecutionTime())) {
    		
    		throw new DomainException(ErrorInfo.ERROR_0001, "체결시각");
    	}
    	
    	// 매수매도구분코드
    	if (CommonUtil.isNullOrBlank(input.getSellBuyTypeCode())) {
    		
    		throw new DomainException(ErrorInfo.ERROR_0001, "매수매도구분코드");
    	}
    	
    	// 체결수량
    	if (CommonUtil.isNullOrZero(input.getExecutionQuantity())) {
    		
    		throw new DomainException(ErrorInfo.ERROR_0001, "거래수량");
    	}
    	
    	// 체결가격
    	if (CommonUtil.isNullOrZero(input.getExecutionPrice())) {
    		
    		throw new DomainException(ErrorInfo.ERROR_0001, "거래가격");
    	}
    	
    	// 체결금액
    	if (CommonUtil.isNullOrZero(input.getExecutionAmount())) {
    		
    		throw new DomainException(ErrorInfo.ERROR_0001, "거래금액");
    	}
    	
    	// 결제일자
    	if (CommonUtil.isNullOrBlank(input.getSettlementDate())) {
    		
    		throw new DomainException(ErrorInfo.ERROR_0001, "결제일자");
    	}
	}

	private void _checkSplitExecutionTransactionInput(SplitExecutionTransactionDomainServiceInput input) {

		// 포트폴리오ID
    	if (CommonUtil.isNullOrZero(input.getPortfolioId())) {
    		
    		throw new DomainException(ErrorInfo.ERROR_0001, "포트폴리오ID");
    	}
    	
    	// 상품ID
    	if (CommonUtil.isNullOrZero(input.getProductId())) {
    		
    		throw new DomainException(ErrorInfo.ERROR_0001, "상품ID");
    	}
    	
    	// 체결일자
    	if (CommonUtil.isNullOrBlank(input.getExecutionDate())) {
    		
    		throw new DomainException(ErrorInfo.ERROR_0001, "체결일자");
    	}
    	
    	// 체결시각
    	if (CommonUtil.isNullOrBlank(input.getExecutionTime())) {
    		
    		throw new DomainException(ErrorInfo.ERROR_0001, "체결시각");
    	}
    	
    	// 매수매도구분코드
    	if (CommonUtil.isNullOrBlank(input.getSellBuyTypeCode())) {
    		
    		throw new DomainException(ErrorInfo.ERROR_0001, "매수매도구분코드");
    	}
    	
    	// 체결수량
    	if (CommonUtil.isNullOrZero(input.getExecutionQuantity())) {
    		
    		throw new DomainException(ErrorInfo.ERROR_0001, "거래수량");
    	}
    	
    	// 체결가격
    	if (CommonUtil.isNullOrZero(input.getExecutionPrice())) {
    		
    		throw new DomainException(ErrorInfo.ERROR_0001, "거래가격");
    	}
    	
    	// 체결금액
    	if (CommonUtil.isNullOrZero(input.getExecutionAmount())) {
    		
    		throw new DomainException(ErrorInfo.ERROR_0001, "거래금액");
    	}
		
    	// 결제일자
    	if (CommonUtil.isNullOrBlank(input.getSettlementDate())) {
    		
    		throw new DomainException(ErrorInfo.ERROR_0001, "결제일자");
    	}
	}

	private void _checkUpdatePortfolioMqProcessingYnInput(UpdatePortfolioMqProcessingYnDomainServiceInput input) {

		// 트렌젝션ID
		if (CommonUtil.isNullOrZero(input.getTransactionId())) {
			
			throw new DomainException(ErrorInfo.ERROR_0001, "트렌젝션ID");
		}
		
	}

	private void _checkValuePositionTransactionInput(ValuePositionDomainServiceInput input) {

		// 포트폴리오ID
    	if (CommonUtil.isNullOrZero(input.getPortfolioId())) {
    		
    		throw new DomainException(ErrorInfo.ERROR_0001, "포트폴리오ID");
    	}
    	
    	// 상품ID
    	if (CommonUtil.isNullOrZero(input.getProductId())) {
    		
    		throw new DomainException(ErrorInfo.ERROR_0001, "상품ID");
    	}
    	
    	// 평가가격
    	if (CommonUtil.isNullOrZero(input.getValuationPrice())) {
    		
    		throw new DomainException(ErrorInfo.ERROR_0001, "평가가격");
    	}
	}

	private void _validReigsterTrxTransactionInput(RegisterTrxTransactionDomainServiceInput input) {

		Boolean result = transactionReader.existsBySourceSystemCodeAndSourceSystemId(WorkContextUtil.getSourceSystemCode(), input.getExecutionId());

		if (result) {

			throw new DomainException(ErrorInfo.ERROR_PROCESSED_MESSAGE);
		}
	}

	private void _validReigsterContTransactionInput(RegisterContractTransactionDomainServiceInput input) {

		// 체결수량
		if (BigDecimal.ZERO.compareTo(input.getExecutionQuantity()) > 0) {

			throw new DomainException(ErrorInfo.ERROR_0002, "거래수량은 0보다 커야 합니다.");
		}
	}
	
	private void _validSplitExecutionTransactionInput(SplitExecutionTransactionDomainServiceInput input) {
		
		// TODO 거래정합성 검증
	}

	private void _validUpdatePortfolioMqProcessingYnInput(UpdatePortfolioMqProcessingYnDomainServiceInput input, Transaction transaction) {

		// TODO 거래정합성 검증
	}

	private void _validValuePositionTransactionInput(ValuePositionDomainServiceInput input) {

		// TODO 거래정합성 검증
	}

	private Transaction _setTransaction(RegisterTrxTransactionDomainServiceInput input) {
		
		Transaction transaction = Transaction.builder()
				.sourceSystemCode(WorkContextUtil.getSourceSystemCode())
				.sourceSystemId(input.getExecutionId())
				.executionId(input.getExecutionId())
				.cancelYn(Code.NO)
				.transactionProcessingDatetime(DateUtil.toDateTime(input.getExecutionDate(), input.getExecutionTime()))
				.build();
		
		return transaction;
	}

	private Contract _setContract(RegisterContractTransactionDomainServiceInput input) {
    	
    	Contract contract = Contract.builder()
	    			.productId(input.getProductDto().getId())
	    			.transactionCounterpartyEntityId(null)
	    			.contractDate(input.getExecutionDate())
	    			.validDate("")
	    			.expiryDate("")
	    			.portfolioId(input.getPortfolioId())
	    			.build();
    	
        return contract;
    }
	
	private List<ContractTransactionDetail> _setContractTransactionDetails(RegisterContractTransactionDomainServiceInput input) {

		// 계약거래상세List
		List<ContractTransactionDetail> contractTransactionDetails = new ArrayList<ContractTransactionDetail>();
    	
    	// 캐쉬 상품인 경우 return
    	if (Code.YES.equals(input.getProductDto().getCashYn())) {
    		
    		return contractTransactionDetails;
    	}
    	
    	// 세금금액
    	BigDecimal taxAmount = BigDecimal.ZERO;
    	// 수수료금액
    	BigDecimal feeAmount = BigDecimal.ZERO;
    	
    	// TODO 금액유형유형코드, 상세금액유형구분코드
    		
    	// 계약거래상세 - 세금
		ContractTransactionDetail contractTransactionDetail = ContractTransactionDetail.builder()
				.amountPatternTypeCode(Code.AMOUNT_PATTERN_TYPE_CODE_TAX)
				.detailAmountPatternTypeCode(Code.AMOUNT_PATTERN_TYPE_CODE_TAX)
				.transactionCurrencyCode(Code.KRW)
				.priceCurrencyCode(Code.KRW)
				.transactionAmount(taxAmount)
				.transactionProfitLossAmount(taxAmount.negate())
				.settlementDate(input.getSettlementDate())
				.settlementCurrencyCode(Code.KRW)
				.settlementAmount(taxAmount)
				.settlementExchangeRate(BigDecimal.ONE)
				.settlementYn(Code.NO)
				.build();
    		
		contractTransactionDetails.add(contractTransactionDetail);
    	
    	// 계약거래상세 - 수수료 
		contractTransactionDetail = ContractTransactionDetail.builder()
    				.amountPatternTypeCode(Code.AMOUNT_PATTERN_TYPE_CODE_FEE)
    				.detailAmountPatternTypeCode(Code.AMOUNT_PATTERN_TYPE_CODE_FEE)
    				.transactionCurrencyCode(Code.KRW)
    				.priceCurrencyCode(Code.KRW)
    				.transactionAmount(feeAmount)
    				.transactionProfitLossAmount(feeAmount.negate())
    				.settlementDate(input.getSettlementDate())
    				.settlementCurrencyCode(Code.KRW)
    				.settlementAmount(feeAmount)
    				.settlementExchangeRate(BigDecimal.ONE)
    				.settlementYn(Code.NO)
    				.build();
    		
		contractTransactionDetails.add(contractTransactionDetail);
        
        return contractTransactionDetails;
    }
	
	private ContractTransaction _setContractTransaction(RegisterContractTransactionDomainServiceInput input, Contract contract, List<ContractTransactionDetail> contractTransactionDetails) {

		// 포트폴리오MQ처리여부
		String portfolioMqProcessingYn = "";

		// 포트폴리오MQ처리대상이 아닌 경우 Y
		if (Code.NO.equals(input.getPortfolioMqProcessingTargetYn())) {
			portfolioMqProcessingYn = Code.YES;
		}

    	ContractTransaction contractTransaction = ContractTransaction.builder()
    			.transactionId(input.getTransactionId())
    			.contract(contract)
    			.portfolioMqProcessingYn(portfolioMqProcessingYn)
    			.transactionDate(input.getExecutionDate())
    			.sellBuyTypeCode(input.getSellBuyTypeCode())
    			.contractTransactionDetails(new ArrayList<>())
    			.build();
    	
    	for (ContractTransactionDetail contractTransactionDetail : contractTransactionDetails) {
    		
    		contractTransaction.addContractTransactionDetail(contractTransactionDetail);
    	}
    	
    	return contractTransaction;
    }
	
	private ContractLeg _setContractLeg(RegisterContractTransactionDomainServiceInput input, Long contractId) {
		
		ContractLeg contractLeg = ContractLeg.builder()
				.contractId(contractId)
				.legTransactionDate(input.getExecutionDate())
				.legValidDate("")
				.legExpiryDate("")
				.productId(input.getProductDto().getId())
				.build();
		
        return contractLeg;
    }
	
	private ContractLegPosition _setContractLegPosition(RegisterContractTransactionDomainServiceInput input, ContractLeg contractLeg, ContractLegPosition previousContractLegPosition, String positionStartTime) {

		log.info("@@ input-> " + input.toString());

    	// 매수매도구분코드
    	String sellBuyTypeCode = input.getSellBuyTypeCode();

		log.info("@@ Constant.SELL_BUY_TYPE_CODE_SELL.equals(sellBuyTypeCode)-> " + Code.SELL_BUY_TYPE_CODE_SELL.equals(sellBuyTypeCode));
    	
    	// 거래수량
    	BigDecimal transactionQuantity  = input.getExecutionQuantity();
    	transactionQuantity = Code.SELL_BUY_TYPE_CODE_SELL.equals(sellBuyTypeCode) ? transactionQuantity.negate() : transactionQuantity;
    	
    	// 거래금액
    	BigDecimal transactionAmount  = input.getExecutionAmount();
    	transactionAmount = Code.SELL_BUY_TYPE_CODE_SELL.equals(sellBuyTypeCode) ? transactionAmount.negate() : transactionAmount;
    	
    	
    	// 이전 포지션수량
    	BigDecimal previousPositionQuantity = BigDecimal.ZERO;
    	// 이전 포지션금액
    	BigDecimal previousTransactionAmount = BigDecimal.ZERO;
    	
    	if (previousContractLegPosition != null) {
    		
    		previousPositionQuantity = previousContractLegPosition.getPositionQuantity();
    		previousTransactionAmount = previousContractLegPosition.getTransactionAmount();
    	}

    	// 포지션수량
    	BigDecimal positionQuantity = previousPositionQuantity.add(transactionQuantity);

		log.info("@@ previousPositionQuantity-> " + previousPositionQuantity);
		log.info("@@ transactionQuantity-> " + transactionQuantity);
		log.info("@@ positionQuantity-> " + positionQuantity);
    	
    	// 포지션금액
    	BigDecimal positionAmount = BigDecimal.ZERO;
    	
    	if (BigDecimal.ZERO.compareTo(positionQuantity) != 0) {
    		
    		positionAmount = previousTransactionAmount.add(transactionAmount);
    	}
    	
    	// 포지션가격 = 포지션금액 / 포지션수량
    	BigDecimal positionPrice = BigDecimal.ZERO;
    	
    	if (BigDecimal.ZERO.compareTo(positionQuantity) != 0 && BigDecimal.ZERO.compareTo(positionAmount) != 0) {

			if (Code.YES.equals(input.getProductDto().getCashYn())) {
				positionPrice = positionAmount.divide(positionQuantity, 2, RoundingMode.HALF_UP);
			} else {
				positionPrice = positionAmount.multiply(BigDecimal.valueOf(10000)).divide(positionQuantity, 2, RoundingMode.HALF_UP);
			}
    	}
    	
    	// TODO 포지션유형코드, 상세포지션유형코드

    	ContractLegPosition contractLegPosition = ContractLegPosition.builder()
    			.contractLeg(contractLeg)
    			.cancelYn(Code.NO)
    			.positionTypeCode(Code.POSITION_TYPE_CODE_PRINCIPAL)
    			.positionStartDatetime(positionStartTime)
    			.positionEndDatetime("99991231235959")
    			.currencyCode(Code.KRW)
                .priceCurrencyCode(Code.KRW)
                .positionQuantity(positionQuantity)
                .transactionPrice(positionPrice)
                .transactionAmount(positionAmount)
    			.build();

    	
    	return contractLegPosition;
    }
	
	private ContractLegTransaction _setContractLegTransaction(RegisterContractTransactionDomainServiceInput input, ContractTransaction contractTransaction, ContractLeg contractLeg, ContractLegPosition contractLegPosition, ContractLegPosition previousContractLegPosition) {

		// 거래손익금액		
		BigDecimal transactionProfitLossAmount = BigDecimal.ZERO;
		
		if ( previousContractLegPosition != null) {
			
			// 이전 포지션수량
			BigDecimal previousPositionQuantity = previousContractLegPosition.getPositionQuantity();
			// 매도매입구본코드
			String sellBuyTypeCode = input.getSellBuyTypeCode();
			
			// + 이전 포지션수량 && 매도 거래인 경우, - 이전 포지션수량 && 매입 거래인 경우
			if (   BigDecimal.ZERO.compareTo(previousPositionQuantity) < 0 && Code.SELL_BUY_TYPE_CODE_SELL.equals(sellBuyTypeCode)
				|| BigDecimal.ZERO.compareTo(previousPositionQuantity) > 0 && Code.SELL_BUY_TYPE_CODE_BUY.equals(sellBuyTypeCode)) {
				
				// 거래가격차이 (거래가격 - 이전 거래가격)
				BigDecimal transactionPriceGap = input.getExecutionPrice().subtract(previousContractLegPosition.getTransactionPrice());
				
				// 거래손익금액 (거래가격차이 * 거래수량)
				transactionProfitLossAmount = transactionPriceGap.multiply(input.getExecutionQuantity()).divide(BigDecimal.valueOf(10000));
				
				if (BigDecimal.ZERO.compareTo(previousPositionQuantity) < 0 ) {
					
					transactionProfitLossAmount.negate();
				}
			}
		}
		
		
		ContractLegTransaction contractLegTransaction = ContractLegTransaction.builder()
				.contractLeg(contractLeg)
				.contractTransactionId(contractTransaction.getId())
				.cancelYn(Code.NO)
				.sellBuyTypeCode(input.getSellBuyTypeCode())
				.transactionCurrencyCode(Code.KRW)
				.priceCurrencyCode(Code.KRW)
				.transactionQuantity(input.getExecutionQuantity())
				.transactionPrice(input.getExecutionPrice())
				.transactionAmount(input.getExecutionAmount())
				.transactionProfitLossAmount(transactionProfitLossAmount)
				.settlementDate(input.getSettlementDate())
				.settlementCurrencyCode(Code.KRW)
				.settlementAmount(input.getExecutionAmount())
				.settlementExchangeRate(BigDecimal.ONE)
				.settlementYn(Code.NO)
				.build();
		
		contractLegTransaction.setContractLegPosition(contractLegPosition);
		
		return contractLegTransaction;
	}
	
	private RegisterTrxTransactionDomainServiceOutput _setRegisterTrxTransactionDomainServiceOutput(Transaction transaction) {
		
		// 트랜잭션
		RegisterTrxTransactionDomainServiceOutput.TransactionDto transactionDto = RegisterTrxTransactionDomainServiceOutput.TransactionDto.builder()
				.id(transaction.getId())
				.executionId(transaction.getExecutionId())
				.cancelYn(transaction.getCancelYn())
				.transactionProcessingDatetime(transaction.getTransactionProcessingDatetime())
				.build();
		
		RegisterTrxTransactionDomainServiceOutput registerTrxTransactionDomainServiceOutput = RegisterTrxTransactionDomainServiceOutput.builder()
				.transactionDto(transactionDto)
				.build();
		
	    return registerTrxTransactionDomainServiceOutput;
	}

	private RegisterContractTransactionDomainServiceOutput _setRegisterContractTransactionDomainServiceOutput(Contract contract
			, ContractTransaction contractTransaction
			, List<ContractTransactionDetail> contractTransactionDetails
			, ContractLeg contractLeg, ContractLegTransaction contractLegTransaction
			, ContractLegPosition contractLegPosition
			, ContractLegPosition previousContractLegPosition) {
		
		// 계약
		RegisterContractTransactionDomainServiceOutput.ContractDto contractDto = RegisterContractTransactionDomainServiceOutput.ContractDto.builder()
				.id(contract.getId())
				.productId(contract.getProductId())
    			.transactionCounterpartyEntityId(contract.getTransactionCounterpartyEntityId())
    			.contractDate(contract.getContractDate())
    			.validDate(contract.getValidDate())
    			.expiryDate(contract.getExpiryDate())
    			.portfolioId(contract.getPortfolioId())
    			.build();
		
		// 계약거래
		RegisterContractTransactionDomainServiceOutput.ContractTransactionDto contractTransactionDto = RegisterContractTransactionDomainServiceOutput.ContractTransactionDto.builder()
				.id(contractTransaction.getId())
				.transactionId(contractTransaction.getTransactionId())
    			.contractId(contractTransaction.getContract().getId())
    			.portfolioMqProcessingYn(contractTransaction.getPortfolioMqProcessingYn())
    			.transactionDate(contractTransaction.getTransactionDate())
    			.sellBuyTypeCode(contractTransaction.getSellBuyTypeCode())
				.build();
			
		// 계약거래상세
		List<RegisterContractTransactionDomainServiceOutput.ContractTransactionDetailDto> ContractTransactionDetailDtoList = new ArrayList<>();
		
		for (ContractTransactionDetail contractTransactionDetail : contractTransactionDetails) {

			RegisterContractTransactionDomainServiceOutput.ContractTransactionDetailDto contractTransactionDetailDto = RegisterContractTransactionDomainServiceOutput.ContractTransactionDetailDto.builder()
					.id(contractTransactionDetail.getId())
					.contractTransactionId(contractTransactionDetail.getContractTransaction().getId())
					.amountPatternTypeCode(contractTransactionDetail.getAmountPatternTypeCode())
					.detailAmountPatternTypeCode(contractTransactionDetail.getDetailAmountPatternTypeCode())
					.transactionCurrencyCode(contractTransactionDetail.getTransactionCurrencyCode())
					.priceCurrencyCode(contractTransactionDetail.getPriceCurrencyCode())
					.transactionAmount(contractTransactionDetail.getTransactionAmount())
					.transactionProfitLossAmount(contractTransactionDetail.getTransactionProfitLossAmount())
					.settlementDate(contractTransactionDetail.getSettlementDate())
					.settlementCurrencyCode(contractTransactionDetail.getSettlementCurrencyCode())
					.settlementAmount(contractTransactionDetail.getSettlementAmount())
					.settlementExchangeRate(contractTransactionDetail.getSettlementExchangeRate())
					.settlementYn(contractTransactionDetail.getSettlementYn())
					.build();
			
			ContractTransactionDetailDtoList.add(contractTransactionDetailDto);
		}
		
		// 계약레그
		RegisterContractTransactionDomainServiceOutput.ContractLegDto contractLegDto = RegisterContractTransactionDomainServiceOutput.ContractLegDto.builder()
				.id(contractLeg.getId())
				.contractId(contractLeg.getContractId())
				.legTransactionDate(contractLeg.getLegTransactionDate())
				.legValidDate(contractLeg.getLegValidDate())
				.legExpiryDate(contractLeg.getLegExpiryDate())
				.productId(contractLeg.getProductId())
				.build();
		
		// 계약레그거래
		RegisterContractTransactionDomainServiceOutput.ContractLegTransactionDto contractLegTransactionDto = RegisterContractTransactionDomainServiceOutput.ContractLegTransactionDto.builder()
				.id(contractLegTransaction.getId())
				.contractLegId(contractLegTransaction.getContractLeg().getId())
				.contractTransactionId(contractLegTransaction.getContractTransactionId())
				.cancelYn(contractLegTransaction.getCancelYn())
				.sellBuyTypeCode(contractLegTransaction.getSellBuyTypeCode())
				.transactionCurrencyCode(contractLegTransaction.getTransactionCurrencyCode())
				.priceCurrencyCode(contractLegTransaction.getPriceCurrencyCode())
				.transactionQuantity(contractLegTransaction.getTransactionQuantity())
				.transactionPrice(contractLegTransaction.getTransactionPrice())
				.transactionAmount(contractLegTransaction.getTransactionAmount())
				.transactionProfitLossAmount(contractLegTransaction.getTransactionProfitLossAmount())
				.settlementDate(contractLegTransaction.getSettlementDate())
				.settlementCurrencyCode(contractLegTransaction.getSettlementCurrencyCode())
				.settlementAmount(contractLegTransaction.getSettlementAmount())
				.settlementExchangeRate(contractLegTransaction.getSettlementExchangeRate())
				.settlementYn(contractLegTransaction.getSettlementYn())
				.build();
		
		// 계약레그포지션
		RegisterContractTransactionDomainServiceOutput.ContractLegPositionDto contractLegPositionDto = RegisterContractTransactionDomainServiceOutput.ContractLegPositionDto.builder()
				.id(contractLegPosition.getId())
				.contractLegId(contractLegPosition.getContractLeg().getId())
				.contractLegTransactionId(contractLegPosition.getContractLegTransaction().getId())
    			.cancelYn(contractLegPosition.getCancelYn())
    			.positionTypeCode(contractLegPosition.getPositionTypeCode())
    			.positionStartDatetime(contractLegPosition.getPositionStartDatetime())
    			.positionEndDatetime(contractLegPosition.getPositionEndDatetime())
    			.currencyCode(contractLegPosition.getCurrencyCode())
                .priceCurrencyCode(contractLegPosition.getPriceCurrencyCode())
                .positionQuantity(contractLegPosition.getPositionQuantity())
                .transactionPrice(contractLegPosition.getTransactionPrice())
                .transactionAmount(contractLegPosition.getTransactionAmount())
				.build();
		
		// 이전 계약레그포지션
		RegisterContractTransactionDomainServiceOutput.PreviousContractLegPositionDto previousContractLegPositionDto = null;
		
		if (previousContractLegPosition != null) {
			
			previousContractLegPositionDto = RegisterContractTransactionDomainServiceOutput.PreviousContractLegPositionDto.builder()
					.id(previousContractLegPosition.getId())
					.positionEndDatetime(previousContractLegPosition.getPositionEndDatetime())
					.build();
		}
		
		RegisterContractTransactionDomainServiceOutput registerContractTransactionDomainServiceOutput = RegisterContractTransactionDomainServiceOutput.builder()
				.contractDto(contractDto)
				.contractTransactionDto(contractTransactionDto)
				.contractTransactionDetailDtoList(ContractTransactionDetailDtoList)
				.contractLegDto(contractLegDto)
				.contractLegTransactionDto(contractLegTransactionDto)
				.contractLegPositionDto(contractLegPositionDto)
				.previousContractLegPositionDto(previousContractLegPositionDto)
				.build();
		
	    return registerContractTransactionDomainServiceOutput;
	}

	private SplitExecutionTransactionDomainServiceOutput _setSplitExecutionTransactionDomainServiceOutput(SplitExecutionTransactionDomainServiceInput input, BigDecimal positionQuantity, String splitYn) {
	
		List<SplitExecutionTransactionDomainServiceOutput.SplitTransactionDto> splitTransactionDtoList = new ArrayList<>();
		
		if (Code.YES.equals(splitYn)) {
			
			// 체결가격
			BigDecimal executionPrice = input.getExecutionPrice();
			
			// 체결수량
			BigDecimal executionQuantity = positionQuantity;
			// 체결금액
			BigDecimal executionAmount = executionQuantity.multiply(executionPrice);
			
			SplitExecutionTransactionDomainServiceOutput.SplitTransactionDto splitTransactionDto = SplitExecutionTransactionDomainServiceOutput.SplitTransactionDto.builder()
					.portfolioId(input.getPortfolioId())
					.productId(input.getProductId())
					.executionDate(input.getExecutionDate())
					.executionTime(input.getExecutionTime())
					.sellBuyTypeCode(input.getSellBuyTypeCode())
					.executionQuantity(executionQuantity)
					.executionPrice(executionPrice)
					.executionAmount(executionAmount)
					.settlementDate(input.getSettlementDate())
					.build();
			
			splitTransactionDtoList.add(splitTransactionDto);
			
			
			// 거래수량
			executionQuantity = input.getExecutionQuantity();
			executionQuantity = Code.SELL_BUY_TYPE_CODE_SELL.equals(input.getSellBuyTypeCode()) ? executionQuantity.negate() : executionQuantity;
			executionQuantity = positionQuantity.add(executionQuantity);
			executionQuantity = executionQuantity.abs();
			// 거래금액
			executionAmount = executionQuantity.multiply(executionPrice);
			
			splitTransactionDto = SplitExecutionTransactionDomainServiceOutput.SplitTransactionDto.builder()
					.portfolioId(input.getPortfolioId())
					.productId(input.getProductId())
					.executionDate(input.getExecutionDate())
					.executionTime(input.getExecutionTime())
					.sellBuyTypeCode(input.getSellBuyTypeCode())
					.executionQuantity(executionQuantity)
					.executionPrice(executionPrice)
					.executionAmount(executionAmount)
					.settlementDate(input.getSettlementDate())
					.build();
			
			splitTransactionDtoList.add(splitTransactionDto);
			
		} else {
			
			SplitExecutionTransactionDomainServiceOutput.SplitTransactionDto splitTransactionDto = SplitExecutionTransactionDomainServiceOutput.SplitTransactionDto.builder()
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
			
			splitTransactionDtoList.add(splitTransactionDto);
			
		}
		
		SplitExecutionTransactionDomainServiceOutput splitExecutionTransactionDomainServiceOutput = SplitExecutionTransactionDomainServiceOutput.builder()
				.splitTransactionDtoList(splitTransactionDtoList)
				.build();
		
		return splitExecutionTransactionDomainServiceOutput;
	}
	
	  
    private ValuePositionDomainServiceOutput _setValuePositionDomainServiceOutput(ContractLegPosition contractLegPosition, BigDecimal positionValuationProfitLossAmount) {

    	ValuePositionDomainServiceOutput valuePositionDomainServiceOutput = ValuePositionDomainServiceOutput.builder()
    			.currencyCode(contractLegPosition.getPriceCurrencyCode())
    			.positionQuantity(contractLegPosition.getPositionQuantity())
    			.positionValuationProfitLossAmount(positionValuationProfitLossAmount)
    			.build();
    	
    	return valuePositionDomainServiceOutput;
	}

	private UpdatePortfolioMqProcessingYnDomainServiceOutput _setUpdatePortfolioMqProcessingYnDomainServiceOutput(Transaction transaction, List<ContractTransaction> contractTransactions) {

		UpdatePortfolioMqProcessingYnDomainServiceOutput.TransactionDto transactionDto = UpdatePortfolioMqProcessingYnDomainServiceOutput.TransactionDto.builder()
				.id(transaction.getId())
				.executionId(transaction.getExecutionId())
				.cancelYn(transaction.getCancelYn())
				.transactionProcessingDatetime(transaction.getTransactionProcessingDatetime())
				.build();
		
		List<UpdatePortfolioMqProcessingYnDomainServiceOutput.ContractTransactionDto> ContractTransactionDtoList = new ArrayList<>();
		
		for (ContractTransaction contractTransaction : contractTransactions) {
			
			UpdatePortfolioMqProcessingYnDomainServiceOutput.ContractTransactionDto contractTransactionDto = UpdatePortfolioMqProcessingYnDomainServiceOutput.ContractTransactionDto.builder()
					.id(contractTransaction.getId())
					.transactionId(contractTransaction.getTransactionId())
					.contractId(contractTransaction.getContract().getId())
					.portfolioMqProcessingYn(contractTransaction.getPortfolioMqProcessingYn())
					.transactionDate(contractTransaction.getTransactionDate())
					.sellBuyTypeCode(contractTransaction.getSellBuyTypeCode())
					.build();
			
			ContractTransactionDtoList.add(contractTransactionDto);
		}
		
		
		UpdatePortfolioMqProcessingYnDomainServiceOutput updatePortfolioMqProcessingYnDomainServiceOutput = UpdatePortfolioMqProcessingYnDomainServiceOutput.builder()
				.transactionDto(transactionDto)
				.contractTransactionDto(ContractTransactionDtoList)
				.build();
		
		return updatePortfolioMqProcessingYnDomainServiceOutput;
	}

	private String _setSplitYn(SplitExecutionTransactionDomainServiceInput input, BigDecimal positionQuantity) {
	
		// 분할여부
		String splitYn = Code.NO;
		
		// 매도매입구분코드
		String sellBuyTypeCode = input.getSellBuyTypeCode();
		// 체결수량
		BigDecimal executionQuantity = input.getExecutionQuantity();
		
		// + 포지션수량 && 매도 거래인 경우, - 포지션수량 && 매입 거래인 경우
		if (   BigDecimal.ZERO.compareTo(positionQuantity) < 0 && "1".equals(sellBuyTypeCode)
			|| BigDecimal.ZERO.compareTo(positionQuantity) > 0 && "2".equals(sellBuyTypeCode)) {
			
			// 포지션수량보다 매도한 체결수량이 더 큰 경우
			if (positionQuantity.abs().compareTo(executionQuantity) < 0) {
				
				splitYn = Code.YES;
			}
		}
		
		return splitYn;
	}

	private ContractLegPosition _setPreviousContractLegPosition(ContractLegPosition previousContractLegPosition, String positionStartTime) {
	
		ContractLegPosition contractLegPosition = previousContractLegPosition = previousContractLegPosition.toBuilder()
				.positionEndDatetime(DateUtil.addSeconds(positionStartTime, -1))
				.build();
		
		return contractLegPosition;
	}

	private ContractLegPosition _getContractLegPosition(Long portfolioId, Long productId) {
	
		// 포지션수량
		BigDecimal positionQuantity = BigDecimal.ZERO;
		
		// 계약 조회
		Contract contract = contractReader.findFirstByPortfolioIdAndProductId(portfolioId, productId);
		
		// 계약레그 조회
		ContractLeg contractLeg = null;
		
		if (contract != null) {
			
			contractLeg = contractLegReader.findFirstByContractIdAndProductId(contract.getId(), productId);
		}
		
		// 계약레그포지션 조회    	
		ContractLegPosition contractLegPosition = null;
		
		if (contractLeg != null) {
		
			contractLegPosition = contractLegPositionReader.findFirstByContractLegOrderByIdDesc(contractLeg);
		}
		
		//ContractLegPosition contractLegPosition = contractLegPositionReader.findFirstByContractLegProductIdOrderByIdDesc(productId);
		
		return contractLegPosition;
	}
}
