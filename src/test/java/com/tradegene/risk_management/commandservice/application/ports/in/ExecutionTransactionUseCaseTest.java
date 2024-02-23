package com.tradegene.risk_management.commandservice.application.ports.in;

import com.tradegene.app.utils.DateUtil;
import com.tradegene.risk_management.commandservice.application.dto.ExecutionTransactionRegisterUseCaseInput;
import com.tradegene.risk_management.commandservice.domain.code.Code;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.math.BigDecimal;

@SpringBootTest
class ExecutionTransactionUseCaseTest {

    @Autowired
    private ExecutionTransactionUseCase executionTransactionUseCase;

    @Commit
    @Test
    void register() {

        ExecutionTransactionRegisterUseCaseInput input = ExecutionTransactionRegisterUseCaseInput.builder()
                .executionId(Long.valueOf(501))
                .portfolioId(Long.valueOf(46))
                .productId(Long.valueOf(3))
                .executionDate(DateUtil.getCurrentDate())
                .executionTime(DateUtil.getCurrentTime())
                .sellBuyTypeCode(Code.SELL_BUY_TYPE_CODE_BUY)
                .executionQuantity(BigDecimal.valueOf(5))
                .executionPrice(BigDecimal.valueOf(1000))
                .executionAmount(BigDecimal.valueOf(5000))
                .settlementDate(DateUtil.getCurrentDate())
                .build();

        executionTransactionUseCase.register(input);
    }
}