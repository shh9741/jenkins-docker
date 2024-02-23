package com.tradegene.risk_management.commandservice.infrastructure.adapter.web;

import com.tradegene.risk_management.commandservice.application.dto.CashTransactionRegisterUseCaseOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tradegene.app.dto.common.response.ApiResponse;
import com.tradegene.risk_management.commandservice.application.dto.CashTransactionRegisterUseCaseInput;
import com.tradegene.risk_management.commandservice.application.ports.in.CashTransactionUseCase;
import com.tradegene.risk_management.commandservice.infrastructure.adapter.dto.CashRegisterRequest;
import com.tradegene.risk_management.commandservice.infrastructure.adapter.dto.CashRegisterResponse;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/cash")
@RequiredArgsConstructor
@RestController
public class CashController {

	private final CashTransactionUseCase cashTransactionUseCase;
	
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CashRegisterResponse>> register(@RequestBody CashRegisterRequest request) {
        
    	/* =================================================================
         * 1. 캐쉬 등록 useCase 호출
         * ================================================================= */
        // 1-1. 입력값 조립
        CashTransactionRegisterUseCaseInput cashTransactionRegisterUseCaseInput = _setCashTransactionRegisterUseCaseInput(request);

        // 1-2. 호출
        CashTransactionRegisterUseCaseOutput cashTransactionRegisterUseCaseOutput = cashTransactionUseCase.register(cashTransactionRegisterUseCaseInput);
        

        /* =================================================================
         * 2. 출력값 조립
         * ================================================================= */
        CashRegisterResponse cashRegisterResponse = _setRegisterResponse(cashTransactionRegisterUseCaseOutput);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(cashRegisterResponse));
    }

	private CashTransactionRegisterUseCaseInput _setCashTransactionRegisterUseCaseInput(CashRegisterRequest request) {
    	
        return CashTransactionRegisterUseCaseInput.builder()
                .amount(request.getAmount())
                .build();
    }
	
    private CashRegisterResponse _setRegisterResponse(CashTransactionRegisterUseCaseOutput cashTransactionRegisterUseCaseOutput) {
    	
    	return CashRegisterResponse.builder()
                .contractTransactionId(cashTransactionRegisterUseCaseOutput.getContractTransactionId())
                .build();
	}
}
