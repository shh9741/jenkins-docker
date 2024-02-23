package com.tradegene.risk_management.commandservice.infrastructure.adapter.messaging.in;

import com.google.gson.Gson;
import com.tradegene.risk_management.commandservice.application.dto.ExecutionTransactionRegisterFailureUseCaseInput;
import com.tradegene.risk_management.commandservice.application.dto.ExecutionTransactionRegisterSuccessUseCaseInput;
import com.tradegene.risk_management.commandservice.application.ports.in.ExecutionTransactionUseCase;
import com.tradegene.risk_management.commandservice.infrastructure.adapter.dto.PortfolioPerformanceAckNackRegisterConsumerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PortfolioPerformanceConsumer {
	
	private final ExecutionTransactionUseCase transactionUseCase;

	@KafkaListener(
			id = "portfolio-performance.register.finalAck",
			topics = "portfolio-performance.register.finalAck",
			groupId = "risk-management",
			errorHandler = "customErrorHandler",
			autoStartup = "true"
	)
    public void subRegisterFinalAck(String input) {

		PortfolioPerformanceAckNackRegisterConsumerDto portfolioPerformanceAckNackRegisterConsumerDto = new Gson().fromJson(input, PortfolioPerformanceAckNackRegisterConsumerDto.class);

		/* =================================================================
		 * 1. 거래 등록(정상거래) useCase 호출
		 * ================================================================= */
		// 1-1. 입력값 조립
		ExecutionTransactionRegisterSuccessUseCaseInput executionTransactionRegisterSuccessUseCaseInput = _setExecutionTransactionRegisterUseCaseInput(portfolioPerformanceAckNackRegisterConsumerDto);

		// 1-1. 호출
		transactionUseCase.registerSuccess(executionTransactionRegisterSuccessUseCaseInput);
	}

	@KafkaListener(
			id = "portfolio-performance.register.initialNack",
			topics = "portfolio-performance.register.initialNack",
			properties = {
					"isolation.level:read_uncommitted"
			},
			groupId = "risk-management",
			errorHandler = "customErrorHandler",
			autoStartup = "true"
	)
    public void subRegisterInitialNack(String input) {

		PortfolioPerformanceAckNackRegisterConsumerDto portfolioPerformanceAckNackRegisterConsumerDto = new Gson().fromJson(input, PortfolioPerformanceAckNackRegisterConsumerDto.class);

		/* =================================================================
		 * 1. 거래 등록(보상거래) useCase 호출
		 * ================================================================= */
		// 1-1. 입력값 조립
		ExecutionTransactionRegisterFailureUseCaseInput executionTransactionRegisterFailureUseCaseInput = _setExecutionTransactionRegisterFailureUseCaseInput(portfolioPerformanceAckNackRegisterConsumerDto);

		// 1-1. 호출
		transactionUseCase.registerFailure(executionTransactionRegisterFailureUseCaseInput);
	}

	@KafkaListener(
			id = "portfolio-performance.register.finalNack",
			topics = "portfolio-performance.register.finalNack",
			groupId = "risk-management",
			errorHandler = "customErrorHandler",
			autoStartup = "true"
	)
	public void subRegisterFinalNack(String input) {

		PortfolioPerformanceAckNackRegisterConsumerDto portfolioPerformanceSumAckNackRegisterConsumerDto = new Gson().fromJson(input, PortfolioPerformanceAckNackRegisterConsumerDto.class);

		/* =================================================================
		 * 1. 거래 등록(보상거래) useCase 호출
		 * ================================================================= */
		// 1-1. 입력값 조립
		ExecutionTransactionRegisterFailureUseCaseInput executionTransactionRegisterFailureUseCaseInput = _setExecutionTransactionRegisterFailureUseCaseInput(portfolioPerformanceSumAckNackRegisterConsumerDto);

		// 1-1. 호출
		transactionUseCase.registerFailure(executionTransactionRegisterFailureUseCaseInput);
	}

	private ExecutionTransactionRegisterSuccessUseCaseInput _setExecutionTransactionRegisterUseCaseInput(
			PortfolioPerformanceAckNackRegisterConsumerDto input) {
		
		return ExecutionTransactionRegisterSuccessUseCaseInput.builder()
				.transactionId(input.getTransactionId())
				.build();
	}

	private ExecutionTransactionRegisterFailureUseCaseInput _setExecutionTransactionRegisterFailureUseCaseInput(
			PortfolioPerformanceAckNackRegisterConsumerDto input) {

		return ExecutionTransactionRegisterFailureUseCaseInput.builder()
				.transactionId(input.getTransactionId())
				.build();
	}
}
